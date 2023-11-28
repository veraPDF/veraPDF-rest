#!/usr/bin/env bats

setup() {
    PROJECT_ROOT="$(cd "$(dirname "$BATS_TEST_FILENAME")/../../.." >/dev/null 2>&1 && pwd)"
    load "$PROJECT_ROOT/tools/test_helper/common-setup.bash"
    _common_setup

    remove_verapdf_config_files

    docker cp $BATS_TEST_DIRNAME/isVerbose/app.xml $DOCKER_CONTAINER:/opt/verapdf-rest/config/app.xml

}

teardown() {
    docker cp $DOCKER_CONTAINER:/opt/verapdf-rest/config/app.xml $BATS_TEST_TMPDIR
    cat $BATS_TEST_TMPDIR/app.xml >&3 
    echo -e "Done ..." >&3
}

@test "--isVerbose, The verbose output in text report, isVerbose=true" {
    skip "Not supported text output"
    run curl -F "file=@$PROJECT_ROOT/Resources/Mustang_505.pdf" localhost:8080/api/validate/1b 
    assert_output --partial "ICC profiles"

    [ "$status" -eq 0 ]
}
