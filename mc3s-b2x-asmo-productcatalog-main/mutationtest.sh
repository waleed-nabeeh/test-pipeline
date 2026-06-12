#!/bin/bash

echo "Step 1: mutation test execution"
rm -rf docs/pit
./mvnw clean test-compile org.pitest:pitest-maven:mutationCoverage

echo "Step 2: license documentation"
./mvnw org.codehaus.mojo:license-maven-plugin:2.4.0:aggregate-download-licenses --define license.writeExcelFile=true

echo "Step 3: copy license documentation to docs/reports"
cp -pr target/generated-resources/licenses.* docs/reports
