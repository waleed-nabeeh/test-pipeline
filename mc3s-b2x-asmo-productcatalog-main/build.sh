#!/bin/bash

rawmodule=$(head -n 1 README.md)
module=${rawmodule//[# ]/}

rawversion=($(grep "version" project.properties) )
version=${rawversion//[version=]/}

echo "Start Build and documentation"
echo "module: ${module}"
echo "Version: ${version}"


aggregator=${module//*-}
# source ../tokens.properties
# echo "Token (swagger) =  '$swagger'"

# echo "downloading contract for '${aggregator}'"
# curl -H "Authorization: Bearer $swagger" -o mc3s-app-aggregator-${aggregator}/src/main/resources/openapi.json "https://${aggregator}.dev.b2x.b2bx.mindcurv.io/v3/api-docs/mc3s-b2x-aggregators"

mvncommands=("list" "tree")
echo "generating reports ..."
for mvncommand in ${mvncommands[@]}; do
  echo "${mvncommand}"
  ./mvnw dependency:${mvncommand} > "docs/reports/mvn_dependency_${mvncommand}.txt"
done
echo ""
echo ""
echo "available reports ..."
for report in docs/reports/*; do
  echo ${report}
done

echo "generate datamodel.png"
cat docs/uml/schemas.plantuml | docker run --rm -i dstockhammer/plantuml:1.2023.8 -pipe > ./docs/images/schemas.png