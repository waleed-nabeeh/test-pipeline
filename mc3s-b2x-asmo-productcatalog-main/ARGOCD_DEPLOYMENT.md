# Argo CD deployment on OpenShift

The OpenShift deployment is rendered from:

```text
k8s/overlays/ocp
```

It uses the Nexus image:

```text
nexus.apps.asmonpeclr.np.asmo.com/marketplace/mc3s-b2x-asmo-productcatalog:ocp
```

## Namespace prerequisites

Create the Nexus image pull secret in the application namespace:

```bash
oc create secret docker-registry nexus-registry-secret \
  --docker-server=nexus.apps.asmonpeclr.np.asmo.com \
  --docker-username=admin \
  --docker-password='<NEXUS_PASSWORD>' \
  -n <APPLICATION_NAMESPACE>
```

Create the CommerceTools Secret from a local properties file. Start with the
committed example:

```bash
cp k8s/overlays/ocp/ct-secret.properties.example \
  k8s/overlays/ocp/ct-secret.properties
```

Fill the required application properties:

```properties
projectKey=<COMMERCETOOLS_PROJECT_KEY>
clientId=<COMMERCETOOLS_CLIENT_ID>
clientSecret=<COMMERCETOOLS_CLIENT_SECRET>
```

Optional properties are documented in the example file. Create or update the
OCP Secret:

```bash
oc create secret generic ct-secret \
  --from-env-file=k8s/overlays/ocp/ct-secret.properties \
  -n <APPLICATION_NAMESPACE> \
  --dry-run=client -o yaml | oc apply -f -
```

The real `ct-secret.properties` file is ignored by Git. Do not commit real
credentials in Kustomize or Argo CD manifests.

## Validate Kustomize

```bash
oc kustomize k8s/overlays/ocp
```

## Configure Argo CD

Edit `argocd/productcatalog-ocp.yaml` and replace:

```text
REPLACE_WITH_GITLAB_REPOSITORY_URL
REPLACE_WITH_APPLICATION_NAMESPACE
```

Then apply it:

```bash
oc apply -f argocd/productcatalog-ocp.yaml
```

If the repository is private, register it in OpenShift GitOps/Argo CD before
creating the Application.

## Image updates

The pipeline pushes both an immutable commit tag and the moving `ocp` tag.
This overlay currently deploys the `ocp` tag with `imagePullPolicy: Always`.

Argo CD does not automatically restart a healthy Deployment when the same
moving tag receives a new image. For fully automated GitOps, use one of:

1. Argo CD Image Updater to write the latest image tag into this overlay.
2. A pipeline job that updates `newTag` to `$CI_COMMIT_SHORT_SHA` and commits
   that Git change.

Using the immutable commit SHA in Git is the recommended production approach.
