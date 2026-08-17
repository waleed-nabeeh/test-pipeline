# ASMO Kafka DEV oc apply Starter Pack

Use this folder for the first ASMO DEV Kafka installation using direct `oc apply`.

Chosen approach:

```text
Product: Red Hat Streams for Apache Kafka
Operator channel: stable
Authentication: SCRAM-SHA-512 over TLS
Authorization: Kafka ACLs
Deployment method: Manual oc apply first, GitOps later
Namespace: asmo-kafka-dev
StorageClass: thin-csi
```

## Before Applying

The first DEV test lets the Streams for Apache Kafka 3.2 operator choose its default supported Kafka version by not setting `spec.kafka.version` or `spec.kafka.metadataVersion`.

After deployment, confirm the selected Kafka version from the Kafka resource status and pod image.

## Apply Order

```bash
oc apply -f manifests/00-namespace.yaml
oc apply -f manifests/01-operatorgroup.yaml
oc apply -f manifests/02-subscription.yaml
```

Wait for the operator:

```bash
oc get csv -n asmo-kafka-dev
oc get pods -n asmo-kafka-dev
oc get crd | grep -E 'kafkas.kafka.strimzi.io|kafkanodepools.kafka.strimzi.io|kafkatopics.kafka.strimzi.io|kafkausers.kafka.strimzi.io'
```

If no CSV or pods appear yet, do not continue to Kafka manifests `03` and `04`. First validate the Subscription and InstallPlan:

```bash
oc get subscription -n asmo-kafka-dev
oc describe subscription amq-streams -n asmo-kafka-dev
oc get installplan -n asmo-kafka-dev
oc get csv -n asmo-kafka-dev
oc get pods -n asmo-kafka-dev
oc get events -n asmo-kafka-dev --sort-by=.lastTimestamp
```

Confirm the package exists in OperatorHub:

```bash
oc get packagemanifest amq-streams -n openshift-marketplace
```

Optional watch command:

```bash
watch -n 5 'oc get subscription,installplan,csv,pods -n asmo-kafka-dev'
```

Continue only when all of these are true:

```text
Subscription exists and has no blocking errors.
InstallPlan exists and is complete/installed.
CSV phase is Succeeded.
Streams for Apache Kafka operator pod is Running.
Kafka CRDs exist.
```

Then apply Kafka:

```bash
oc apply -f manifests/03-kafka-nodepool-dev.yaml
oc apply -f manifests/04-kafka-cluster-dev.yaml
```

The Kafka manifests use:

```yaml
apiVersion: kafka.strimzi.io/v1
```

If an older local copy prints this warning, the resource may still be created, but update your local copy from GitHub before continuing:

```text
Warning: Version v1beta2 of the Kafka/KafkaNodePool API is deprecated. Please use the v1 version instead.
```

Wait for Kafka:

```bash
oc wait kafka/asmo-dev-kafka -n asmo-kafka-dev --for=condition=Ready --timeout=30m
oc get kafka -n asmo-kafka-dev
oc get kafkanodepool -n asmo-kafka-dev
oc get pods -n asmo-kafka-dev
oc get pvc -n asmo-kafka-dev
oc get routes -n asmo-kafka-dev
```

Check which Kafka version the operator selected:

```bash
oc get kafka asmo-dev-kafka -n asmo-kafka-dev \
  -o jsonpath='{.status.kafkaVersion}{"\n"}{.status.kafkaMetadataVersion}{"\n"}'
```

If Kafka does not become ready within 30 minutes, collect troubleshooting details:

```bash
oc describe kafka asmo-dev-kafka -n asmo-kafka-dev
oc get events -n asmo-kafka-dev --sort-by=.lastTimestamp
oc logs deploy/amq-streams-cluster-operator-v3.2.1-8 -n asmo-kafka-dev --tail=200
```

Then apply topic and SCRAM user:

```bash
oc apply -f manifests/topics/asmo-events-dev.yaml
oc apply -f manifests/users/asmo-app-client-scram.yaml
```

Validate:

```bash
bash scripts/validate-kafka.sh
```

Extract SCRAM credentials and Kafka CA:

```bash
bash scripts/extract-scram-client-credentials.sh
```

## Important Notes

- Do not install into `gitlab-system` unless explicitly approved.
- Do not store generated secrets, decoded passwords, private keys, or extracted certs in Git.
- The external Kafka bootstrap hostname is generated after the Kafka cluster route listener is ready.
- OpenShift route-based Kafka external access usually uses port `443`.
