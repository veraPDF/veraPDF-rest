#!/usr/bin/env bats
BATS_TEST_TIMEOUT=120  # sec

setup() {
    PROJECT_ROOT="$(cd "$(dirname "$BATS_TEST_FILENAME")/../../.." >/dev/null 2>&1 && pwd)"
    load "$PROJECT_ROOT/tools/test_helper/common-setup.bash"
    _common_setup

    prepare_fresh_verapdf_config_files

}

teardown() {
    echo -e "\nDone ..." >&3
}

@test "-e VERAPDF_MAX_FILE_SIZE, Limiting PDF file size, VERAPDF_MAX_FILE_SIZE=1 MB" {

    run curl -F "file=@$PROJECT_ROOT/Resources/PDFBOX_allowed_file_size.pdf" localhost:8080/api/validate/3a 

    echo "$output" >&3
    assert_output --partial '"code":400'
    assert_output --partial '"Maximum allowed file size exceeded: 1 MB"}'
    
    [ "$status" -eq 0 ]
}
