#!/bin/bash

# Stop the script if any command fails
set -e

# Default publishing task
PUBLISH_TASK="publishToMavenLocal"

# Check for the --remote flag
if [ "$1" == "--remote" ]; then
  PUBLISH_TASK="publishAllPublicationsToMavenCentralRepository"
  shift
fi

projects=("io-core" "io" "uri" "kotlinx-io" "okio" "charset" "charset-ext")

for projectName in "${projects[@]}"; do
  echo "Publishing $projectName"
  ./gradlew ":$projectName:$PUBLISH_TASK" -PlibBuildType="$buildType" --quiet --warning-mode=none --no-configuration-cache
done

echo "Publishing completed successfully."
