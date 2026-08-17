#!/usr/bin/env bash
set -euo pipefail

KAFKA_NAMESPACE="${KAFKA_NAMESPACE:-asmo-kafka-dev}"
KAFKA_CLUSTER="${KAFKA_CLUSTER:-asmo-dev-kafka}"

echo "Namespace: ${KAFKA_NAMESPACE}"
oc get namespace "${KAFKA_NAMESPACE}"

echo
echo "Operator status:"
oc get csv -n "${KAFKA_NAMESPACE}"

echo
echo "Kafka resources:"
oc get kafka "${KAFKA_CLUSTER}" -n "${KAFKA_NAMESPACE}"
oc get kafkanodepool -n "${KAFKA_NAMESPACE}"
oc get kafkatopic -n "${KAFKA_NAMESPACE}"
oc get kafkauser -n "${KAFKA_NAMESPACE}"

echo
echo "Pods:"
oc get pods -n "${KAFKA_NAMESPACE}" -o wide

echo
echo "PVCs:"
oc get pvc -n "${KAFKA_NAMESPACE}"

echo
echo "Routes:"
oc get routes -n "${KAFKA_NAMESPACE}"

echo
echo "Listener bootstrap servers:"
oc get kafka "${KAFKA_CLUSTER}" -n "${KAFKA_NAMESPACE}" \
  -o jsonpath='{range .status.listeners[*]}{.name}{" => "}{.bootstrapServers}{"\n"}{end}'

echo
echo "Recent events:"
oc get events -n "${KAFKA_NAMESPACE}" --sort-by=.lastTimestamp | tail -n 30
