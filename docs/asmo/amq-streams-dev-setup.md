# ASMO DEV AMQ Streams Setup Instructions

## Purpose

This document provides implementation guidance for installing and configuring Red Hat AMQ Streams on the ASMO DEV OpenShift environment.

AMQ Streams provides Apache Kafka on OpenShift using the Red Hat-supported operator model. It is required if ASMO services need event streaming, asynchronous integration, Kafka topics, or external producer/consumer connectivity.

## Requirement Summary

1. Install and configure Red Hat AMQ Streams on the DEV OpenShift environment.
2. Configure the required Kafka brokers, topics, and basic cluster configuration.
3. Configure secure external connectivity so external clients can securely connect to AMQ Streams.
4. Configure the required client authentication and authorization mechanisms.

## Complexity Level

Overall complexity: **Medium to High**

Estimated rating for DEV: **7/10**

| Area | Complexity | Notes |
| --- | --- | --- |
| AMQ Streams operator installation | Medium | Straightforward if OpenShift permissions and OperatorHub access are ready. |
| Kafka broker setup | Medium | Requires decisions on replicas, storage, CPU, memory, and persistence. |
| Topic setup | Low to Medium | Simple technically, but naming, partitions, and retention must be agreed. |
| Secure external connectivity | High | Requires routes or load balancers, DNS, TLS certificates, firewall rules, and testing. |
| Authentication | Medium to High | Requires choosing TLS, SCRAM, or OAuth and distributing client credentials. |
| Authorization / ACLs | High | Requires topic-level permissions for producer and consumer clients. |

For DEV, expected effort is usually **2 to 5 working days**, depending on access, DNS, certificates, firewall approvals, and security requirements.

## Assumptions

Update these values before implementation:

| Item | Example / Placeholder |
| --- | --- |
| OpenShift cluster | `ASMO DEV OpenShift` |
| Kafka namespace | `asmo-kafka-dev` |
| Kafka cluster name | `asmo-dev-kafka` |
| AMQ Streams operator channel | Confirm with platform team |
| Storage class | `<dev-storage-class>` |
| External access type | `route`, `loadbalancer`, or `nodeport` |
| Authentication type | `tls`, `scram-sha-512`, or `oauth` |
| Authorization type | `simple` ACL authorization |
| Bootstrap DNS | `<bootstrap-hostname>` |

## ASMO DEV Values Seen In OpenShift Console

The following values were visible from the ASMO DEV OpenShift console screenshots:

| Item | Value |
| --- | --- |
| Product tile | `Streams for Apache Kafka` |
| Operator channel | `stable` |
| Operator version shown | `3.2.1-8` |
| OpenShift version | `4.20.22` |
| StorageClass | `thin-csi` |
| Storage provisioner | `csi.vsphere.vmware.com` |
| OpenShift apps route domain | `apps.asmonpeclr.np.asmo.com` |
| Recommended Kafka namespace | `asmo-kafka-dev` |

Use `thin-csi` as the storage class unless the platform team provides a different block storage class for Kafka.

## Prerequisites

Before starting, confirm the following:

- OpenShift CLI `oc` is installed and logged in.
- User has permission to create namespaces, operators, CRDs, routes, secrets, and persistent volumes.
- Red Hat AMQ Streams is available from OperatorHub or the approved internal operator catalog.
- Storage class for Kafka persistent volumes is available.
- DNS and network path are available for external Kafka access.
- TLS certificate strategy is agreed with the platform/security team.
- Required Kafka topics, applications, producers, and consumers are known.
- Required authentication and authorization model is agreed.

## Recommended DEV Architecture

For a DEV environment, start with a small but realistic setup:

| Component | Recommended DEV Value |
| --- | --- |
| Kafka brokers | 3 |
| Kafka controllers | 3, using KRaft mode |
| ZooKeeper | Not used for new Streams for Apache Kafka 3.x / Kafka 4.x deployments |
| Kafka node pools | Enabled |
| Entity Operator | Enabled |
| Topic Operator | Enabled |
| User Operator | Enabled |
| Storage | Persistent |
| Internal listener | TLS |
| External listener | Route or load balancer with TLS |
| Authorization | Simple ACLs |

Use 3 Kafka brokers even in DEV if the goal is to test real replication, failover, and client behavior. Use fewer only for temporary proof-of-concept work.

## Step 1: Create Namespace

```bash
oc new-project asmo-kafka-dev
```

If the namespace already exists:

```bash
oc project asmo-kafka-dev
```

## Step 2: Install AMQ Streams Operator

Install Red Hat AMQ Streams from OpenShift OperatorHub into the Kafka namespace or through the approved platform operator process.

Confirm the operator is running:

```bash
oc get pods -n asmo-kafka-dev
oc get csv -n asmo-kafka-dev
```

Expected result:

- AMQ Streams operator pod is running.
- ClusterServiceVersion is in `Succeeded` phase.
- Kafka custom resources are available.

Check Kafka CRDs:

```bash
oc get crd | grep kafka
```

## Step 3: Create Kafka Cluster

Create Kafka cluster manifests using KRaft mode.

New Streams for Apache Kafka 3.x / Kafka 4.x deployments do not use ZooKeeper. They use KRaft mode with `KafkaNodePool` resources.

File example: `kafka-nodepool-dev.yaml`

```yaml
apiVersion: kafka.strimzi.io/v1
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
        class: <dev-storage-class>
        deleteClaim: false
  resources:
    requests:
      memory: 2Gi
      cpu: "1"
    limits:
      memory: 4Gi
      cpu: "2"
```

File example: `kafka-cluster-dev.yaml`

```yaml
apiVersion: kafka.strimzi.io/v1
kind: Kafka
metadata:
  name: asmo-dev-kafka
  namespace: asmo-kafka-dev
  annotations:
    strimzi.io/node-pools: enabled
    strimzi.io/kraft: enabled
spec:
  kafka:
    version: <supported-kafka-version>
    metadataVersion: <supported-metadata-version>
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
      offsets.topic.replication.factor: 3
      transaction.state.log.replication.factor: 3
      transaction.state.log.min.isr: 2
      default.replication.factor: 3
      min.insync.replicas: 2
      num.partitions: 3
  entityOperator:
    topicOperator: {}
    userOperator: {}
```

Apply the manifest:

```bash
oc apply -f kafka-nodepool-dev.yaml
oc apply -f kafka-cluster-dev.yaml
```

Monitor deployment:

```bash
oc get kafka -n asmo-kafka-dev
oc get kafkanodepool -n asmo-kafka-dev
oc get pods -n asmo-kafka-dev
oc describe kafka asmo-dev-kafka -n asmo-kafka-dev
```

Wait until Kafka node pool pods and the Entity Operator are running and ready.

## Step 4: Configure Kafka Topics

Create one `KafkaTopic` per required ASMO topic.

File example: `topic-asmo-events-dev.yaml`

```yaml
apiVersion: kafka.strimzi.io/v1
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
oc get kafkatopic -n asmo-kafka-dev
```

Recommended topic information to collect before final setup:

| Topic Name | Producer | Consumer | Partitions | Retention | Notes |
| --- | --- | --- | --- | --- | --- |
| `<topic-name>` | `<app-name>` | `<app-name>` | `3` | `7 days` | `<purpose>` |

## Step 5: Configure External Connectivity

For OpenShift DEV, `route` is often the easiest external listener type if clients can reach OpenShift routes.

After Kafka is deployed, check generated routes:

```bash
oc get routes -n asmo-kafka-dev
```

Get bootstrap endpoint:

```bash
oc get kafka asmo-dev-kafka -n asmo-kafka-dev -o yaml
```

Look for the external listener bootstrap address in the Kafka status.

If custom hostnames are required, configure route host overrides in the Kafka listener. Example:

```yaml
listeners:
  - name: external
    port: 9094
    type: route
    tls: true
    authentication:
      type: tls
    configuration:
      bootstrap:
        host: <bootstrap-hostname>
      brokers:
        - broker: 0
          host: <broker-0-hostname>
        - broker: 1
          host: <broker-1-hostname>
        - broker: 2
          host: <broker-2-hostname>
```

Confirm with platform/network team:

- DNS records resolve correctly.
- External clients can reach OpenShift router or load balancer.
- Required firewall ports are open.
- TLS certificate chain is trusted by clients.

## Step 6: Configure Client Authentication

### Option A: TLS Client Authentication

Use this when client certificates are acceptable and the platform team can manage certificate distribution.

Example Kafka user:

```yaml
apiVersion: kafka.strimzi.io/v1
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
          - Read
          - Write
          - Describe
      - resource:
          type: group
          name: asmo-app
          patternType: prefix
        operations:
          - Read
```

Apply:

```bash
oc apply -f kafka-user-asmo-app-client.yaml
```

Retrieve generated user secret:

```bash
oc get secret asmo-app-client -n asmo-kafka-dev -o yaml
```

The secret contains certificates and keys required by the client.

### Required Certificates And Credentials

Certificates and credentials are generated after the Kafka cluster and `KafkaUser` are created.

For TLS/mTLS authentication, provide clients with:

```text
Kafka cluster CA certificate
Client certificate
Client private key
Optional PKCS12 keystore and password
```

For SCRAM authentication, provide clients with:

```text
Kafka cluster CA certificate
SCRAM username
SCRAM password
```

Get the Kafka CA certificate:

```bash
mkdir -p ./kafka-certs/cluster-ca
oc extract secret/asmo-dev-kafka-cluster-ca-cert \
  -n asmo-kafka-dev \
  --to=./kafka-certs/cluster-ca \
  --confirm
```

Get TLS client certificate/key:

```bash
mkdir -p ./kafka-certs/asmo-app-client
oc extract secret/asmo-app-client \
  -n asmo-kafka-dev \
  --to=./kafka-certs/asmo-app-client \
  --confirm
```

Get SCRAM password:

```bash
oc get secret asmo-app-client \
  -n asmo-kafka-dev \
  -o jsonpath='{.data.password}' | base64 -d
echo
```

### Option B: SCRAM Authentication

Use this when username/password authentication is preferred.

Kafka user example:

```yaml
apiVersion: kafka.strimzi.io/v1
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
          - Read
          - Write
          - Describe
      - resource:
          type: group
          name: asmo-app
          patternType: prefix
        operations:
          - Read
```

The Kafka listener must also be configured with:

```yaml
authentication:
  type: scram-sha-512
```

## Step 7: Configure Authorization

Use Simple ACL authorization for DEV unless ASMO security architecture requires OAuth or another centralized model.

Recommended ACL model:

| Client | Topic Access | Consumer Group Access |
| --- | --- | --- |
| Producer app | `Write`, `Describe` | Not required |
| Consumer app | `Read`, `Describe` | `Read` on group |
| App with both roles | `Read`, `Write`, `Describe` | `Read` on group |

Avoid broad wildcard ACLs unless required for temporary DEV troubleshooting.

## Step 8: Client Connection Information

Provide each application team with:

- Bootstrap server address.
- Authentication type.
- Client certificate or SCRAM credentials.
- CA certificate.
- Topic names.
- Consumer group naming standard.
- Required client properties.

Example Java client properties for TLS:

```properties
bootstrap.servers=<external-bootstrap-host>:443
security.protocol=SSL
ssl.truststore.location=<path-to-truststore>
ssl.truststore.password=<password>
ssl.keystore.location=<path-to-keystore>
ssl.keystore.password=<password>
ssl.key.password=<password>
```

Example Java client properties for SCRAM over TLS:

```properties
bootstrap.servers=<external-bootstrap-host>:443
security.protocol=SASL_SSL
sasl.mechanism=SCRAM-SHA-512
sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username="<username>" password="<password>";
ssl.truststore.location=<path-to-truststore>
ssl.truststore.password=<password>
```

## Step 9: Validation Tests

Run these checks after deployment.

### Cluster Health

```bash
oc get pods -n asmo-kafka-dev
oc get kafka -n asmo-kafka-dev
oc get kafkanodepool -n asmo-kafka-dev
oc get kafkatopic -n asmo-kafka-dev
oc get kafkauser -n asmo-kafka-dev
```

Expected:

- Kafka resource is ready.
- Kafka node pool pods are ready.
- Entity Operator pods are ready.
- Topics and users are created successfully.

### Internal Producer/Consumer Test

Run a temporary Kafka client pod inside OpenShift and test producing and consuming messages.

```bash
oc run kafka-client -n asmo-kafka-dev -ti --image=registry.redhat.io/amq-streams/kafka-37-rhel9:latest --rm=true --restart=Never -- bash
```

Inside the pod, run producer and consumer tests using the proper bootstrap server and security configuration.

### External Connectivity Test

From an approved external client machine:

```bash
openssl s_client -connect <external-bootstrap-host>:443 -servername <external-bootstrap-host>
```

Expected:

- TLS handshake succeeds.
- Certificate chain is valid.
- Client can produce and consume messages using assigned credentials.

## Step 10: Handover Checklist

Before closing the task, confirm:

- AMQ Streams operator is installed and healthy.
- Kafka cluster is deployed and ready.
- Persistent storage is bound.
- Required topics are created.
- External bootstrap endpoint is available.
- DNS resolves correctly.
- TLS certificates are valid.
- Authentication is enabled.
- Authorization ACLs are configured.
- Test producer and consumer are successful.
- Application teams received connection details.
- Monitoring and logs are available to platform/support team.

## Risks and Dependencies

| Risk / Dependency | Impact |
| --- | --- |
| DNS not ready | External clients cannot connect. |
| Firewall rules missing | External connectivity tests fail. |
| Certificate chain not trusted | TLS clients fail handshake. |
| Incorrect ACLs | Clients authenticate but cannot read/write topics. |
| Storage class issues | Kafka pods may not start or may lose persistence. |
| Low broker resources | Performance and stability issues during testing. |
| Wrong listener/authentication configuration | Clients fail to connect. |

## Recommended Next Decisions

Confirm these before implementation starts:

1. Does ASMO need Kafka only inside OpenShift, or also from external clients?
2. Which external listener type should be used: `route`, `loadbalancer`, or `nodeport`?
3. Which authentication mechanism is required: TLS, SCRAM, or OAuth?
4. What are the required topic names, partitions, and retention periods?
5. Which applications will produce and consume each topic?
6. Who owns DNS, firewall, and certificate setup?

## Final Recommendation

For ASMO DEV, proceed with AMQ Streams installation if ASMO has event-driven integrations or external producer/consumer requirements.

Use this baseline unless the platform/security team requires otherwise:

- 3 Kafka brokers.
- Persistent storage.
- TLS-enabled internal and external listeners.
- Route-based external access for DEV.
- TLS or SCRAM authentication.
- Simple ACL authorization.
- Topic and user management through AMQ Streams custom resources.
