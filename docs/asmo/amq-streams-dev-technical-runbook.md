# ASMO DEV AMQ Streams Technical Implementation Runbook

## Scope From Requirement

This runbook covers the exact implementation sequence for the following ASMO DEV scope:

1. Install and configure Red Hat AMQ Streams on the DEV OpenShift environment.
2. Configure the required Kafka brokers, topics, and basic cluster configuration.
3. Configure secure external connectivity to allow external clients to securely connect to AMQ Streams.
4. Configure the required client authentication and authorization mechanisms.

## Important Version Notes

Red Hat renamed AMQ Streams documentation/product wording in newer releases to **Streams for Apache Kafka**. The OpenShift custom resources are still Strimzi-style resources such as:

- `Kafka`
- `KafkaTopic`
- `KafkaUser`

Before applying any YAML, confirm the installed operator version and supported Kafka versions in your OpenShift cluster.

Use placeholders in this runbook only after replacing them with ASMO DEV values:

- `<KAFKA_NAMESPACE>`
- `<KAFKA_CLUSTER>`
- `<STORAGE_CLASS>`
- `<BOOTSTRAP_HOST>`
- `<BROKER_0_HOST>`
- `<BROKER_1_HOST>`
- `<BROKER_2_HOST>`
- `<CLIENT_NAME>`
- `<TOPIC_NAME>`
- `<CONSUMER_GROUP>`

Recommended DEV baseline:

```text
KAFKA_NAMESPACE=asmo-kafka-dev
KAFKA_CLUSTER=asmo-dev-kafka
STORAGE_CLASS=thin-csi
EXTERNAL_LISTENER_TYPE=route
AUTHENTICATION=scram-sha-512
AUTHORIZATION=simple
```

## Values Captured From ASMO DEV Screenshots

These values were visible from the OpenShift console screenshots and can be used as the current ASMO DEV baseline.

| Item | Value |
| --- | --- |
| Product tile | `Streams for Apache Kafka` |
| Provider | `Red Hat` |
| Operator channel | `stable` |
| Operator version shown | `3.2.1-8` |
| OpenShift version | `4.20.22` |
| StorageClass | `thin-csi` |
| Storage provisioner | `csi.vsphere.vmware.com` |
| Storage reclaim policy | `Delete` |
| OpenShift apps route domain | `apps.asmonpeclr.np.asmo.com` |
| Existing GitLab route example | `gitlab.apps.asmonpeclr.np.asmo.com` |
| Current console project in screenshot | `gitlab-system` |
| Recommended Kafka namespace | `asmo-kafka-dev` |

Do not install the Kafka cluster into `gitlab-system` unless ASMO/platform explicitly approves it. Use `asmo-kafka-dev` for the Kafka operator and Kafka resources unless another namespace is assigned.

Use this baseline variable block for copy/paste commands:

```bash
export KAFKA_NAMESPACE=asmo-kafka-dev
export KAFKA_CLUSTER=asmo-dev-kafka
export STORAGE_CLASS=thin-csi
export OPERATOR_CHANNEL=stable
export CLIENT_NAME=asmo-app-client
export TOPIC_NAME=asmo.events.dev
export CONSUMER_GROUP=asmo-app
export APPS_DOMAIN=apps.asmonpeclr.np.asmo.com
```

For the first DEV test, the Kafka manifest can omit `spec.kafka.version` and `spec.kafka.metadataVersion`. The Streams for Apache Kafka 3.2 operator then uses its default supported Kafka version. After deployment, confirm the selected Kafka version from the Kafka resource status and Kafka pod image.

```text
KAFKA_VERSION=<operator-default-for-streams-3.2>
METADATA_VERSION=<operator-default-for-streams-3.2>
```

## Target Version And Best-Practice Status

This runbook targets the ASMO DEV environment shown in the screenshots:

```text
OpenShift Container Platform: 4.20.22
Streams for Apache Kafka operator: 3.2.1-8
Operator channel: stable
StorageClass: thin-csi
```

The runbook does not hardcode `spec.kafka.version` or `spec.kafka.metadataVersion` in the first `oc apply` path. This avoids accidentally setting an unsupported value. The operator selects its default supported Kafka version. After the cluster is ready, capture the actual Kafka version from resource status and pod image.

This runbook follows current DEV best practices for new Streams for Apache Kafka deployments:

| Area | Status | Notes |
| --- | --- | --- |
| KRaft mode | Yes | New deployments use KRaft, not ZooKeeper. |
| KafkaNodePool | Yes | Required for KRaft-based deployments. |
| Persistent storage | Yes | Uses block storage through `thin-csi`; do not use NFS for Kafka. |
| TLS external access | Yes | External listener is TLS-enabled. |
| Authentication | Yes | Uses SCRAM-SHA-512 over TLS for the first ASMO DEV implementation. |
| Authorization | Yes | Uses Kafka ACLs through `KafkaUser`. |
| Topic/User operators | Yes | Topics and users are managed as OpenShift custom resources. |
| DEV sizing | Acceptable | Uses 3 dual-role broker/controller nodes for DEV. |

Important production note:

```text
The 3-node dual-role KafkaNodePool is acceptable for DEV/testing.
For production or production-like performance testing, use dedicated controller node pools and dedicated broker node pools, plus monitoring, alerting, resource sizing, node affinity, anti-affinity, backup/DR, and certificate lifecycle management.
```

## Chosen Implementation Path

For the first ASMO DEV test, use:

```text
Deployment method: Manual oc apply
Authentication: SCRAM-SHA-512 over TLS
Authorization: Simple Kafka ACLs through KafkaUser
GitOps: Later, after the oc apply deployment is validated
```

Reason:

```text
Manual oc apply is easier for first-time troubleshooting because each resource can be applied, inspected, and fixed step by step.
After the Kafka cluster is proven, the same tested manifests can be moved into GitOps.
```

Starter manifests and scripts are available in:

```text
docs/asmo/oc-apply/
```

## Phase 0: Pre-Implementation Checks

### 0.1 Login To OpenShift

```bash
oc login <openshift-api-url>
```

Confirm current user:

```bash
oc whoami
```

Confirm cluster access:

```bash
oc cluster-info
oc get nodes
```

If `oc get nodes` is not allowed, confirm with the OpenShift platform team that your account can at least manage the target namespace and install/use the operator.

### 0.2 Confirm Storage Classes

```bash
oc get storageclass
```

Pick the approved DEV storage class and record it:

```text
STORAGE_CLASS=thin-csi
```

Kafka must use persistent storage for any realistic DEV validation.

### 0.3 Confirm Operator Availability

Search available operator packages:

```bash
oc get packagemanifests -n openshift-marketplace | grep -Ei 'amq|streams|kafka'
```

If the package is named `amq-streams`, inspect it:

```bash
oc get packagemanifest amq-streams -n openshift-marketplace -o yaml
```

Check available channels:

```bash
oc get packagemanifest amq-streams -n openshift-marketplace \
  -o jsonpath='{range .status.channels[*]}{.name}{"\n"}{end}'
```

If `amq-streams` does not exist, use the package name returned by the search command and replace `amq-streams` in the Subscription example.

### 0.4 Confirm Required DNS And Network

For route-based external access, confirm the OpenShift route base domain:

```bash
oc get ingresses.config/cluster -o jsonpath='{.spec.domain}{"\n"}'
```

Confirm with the network/platform team:

```text
1. External client source networks are allowed to reach OpenShift routes/load balancer.
2. DNS can be created for bootstrap and broker hosts, if custom hosts are required.
3. TLS certificate approach is agreed: OpenShift route cert, internal CA, or public CA.
4. Firewall/proxy inspection will not break Kafka TLS traffic.
```

## Phase 1: Create Namespace

Set variables locally:

```bash
export KAFKA_NAMESPACE=asmo-kafka-dev
export KAFKA_CLUSTER=asmo-dev-kafka
```

Create or select the namespace:

```bash
oc new-project "${KAFKA_NAMESPACE}"
```

If it already exists:

```bash
oc project "${KAFKA_NAMESPACE}"
```

Validate:

```bash
oc project
oc get all -n "${KAFKA_NAMESPACE}"
```

## Phase 2: Install Red Hat AMQ Streams Operator

Use the OpenShift web console OperatorHub if this is the approved ASMO process.

If CLI installation is approved, use this pattern.

## Phase 2A: Install From OpenShift Console

Use this section if installing from the OpenShift web console instead of CLI.

Based on the OpenShift console search result, select:

```text
Streams for Apache Kafka
Provided by Red Hat
```

Do not select these for the base Kafka operator installation:

```text
Streams for Apache Kafka Proxy
Streams for Apache Kafka Console
```

The `Proxy` and `Console` tiles are optional/additional components. The base requirement in the ASMO scope is to install and configure Kafka through Red Hat Streams for Apache Kafka.

### 2A.1 Select The Correct Project

In the screenshot, the selected project is:

```text
gitlab-system
```

Do not install the Kafka cluster into `gitlab-system` unless this is explicitly approved. `gitlab-system` should normally be reserved for GitLab components.

Recommended Kafka namespace:

```text
asmo-kafka-dev
```

From the OpenShift console:

```text
Home > Projects > Create Project
```

Create:

```text
Name: asmo-kafka-dev
Display name: ASMO Kafka DEV
Description: Red Hat Streams for Apache Kafka for ASMO DEV
```

Then switch the project selector from:

```text
gitlab-system
```

to:

```text
asmo-kafka-dev
```

### 2A.2 Install The Operator

From the OpenShift console:

```text
Developer or Administrator perspective
Ecosystem > Software Catalog
Search: streams
Click: Streams for Apache Kafka
Click: Install
```

Use these recommended installation choices:

| Field | Recommended Value |
| --- | --- |
| Update channel | Use the latest approved stable channel shown by OpenShift |
| Installation mode | A specific namespace on the cluster |
| Installed namespace | `asmo-kafka-dev` |
| Update approval | Automatic for DEV, Manual if platform team requires approval |

Click:

```text
Install
```

### 2A.3 Verify Console Installation

From the console:

```text
Operators > Installed Operators
Project: asmo-kafka-dev
```

Expected:

```text
Streams for Apache Kafka
Status: Succeeded
```

Then verify from CLI:

```bash
oc project asmo-kafka-dev
oc get csv -n asmo-kafka-dev
oc get pods -n asmo-kafka-dev
oc get crd | grep -E 'kafkas.kafka.strimzi.io|kafkatopics.kafka.strimzi.io|kafkausers.kafka.strimzi.io'
```

If the operator is accidentally installed in `gitlab-system`, stop before creating Kafka resources and confirm with the platform team whether it should be moved to `asmo-kafka-dev`.

### 2.1 Create OperatorGroup

Create `operatorgroup-amq-streams.yaml`:

```yaml
apiVersion: operators.coreos.com/v1
kind: OperatorGroup
metadata:
  name: amq-streams-operator-group
  namespace: asmo-kafka-dev
spec:
  targetNamespaces:
    - asmo-kafka-dev
```

Apply:

```bash
oc apply -f operatorgroup-amq-streams.yaml
```

Validate:

```bash
oc get operatorgroup -n "${KAFKA_NAMESPACE}"
```

### 2.2 Create Subscription

First confirm the channel:

```bash
oc get packagemanifest amq-streams -n openshift-marketplace \
  -o jsonpath='{range .status.channels[*]}{.name}{"\n"}{end}'
```

Create `subscription-amq-streams.yaml`.

Replace `<CHANNEL>` with the approved channel from the previous command.

```yaml
apiVersion: operators.coreos.com/v1alpha1
kind: Subscription
metadata:
  name: amq-streams
  namespace: asmo-kafka-dev
spec:
  channel: <CHANNEL>
  installPlanApproval: Automatic
  name: amq-streams
  source: redhat-operators
  sourceNamespace: openshift-marketplace
```

Apply:

```bash
oc apply -f subscription-amq-streams.yaml
```

Validate operator installation:

```bash
oc get subscription -n "${KAFKA_NAMESPACE}"
oc get installplan -n "${KAFKA_NAMESPACE}"
oc get csv -n "${KAFKA_NAMESPACE}"
oc get pods -n "${KAFKA_NAMESPACE}"
```

Expected:

```text
ClusterServiceVersion phase is Succeeded.
AMQ Streams / Streams for Apache Kafka operator pod is Running.
```

Validate CRDs:

```bash
oc get crd | grep -E 'kafkas.kafka.strimzi.io|kafkatopics.kafka.strimzi.io|kafkausers.kafka.strimzi.io'
```

## Phase 3: Create Kafka Cluster

### 3.1 Confirm Supported Kafka Versions

After operator installation, inspect supported Kafka versions from the Cluster Operator config or documentation for the installed operator.

Also check the operator logs if needed:

```bash
oc logs -n "${KAFKA_NAMESPACE}" deploy/strimzi-cluster-operator --tail=200
```

Use only a Kafka version supported by the installed operator.

### 3.2 Create Kafka Cluster YAML

Create KRaft-based Kafka manifests.

New Streams for Apache Kafka 3.x / Kafka 4.x deployments do not use ZooKeeper. KRaft mode requires:

```text
KafkaNodePool resources
Kafka metadata annotations:
  strimzi.io/node-pools: enabled
  strimzi.io/kraft: enabled
```

For DEV, a 3-node dual-role pool is acceptable when the goal is functional validation. For production-like environments, use dedicated controller and broker node pools.

Create `kafka-nodepool-dev.yaml`.

The first DEV test omits `spec.kafka.version` and `spec.kafka.metadataVersion` so the installed operator can select its default supported Kafka version.

Replace:

- `<STORAGE_CLASS>` with the approved storage class.

```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaNodePool
metadata:
  name: dual-role
  namespace: asmo-kafka-dev
  labels:
    strimzi.io/cluster: asmo-dev-kafka
spec:
  replicas: 3
  roles:
    - controller
    - broker
  storage:
    type: jbod
    volumes:
      - id: 0
        type: persistent-claim
        size: 50Gi
        class: <STORAGE_CLASS>
        deleteClaim: false
  resources:
    requests:
      memory: 2Gi
      cpu: "1"
    limits:
      memory: 4Gi
      cpu: "2"
```

Create `kafka-cluster-dev.yaml`.

```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: Kafka
metadata:
  name: asmo-dev-kafka
  namespace: asmo-kafka-dev
  annotations:
    strimzi.io/node-pools: enabled
    strimzi.io/kraft: enabled
spec:
  kafka:
    listeners:
      - name: tls
        port: 9093
        type: internal
        tls: true
        authentication:
          type: tls
      - name: external
        port: 9094
        type: route
        tls: true
        authentication:
          type: tls
    authorization:
      type: simple
    config:
      auto.create.topics.enable: "false"
      default.replication.factor: 3
      min.insync.replicas: 2
      offsets.topic.replication.factor: 3
      transaction.state.log.replication.factor: 3
      transaction.state.log.min.isr: 2
      num.partitions: 3
  entityOperator:
    topicOperator: {}
    userOperator: {}
```

Apply:

```bash
oc apply -f kafka-nodepool-dev.yaml
oc apply -f kafka-cluster-dev.yaml
```

### 3.3 Monitor Kafka Deployment

```bash
oc get kafka -n "${KAFKA_NAMESPACE}"
oc get kafkanodepool -n "${KAFKA_NAMESPACE}"
oc get pods -n "${KAFKA_NAMESPACE}" -w
```

In another terminal:

```bash
oc describe kafka "${KAFKA_CLUSTER}" -n "${KAFKA_NAMESPACE}"
```

Check persistent volume claims:

```bash
oc get pvc -n "${KAFKA_NAMESPACE}"
```

Expected:

```text
Kafka node pool pods are Running and Ready.
PVCs are Bound.
Entity Operator pod is Running.
Kafka custom resource Ready condition is True.
```

Check readiness:

```bash
oc get kafka "${KAFKA_CLUSTER}" -n "${KAFKA_NAMESPACE}" \
  -o jsonpath='{.status.conditions}{"\n"}'
```

## Phase 4: Configure Required Kafka Topics

### 4.1 Prepare Topic Matrix

Collect this from ASMO application teams before creating final topics:

| Topic | Producer | Consumer | Partitions | Replicas | Retention | Cleanup Policy |
| --- | --- | --- | --- | --- | --- | --- |
| `asmo.events.dev` | `<producer-app>` | `<consumer-app>` | `3` | `3` | `7 days` | `delete` |

### 4.2 Create KafkaTopic

Create `topic-asmo-events-dev.yaml`:

```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaTopic
metadata:
  name: asmo-events-dev
  namespace: asmo-kafka-dev
  labels:
    strimzi.io/cluster: asmo-dev-kafka
spec:
  topicName: asmo.events.dev
  partitions: 3
  replicas: 3
  config:
    retention.ms: 604800000
    cleanup.policy: delete
```

Apply:

```bash
oc apply -f topic-asmo-events-dev.yaml
```

Validate:

```bash
oc get kafkatopic -n "${KAFKA_NAMESPACE}"
oc describe kafkatopic asmo-events-dev -n "${KAFKA_NAMESPACE}"
```

Expected:

```text
KafkaTopic Ready condition is True.
Topic exists in Kafka.
```

## Phase 5: Configure Secure External Connectivity

### 5.1 Confirm Generated Routes

For `type: route`, AMQ Streams creates a bootstrap route and broker routes.

```bash
oc get routes -n "${KAFKA_NAMESPACE}"
```

Get Kafka listener status:

```bash
oc get kafka "${KAFKA_CLUSTER}" -n "${KAFKA_NAMESPACE}" -o yaml
```

Look under:

```text
status.listeners
```

Capture the external bootstrap server.

Example:

```bash
oc get kafka "${KAFKA_CLUSTER}" -n "${KAFKA_NAMESPACE}" \
  -o jsonpath='{range .status.listeners[*]}{.name}{" => "}{.bootstrapServers}{"\n"}{end}'
```

### 5.2 Optional: Configure Custom External Route Hosts

If ASMO requires fixed DNS names, update the external listener with route host overrides.

Example listener block:

```yaml
- name: external
  port: 9094
  type: route
  tls: true
  authentication:
    type: tls
  configuration:
    bootstrap:
      host: <BOOTSTRAP_HOST>
    brokers:
      - broker: 0
        host: <BROKER_0_HOST>
      - broker: 1
        host: <BROKER_1_HOST>
      - broker: 2
        host: <BROKER_2_HOST>
```

Apply the updated Kafka manifest:

```bash
oc apply -f kafka-cluster-dev.yaml
```

Monitor rolling updates:

```bash
oc get pods -n "${KAFKA_NAMESPACE}" -w
oc get routes -n "${KAFKA_NAMESPACE}"
```

### 5.3 Validate DNS

From a machine that should connect to Kafka:

```bash
nslookup <BOOTSTRAP_HOST>
```

or:

```bash
dig <BOOTSTRAP_HOST>
```

Validate TLS reachability:

```bash
openssl s_client -connect <BOOTSTRAP_HOST>:443 -servername <BOOTSTRAP_HOST>
```

Expected:

```text
TCP connection succeeds.
TLS handshake succeeds.
Certificate presented by route/listener is trusted or can be trusted by client truststore.
```

## Phase 6: Configure Authentication

The Kafka listener authentication must match the KafkaUser authentication.

Use one of the following approved options.

## Option A: mTLS Authentication

Use mTLS when certificates are acceptable for ASMO client authentication.

### 6A.1 Ensure Listener Uses TLS Auth

Kafka listener:

```yaml
authentication:
  type: tls
```

### 6A.2 Create KafkaUser For Client

Create `user-asmo-app-client-tls.yaml`:

```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaUser
metadata:
  name: asmo-app-client
  namespace: asmo-kafka-dev
  labels:
    strimzi.io/cluster: asmo-dev-kafka
spec:
  authentication:
    type: tls
  authorization:
    type: simple
    acls:
      - resource:
          type: topic
          name: asmo.events.dev
          patternType: literal
        operations:
          - Create
          - Describe
          - Read
          - Write
        host: "*"
      - resource:
          type: group
          name: asmo-app
          patternType: prefix
        operations:
          - Read
        host: "*"
```

Apply:

```bash
oc apply -f user-asmo-app-client-tls.yaml
```

Validate:

```bash
oc get kafkauser -n "${KAFKA_NAMESPACE}"
oc get kafkauser asmo-app-client -n "${KAFKA_NAMESPACE}" -o yaml
```

Wait until Ready is `True`:

```bash
oc get kafkausers -o wide -w -n "${KAFKA_NAMESPACE}"
```

### 6A.3 Extract Client Certificates

The User Operator creates a secret with the same name as the `KafkaUser`.

```bash
oc get secret "${CLIENT_NAME}" -n "${KAFKA_NAMESPACE}"
```

Create local files:

```bash
mkdir -p ./kafka-certs/"${CLIENT_NAME}"
oc extract secret/"${CLIENT_NAME}" \
  -n "${KAFKA_NAMESPACE}" \
  --to=./kafka-certs/"${CLIENT_NAME}" \
  --confirm
```

Extract cluster CA certificate:

```bash
mkdir -p ./kafka-certs/cluster-ca
oc extract secret/"${KAFKA_CLUSTER}"-cluster-ca-cert \
  -n "${KAFKA_NAMESPACE}" \
  --to=./kafka-certs/cluster-ca \
  --confirm
```

Expected generated files normally include client certificate/key material and CA certificate material. Confirm file names:

```bash
ls -la ./kafka-certs/"${CLIENT_NAME}"
ls -la ./kafka-certs/cluster-ca
```

Typical mTLS client files:

```text
ca.crt
user.crt
user.key
user.p12
user.password
```

The exact secret keys can be checked with:

```bash
oc get secret "${CLIENT_NAME}" -n "${KAFKA_NAMESPACE}" -o jsonpath='{.data}' | jq 'keys'
```

If `jq` is not available:

```bash
oc describe secret "${CLIENT_NAME}" -n "${KAFKA_NAMESPACE}"
```

### 6A.4 Build Java Truststore And Keystore For mTLS Clients

Many Java clients need JKS or PKCS12 files.

Create a truststore from the Kafka cluster CA:

```bash
keytool -importcert \
  -alias asmo-kafka-cluster-ca \
  -file ./kafka-certs/cluster-ca/ca.crt \
  -keystore ./kafka-certs/asmo-kafka-truststore.jks \
  -storepass changeit \
  -noprompt
```

If `user.p12` and `user.password` were generated, use them directly as the client keystore:

```bash
cat ./kafka-certs/"${CLIENT_NAME}"/user.password
ls -la ./kafka-certs/"${CLIENT_NAME}"/user.p12
```

Example mTLS Java client properties:

```properties
security.protocol=SSL
ssl.truststore.location=./kafka-certs/asmo-kafka-truststore.jks
ssl.truststore.password=changeit
ssl.keystore.location=./kafka-certs/asmo-app-client/user.p12
ssl.keystore.password=<value-from-user.password>
ssl.key.password=<value-from-user.password>
```

## Option B: SCRAM-SHA-512 Authentication

Use SCRAM when username/password is easier for application teams.

### 6B.1 Update Listener Auth To SCRAM

Kafka listener example:

```yaml
authentication:
  type: scram-sha-512
```

Apply updated Kafka manifest:

```bash
oc apply -f kafka-cluster-dev.yaml
```

Monitor readiness:

```bash
oc get kafka "${KAFKA_CLUSTER}" -n "${KAFKA_NAMESPACE}" -w
```

### 6B.2 Create SCRAM KafkaUser

Create `user-asmo-app-client-scram.yaml`:

```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaUser
metadata:
  name: asmo-app-client
  namespace: asmo-kafka-dev
  labels:
    strimzi.io/cluster: asmo-dev-kafka
spec:
  authentication:
    type: scram-sha-512
  authorization:
    type: simple
    acls:
      - resource:
          type: topic
          name: asmo.events.dev
          patternType: literal
        operations:
          - Create
          - Describe
          - Read
          - Write
        host: "*"
      - resource:
          type: group
          name: asmo-app
          patternType: prefix
        operations:
          - Read
        host: "*"
```

Apply:

```bash
oc apply -f user-asmo-app-client-scram.yaml
```

Wait for Ready:

```bash
oc get kafkausers -o wide -w -n "${KAFKA_NAMESPACE}"
```

Extract SCRAM password:

```bash
oc get secret "${CLIENT_NAME}" -n "${KAFKA_NAMESPACE}" \
  -o jsonpath='{.data.password}' | base64 -d
echo
```

Extract cluster CA:

```bash
mkdir -p ./kafka-certs/cluster-ca
oc extract secret/"${KAFKA_CLUSTER}"-cluster-ca-cert \
  -n "${KAFKA_NAMESPACE}" \
  --to=./kafka-certs/cluster-ca \
  --confirm
```

Create a Java truststore for SCRAM-over-TLS clients:

```bash
keytool -importcert \
  -alias asmo-kafka-cluster-ca \
  -file ./kafka-certs/cluster-ca/ca.crt \
  -keystore ./kafka-certs/asmo-kafka-truststore.jks \
  -storepass changeit \
  -noprompt
```

Example SCRAM Java client properties:

```properties
security.protocol=SASL_SSL
sasl.mechanism=SCRAM-SHA-512
sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username="asmo-app-client" password="<password-from-secret>";
ssl.truststore.location=./kafka-certs/asmo-kafka-truststore.jks
ssl.truststore.password=changeit
```

## Phase 6C: Certificate And Credential Summary

Certificates and credentials are not available immediately after operator installation. They are created after the Kafka cluster and `KafkaUser` resources are ready.

| Auth Type | Required For Client | Source Secret |
| --- | --- | --- |
| TLS/mTLS | Kafka CA, client certificate, client private key, optional `user.p12` | `${KAFKA_CLUSTER}-cluster-ca-cert` and `${CLIENT_NAME}` |
| SCRAM | Kafka CA and SCRAM password | `${KAFKA_CLUSTER}-cluster-ca-cert` and `${CLIENT_NAME}` |

Get Kafka CA certificate:

```bash
mkdir -p ./kafka-certs/cluster-ca
oc extract secret/"${KAFKA_CLUSTER}"-cluster-ca-cert \
  -n "${KAFKA_NAMESPACE}" \
  --to=./kafka-certs/cluster-ca \
  --confirm
```

Get TLS client certificate/key:

```bash
mkdir -p ./kafka-certs/"${CLIENT_NAME}"
oc extract secret/"${CLIENT_NAME}" \
  -n "${KAFKA_NAMESPACE}" \
  --to=./kafka-certs/"${CLIENT_NAME}" \
  --confirm
```

Get SCRAM password:

```bash
oc get secret "${CLIENT_NAME}" \
  -n "${KAFKA_NAMESPACE}" \
  -o jsonpath='{.data.password}' | base64 -d
echo
```

For external route-based Kafka access, the bootstrap port is usually `443`, and clients normally need to trust the Kafka cluster CA generated by Streams for Apache Kafka.

## Phase 7: Configure Authorization

Authorization is enabled at Kafka cluster level:

```yaml
authorization:
  type: simple
```

Access is granted through `KafkaUser.spec.authorization.acls`.

Recommended minimum ACLs:

### Producer Only

```yaml
acls:
  - resource:
      type: topic
      name: asmo.events.dev
      patternType: literal
    operations:
      - Create
      - Describe
      - Write
    host: "*"
```

### Consumer Only

```yaml
acls:
  - resource:
      type: topic
      name: asmo.events.dev
      patternType: literal
    operations:
      - Describe
      - Read
    host: "*"
  - resource:
      type: group
      name: asmo-app
      patternType: prefix
    operations:
      - Read
    host: "*"
```

### Producer And Consumer

```yaml
acls:
  - resource:
      type: topic
      name: asmo.events.dev
      patternType: literal
    operations:
      - Create
      - Describe
      - Read
      - Write
    host: "*"
  - resource:
      type: group
      name: asmo-app
      patternType: prefix
    operations:
      - Read
    host: "*"
```

Validate user status after ACL changes:

```bash
oc get kafkauser asmo-app-client -n "${KAFKA_NAMESPACE}" -o yaml
```

## Phase 8: Internal Kafka Test

### 8.1 Start Temporary Kafka Client Pod

Use an image version compatible with the installed AMQ Streams version.

```bash
oc run kafka-client \
  -n "${KAFKA_NAMESPACE}" \
  -ti \
  --image=registry.redhat.io/amq-streams/kafka-37-rhel9:latest \
  --rm=true \
  --restart=Never \
  -- bash
```

If the image tag is unavailable, ask the platform team for the approved AMQ Streams Kafka client image.

### 8.2 Internal Bootstrap Address

Inside the namespace, internal bootstrap is normally:

```text
asmo-dev-kafka-kafka-bootstrap:9093
```

Use the service list to confirm:

```bash
oc get svc -n "${KAFKA_NAMESPACE}" | grep bootstrap
```

### 8.3 Test Topic Metadata

Inside the client pod, create a client config that matches TLS/SCRAM setup.

For mTLS, mount/copy proper certificates first. For quick platform validation, you can also execute Kafka CLI from a pod that has required certs mounted as secrets.

List topics:

```bash
bin/kafka-topics.sh \
  --bootstrap-server asmo-dev-kafka-kafka-bootstrap:9093 \
  --command-config /tmp/client.properties \
  --list
```

Expected:

```text
asmo.events.dev
```

## Phase 9: External Client Test

### 9.1 Get Bootstrap Server

```bash
oc get kafka "${KAFKA_CLUSTER}" -n "${KAFKA_NAMESPACE}" \
  -o jsonpath='{range .status.listeners[*]}{.name}{" => "}{.bootstrapServers}{"\n"}{end}'
```

Use the external bootstrap server.

For OpenShift routes, Kafka clients usually connect on port `443`.

### 9.2 Validate TLS Handshake

From external client machine:

```bash
openssl s_client -connect <BOOTSTRAP_HOST>:443 -servername <BOOTSTRAP_HOST>
```

Success criteria:

```text
Connection opens.
Certificate subject/SAN matches expected hostname.
Certificate chain is trusted or can be imported into client truststore.
```

### 9.3 Example SCRAM Client Properties

Create `client-scram.properties`:

```properties
security.protocol=SASL_SSL
sasl.mechanism=SCRAM-SHA-512
sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username="asmo-app-client" password="<PASSWORD>";
ssl.truststore.location=<TRUSTSTORE_PATH>
ssl.truststore.password=<TRUSTSTORE_PASSWORD>
```

List topics:

```bash
kafka-topics.sh \
  --bootstrap-server <BOOTSTRAP_HOST>:443 \
  --command-config client-scram.properties \
  --list
```

Produce test message:

```bash
kafka-console-producer.sh \
  --bootstrap-server <BOOTSTRAP_HOST>:443 \
  --producer.config client-scram.properties \
  --topic asmo.events.dev
```

Enter:

```text
test-message-from-external-client
```

Consume:

```bash
kafka-console-consumer.sh \
  --bootstrap-server <BOOTSTRAP_HOST>:443 \
  --consumer.config client-scram.properties \
  --topic asmo.events.dev \
  --group asmo-app-test \
  --from-beginning \
  --timeout-ms 15000
```

### 9.4 Example mTLS Client Properties

Create `client-mtls.properties`:

```properties
security.protocol=SSL
ssl.truststore.location=<TRUSTSTORE_PATH>
ssl.truststore.password=<TRUSTSTORE_PASSWORD>
ssl.keystore.location=<KEYSTORE_PATH>
ssl.keystore.password=<KEYSTORE_PASSWORD>
ssl.key.password=<KEY_PASSWORD>
```

List topics:

```bash
kafka-topics.sh \
  --bootstrap-server <BOOTSTRAP_HOST>:443 \
  --command-config client-mtls.properties \
  --list
```

Produce:

```bash
kafka-console-producer.sh \
  --bootstrap-server <BOOTSTRAP_HOST>:443 \
  --producer.config client-mtls.properties \
  --topic asmo.events.dev
```

Consume:

```bash
kafka-console-consumer.sh \
  --bootstrap-server <BOOTSTRAP_HOST>:443 \
  --consumer.config client-mtls.properties \
  --topic asmo.events.dev \
  --group asmo-app-test \
  --from-beginning \
  --timeout-ms 15000
```

## Phase 10: Operational Verification

### 10.1 Check Kafka Resource

```bash
oc get kafka -n "${KAFKA_NAMESPACE}"
oc describe kafka "${KAFKA_CLUSTER}" -n "${KAFKA_NAMESPACE}"
```

### 10.2 Check Pods

```bash
oc get pods -n "${KAFKA_NAMESPACE}" -o wide
```

Expected pods:

```text
Cluster Operator pod
Kafka node pool pods
Entity Operator pod
```

### 10.3 Check Events

```bash
oc get events -n "${KAFKA_NAMESPACE}" --sort-by=.lastTimestamp
```

### 10.4 Check Operator Logs

```bash
oc logs -n "${KAFKA_NAMESPACE}" deploy/strimzi-cluster-operator --tail=300
```

Entity Operator logs:

```bash
oc get pods -n "${KAFKA_NAMESPACE}" | grep entity-operator
oc logs -n "${KAFKA_NAMESPACE}" <entity-operator-pod-name> --tail=300
```

### 10.5 Check Routes And Services

```bash
oc get svc -n "${KAFKA_NAMESPACE}"
oc get routes -n "${KAFKA_NAMESPACE}"
```

### 10.6 Check Secrets

```bash
oc get secrets -n "${KAFKA_NAMESPACE}" | grep -E 'cluster-ca|clients-ca|asmo-app-client'
```

## Phase 11: Troubleshooting

### KafkaUser Not Ready

Check label:

```bash
oc get kafkauser asmo-app-client -n "${KAFKA_NAMESPACE}" -o yaml
```

The label must match the Kafka resource name:

```yaml
labels:
  strimzi.io/cluster: asmo-dev-kafka
```

Check User Operator logs:

```bash
oc logs -n "${KAFKA_NAMESPACE}" <entity-operator-pod-name> --tail=300
```

### Client Authenticates But Cannot Read Or Write

Likely ACL issue. Check `KafkaUser` ACLs:

```bash
oc get kafkauser asmo-app-client -n "${KAFKA_NAMESPACE}" -o yaml
```

Confirm:

```text
Topic name exactly matches.
Consumer group name matches or prefix pattern is used.
Operations include Read/Write/Describe as required.
Kafka cluster has authorization.type: simple.
```

### External Client Cannot Connect

Check:

```bash
oc get routes -n "${KAFKA_NAMESPACE}"
oc get kafka "${KAFKA_CLUSTER}" -n "${KAFKA_NAMESPACE}" -o yaml
nslookup <BOOTSTRAP_HOST>
openssl s_client -connect <BOOTSTRAP_HOST>:443 -servername <BOOTSTRAP_HOST>
```

Common causes:

```text
DNS missing or wrong.
Firewall blocks client network.
Wrong bootstrap hostname.
Certificate SAN mismatch.
Client does not trust CA.
External listener is not ready.
```

### Kafka Pods Pending

Check PVCs and events:

```bash
oc get pvc -n "${KAFKA_NAMESPACE}"
oc describe pod <pod-name> -n "${KAFKA_NAMESPACE}"
oc get events -n "${KAFKA_NAMESPACE}" --sort-by=.lastTimestamp
```

Common causes:

```text
Invalid storage class.
Insufficient storage quota.
Insufficient CPU/memory quota.
Pod security or SCC restriction.
```

## Phase 12: Rollback / Cleanup For DEV

Do not run cleanup unless this is confirmed with the platform/application team.

Delete test users:

```bash
oc delete kafkauser asmo-app-client -n "${KAFKA_NAMESPACE}"
```

Delete test topics:

```bash
oc delete kafkatopic asmo-events-dev -n "${KAFKA_NAMESPACE}"
```

Delete Kafka cluster:

```bash
oc delete kafka "${KAFKA_CLUSTER}" -n "${KAFKA_NAMESPACE}"
```

PVCs may remain because `deleteClaim: false` is configured. This is intentional to avoid accidental data loss.

List PVCs:

```bash
oc get pvc -n "${KAFKA_NAMESPACE}"
```

Delete PVCs only after explicit approval:

```bash
oc delete pvc <pvc-name> -n "${KAFKA_NAMESPACE}"
```

## Phase 13: Optional GitOps Deployment Flow

Use this phase if ASMO wants to test Kafka deployment through GitOps first, then migrate the same tested manifests to GitLab.

Recommended approach:

```text
Temporary testing source: GitHub repo
Deployment controller: OpenShift GitOps / Argo CD
Target cluster: ASMO DEV OpenShift
Final source of truth: ASMO GitLab repo
```

This approach is valid and recommended for controlled testing because every Kafka resource is stored as code and can be reviewed, reapplied, compared, and migrated later.

### 13.1 Recommended GitOps Repository Structure

Create a temporary GitHub repo such as:

```text
asmo-kafka-openshift-test
```

Recommended structure:

```text
asmo-kafka-openshift-test/
├── README.md
├── manifests/
│   ├── 00-namespace.yaml
│   ├── 01-operatorgroup.yaml
│   ├── 02-subscription.yaml
│   ├── 03-kafka-nodepool-dev.yaml
│   ├── 04-kafka-cluster-dev.yaml
│   ├── topics/
│   │   └── asmo-events-dev.yaml
│   └── users/
│       └── asmo-app-client.yaml
└── scripts/
    ├── validate-kafka.sh
    └── extract-client-credentials.sh
```

Do not store generated private keys, decoded passwords, extracted cert bundles, or application secrets in Git.

Safe to store:

```text
Kafka
KafkaNodePool
KafkaTopic
KafkaUser
OperatorGroup
Subscription
Namespace
README and scripts
```

Do not store:

```text
Decoded SCRAM passwords
Private keys
Extracted user certificates
Generated OpenShift secrets
Production credentials
```

### 13.2 GitOps Sync Order

Kafka resources must be applied in this order:

```text
1. Namespace
2. OperatorGroup
3. Subscription
4. Wait for operator CSV to become Succeeded
5. KafkaNodePool
6. Kafka cluster
7. KafkaTopic
8. KafkaUser
```

In GitOps, the operator installation may take a few minutes. The Kafka custom resources should not be considered healthy until the operator CRDs are available.

### 13.3 Example Argo CD Application

If OpenShift GitOps / Argo CD is installed, create an Argo CD `Application` pointing to the temporary GitHub repo.

Create `argocd-application-asmo-kafka-dev.yaml`:

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: asmo-kafka-dev
  namespace: openshift-gitops
spec:
  project: default
  source:
    repoURL: https://github.com/<github-org-or-user>/asmo-kafka-openshift-test.git
    targetRevision: main
    path: manifests
  destination:
    server: https://kubernetes.default.svc
    namespace: asmo-kafka-dev
  syncPolicy:
    automated:
      prune: false
      selfHeal: true
    syncOptions:
      - CreateNamespace=true
```

Apply:

```bash
oc apply -f argocd-application-asmo-kafka-dev.yaml
```

Validate Argo CD application:

```bash
oc get application asmo-kafka-dev -n openshift-gitops
oc describe application asmo-kafka-dev -n openshift-gitops
```

If the cluster uses a different Argo CD namespace, replace:

```text
openshift-gitops
```

with the approved namespace.

### 13.4 GitOps Health Checks

Check whether OpenShift GitOps is installed:

```bash
oc get ns | grep -E 'openshift-gitops|argocd'
oc get pods -n openshift-gitops
```

Check operator and Kafka resources:

```bash
oc get csv -n "${KAFKA_NAMESPACE}"
oc get kafka -n "${KAFKA_NAMESPACE}"
oc get kafkanodepool -n "${KAFKA_NAMESPACE}"
oc get kafkatopic -n "${KAFKA_NAMESPACE}"
oc get kafkauser -n "${KAFKA_NAMESPACE}"
oc get pods -n "${KAFKA_NAMESPACE}"
```

Expected:

```text
Argo CD Application is Synced.
Argo CD Application is Healthy.
Streams for Apache Kafka operator CSV is Succeeded.
Kafka custom resource is Ready.
KafkaNodePool is Ready.
KafkaTopic resources are Ready.
KafkaUser resources are Ready.
```

### 13.5 GitHub To GitLab Migration

After testing is successful, migrate the exact tested manifests to GitLab.

Recommended migration steps:

```bash
git clone https://github.com/<github-org-or-user>/asmo-kafka-openshift-test.git
cd asmo-kafka-openshift-test
git remote add gitlab <gitlab-repo-url>
git push gitlab main
```

Then update the Argo CD `Application` source:

```yaml
spec:
  source:
    repoURL: <gitlab-repo-url>
    targetRevision: main
    path: manifests
```

Apply the updated application:

```bash
oc apply -f argocd-application-asmo-kafka-dev.yaml
```

Validate:

```bash
oc get application asmo-kafka-dev -n openshift-gitops
oc describe application asmo-kafka-dev -n openshift-gitops
```

Acceptance condition:

```text
Application remains Synced and Healthy after switching from GitHub to GitLab.
Kafka cluster is unchanged.
Topics and users remain ready.
Producer/consumer test still succeeds.
```

### 13.6 GitOps Best-Practice Notes

Use pull requests or merge requests for all Kafka manifest changes.

Recommended branch model:

```text
main        = approved DEV state
feature/*   = proposed changes
```

Recommended GitOps controls:

```text
Disable prune during first test.
Enable prune only after the team agrees on ownership and deletion behavior.
Keep secrets out of Git.
Use External Secrets, Sealed Secrets, Vault, or another approved secret mechanism if secret GitOps is required.
Protect the GitLab main branch before making it the final source of truth.
Tag the tested GitHub commit before migration.
```

## Final Acceptance Criteria

The scope is complete when all items below are true:

| Requirement | Acceptance Criteria |
| --- | --- |
| Install AMQ Streams | Operator CSV is `Succeeded`; CRDs exist; operator pod is running. |
| Configure brokers | Kafka CR is Ready; 3 broker pods are ready; PVCs are Bound. |
| Configure topics | Required `KafkaTopic` resources are Ready and visible from Kafka client. |
| Configure external connectivity | External bootstrap endpoint exists; DNS resolves; TLS handshake succeeds from external client. |
| Configure authentication | Kafka listener auth matches KafkaUser auth; client credentials generated; client can authenticate. |
| Configure authorization | ACLs are applied; allowed read/write succeeds; unauthorized access is denied. |
| Validate end-to-end | External producer sends a message and external consumer receives it successfully. |

## References

- Red Hat Streams for Apache Kafka on OpenShift documentation: securing access to Kafka brokers and users.
- Red Hat Streams for Apache Kafka on OpenShift documentation: setting up client access using listeners.
- Red Hat Streams for Apache Kafka on OpenShift documentation: using the User Operator to manage Kafka users.
