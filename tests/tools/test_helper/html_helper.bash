#!/usr/bin/env bash
local LAST_BUILD="Nothing"
local LAST_BUILD_PAGE="Nothing"

print_html_tags-values() {
    cat $1 | while xmlgetnext; do echo $TAG $VALUE; done
}

xmlgetnext() {
    local IFS='>'
    read -d '<' TAG VALUE
}

get_last_plugins_build() {
    get_last_build_page_on_jenkins $1
    LAST_BUILD=$(print_html_tags-values index.html | grep 'aria-current="page"')

    #Checking version
    currentIFS=$IFS
    IFS=' '
    read -r -a array <<<"$LAST_BUILD"
    BULID_VERSION="${array[3]}"
    IFS=$currentIFS

    echo "$BULID_VERSION"
}

get_last_build_page_on_jenkins() {
    #store the index.html file in the current directory
    wget https://jenkins.openpreservation.org/job/veraPDF/job/$1/job/plugins/lastBuild/
}

get_last_plugins_version(){

    echo "FOLDER: $1" >&3
    BUILD_VERSION=$(curl https://jenkins.openpreservation.org/job/veraPDF/job/$1/job/plugins/lastBuild/ | grep -o 'class="jenkins-icon-adjacent">Build [0-9].[0-9][0-9].[0-9]*' | grep -o [0-9].[0-9][0-9].[0-9]*)
    echo "BUILD_VERSION: $BUILD_VERSION" >&3
    echo "$BUILD_VERSION"
}

