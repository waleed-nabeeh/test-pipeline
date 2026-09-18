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

## Optional: Use ASMO Non-Prod Certificate For External Kafka Routes

Use this only after testing OIC with the operator-generated Kafka CA. The working Kafka CA and client configuration remain valid for rollback. No `KafkaUser` change is needed.

The ASMO non-prod certificate must include SAN entries for the Kafka bootstrap route and all broker routes:

```text
asmo-dev-kafka-kafka-bootstrap-asmo-kafka-dev.apps.asmonpeclr.np.asmo.com
asmo-dev-kafka-dual-role-0-asmo-kafka-dev.apps.asmonpeclr.np.asmo.com
asmo-dev-kafka-dual-role-1-asmo-kafka-dev.apps.asmonpeclr.np.asmo.com
asmo-dev-kafka-dual-role-2-asmo-kafka-dev.apps.asmonpeclr.np.asmo.com
```

### Use the existing OpenShift ingress certificate

The `apps-tls` Secret in `openshift-ingress` contains `tls.crt` and `tls.key`. Check its wildcard SAN and how many certificates `tls.crt` contains:

```bash
oc get secret apps-tls -n openshift-ingress -o jsonpath='{.data.tls\.crt}' |
  base64 -d | openssl x509 -noout -subject -issuer -dates -text |
  grep -A1 'Subject Alternative Name'

oc get secret apps-tls -n openshift-ingress -o jsonpath='{.data.tls\.crt}' |
  base64 -d | grep -c 'BEGIN CERTIFICATE'
```

The SAN must cover all four route hostnames above. A count of one means `tls.crt` contains only the server certificate, as observed in `openshift-ingress/apps-tls`. In that case, OIC must have both the ASMO intermediate and root CA available in its truststore; do not assume importing a multi-certificate bundle under one alias imported both. Do not copy the source Secret's YAML into Git or edit the Secret in `openshift-ingress`. Kafka Routes use TLS passthrough; the router does not present this Secret to Kafka clients unless the Kafka external listener is configured to use a copy.

Run this from `docs/asmo/oc-apply` with `oc` and `jq` installed and logged into the correct OpenShift cluster. The helper copies the Secret into `asmo-kafka-dev`, checks its expiry, wildcard SAN, key format, and matching key pair, then patches only the external listener's certificate reference. It warns if the source contains only the server certificate:

```bash
oc get kafka asmo-dev-kafka -n asmo-kafka-dev -o yaml > ~/kafka-before-asmo-cert.yaml
bash scripts/switch-external-certificate.sh apply
```

Do not apply `manifests/05-kafka-external-asmo-cert-patch.yaml` to an existing cluster for this test; it is a full Kafka CR and could replace newer live settings. The operator rolls the brokers when the certificate reference changes. Check reconciliation and broker health:

```bash
oc get kafka asmo-dev-kafka -n asmo-kafka-dev -o json |
  jq '{generation:.metadata.generation,
       observedGeneration:.status.observedGeneration,
       ready:[.status.conditions[]? | select(.type=="Ready") | {status,reason,message}]}'
oc get pods -n asmo-kafka-dev
```

Wait until `observedGeneration` equals `generation`, `Ready` is `True`, and all broker pods are running. A successful `oc patch` alone does not prove the brokers are serving the new certificate.

Compare the copied Secret's public certificate fingerprint with what the bootstrap and every broker route serves:

```bash
check_external_certificate() (
  set -euo pipefail
  expected=$(oc get secret asmo-kafka-external-cert -n asmo-kafka-dev \
    -o jsonpath='{.data.tls\.crt}' | base64 -d |
    openssl x509 -noout -fingerprint -sha256)

  for host in \
    asmo-dev-kafka-kafka-bootstrap-asmo-kafka-dev.apps.asmonpeclr.np.asmo.com \
    asmo-dev-kafka-dual-role-0-asmo-kafka-dev.apps.asmonpeclr.np.asmo.com \
    asmo-dev-kafka-dual-role-1-asmo-kafka-dev.apps.asmonpeclr.np.asmo.com \
    asmo-dev-kafka-dual-role-2-asmo-kafka-dev.apps.asmonpeclr.np.asmo.com; do
    served=$(openssl s_client -connect "$host:443" -servername "$host" \
      </dev/null 2>/dev/null | openssl x509 -noout -fingerprint -sha256)
    printf '%s: %s\n' "$host" "$served"
    [[ $served == "$expected" ]] || {
      printf 'Certificate fingerprint does not match the copied Secret.\n' >&2
      return 1
    }
  done
)
check_external_certificate
```

All four fingerprints must match. To see the subject and issuer for any route, run `openssl s_client -connect "$host:443" -servername "$host" </dev/null 2>/dev/null | openssl x509 -noout -subject -issuer -dates`; the expected issuer is `ASMO-SUBCA-NP`. For OIC, use the same bootstrap URL and SCRAM settings, but trust the ASMO non-prod CA chain or the exact wildcard leaf certificate instead of the Kafka-generated CA. OIC still needs network access to bootstrap and all three broker routes.

### Small truststore test from the ASMO PFX

This section is only for preparing a separate JKS for OIC on the machine that has the PFX. **Do not run it on the OpenShift node to deploy the prebuilt test image.**

The public server certificate inside `emarketpfx.pfx` has the same SHA-256 fingerprint as the certificate currently served by all four Kafka routes. For a short test, import only that public certificate into a new JKS truststore. Run this in Bash on a machine with the PFX, OpenSSL, and `keytool` installed; replace the PFX path. OpenSSL prompts for the PFX password. Do not put either password or the PFX in Git.

```bash
set -o pipefail
umask 077
mkdir -p ~/asmo-kafka-cert-test
cd ~/asmo-kafka-cert-test
pfx=/path/to/emarketpfx.pfx

openssl pkcs12 -in "$pfx" -clcerts -nokeys |
  openssl x509 -out asmo-kafka-server.crt
openssl x509 -in asmo-kafka-server.crt -noout -subject -issuer -fingerprint -sha256

read -r -s -p 'New test JKS password (at least 6 letters/digits only): ' TRUSTSTORE_PASSWORD; printf '\n'
while ! [[ "$TRUSTSTORE_PASSWORD" =~ ^[[:alnum:]]{6,}$ ]]; do
  read -r -s -p 'Use at least 6 letters/digits; try again: ' TRUSTSTORE_PASSWORD; printf '\n'
done
export TRUSTSTORE_PASSWORD
printf '%s' "$TRUSTSTORE_PASSWORD" > truststore.password
keytool -importcert -storetype JKS -keystore asmo-kafka-test-truststore.jks \
  -storepass:env TRUSTSTORE_PASSWORD -alias asmo-kafka-server \
  -file asmo-kafka-server.crt -noprompt
keytool -list -v -storetype JKS -keystore asmo-kafka-test-truststore.jks \
  -storepass:env TRUSTSTORE_PASSWORD
unset TRUSTSTORE_PASSWORD
```

Check that the JKS entry is a `trustedCertEntry` and its SHA-256 fingerprint matches the public certificate above. From a machine that can reach Kafka, compare it with the live bootstrap route:

```bash
host=asmo-dev-kafka-kafka-bootstrap-asmo-kafka-dev.apps.asmonpeclr.np.asmo.com
openssl s_client -connect "$host:443" -servername "$host" </dev/null 2>/dev/null |
  openssl x509 -noout -fingerprint -sha256
```

Upload **only** `asmo-kafka-test-truststore.jks` to OIC as its TrustStore and enter the new truststore password. Keep the existing bootstrap URL, `SASL SCRAM Over SSL`, `SCRAM-SHA-512`, and Kafka username/password. No Kafka CR, KafkaUser, listener, or client keystore change is needed for this test. This JKS pins the current leaf certificate: renewals require rebuilding it. If reverting the Kafka listener, switch OIC back to its previous Kafka-generated CA truststore.

### Image-based Kafka client test through the external route

The published test image is `docker.io/waleednabeeh/asmo-kafka-smoke:2026-09-18` (`linux/amd64`). It contains the Kafka tools, [test script](scripts/test-external-kafka-client.sh), and a JKS built from the **public leaf certificate** in `emarketpfx.pfx`. The image also contains that JKS's test password, so treat it as a disposable test artifact, not a production credential. It does **not** contain the PFX, private key, or Kafka SCRAM password. The pod mounts the existing `asmo-app-client` Secret for SCRAM authentication. Compare the public certificate fingerprint with the live routes before testing.

**Deploy only; do not run `podman`, prepare a JKS, or copy `build-assets` on the OpenShift node.** Those steps were already completed when publishing the image. The pod requires only the existing `asmo-app-client` Secret. If the Docker Hub repository is private, arrange an image pull Secret before applying the pod. On the OpenShift machine, pull this repo branch, change to `docs/asmo/oc-apply`, and run:

```bash
oc get secret asmo-app-client -n asmo-kafka-dev
oc delete pod kafka-external-cert-smoke -n asmo-kafka-dev --ignore-not-found
oc apply -f manifests/kafka-external-cert-smoke.yaml
oc wait pod/kafka-external-cert-smoke -n asmo-kafka-dev --for=condition=Ready --timeout=5m
oc exec -n asmo-kafka-dev kafka-external-cert-smoke -- \
  bash /opt/kafka/test-external-kafka-client.sh metadata
oc exec -n asmo-kafka-dev kafka-external-cert-smoke -- \
  bash /opt/kafka/test-external-kafka-client.sh consume
```

The metadata command must show `asmo.events.dev`. The consumer succeeds only when it reads one record; record contents are suppressed. If the topic is empty, the optional `produce` command appends one test record. **Only run it if an application processing that record is acceptable.**

```bash
oc exec -n asmo-kafka-dev kafka-external-cert-smoke -- \
  bash /opt/kafka/test-external-kafka-client.sh produce
oc exec -n asmo-kafka-dev kafka-external-cert-smoke -- \
  bash /opt/kafka/test-external-kafka-client.sh consume
oc delete pod kafka-external-cert-smoke -n asmo-kafka-dev
```

This tests TLS, SCRAM, metadata, and consumption via the external Kafka routes from an OpenShift pod. It does **not** prove that the OIC gateway can reach those routes. No KafkaUser or Kafka listener change is needed.

For the OIC handoff, upload the separate `asmo-kafka-test-truststore.jks` created above, use the password saved in `truststore.password`, and configure SCRAM username `asmo-app-client`. The Kafka SCRAM password remains in the existing OpenShift Secret; retrieve it privately when configuring OIC:

```bash
oc get secret asmo-app-client -n asmo-kafka-dev -o jsonpath='{.data.password}' | base64 -d
```

Do not use the PFX password as either the JKS or SCRAM password. Do not paste the SCRAM password into tickets or commit it to Git.

### Revert to the Kafka-generated certificate

Remove only the custom external listener certificate reference:

```bash
bash scripts/switch-external-certificate.sh revert
```

Wait for `observedGeneration` to match `generation` and `Ready` to return to `True`, then repeat the `openssl s_client` issuer check on bootstrap and each broker route. The issuer should again be the Kafka-generated cluster CA. Restore the Kafka CA truststore in OIC and confirm the existing Offset Explorer connection. The copied `asmo-kafka-external-cert` Secret can remain unused for a later test.

While the ASMO certificate is active, use these OIC settings:

```text
Connection URL: asmo-dev-kafka-kafka-bootstrap-asmo-kafka-dev.apps.asmonpeclr.np.asmo.com:443
Security policy: SASL SCRAM Over SSL
SASL Mechanism: SCRAM-SHA-512
Username: asmo-app-client
Password: SCRAM password
TrustStore: ASMO non-prod CA chain, or the test JKS above that trusts the exact served leaf
Keystore: not required
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
