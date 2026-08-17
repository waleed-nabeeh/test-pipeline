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

Then apply Kafka:

```bash
oc apply -f manifests/03-kafka-nodepool-dev.yaml
oc apply -f manifests/04-kafka-cluster-dev.yaml
```

Wait for Kafka:

```bash
oc get kafka -n asmo-kafka-dev
oc get kafkanodepool -n asmo-kafka-dev
oc get pods -n asmo-kafka-dev -w
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
