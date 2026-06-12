#!/bin/bash

rawmodule=$(head -n 1 README.md)
module=${rawmodule//[# ]/}

rawversion=($(grep "version" project.properties) )
version=${rawversion//[version=]/}

mvncommands=("list" "tree")
echo "generating reports ..."
for mvncommand in ${mvncommands[@]}; do
  echo "${mvncommand}"
  ./mvnw dependency:${mvncommand} > "docs/reports/mvn_dependency_${mvncommand}.txt"
done
