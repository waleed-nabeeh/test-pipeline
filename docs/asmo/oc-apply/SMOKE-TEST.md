# External Kafka Certificate Smoke Test

The test image is already published as `docker.io/waleednabeeh/asmo-kafka-smoke:2026-09-18`. It contains the PFX-derived **public-certificate JKS** and test truststore password. The Kafka SCRAM password is not in the image; the pod mounts the existing `asmo-app-client` Secret.

**Run only the `oc` commands below on the OpenShift machine. Do not run Podman, copy a PFX, or create a JKS there.** This test does not change the Kafka cluster or KafkaUser.

## Deploy

After pulling branch `product-catalog-pipeline-test`, change to `docs/asmo/oc-apply`:

```bash
oc get secret asmo-app-client -n asmo-kafka-dev
oc delete pod kafka-external-cert-smoke -n asmo-kafka-dev --ignore-not-found
oc apply -f manifests/kafka-external-cert-smoke.yaml
oc wait pod/kafka-external-cert-smoke -n asmo-kafka-dev --for=condition=Ready --timeout=5m
```

If the pod does not become Ready, check `oc describe pod kafka-external-cert-smoke -n asmo-kafka-dev`. An `ImagePullBackOff` means the cluster cannot pull the Docker Hub image; it is not a Kafka certificate result.

## Test

```bash
oc exec -n asmo-kafka-dev kafka-external-cert-smoke -- \
  bash /opt/kafka/test-external-kafka-client.sh metadata
oc exec -n asmo-kafka-dev kafka-external-cert-smoke -- \
  bash /opt/kafka/test-external-kafka-client.sh consume
```

`metadata` should describe `asmo.events.dev`. `consume` succeeds after reading one record through the external `:443` route; the record content is suppressed. If the topic is empty, `consume` times out even when TLS and SCRAM work. Only if a test record is acceptable to downstream applications, run:

```bash
oc exec -n asmo-kafka-dev kafka-external-cert-smoke -- \
  bash /opt/kafka/test-external-kafka-client.sh produce
oc exec -n asmo-kafka-dev kafka-external-cert-smoke -- \
  bash /opt/kafka/test-external-kafka-client.sh consume
```

This tests the pod's access to bootstrap and advertised broker routes. It does **not** prove OIC gateway network access.

## Clean Up

```bash
oc delete pod kafka-external-cert-smoke -n asmo-kafka-dev
```

The separate JKS file for OIC upload is on the machine where the PFX was processed, not on this OpenShift node. OIC uses the existing `asmo-app-client` username and its password from the OpenShift Secret.
