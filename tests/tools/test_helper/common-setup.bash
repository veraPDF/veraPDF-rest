#!/usr/bin/env bash

_common_setup() {
    PROJECT_ROOT="$(cd "$(dirname "$BATS_TEST_FILENAME")/../../.." >/dev/null 2>&1 && pwd)"
    PATH="$PROJECT_ROOT/tools/test_helper/:$PATH"

    load "$PROJECT_ROOT/bats-support/load.bash"
    load "$PROJECT_ROOT/bats-assert/load.bash"

    export DOCKER_CONTAINER=$(echo $(docker ps | grep "rest:" | awk '{print $1}'))
    echo -e "\nDOCKER_CONTAINER_ID: $DOCKER_CONTAINER \n" >&3
}

remove_verapdf_config_files() {
    echo -e "Removing files ... " >&3
    echo -e $(docker exec -it --user=root $DOCKER_CONTAINER ls -la /opt/verapdf-rest/config/) >&3

    docker exec --user=root $DOCKER_CONTAINER rm -rf /opt/verapdf-rest/config/features.xml
    docker exec --user=root $DOCKER_CONTAINER rm -rf /opt/verapdf-rest/config/fixer.xml
    docker exec --user=root $DOCKER_CONTAINER rm -rf /opt/verapdf-rest/config/plugins.xml
    docker exec --user=root $DOCKER_CONTAINER rm -rf /opt/verapdf-rest/config/validator.xml
    docker exec --user=root $DOCKER_CONTAINER rm -rf /opt/verapdf-rest/config/app.xml
    
    echo -e "Removing files ... Done\n" >&3

    echo -e "Files in the container before the test ... " >&3
    echo -e $(docker exec -it --user=root $DOCKER_CONTAINER ls -la /opt/verapdf-rest/config/) >&3
    echo -e "\n" >&3
}
