#!/usr/bin/env bats

setup() {
    PROJECT_ROOT="$(cd "$(dirname "$BATS_TEST_FILENAME")/../../.." >/dev/null 2>&1 && pwd)"
    load "$PROJECT_ROOT/tools/test_helper/common-setup.bash"
    _common_setup

    prepare_fresh_verapdf_config_files

    docker cp $BATS_TEST_DIRNAME/flavour/validator.xml $DOCKER_CONTAINER:/opt/verapdf-rest/config/validator.xml

}

teardown() {
    docker cp $DOCKER_CONTAINER:/opt/verapdf-rest/config/validator.xml $BATS_TEST_TMPDIR
    cat $BATS_TEST_TMPDIR/validator.xml >&3

    echo -e "\nDone ..." >&3
}

@test "--flavour, flavour will be applied based on a curl options, flavour=2b" {

    run curl -F "file=@$PROJECT_ROOT/Resources/a.pdf" localhost:8080/api/validate/2b  -H 'accept: application/json'
    assert_output --partial '"profileName" : "PDF/A-2B validation profile"'

    [ "$status" -eq 0 ]
}
