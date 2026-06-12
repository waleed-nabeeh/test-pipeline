#!/bin/sh
# requires https://bitbucket.org/blog/cloning-another-bitbucket-repository-in-bitbucket-pipelines

REPO_SLUG='b2x-pipelines'
BB_AUTH_STRING="${BITBUCKET_USER}:${PIPELINE_EXECUTION_APP_PWD}"

curl -X POST -is -u "${BB_AUTH_STRING}" \
  -H 'Content-Type: application/json' \
  https://api.bitbucket.org/2.0/repositories/${BITBUCKET_REPO_OWNER}/${REPO_SLUG}/pipelines/ \
  -d "
  {
    \"target\": {
      \"ref_type\": \"branch\",
      \"type\": \"pipeline_ref_target\",
      \"ref_name\": \"master\",
      \"selector\": {
        \"type\": \"custom\",
        \"pattern\": \"run-renovate\"
      }
    },
    \"variables\": [
      {
        \"key\": \"repositories\",
        \"value\": \"${BITBUCKET_REPO_SLUG}\"
      }
    ]
  }"