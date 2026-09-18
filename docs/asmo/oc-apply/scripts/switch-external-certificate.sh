#!/usr/bin/env bash
set -euo pipefail

namespace=asmo-kafka-dev
kafka=asmo-dev-kafka
secret=asmo-kafka-external-cert
source_namespace=openshift-ingress
source_secret=apps-tls

if [[ ${1:-} != apply && ${1:-} != revert ]]; then
  printf 'Usage: bash %s apply|revert\n' "$0" >&2
  exit 2
fi

for tool in oc jq; do
  command -v "$tool" >/dev/null || { printf '%s is required\n' "$tool" >&2; exit 1; }
done

kafka_json=$(oc get kafka "$kafka" -n "$namespace" -o json)
listener_index=$(jq -er '[.spec.kafka.listeners | to_entries[] | select(.value.name == "external") | .key] | if length == 1 then .[0] else error("expected exactly one external listener") end' <<<"$kafka_json")

if [[ $1 == apply ]]; then
  for tool in openssl base64; do
    command -v "$tool" >/dev/null || { printf '%s is required\n' "$tool" >&2; exit 1; }
  done

  if jq -e --argjson i "$listener_index" '.spec.kafka.listeners[$i].configuration.brokerCertChainAndKey != null' <<<"$kafka_json" >/dev/null; then
    printf 'External listener already has a custom certificate. No change made.\n' >&2
    exit 1
  fi

  source_json=$(oc get secret "$source_secret" -n "$source_namespace" -o json)
  jq -e '.data["tls.crt"] and .data["tls.key"]' <<<"$source_json" >/dev/null || {
    printf 'Source secret must contain tls.crt and tls.key.\n' >&2
    exit 1
  }

  cert_data=$(jq -r '.data["tls.crt"]' <<<"$source_json")
  cert_pem=$(printf '%s' "$cert_data" | base64 -d)
  cert_count=$(grep -c -- '-----BEGIN CERTIFICATE-----' <<<"$cert_pem")
  if (( cert_count < 2 )); then
    printf 'Source tls.crt has no intermediate chain. Add the chain before switching Kafka.\n' >&2
    exit 1
  fi
  printf '%s\n' "$cert_pem" | openssl x509 -noout -checkend 0 >/dev/null
  printf '%s\n' "$cert_pem" | openssl x509 -noout -text |
    grep -A1 'Subject Alternative Name' |
    grep -F 'DNS:*.apps.asmonpeclr.np.asmo.com' >/dev/null || {
      printf 'Source certificate must have the ASMO apps wildcard SAN.\n' >&2
      exit 1
    }
  jq -r '.data["tls.key"]' <<<"$source_json" | base64 -d |
    grep -F -- '-----BEGIN PRIVATE KEY-----' >/dev/null || {
      printf 'Source tls.key must be an unencrypted PKCS#8 private key.\n' >&2
      exit 1
    }
  cert_public_key=$(printf '%s\n' "$cert_pem" | openssl x509 -pubkey -noout |
    openssl pkey -pubin -outform DER | openssl dgst -sha256)
  key_public_key=$(jq -r '.data["tls.key"]' <<<"$source_json" | base64 -d |
    openssl pkey -pubout -outform DER | openssl dgst -sha256)
  if [[ $cert_public_key != "$key_public_key" ]]; then
    printf 'Source certificate and private key do not match.\n' >&2
    exit 1
  fi

  jq -e --arg name "$secret" --arg ns "$namespace" \
    '{apiVersion:"v1",kind:"Secret",metadata:{name:$name,namespace:$ns},type:"kubernetes.io/tls",data:{"tls.crt":.data["tls.crt"],"tls.key":.data["tls.key"]}}' \
    <<<"$source_json" | oc apply -f -

  certificate_ref='{"secretName":"asmo-kafka-external-cert","certificate":"tls.crt","key":"tls.key"}'
  if jq -e --argjson i "$listener_index" '.spec.kafka.listeners[$i].configuration != null' <<<"$kafka_json" >/dev/null; then
    patch=$(jq -nc --arg path "/spec/kafka/listeners/$listener_index/configuration/brokerCertChainAndKey" --argjson value "$certificate_ref" '[{op:"add",path:$path,value:$value}]')
  else
    patch=$(jq -nc --arg path "/spec/kafka/listeners/$listener_index/configuration" --argjson value "$certificate_ref" '[{op:"add",path:$path,value:{brokerCertChainAndKey:$value}}]')
  fi
  oc patch kafka "$kafka" -n "$namespace" --type=json -p "$patch"
  printf 'External listener now references %s. Wait for the broker roll, then check the served certificate.\n' "$secret"
else
  if ! jq -e --argjson i "$listener_index" --arg name "$secret" '.spec.kafka.listeners[$i].configuration.brokerCertChainAndKey.secretName == $name' <<<"$kafka_json" >/dev/null; then
    printf 'External listener is not using %s. No change made.\n' "$secret" >&2
    exit 1
  fi

  config_keys=$(jq -r --argjson i "$listener_index" '.spec.kafka.listeners[$i].configuration | keys | length' <<<"$kafka_json")
  if (( config_keys == 1 )); then
    path="/spec/kafka/listeners/$listener_index/configuration"
  else
    path="/spec/kafka/listeners/$listener_index/configuration/brokerCertChainAndKey"
  fi
  patch=$(jq -nc --arg path "$path" '[{op:"remove",path:$path}]')
  oc patch kafka "$kafka" -n "$namespace" --type=json -p "$patch"
  printf 'External listener will return to the operator-generated certificate after the broker roll.\n'
fi
