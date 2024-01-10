#!/usr/bin/env bats
BATS_TEST_TIMEOUT=120  # sec

setup() {
    PROJECT_ROOT="$(cd "$(dirname "$BATS_TEST_FILENAME")/../../.." >/dev/null 2>&1 && pwd)"
    load "$PROJECT_ROOT/tools/test_helper/common-setup.bash"
    _common_setup

    prepare_fresh_verapdf_config_files

    docker cp $BATS_TEST_DIRNAME/debug/validator.xml $DOCKER_CONTAINER:/opt/verapdf-rest/config/validator.xml

}

teardown() {
    docker cp $DOCKER_CONTAINER:/opt/verapdf-rest/config/validator.xml $BATS_TEST_TMPDIR
    echo -e "\n" >&3
    cat $BATS_TEST_TMPDIR/validator.xml >&3

    echo -e "\nDone ..." >&3
}

@test "--debug, Outputs all processed file names, debug=true" {

    run curl -F "file=@$PROJECT_ROOT/Resources/a_for_debug.pdf" localhost:8080/api/validate/3a -H "Accept:text/html"
    [ "$status" -eq 0 ]

    sleep 3 # sleep sec
    run docker logs $DOCKER_CONTAINER -t --since=$test_logs_starting_time
    
    sleep 3 # sleep sec
    echo "$output" >&3
    assert_output --partial "INFO: a_for_debug.pdf"
}
