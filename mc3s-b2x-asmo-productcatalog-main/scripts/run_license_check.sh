#!/bin/bash

# Path to the generated license.xml from the Maven license plugin.
LICENSE_FILE="target/generated-resources/licenses.xml"

# Path to the whitelist_licenses.txt
WHITELIST_FILE="whitelisted-licenses.xml"

# Check if the license.xml file exists
if [[ ! -f "$LICENSE_FILE" ]]; then
    echo "Error: license.xml not found!"
    exit 1
fi

# Check if whitelist.txt exists
if [[ ! -f "$WHITELIST_FILE" ]]; then
    echo "Error: $WHITELIST_FILE not found!"
    exit 1
fi


# Parse the licenses in the license.xml file
licenses_in_xml=$(xmlstarlet sel -t -m "//license/name" -v . -n $LICENSE_FILE)

# Parse the licenses in the whitelisted-licenses.xml file and normalize each license
whitelisted_licenses_in_xml=$(xmlstarlet sel -t -m "//licenseSummary/license/names/name" -v . -n "$WHITELIST_FILE")

# collect all whitelisted licenses
all_whitelisted_licenses=""
while IFS= read -r license; do
  all_whitelisted_licenses+="$license"$'\n'
done <<< "$whitelisted_licenses_in_xml"

# Initialize flag for invalid licenses
invalid_license_found=0

# Loop through the licenses found in license.xml
while IFS= read -r license; do

  #echo "reading license.xml and found the name : $license";
   if ! echo "$all_whitelisted_licenses" | grep -Fxq "$license"; then
      echo "Error: License '${license}' is not in the whitelist!"
        invalid_license_found=1
    fi
done <<< "$licenses_in_xml"

# Fail the pipeline if any invalid license is found
if [ $invalid_license_found -eq 1 ]; then
    exit 1
fi

echo "All licenses in license.xml are valid!"