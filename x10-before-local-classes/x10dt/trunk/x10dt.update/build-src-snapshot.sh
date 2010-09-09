#!/bin/bash

function usage() {
    echo "Usage: $(basename $0)"
}

getArgs() {
    while [[ $# -gt 0 ]]; do
	if [[ "$1" = "--help" || "$1" = "-h" ]]; then
	    usage
	    exit 0
	elif [[ "$1" = -* ]]; then
	    echo "Invalid option: $1, or missing argument"
	    usage
	    exit 1
        elif [[ -z "${archiveFile}" ]]; then
	    archiveFile="$1"
        else
	    echo "Invalid argument: $1"
	    usage
	    exit 1
	fi
	shift
    done
}

setDefaults() {
    archiveFile=""
    tempDir="${HOME}/safariTemp"

    safariHost="orquesta"
    safariRepoPath="/usr/src/cvs/safari"
    safariUser="rfuhrer"

    updateProject="x10dt.update"
    featureProjects="x10dt.feature"
    # TODO read feature manifests to determine set of plugins
    pluginProjects="x10dt.core x10dt.ui"
    safariProjects="$updateProject $featureProjects $pluginProjects"

    # TODO Extract this info from a CVS repo descriptor (e.g. ":ext:user@host:path")
    polyglotHost="gforge.cis.cornell.edu"
    polyglotRepoPath="/cvsroot/polyglot"
    polyglotUser="rmfuhrer"
    polyglotProjects="polyglot"

    x10Host="x10.cvs.sourceforge.net"
    x10RepoPath="/cvsroot/x10"
    x10User="rmfuhrer"
    x10Projects="x10.compiler x10.runtime"

    lpgHost="lpg.cvs.sourceforge.net"
    lpgRepoPath="/cvsroot/lpg"
    lpgUser="rmfuhrer"
    lpgProjects="lpg.runtime.java"

    # These must be put in place manually
    extraProjects="com.ibm.shrike"
}

checkSettings() {
    echo "Archiving to $archiveFile"
    echo "CVS host is $cvsHost"
    echo "CVS repository path is $cvsRepoPath"
    echo "Temporary source directory is $tempDir"

    if [[ -z "$archiveFile" ]]; then
        usage
        exit 1
    fi
    if [[ ! -d "${tempDir}" ]]; then
        mkdir ${tempDir}
    fi
    if [[ ! -d "${tempDir}" ]]; then
	echo "Couldn't find/create temporary source directory ${tempDir}"
	exit 1
    fi

    missing=""
    for extraProject in ${extraProjects}; do
        if [[ ! -d "${tempDir}/$extraProject" ]]; then
	    echo "You must obtain the source for $extraProject and place it in ${tempDir}"
	    missing="1"
	fi
    done
    if [[ ! -z "$missing" ]]; then
        echo "Missing source; aborting"
	exit 1
    fi
}

extractFromCVS() {
    local cvsHost="$1"
    local cvsRepoPath="$2"
    local cvsUser="$3"
    local cvsProjects="$4"
    echo "Extracting source from CVS host ${cvsHost}..."
    cvs -d :ext:${cvsUser}@${cvsHost}:${cvsRepoPath} co ${cvsProjects}
}

buildSnapshot() {
    cd ${tempDir}

    extractFromCVS $polyglotHost $polyglotRepoPath $polyglotUser "$polyglotProjects"
    extractFromCVS $lpgHost      $lpgRepoPath      $lpgUser      "$lpgProjects"
    extractFromCVS $x10Host      $x10RepoPath      $x10User      "$x10Projects"
    extractFromCVS $safariHost   $safariRepoPath   $safariUser   "$safariProjects"

    tar czf ${archiveFile} .
    echo "Done."
}

main() {
    setDefaults
    getArgs "$@"
    checkSettings
    buildSnapshot
}

main "$@"
exit 0
