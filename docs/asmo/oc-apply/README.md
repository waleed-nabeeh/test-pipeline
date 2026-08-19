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

Wait for topic and user readiness:

```bash
oc wait kafkatopic/asmo-events-dev -n asmo-kafka-dev --for=condition=Ready --timeout=10m
oc wait kafkauser/asmo-app-client -n asmo-kafka-dev --for=condition=Ready --timeout=10m
oc get kafkatopic -n asmo-kafka-dev
oc get kafkauser -n asmo-kafka-dev
```

Validate:

```bash
bash scripts/validate-kafka.sh
```

Extract SCRAM credentials and Kafka CA:

```bash
bash scripts/extract-scram-client-credentials.sh
```

## Update Existing User Permissions For New Topic Prefixes

Use this section only when the existing `asmo-app-client` user must create or manage additional topics from a client tool.

The base deployment gives access to the original topic:

```text
asmo.events.dev
```

If the client must create more topics under both ASMO and canary naming, apply this update:

```bash
oc apply -f - <<'EOF'
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
          name: asmo.
          patternType: prefix
        operations:
          - Create
          - Describe
          - Read
          - Write
          - DescribeConfigs
          - AlterConfigs
        host: "*"
      - resource:
          type: topic
          name: canary.
          patternType: prefix
        operations:
          - Create
          - Describe
          - Read
          - Write
          - DescribeConfigs
          - AlterConfigs
        host: "*"
      - resource:
          type: group
          name: asmo-app
          patternType: prefix
        operations:
          - Read
          - Describe
        host: "*"
EOF
```

Wait for the user update:

```bash
oc wait kafkauser/asmo-app-client -n asmo-kafka-dev --for=condition=Ready --timeout=10m
oc get kafkauser asmo-app-client -n asmo-kafka-dev -o yaml
```

Allowed topic examples:

```text
asmo.events.dev
asmo.orders.v1
canary.events.v1
canary.test.topic
```

Not allowed:

```text
payments.events.v1
test.topic
```

## Producer/Consumer Test

Use this test to prove the Kafka cluster, topic, SCRAM authentication, TLS, and ACL authorization are working.

This method creates a temporary Kafka client pod inside `asmo-kafka-dev` and mounts the existing OpenShift secrets:

```text
asmo-dev-kafka-cluster-ca-cert
asmo-app-client
```

### Create Temporary Kafka Client Pod

```bash
oc apply -n asmo-kafka-dev -f - <<'EOF'
apiVersion: v1
kind: Pod
metadata:
  name: kafka-client
  namespace: asmo-kafka-dev
spec:
  restartPolicy: Never
  containers:
    - name: kafka-client
      image: registry.redhat.io/amq-streams/kafka-42-rhel9:3.2.0
      command:
        - sleep
        - "3600"
      volumeMounts:
        - name: cluster-ca
          mountPath: /opt/kafka/cluster-ca
          readOnly: true
        - name: user-secret
          mountPath: /opt/kafka/user
          readOnly: true
  volumes:
    - name: cluster-ca
      secret:
        secretName: asmo-dev-kafka-cluster-ca-cert
    - name: user-secret
      secret:
        secretName: asmo-app-client
EOF
```

Wait for the client pod:

```bash
oc wait pod/kafka-client -n asmo-kafka-dev --for=condition=Ready --timeout=5m
```

Create the Kafka client properties inside the pod:

```bash
oc exec -n asmo-kafka-dev kafka-client -- bash -c 'cat > /tmp/client.properties <<EOF
security.protocol=SASL_SSL
sasl.mechanism=SCRAM-SHA-512
sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username="asmo-app-client" password="$(cat /opt/kafka/user/password)";
ssl.truststore.location=/opt/kafka/cluster-ca/ca.p12
ssl.truststore.password=$(cat /opt/kafka/cluster-ca/ca.password)
ssl.truststore.type=PKCS12
EOF'
```

### List Topics

```bash
oc exec -n asmo-kafka-dev kafka-client -- \
  /opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server asmo-dev-kafka-kafka-bootstrap.asmo-kafka-dev.svc:9093 \
  --command-config /tmp/client.properties \
  --list
```

Expected output:

```text
asmo.events.dev
```

### Produce Test Message

```bash
echo "asmo kafka test $(date)" | oc exec -i -n asmo-kafka-dev kafka-client -- \
  /opt/kafka/bin/kafka-console-producer.sh \
  --bootstrap-server asmo-dev-kafka-kafka-bootstrap.asmo-kafka-dev.svc:9093 \
  --command-config /tmp/client.properties \
  --topic asmo.events.dev
```

### Consume Test Message

```bash
oc exec -n asmo-kafka-dev kafka-client -- \
  /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server asmo-dev-kafka-kafka-bootstrap.asmo-kafka-dev.svc:9093 \
  --command-config /tmp/client.properties \
  --topic asmo.events.dev \
  --group asmo-app-test \
  --from-beginning \
  --timeout-ms 15000
```

Expected result:

```text
The message produced in the previous step appears in the consumer output.
```

If the consumer prints a `TimeoutException` after showing the message, the test is still successful. The timeout happens because `--timeout-ms 15000` tells the console consumer to stop after 15 seconds when there are no additional messages.

Successful test indicators:

```text
Topic list shows: asmo.events.dev
Producer command completes without authentication or authorization errors.
Consumer output shows the produced message.
Consumer summary shows: Processed a total of 1 messages
```

### Cleanup Temporary Client Pod

```bash
oc delete pod kafka-client -n asmo-kafka-dev
```

## External Client Connection Values

Use these values for external applications after network/DNS/firewall access is confirmed:

```properties
bootstrap.servers=asmo-dev-kafka-kafka-bootstrap-asmo-kafka-dev.apps.asmonpeclr.np.asmo.com:443
security.protocol=SASL_SSL
sasl.mechanism=SCRAM-SHA-512
sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username="asmo-app-client" password="<password-from-secret>";
ssl.truststore.location=<path-to-ca.p12>
ssl.truststore.password=<value-from-ca.password>
ssl.truststore.type=PKCS12
```

## Important Notes

- Do not install into `gitlab-system` unless explicitly approved.
- Do not store generated secrets, decoded passwords, private keys, or extracted certs in Git.
- The external Kafka bootstrap hostname is generated after the Kafka cluster route listener is ready.
- OpenShift route-based Kafka external access usually uses port `443`.
