#!/bin/bash
if [[ ${ENV} != "dev" ]]; then
if [ "$VERSION" = "latest" ]; then VERSION=$(git fetch && git tag -l --sort=-creatordate | head -n 1) ; fi
git fetch --all --tags && git checkout $VERSION -b $VERSION && export BUILD_VERSION=$VERSION ; fi
source set_env.sh
echo "[INFO] Generating manifests with kustomize"
test -f target/build.properties && source target/build.properties
test -f target/build.properties && source target/build.properties
cd k8s/overlays/${ENV}
kustomize edit set image custom_image=${AWS_ACCOUNT_ID}.dkr.ecr.eu-central-1.amazonaws.com/mc3s-b2x-aggregator-${APP_NAME}:$BUILD_VERSION
cd ${BITBUCKET_CLONE_DIR}
mkdir manifests
kustomize build k8s/overlays/${ENV} --output manifests
echo "[INFO] Verifying manifests"
kubeconform -kubernetes-version 1.21.5 -ignore-missing-schemas -summary manifests
echo "[INFO] Pushing manifests to ArgoCD"
git clone https://x-token-auth:$PAT@bitbucket.org/mck8s/mindkube2.0-addons-sync.git
rm -rf mindkube2.0-addons-sync/AWS/B2X/${APP_NAME}/${ENV}/manifests
mkdir -p mindkube2.0-addons-sync/AWS/B2X/${APP_NAME}/${ENV}
cp -r manifests mindkube2.0-addons-sync/AWS/B2X/${APP_NAME}/${ENV}/
export COMMIT_MESSAGE=$(git log -1 --pretty=%B)
cd mindkube2.0-addons-sync
[[ "$(git status --porcelain)" ]] || exit 0
git add -A
git config user.email "b2x-${ENV}@bitbucket.org"
git config user.name "Bitbucket Pipeline"
git commit -m "${COMMIT_MESSAGE}"          
git pull --rebase origin main || {
  echo "Rebase failed. Attempting to abort and retry...";
  git rebase --abort;
  git pull --rebase origin main;
  }
git push || {
  echo "Initial push failed. Retrying after pulling latest changes...";
  git pull --rebase origin main;
  git push;
  }