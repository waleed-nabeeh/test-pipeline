#!/bin/bash

# Run Gitleaks with source directory.
gitleaks detect --source . --no-git --report-path=target/gitleaks-out.json --verbose --redact

if [ ! -f target/gitleaks-out.json ]; then echo "[]" > target/gitleaks-out.json; fi
cat "target/gitleaks-out.json"
