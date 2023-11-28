#!/usr/bin/env bats

setup() {
    PROJECT_ROOT="$(cd "$(dirname "$BATS_TEST_FILENAME")/../../.." >/dev/null 2>&1 && pwd)"
    load "$PROJECT_ROOT/tools/test_helper/common-setup.bash"
    _common_setup

    remove_verapdf_config_files

    sed -i '3 c\    <fixerFolder>'$BATS_TEST_TMPDIR'</fixerFolder>' $BATS_TEST_DIRNAME/wikiPath/app.xml
    docker cp $BATS_TEST_DIRNAME/wikiPath/app.xml $DOCKER_CONTAINER:/opt/verapdf-rest/config/app.xml

}

teardown() {
    docker cp $DOCKER_CONTAINER:/opt/verapdf-rest/config/app.xml $BATS_TEST_TMPDIR
    cat $BATS_TEST_TMPDIR/app.xml >&3 
    echo -e "Done ..." >&3
}

@test "--wikiPath, Defines the base URL used to create reference links in the HTML report, wikiPath=https://baseURL.com/wiki/" {
 
    run curl -F "file=@$PROJECT_ROOT/Resources/Mustang_505.pdf" localhost:8080/api/validate/2b -H "Accept:text/html"
    assert_output --partial "https://baseURL.com/wiki/"

    [ "$status" -eq 0 ]
}
