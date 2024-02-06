#!/usr/bin/env bats

setup() {
    PROJECT_ROOT="$(cd "$(dirname "$BATS_TEST_FILENAME")/../../.." >/dev/null 2>&1 && pwd)"
    load "$PROJECT_ROOT/tools/test_helper/common-setup.bash"
    _common_setup

    prepare_fresh_verapdf_config_files

    docker cp $BATS_TEST_DIRNAME/recordPasses/validator.xml $DOCKER_CONTAINER:/opt/verapdf-rest/config/validator.xml

}

teardown() {
    docker cp $DOCKER_CONTAINER:/opt/verapdf-rest/config/validator.xml $BATS_TEST_TMPDIR
    cat $BATS_TEST_TMPDIR/validator.xml >&3

    echo -e "\nDone ..." >&3
}

@test "--recordPasses, Logs successful validation checks, recordPasses=true" {

    run curl -F "file=@$PROJECT_ROOT/Resources/veraPDFPDFAConformanceCheckerGUI.pdf" localhost:8080/api/validate/1b -H "Accept:text/html"
    assert_output --partial '<b>Passed</b>'

    [ "$status" -eq 0 ]
}
