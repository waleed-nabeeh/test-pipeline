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
STORAGE_CLASS=<confirm-from-oc-get-storageclass>
EXTERNAL_LISTENER_TYPE=route
AUTHENTICATION=tls or scram-sha-512
AUTHORIZATION=simple
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
STORAGE_CLASS=<approved-storage-class>
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

Create `kafka-cluster-dev.yaml`.

Replace:

- `<STORAGE_CLASS>` with the approved storage class.
- `<KAFKA_VERSION>` with the supported Kafka version for the installed operator.

```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: Kafka
metadata:
  name: asmo-dev-kafka
  namespace: asmo-kafka-dev
spec:
  kafka:
    version: <KAFKA_VERSION>
    replicas: 3
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
    storage:
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
  zookeeper:
    replicas: 3
    storage:
      type: persistent-claim
      size: 20Gi
      class: <STORAGE_CLASS>
      deleteClaim: false
    resources:
      requests:
        memory: 1Gi
        cpu: "500m"
      limits:
        memory: 2Gi
        cpu: "1"
  entityOperator:
    topicOperator: {}
    userOperator: {}
```

Apply:

```bash
oc apply -f kafka-cluster-dev.yaml
```

### 3.3 Monitor Kafka Deployment

```bash
oc get kafka -n "${KAFKA_NAMESPACE}"
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
Kafka broker pods are Running and Ready.
ZooKeeper pods are Running and Ready if the installed operator uses ZooKeeper.
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
oc get secret asmo-app-client -n "${KAFKA_NAMESPACE}"
```

Create local files:

```bash
oc extract secret/asmo-app-client -n "${KAFKA_NAMESPACE}" --to=./asmo-app-client-certs --confirm
```

Extract cluster CA certificate:

```bash
oc extract secret/"${KAFKA_CLUSTER}"-cluster-ca-cert -n "${KAFKA_NAMESPACE}" --to=./asmo-cluster-ca --confirm
```

Expected generated files normally include client certificate/key material and CA certificate material. Confirm file names:

```bash
ls -la ./asmo-app-client-certs
ls -la ./asmo-cluster-ca
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
oc get secret asmo-app-client -n "${KAFKA_NAMESPACE}" \
  -o jsonpath='{.data.password}' | base64 -d
echo
```

Extract cluster CA:

```bash
oc extract secret/"${KAFKA_CLUSTER}"-cluster-ca-cert -n "${KAFKA_NAMESPACE}" --to=./asmo-cluster-ca --confirm
```

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
Kafka broker pods
ZooKeeper pods if applicable
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
