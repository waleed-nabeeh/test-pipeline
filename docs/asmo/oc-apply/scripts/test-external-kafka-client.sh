#!/usr/bin/env bash
set -euo pipefail

mode=${1:-}
bootstrap=asmo-dev-kafka-kafka-bootstrap-asmo-kafka-dev.apps.asmonpeclr.np.asmo.com:443
topic=asmo.events.dev
truststore_dir=/opt/kafka/test-truststore
password_file=/opt/kafka/user/password

case "$mode" in
  metadata|consume|produce) ;;
  *) printf 'Usage: bash -s -- metadata|consume|produce\n' >&2; exit 2 ;;
esac

[[ -s "$truststore_dir/truststore.jks" && -s "$truststore_dir/truststore.password" && -s "$password_file" ]] || {
  printf 'Required mounted truststore or SCRAM password is missing.\n' >&2
  exit 1
}

truststore_password=$(<"$truststore_dir/truststore.password")
scram_password=$(<"$password_file")
[[ "$truststore_password" =~ ^[[:alnum:]]{6,}$ ]] || {
  printf 'Use a test JKS password of at least six letters/digits.\n' >&2
  exit 1
}
[[ "$scram_password" =~ ^[A-Za-z0-9_+./=-]+$ ]] || {
  printf 'SCRAM password needs Java properties/JAAS escaping; this smoke test supports simple generated passwords only.\n' >&2
  exit 1
}

umask 077
props=$(mktemp /tmp/kafka-external-smoke.XXXXXX)
trap 'rm -f "$props"' EXIT
printf '%s\n' \
  'security.protocol=SASL_SSL' \
  'sasl.mechanism=SCRAM-SHA-512' \
  "sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username=\"asmo-app-client\" password=\"$scram_password\";" \
  "ssl.truststore.location=$truststore_dir/truststore.jks" \
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
