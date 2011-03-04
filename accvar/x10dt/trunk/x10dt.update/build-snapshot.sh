#!/bin/bash

function usage() {
    echo "Usage: $(basename $0) <eclipseDirectory> <archiveFile>"
    echo ""
    echo "where:"
    echo "  <eclipseDirectory> is the path to an Eclipse install containing SAFARI"
    echo "  <archiveFile>      is an archive file to be created by this script"
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
	elif [[ -z "$eclipseDir" ]]; then
	    eclipseDir="$1"
        elif [[ -z "$archiveFile" ]]; then
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
    eclipseDir=""
    archiveFile=""
    extraFeatures="lpg.runtime polyglot"
    featureNames="  "
    # TODO read feature manifests to determine set of plugins
    pluginNames="polyglot x10.runtime x10.compiler x10dt.core x10dt.ui"
}

checkSettings() {
    echo "Eclipse directory is $eclipseDir"
    echo "Archiving to         $archiveFile"
    if [[ -z "${eclipseDir}" || ! -r ${eclipseDir}/eclipse.ini ]]; then
        echo "Error: need an eclipse installation directory"
        usage
        exit 1
    fi
    if [[ -z "$archiveFile" ]]; then
        usage
        exit 1
    fi
}

collectInfo() {
    echo "Collecting features to snapshot..."
    for featureName in ${featureNames} ${extraFeatures}; do
        newestFeature=$(ls -1td features/${featureName}* | head -1)
	featureList="${featureList} ${newestFeature}"
    done
    echo "Collecting plugins to snapshot..."
    for pluginName in ${pluginNames}; do
        newestPlugin=$(ls -1td plugins/${pluginName}* | head -1)
	pluginList="${pluginList} ${newestPlugin}"
    done
}

buildSnapshot() {
    cd ${eclipseDir}
    collectInfo
    echo "Building snapshot..."
    tar czf ${archiveFile} ${featureList} ${pluginList}
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
