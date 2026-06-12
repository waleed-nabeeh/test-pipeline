#!/bin/bash
( cd ~ && rm -rf .m2 && tar xf "${BITBUCKET_CLONE_DIR}/m2.tar" )
# Run Maven build with OWASP Dependency-Check plugin
echo "Running Maven build with Dependency-Check..."
./mvnw clean install -B -Pci,!install-git-hooks org.owasp:dependency-check-maven:aggregate -DskipTests -DfailBuildOnCVSS=8

# Check if the Maven command succeeded or failed
if [ $? -ne 0 ]; then
  echo "OWASP Dependency Check found vulnerabilities with CVSS >= 8, build failed!"
  exit 1
else
  echo "OWASP Dependency Check completed successfully, no critical vulnerabilities found."
fi