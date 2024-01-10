#!/usr/bin/env bats

setup() {
    PROJECT_ROOT="$(cd "$(dirname "$BATS_TEST_FILENAME")/../../.." >/dev/null 2>&1 && pwd)"
    load "$PROJECT_ROOT/tools/test_helper/common-setup.bash"
    _common_setup

    prepare_fresh_verapdf_config_files

}

teardown() {
    docker cp $DOCKER_CONTAINER:/opt/verapdf-rest/config/validator.xml $BATS_TEST_TMPDIR
    cat $BATS_TEST_TMPDIR/validator.xml >&3

    echo -e "\nDone ..." >&3
}

@test "--maxNumberOfDisplayedFailedChecks, Sets maximum amount of failed checks displayed for each rule, maxNumberOfDisplayedFailedChecks=0" {

    docker cp $BATS_TEST_DIRNAME/maxNumberOfDisplayedFailedChecks/validator_Checks_0.xml $DOCKER_CONTAINER:/opt/verapdf-rest/config/validator.xml

    run curl -F "file=@$PROJECT_ROOT/Resources/a.pdf" localhost:8080/api/validate/1b -H "Accept:text/html"
    refute_output --partial '<b>Rule</b>'

    [ "$status" -eq 0 ]
}

@test "--maxNumberOfDisplayedFailedChecks, Sets maximum amount of failed checks displayed for each rule, maxNumberOfDisplayedFailedChecks=2" {

    docker cp $BATS_TEST_DIRNAME/maxNumberOfDisplayedFailedChecks/validator_Checks_2.xml $DOCKER_CONTAINER:/opt/verapdf-rest/config/validator.xml

    failedChecks=$(curl -F "file=@$PROJECT_ROOT/Resources/5-t02-fail-a.pdf" localhost:8080/api/validate/1b -H "Accept:application/xml" | grep -w -o '(8 0 obj PDContentStream)' | grep -w -o PDContentStream | wc -w)

    echo -e "$failedChecks\n" >&3

    run echo $failedChecks
    assert_equal $failedChecks "2"

    [ "$status" -eq 0 ]
}
