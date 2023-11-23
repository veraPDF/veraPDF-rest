#!/usr/bin/env bats

setup() {
    PROJECT_ROOT="$(cd "$(dirname "$BATS_TEST_FILENAME")/../../.." >/dev/null 2>&1 && pwd)"
    load "$PROJECT_ROOT/tools/test_helper/common-setup.bash"
    _common_setup

    docker exec --user=root $DOCKER_CONTAINER rm -rf /opt/verapdf-rest/config/features.xml
    docker exec --user=root $DOCKER_CONTAINER rm -rf /opt/verapdf-rest/config/fixer.xml
    docker exec --user=root $DOCKER_CONTAINER rm -rf /opt/verapdf-rest/config/plugins.xml
    docker exec --user=root $DOCKER_CONTAINER rm -rf /opt/verapdf-rest/config/validator.xml
    docker exec --user=root $DOCKER_CONTAINER rm -rf /opt/verapdf-rest/config/app.xml

    docker cp $BATS_TEST_DIRNAME/maxFails/validator* $DOCKER_CONTAINER:/opt/verapdf-rest/config
}

teardown() {
    docker cp $DOCKER_CONTAINER:/opt/verapdf-rest/config/validator.xml $BATS_TEST_TMPDIR
    cat $BATS_TEST_TMPDIR/validator.xml #out to console validator.xml to see options set after test >&3

    echo -e "Done ..." >&3
}

@test "--maxFails, Sets maximum amount of failed checks, maxFails=1" {

    run curl -F "file=@$PROJECT_ROOT/Resources/veraPDFPDFAConformanceCheckerGUI.pdf" localhost:8080/api/validate/1b -H "Accept:application/xml"
    echo "Status: $status" >&3
    assert_output --partial 'failedRules="1"'

    [ "$status" -eq 0 ]
}
