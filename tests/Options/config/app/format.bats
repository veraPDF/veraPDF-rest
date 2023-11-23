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

    docker cp $BATS_TEST_DIRNAME/format/app.xml $DOCKER_CONTAINER:/opt/verapdf-rest/config/app.xml

}

teardown() {
    docker cp $DOCKER_CONTAINER:/opt/verapdf-rest/config/app.xml $BATS_TEST_TMPDIR
    cat $BATS_TEST_TMPDIR/app.xml >&3 #out to console to see options set after test >&3

    echo -e "Done ..." >&3
}

@test "--format, Chooses the default reporting format, format=JSON" {
    skip "Looks like it isn't ready to go yet"
 
    run curl -F "file=@$PROJECT_ROOT/Resources/Mustang_505.pdf" localhost:8080/api/validate/1b  
    assert_output --partial "ICC profiles"

    [ "$status" -eq 0 ]
}
