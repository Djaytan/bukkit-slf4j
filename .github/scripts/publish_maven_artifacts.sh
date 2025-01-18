#!/usr/bin/env bash
#
# Publish Maven artifacts

set -Eeuo pipefail
trap 'echo "Error encountered while executing the script." >&2' ERR

SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" &>/dev/null && pwd -P); readonly SCRIPT_DIR
ROOT_REPOSITORY_DIR="$(realpath "${SCRIPT_DIR}/../..")"; readonly ROOT_REPOSITORY_DIR

mvnw="${ROOT_REPOSITORY_DIR}/mvnw"

readonly NEW_VERSION="$1"
readonly DEV_VERSION='0.0.1-DEV-SNAPSHOT'

main() {
  echo "Publishing version ${NEW_VERSION}..."

  cd "${ROOT_REPOSITORY_DIR}"

  $mvnw versions:set -DnewVersion="${NEW_VERSION}"
  $mvnw deploy -Prelease

  echo "The new version ${NEW_VERSION} has been published successfully!"

  echo 'Putting back the DEV version...'
  $mvnw versions:set -DnewVersion="${DEV_VERSION}"
}

main
