#!/usr/bin/env bats
BATS_TEST_TIMEOUT=120 # sec

setup() {
    PROJECT_ROOT="$(cd "$(dirname "$BATS_TEST_FILENAME")/../../.." >/dev/null 2>&1 && pwd)"
    load "$PROJECT_ROOT/tools/test_helper/common-setup.bash"
    _common_setup

    prepare_fresh_verapdf_config_files

}

teardown() {
    echo -e "\nDone ..." >&3
}

@test "-v, Mounted local files in the Docker container check, bind /tmp:/home/Res_tmp" {

    docker cp  $BATS_TEST_DIRNAME/steps_in_docker.sh $DOCKER_CONTAINER:/home/steps_in_docker.sh
    cp -f $PROJECT_ROOT/Resources/a.pdf /tmp/a.pdf

    run docker exec -i --user root $DOCKER_CONTAINER  /home/steps_in_docker.sh

    echo "$output" >&3

    run cat /tmp/res_tmp_log.log

    echo "$output" >&3

    assert_output --partial "<report>"
    assert_output --partial "<name>a.pdf</name>"
    assert_output --partial 'profileName="PDF/A-3A validation profile"'

}
