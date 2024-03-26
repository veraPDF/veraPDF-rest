#!/usr/bin/env bats

setup() {
    PROJECT_ROOT="$(cd "$(dirname "$BATS_TEST_FILENAME")/../../.." >/dev/null 2>&1 && pwd)"
    load "$PROJECT_ROOT/tools/test_helper/common-setup.bash"
    _common_setup

    prepare_fresh_verapdf_config_files

    docker cp $BATS_TEST_DIRNAME/type/app.xml $DOCKER_CONTAINER:/opt/verapdf-rest/config/app.xml
    docker cp $BATS_TEST_DIRNAME/type/features.xml $DOCKER_CONTAINER:/opt/verapdf-rest/config/features.xml
    docker cp $BATS_TEST_DIRNAME/type/validator.xml $DOCKER_CONTAINER:/opt/verapdf-rest/config/validator.xml

}

teardown() {
    docker cp $DOCKER_CONTAINER:/opt/verapdf-rest/config/app.xml $BATS_TEST_TMPDIR
    docker cp $DOCKER_CONTAINER:/opt/verapdf-rest/config/validator.xml $BATS_TEST_TMPDIR
    docker cp $DOCKER_CONTAINER:/opt/verapdf-rest/config/features.xml $BATS_TEST_TMPDIR

    cat $BATS_TEST_TMPDIR/app.xml >&3
    cat $BATS_TEST_TMPDIR/validator.xml >&3
    cat $BATS_TEST_TMPDIR/features.xml >&3

    echo -e "Done ..." >&3
}

@test "--type, The default processing model for the GUI, type=VALIDATE_EXTRACT" {

    run curl -F "file=@$PROJECT_ROOT/Resources/5-t02-fail-a.pdf" localhost:8080/api/validate/ua1 -H "Accept:text/html"
    assert_output --partial "Information dictionary"
    assert_output --partial "<td>PDF/UA-1 validation profile</td>"

    [ "$status" -eq 0 ]
}
