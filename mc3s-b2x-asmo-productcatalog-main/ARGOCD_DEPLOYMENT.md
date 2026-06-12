# Argo CD deployment on OpenShift

The OpenShift GitOps files are self-contained under:

```text
openshift/
├── base/
├── overlays/dev/
└── argocd/
```

The development deployment is rendered from:

```text
openshift/overlays/dev
```

It currently uses the Nexus image:

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

Kustomize generates `ct-secret` from the committed placeholder file:

```text
openshift/overlays/dev/ct-secret.properties
```

Before deployment, replace these placeholder properties:

```properties
projectKey=<COMMERCETOOLS_PROJECT_KEY>
clientId=<COMMERCETOOLS_CLIENT_ID>
clientSecret=<COMMERCETOOLS_CLIENT_SECRET>
```

Optional properties are documented in that file. Argo CD creates and updates
the generated Secret when the file changes.

The committed values are deliberately invalid placeholders. Storing real
secrets directly in Git is not recommended. Before production, replace this
plain Secret generator with Sealed Secrets, External Secrets, or SOPS.

## Validate Kustomize

```bash
oc kustomize openshift/overlays/dev
```

## Configure Argo CD

Edit `openshift/argocd/application-dev.yaml` and replace:

```text
REPLACE_WITH_GITOPS_REPOSITORY_URL
REPLACE_WITH_DEV_NAMESPACE
```

Then apply it:

```bash
oc apply -f openshift/argocd/application-dev.yaml
```

If the repository is private, register it in OpenShift GitOps/Argo CD before
creating the Application.

## Image updates

The application pipeline currently pushes both an immutable commit tag and
the moving `ocp` tag. The development overlay currently deploys that `ocp`
tag with `imagePullPolicy: Always`.

Argo CD does not automatically restart a healthy Deployment when the same
moving tag receives a new image. For fully automated GitOps, use one of:

1. Argo CD Image Updater to write the latest image tag into this overlay.
2. A pipeline job that updates `newTag` to `$CI_COMMIT_SHORT_SHA` and commits
   that Git change.

Using the immutable commit SHA in Git is the recommended production approach.
