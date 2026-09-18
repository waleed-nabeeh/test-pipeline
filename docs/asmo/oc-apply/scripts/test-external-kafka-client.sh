#!/usr/bin/env bash
set -euo pipefail

mode=${1:-}
bootstrap=asmo-dev-kafka-kafka-bootstrap-asmo-kafka-dev.apps.asmonpeclr.np.asmo.com:443
topic=asmo.events.dev
truststore_dir=/opt/kafka/test-truststore
public_cert=/opt/kafka/public-cert/tls.crt
password_file=/opt/kafka/user/password

case "$mode" in
  metadata|consume|produce) ;;
  *) printf 'Usage: bash -s -- metadata|consume|produce\n' >&2; exit 2 ;;
esac

[[ -s "$password_file" ]] || {
  printf 'Mounted SCRAM password is missing.\n' >&2
  exit 1
}

umask 077
workdir=$(mktemp -d /tmp/kafka-external-smoke.XXXXXX)
trap 'rm -rf "$workdir"' EXIT

if [[ -s "$truststore_dir/truststore.jks" && -s "$truststore_dir/truststore.password" ]]; then
  truststore="$truststore_dir/truststore.jks"
  truststore_password=$(<"$truststore_dir/truststore.password")
elif [[ -s "$public_cert" ]]; then
  truststore="$workdir/truststore.jks"
  truststore_password=$(openssl rand -hex 16)
  export TRUSTSTORE_PASSWORD="$truststore_password"
  keytool -importcert -storetype JKS -keystore "$truststore" \
    -storepass:env TRUSTSTORE_PASSWORD -alias asmo-kafka-server \
    -file "$public_cert" -noprompt > /dev/null
  unset TRUSTSTORE_PASSWORD
else
  printf 'Mount a JKS truststore or the public Kafka external certificate.\n' >&2
  exit 1
fi

scram_password=$(<"$password_file")
[[ "$truststore_password" =~ ^[[:alnum:]]{6,}$ ]] || {
  printf 'Use a test JKS password of at least six letters/digits.\n' >&2
  exit 1
}
[[ "$scram_password" =~ ^[A-Za-z0-9_+./=-]+$ ]] || {
  printf 'SCRAM password needs Java properties/JAAS escaping; this smoke test supports simple generated passwords only.\n' >&2
  exit 1
}

props="$workdir/client.properties"
printf '%s\n' \
  'security.protocol=SASL_SSL' \
  'sasl.mechanism=SCRAM-SHA-512' \
  "sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username=\"asmo-app-client\" password=\"$scram_password\";" \
  "ssl.truststore.location=$truststore" \
  "ssl.truststore.password=$truststore_password" \
  'ssl.truststore.type=JKS' \
  'ssl.endpoint.identification.algorithm=https' > "$props"
unset truststore_password scram_password

case "$mode" in
  metadata)
    /opt/kafka/bin/kafka-topics.sh --bootstrap-server "$bootstrap" \
      --command-config "$props" --describe --topic "$topic"
    ;;
  consume)
    /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server "$bootstrap" \
      --consumer.config "$props" --topic "$topic" \
      --group "asmo-app-cert-smoke-$$" --from-beginning \
      --max-messages 1 --timeout-ms 15000 \
      --consumer-property enable.auto.commit=false > /dev/null
    printf 'Consumed one record through the external Kafka route.\n'
    ;;
  produce)
    printf 'asmo-kafka-cert-smoke-%s\n' "$(date -u +%Y%m%dT%H%M%SZ)" |
      /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server "$bootstrap" \
        --producer.config "$props" --topic "$topic"
    printf 'Sent one test record to %s through the external Kafka route.\n' "$topic"
    ;;
esac
