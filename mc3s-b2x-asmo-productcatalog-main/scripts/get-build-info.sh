#!/bin/bash
mkdir target

PROJECT_VERSION=$(date +%Y.%m)
# PROJECT_VERSION=$(grep 'version=' project.properties | cut -d '=' -f2)
echo "Build Version Number = ${PROJECT_VERSION}.${BITBUCKET_BUILD_NUMBER}"
echo "BUILD_VERSION=${PROJECT_VERSION}.${BITBUCKET_BUILD_NUMBER}" > target/build.properties
