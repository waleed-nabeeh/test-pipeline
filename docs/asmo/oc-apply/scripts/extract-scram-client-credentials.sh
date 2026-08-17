#!/usr/bin/env bash
set -euo pipefail

KAFKA_NAMESPACE="${KAFKA_NAMESPACE:-asmo-kafka-dev}"
KAFKA_CLUSTER="${KAFKA_CLUSTER:-asmo-dev-kafka}"
CLIENT_NAME="${CLIENT_NAME:-asmo-app-client}"
OUTPUT_DIR="${OUTPUT_DIR:-./kafka-certs}"

mkdir -p "${OUTPUT_DIR}/cluster-ca"

echo "Extracting Kafka cluster CA certificate..."
oc extract secret/"${KAFKA_CLUSTER}"-cluster-ca-cert \
  -n "${KAFKA_NAMESPACE}" \
  --to="${OUTPUT_DIR}/cluster-ca" \
  --confirm

echo
echo "SCRAM username:"
echo "${CLIENT_NAME}"

echo
echo "SCRAM password:"
oc get secret "${CLIENT_NAME}" \
  -n "${KAFKA_NAMESPACE}" \
  -o jsonpath='{.data.password}' | base64 -d
echo

echo
echo "CA certificate files:"
ls -la "${OUTPUT_DIR}/cluster-ca"

echo
echo "Do not commit ${OUTPUT_DIR} or decoded passwords to Git."
