#!/usr/bin/env bash

_common_setup() {
    PROJECT_ROOT="$(cd "$(dirname "$BATS_TEST_FILENAME")/../../.." >/dev/null 2>&1 && pwd)"
    PATH="$PROJECT_ROOT/tools/test_helper/:$PATH"

    load "$PROJECT_ROOT/bats-support/load.bash"
    load "$PROJECT_ROOT/bats-assert/load.bash"

    if [ -d "$HOME/.verapdf/config/" ]; then
        rm -r "$HOME/.verapdf/config/"
    fi

    export DOCKER_CONTAINER=$(echo $(docker ps | grep verapdf/rest:dev | awk '{print $1}'))
    echo -e "\nDOCKER_CONTAINER_ID: $DOCKER_CONTAINER \n" >&3
}

get_verapdf_version() {

    currentIFS=$IFS
    IFS=' '
    veraPDF_info=$($PROJECT_ROOT/verapdf/verapdf --version | grep veraPDF)
    read -r -a array <<<"$veraPDF_info"
    VERAPDF_VERSION="${array[1]}"
    IFS=$currentIFS

    echo "$VERAPDF_VERSION"
}

verapdf_folder() {

    currentIFS=$IFS
    IFS='.'
    folder_info=$(get_verapdf_version)
    read -r -a array <<<"$folder_info"
    FOLDER_VERSION="${array[0]}"."${array[1]}"
    IFS=$currentIFS

    echo "$FOLDER_VERSION"
}

get_plugin_version() {
    PLUGIN_NAME=$1

    currentIFS=$IFS
    IFS='-'
    plugin_info=$(cat "$PROJECT_ROOT/verapdf/config/plugins.xml" | grep "$PLUGIN_NAME")
    read -r -a array <<<"$plugin_info"
    version_info="${array[3]}"
    IFS='.'
    read -r -a array <<<"$version_info"
    PLUGIN_VERSION="${array[0]}"."${array[1]}"."${array[2]}"
    IFS=$currentIFS

    echo "$PLUGIN_VERSION"
}
