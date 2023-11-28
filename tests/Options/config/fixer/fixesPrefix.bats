#!/usr/bin/env bats

setup() {
    PROJECT_ROOT="$(cd "$(dirname "$BATS_TEST_FILENAME")/../../.." >/dev/null 2>&1 && pwd)"
    load "$PROJECT_ROOT/tools/test_helper/common-setup.bash"
    _common_setup

    remove_verapdf_config_files

    sed -i '3 c\    <fixerFolder>'/tmp/123'</fixerFolder>' $BATS_TEST_DIRNAME/fixesPrefix/app.xml
    docker cp $BATS_TEST_DIRNAME/fixesPrefix/app.xml $DOCKER_CONTAINER:/opt/verapdf-rest/config/app.xml
    docker cp $BATS_TEST_DIRNAME/fixesPrefix/fixer.xml $DOCKER_CONTAINER:/opt/verapdf-rest/config/fixer.xml

}

teardown() {
    docker cp $DOCKER_CONTAINER:/opt/verapdf-rest/config/fixer.xml $BATS_TEST_TMPDIR
    docker cp $DOCKER_CONTAINER:/opt/verapdf-rest/config/app.xml $BATS_TEST_TMPDIR
    cat $BATS_TEST_TMPDIR/fixer.xml >&3 
    echo -e "\n" >&3 
    cat $BATS_TEST_TMPDIR/app.xml >&3 
    echo files in the $BATS_TEST_TMPDIR  folder: $(ls $BATS_TEST_TMPDIR)  >&3
    echo -e "Done ..." >&3
}

@test "--fixesPrefix, Save rapared files with prefix, fixesPrefix=veraFixMd_25_" {
    skip "Looks like it isn't ready to go yet"

    run curl -F "file=@$PROJECT_ROOT/Resources/veraPDF_MF3.pdf" localhost:8080/api/validate/1b 
    assert_output --partial 'metadataRepairReport status="Repair successful"'

    [ "$status" -eq 1 ] # check if the file exist ?
}
