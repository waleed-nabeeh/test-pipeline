#!/bin/bash

rawmodule=$(head -n 1 README.md)
module=${rawmodule//[# ]/}

rawversion=($(grep "version" project.properties) )
version=${rawversion//[version=]/}

echo "Start documentation"
echo "module: ${module}"
echo "Version: ${version}"

aggregator=${module//*-}

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
# insert scale at line 2 to print all classes.
# cat docs/uml/schemas.plantuml | sed '2i \
# scale 0.5' | docker run --rm -i dstockhammer/plantuml:1.2023.8 -pipe > ./docs/images/schemas.png
cat docs/uml/schemas.plantuml | sed '2i \
   scale 0.5' | docker run --rm -i dstockhammer/plantuml:1.2023.8 -pipe > ./docs/images/schemas.png
cat docs/force/schemas.plantuml | sed '2i \
   scale 0.5' | docker run --rm -i dstockhammer/plantuml:1.2023.8 -pipe > ./docs/images/force.png
cat mc3s-b2x-akeneo-base/docs/uml/schemas.plantuml | sed '2i \
  scale 0.5' | docker run --rm -i dstockhammer/plantuml:1.2023.8 -pipe > ./docs/images/akeneo.png

set PLANTUML_LIMIT_SIZE=8192
cat mc3s-b2x-akeneo-base/docs/uml/schemas.plantuml | sed '2i \
  scale 0.5' | docker run --rm -i dstockhammer/plantuml:1.2023.8 -pipe > mc3s-b2x-akeneo-base/docs/images/akeneo.png
cat mc3s-b2x-salesforce-base/docs/uml/schemas.plantuml | sed '2i \
   scale 0.5' | docker run --rm -i dstockhammer/plantuml:1.2023.8 -pipe > ./mc3s-b2x-salesforce-base/docs/images/datamodel.png
cat mc3s-b2x-sync-algolia/docs/uml/schemas.plantuml | sed '2i \
   scale 0.5' |docker run --rm -i dstockhammer/plantuml:1.2023.8 -pipe > ./mc3s-b2x-sync-algolia/docs/images/datamodel.png
