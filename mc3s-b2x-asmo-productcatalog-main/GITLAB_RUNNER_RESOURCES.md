# GitLab Runner Resources

The exported `gitlab runner config` file is generated and owned by the
GitLab Runner Operator. Do not edit it directly because the Operator can
overwrite the changes.

## Apply the job resource configuration

Run these commands from a terminal logged in to the OpenShift cluster:

```bash
oc project gitlab-system

oc apply -f gitlab-runner-job-resources.yaml

oc patch runner gitlab-runner --type merge \
  -p '{"spec":{"concurrent":2,"config":"gitlab-runner-job-resources"}}'
```

The configured CI job pod resources are:

| Container | CPU request | CPU limit | Memory request | Memory limit |
|---|---:|---:|---:|---:|
| Maven build | 1 CPU | 4 CPU | 2 GiB | 6 GiB |
| GitLab helper | 250m | 1 CPU | 512 MiB | 1 GiB |
| Service | 250m | 1 CPU | 256 MiB | 1 GiB |

The build container also receives a 5 GiB ephemeral-storage request and a
15 GiB limit. This is important for Maven dependencies, compiled classes,
and container image layers.

`concurrent: 2` is intentional. The previous generated configuration showed
`concurrent = 10`. Ten simultaneous Maven builds can exhaust the OCP node and
make every job slower.

## Verify

Wait for the Operator to restart/reconfigure the runner:

```bash
oc get runner gitlab-runner
oc get pods -w
```

Start a GitLab pipeline, then find its job pod:

```bash
oc get pods
oc describe pod <JOB-POD-NAME> | sed -n '/Requests:/,/Conditions:/p'
```

The job pod should show the CPU, memory, and ephemeral-storage values from
`gitlab-runner-job-resources.yaml`.

Check that the Runner resource references the custom configuration:

```bash
oc get runner gitlab-runner \
  -o jsonpath='{.spec.concurrent}{"\n"}{.spec.config}{"\n"}'
```

Expected output:

```text
2
gitlab-runner-job-resources
```

## Important distinction

These settings increase resources for CI job pods. They do not increase the
GitLab artifact upload-size limit. The previous `413 Request Entity Too Large`
must be handled through GitLab/ingress upload settings, or avoided by pushing
the container image directly to Quay instead of uploading the large JAR as a
GitLab artifact.
