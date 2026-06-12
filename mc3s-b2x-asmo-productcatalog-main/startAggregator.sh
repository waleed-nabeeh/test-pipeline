#!/bin/bash
echo "Start Build"
#./mvnw git-code-format:format-code clean install -U -Dmaven.test.skip=true
echo "Starting aggregator app"
cd mc3s-app-aggregator-productcatalog || exit
set -x
export SecretsKey=mc3s-ct-dev,mc3s-default-dev
export AWS_ACCESS_KEY_ID=ADD_YOUR_AWS_ACCESS_KEY_ID
export AWS_SECRET_ACCESS_KEY=ADD_YOUR_AWS_SECRET_ACCESS_KEY
export AWS_SESSION_TOKEN=ADD_YOUR_AWS_SESSION_TOKEN
export AWS_REGION=eu-central-1

./mvnw spring-boot:run -Dspring-boot.run.profiles=local
