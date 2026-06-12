#!/bin/bash

if [[ -z "$CLIENT_ID" ]]; then
    echo "To run variable CLIENT_ID must be set." 1>&2
    exit 0
fi

if [[ -z "$CLIENT_SECRET" ]]; then
    echo "To run variable CLIENT_SECRET must be set." 1>&2
    exit 0
fi

if [[ -z "$USER" ]]; then
    echo "To run variable USER must be set." 1>&2
    exit 0
fi
if [[ -z "$PWD" ]]; then
    echo "To run variable PWD must be set." 1>&2
    exit 0
fi


echo ""
echo "Starting Token Retrieval"
echo ""

SECRET_HASH=$(echo -n "${USER}${CLIENT_ID}" | openssl dgst -sha256 -hmac "${CLIENT_SECRET}" -binary | base64)
echo "SECRET_HASH" "${SECRET_HASH}"

echo "Sending authentication request to AWS Cognito"
AUTH_RESPONSE=$(curl -s -X POST "https://cognito-idp.eu-central-1.amazonaws.com/" \
  -H "Content-Type: application/x-amz-json-1.1" \
  -H "X-Amz-Target: AWSCognitoIdentityProviderService.InitiateAuth" \
  --data '{
      "AuthParameters" : {
        "USERNAME" : "'"${USER}"'",
        "PASSWORD" : "'"${PWD}"'",
        "SECRET_HASH": "'"${SECRET_HASH}"'"
      },
      "AuthFlow" : "USER_PASSWORD_AUTH",
      "ClientId" : "'"${CLIENT_ID}"'"

    }')

echo "Extracting Token"
THE_TOKEN=$(echo "$AUTH_RESPONSE" | grep -o '"IdToken":"[^"]*' | sed 's/"IdToken":"//')

if [[ -z "$THE_TOKEN" ]]; then
  echo "Authentication failed: $(echo "$AUTH_RESPONSE" | grep -o '"message":"[^"]*' | sed 's/"message":"//')"
  exit 1
fi

echo ""
echo "Swagger Token Retrieved Successfully"
echo "$THE_TOKEN"

