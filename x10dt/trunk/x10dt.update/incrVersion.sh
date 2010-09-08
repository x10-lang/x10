#!/bin/bash

function usage() {
    echo "Usage: $0 [-h | --help] [--checkOnly] [--features <feature-list>]"
    echo "       [--featureFile <feature-file>] [--exclude <plugin-list>]"
    echo "       [--incrementBy <version-spec>]"
    echo ""
    echo "where:"
    echo "  <feature-list> is a space-separated list of feature ID's to be processed"
    echo "  <feature-file> is a file containing a whitespace-separated list of feature"
    echo "                 ID's to be processed"
    echo "  <plugin-list>  is a space-separated list of plugin ID's to be excluded"
    echo "                 from processing"
    echo "  <version-spec> is a dot-separated list of 0's and 1's, such as \"0.0.1\" that"
    echo "                 specifies which component(s) of the versions to increment"
    echo ""
    echo "If <feature-list> is not specified, the file \"features.list\" is read for the"
    echo "list of features to process."
    echo "If <version-spec> is not specified, all versions are incremented by 0.0.1"
    echo ""
    echo "This tool checks that plugin versions are currently all the same as their"
    echo "owning features, and if not, will not modify any files."
}

getArgs() {
    while [[ $# -gt 0 ]]; do
	if [[ "$1" = "--help" || "$1" = "-h" ]]; then
	    usage
	    exit 0
	elif [[ "$1" = "--features" && $# -ge 2 ]]; then
	    features="$2"
	    shift
	elif [[ "$1" = "--featureFile" && $# -ge 2 ]]; then
	    readFeatures "$2"
	    shift
	elif [[ "$1" = "--checkOnly" ]]; then
	    checkOnly=1
	elif [[ "$1" = "--exclude" && $# -ge 2 ]]; then
	    excludePlugins="$1"
	    shift
	elif [[ "$1" = "--incrementBy" && $# -ge 2 ]]; then
	    processIncr "$2"
	    shift
	elif [[ "$1" = -* ]]; then
	    echo "Invalid option: $1, or missing argument"
	    usage
	    exit 1
	else
	    echo "Invalid argument: $1"
	    usage
	    exit 1
	fi
	shift
    done
}

processIncr() {
    local incrementBy="$1"
    # For now, just assumes that 'incrementBy' matches [0-9].[0-9].[0-9]
    # Should check this, though...
    incrMajor=$(echo $incrementBy | sed -e 's/\([0-9]\)\.[0-9]\.[0-9]/\1/')
    incrMinor=$(echo $incrementBy | sed -e 's/[0-9]\.\([0-9]\)\.[0-9]/\1/')
    incrRelease=$(echo $incrementBy | sed -e 's/[0-9]\.[0-9]\.\([0-9]\)/\1/')
    echo "Major version increment: $incrMajor"
    echo "Minor version increment: $incrMinor"
    echo "Release version increment: $incrRelease"
}

readFeatures() {
    local featureFile="$1"
    if [[ ! -r "${featureFile}" ]]; then
	echo "Can't find/read feature file ${featureFile}!"
	exit 1
    fi
    features=$(cat ${featureFile})
}

setDefaults() {
    features="org.eclipse.imp.runtime org.eclipse.imp org.eclipse.imp.lpg x10dt"
    excludePlugins="com.ibm.wala.shrike polyglot lpg.runtime.java"
    processIncr "0.0.1"
}

listFeatures() {
    echo "#########"
    echo "Features:"
    echo "#########"
    for feature in ${features}; do echo "  ${feature}"; done
}

checkAll() {
# Pre-flight check w/ no modifications
    for feature in ${features}; do
	echo ""
	echo "######################################"
	echo "Checking ${feature}:"
	echo "######################################"

	featureXML=../${feature}.feature/feature.xml

	# N.B.: Skip over the version number of the xml header
	oldVersion=$(grep 'version=' ${featureXML} | tail +2 | head -1)

	oldVersion=$(echo $oldVersion | sed -e 's/version="\([0-9]\+\.[0-9]\+\.[0-9]\+\)"/\1/')
#       echo "${feature} => ${oldVersion}"

	oldMajor=$(echo $oldVersion | sed -e 's/\([0-9]\+\)\.[0-9]\+\.[0-9]\+/\1/')
	oldMinor=$(echo $oldVersion | sed -e 's/[0-9]\+\.\([0-9]\+\)\.[0-9]\+/\1/')
	oldRelease=$(echo $oldVersion | sed -e 's/[0-9]\+\.[0-9]\+\.\([0-9]\+\)/\1/')

	echo "Old version = ${oldMajor}.${oldMinor}.${oldRelease}"

	newMajor=${oldMajor}
	newMinor=${oldMinor}
	newRelease=${oldRelease}

	[ ${incrMajor} -gt 0 ] && let newMajor=oldMajor+${incrMajor}
	[ ${incrMinor} -gt 0 ] && let newMinor=oldMinor+${incrMinor}
	[ ${incrRelease} -gt 0 ] && let newRelease=oldRelease+${incrRelease}

	newVersion="${newMajor}.${newMinor}.${newRelease}"

	echo "New version = ${newVersion}"

	# Need to check versions of plugins referenced in the feature.xml.
	# N.B.: Filter out any "import" directives (they can have versions too),
	#       and skip both the xml header version and the feature version
	#       (which was obtained above).
	pluginVersions=($(grep 'version=' ${featureXML} | grep -v '<import' | tail +3 | sed -e 's/version="\(.\+\)"\(\/>\)\?/\1/'))

	# Discard the first "ID"; it will be the feature itself
	plugins=($(sed -e 's/id="\(.\+\)"/\1/p
1,$d' < ${featureXML} | tail +2))

	echo Feature ${feature} contains plugins:
	for plugin in ${plugins[*]}; do echo "   ${plugin}"; done
	echo ""

	errors=""

	local N=${#plugins[*]}
	for((i=0; i < ${N}; i++)); do
	    plugin=${plugins[$i]}
	    pluginVersion=${pluginVersions[$i]}

#	    echo ${plugin}
#	    echo ${pluginVersion}

	    # Is this an excluded plugin? If so, skip it
	    found=""
	    for excPlug in ${excludePlugins}; do
		[ ${excPlug} = ${plugin} ] && found=1
	    done
	    if [ -n "${found}" ]; then echo "Skipping plugin ${plugin}"; continue; fi
	
	    echo Checking plugin ${plugin} version ${pluginVersion} ...

	    pluginRelease=$(echo $pluginVersion | sed -e 's/[0-9]\+\.[0-9]\+\.\([0-9]\+\)/\1/')

	    [ ${pluginRelease} = ${oldRelease} ] || (echo "  Plugin version mismatch in feature.xml: expected $oldRelease"; errors="1")

	    manifest=../${plugin}/META-INF/MANIFEST.MF

	    manifestVersion=$(sed -e 's/Bundle-Version: \(.\+\)/\1/p
1,$d' ${manifest})
	    manifestRelease=$(echo $manifestVersion | sed -e 's/[0-9]\+\.[0-9]\+\.\([0-9]\+\)/\1/')
	    [ ${manifestRelease} = ${oldRelease} ] || (echo "  Plugin version mismatch in manifest: expected $oldRelease"; errors="1")
	done

#       echo "Checking version of feature ${feature} in site.xml..."
#       siteXML=site.xml
#       featureVersion=$(grep "id=\"${feature}\"" ${siteXML} | grep "version=\"${oldVersion}\"")
#       echo ${featureVersion}
#       [ -n "${featureVersion}" ] || (echo "Feature version mismatch in feature.xml"; errors="1")
    done

    [ -n "${errors}" ] && (echo "Checking failed; aborting."; exit 1)
}

incrAll() {
    [ -w site.xml ] || chmod u+w site.xml

    for feature in ${features}; do
	echo ""
	echo "######################"
	echo "Processing ${feature}:"
	echo "######################"

	featureXML=../${feature}.feature/feature.xml

	oldVersion=$(grep 'version=' ${featureXML} | tail +2 | head -1)
	oldVersion=$(echo $oldVersion | sed -e 's/version="\([0-9]\+\.[0-9]\+\.[0-9]\+\)"/\1/')

	oldMajor=$(echo $oldVersion | sed -e 's/\([0-9]\+\)\.[0-9]\+\.[0-9]\+/\1/')
	oldMinor=$(echo $oldVersion | sed -e 's/[0-9]\+\.\([0-9]\+\)\.[0-9]\+/\1/')
	oldRelease=$(echo $oldVersion | sed -e 's/[0-9]\+\.[0-9]\+\.\([0-9]\+\)/\1/')

	newMajor=${oldMajor}
	newMinor=${oldMinor}
	newRelease=${oldRelease}

	[ ${incrMajor} -gt 0 ] && let newMajor=oldMajor+${incrMajor}
	[ ${incrMinor} -gt 0 ] && let newMinor=oldMinor+${incrMinor}
	[ ${incrRelease} -gt 0 ] && let newRelease=oldRelease+${incrRelease}

	newVersion="${newMajor}.${newMinor}.${newRelease}"

        # Bump version number in feature.xml
        # N.B. Need to bump versions of plugins referenced in the feature.xml too.
        # This actually makes the task easier, since all matching "version" attributes
        # must be rewritten.
	sed -i -e "s/version=\"${oldVersion}\"/version=\"${newVersion}\"/" ${featureXML}

        # Discard the first "ID"; it will be the feature itself
	plugins=$(sed -e 's/id="\(.\+\)"/\1/p
1,$d' < ${featureXML} | tail +2)

	echo Feature ${feature} contains plugins:
	for plugin in ${plugins}; do echo "   ${plugin}"; done

	for plugin in ${plugins}; do
	    found=""
	    for excPlug in ${excludePlugins}; do
		[ ${excPlug} = ${plugin} ] && found=1
	    done
	    if [ -n "${found}" ]; then continue; fi
	    echo "  Incrementing version of plugin ${plugin}..."

	    manifest=../${plugin}/META-INF/MANIFEST.MF

	    oldPluginVersion=$(grep 'Bundle-Version: ' ${manifest})
	    oldPluginVersion=$(echo $oldPluginVersion | sed -e 's/Bundle-Version: \([0-9]\+\.[0-9]\+\.[0-9]\+\)/\1/')

	    oldPluginMajor=$(echo $oldPluginVersion | sed -e 's/\([0-9]\+\)\.[0-9]\+\.[0-9]\+/\1/')
	    oldPluginMinor=$(echo $oldPluginVersion | sed -e 's/[0-9]\+\.\([0-9]\+\)\.[0-9]\+/\1/')
	    oldPluginRelease=$(echo $oldPluginVersion | sed -e 's/[0-9]\+\.[0-9]\+\.\([0-9]\+\)/\1/')

	    newPluginMajor=${oldPluginMajor}
	    newPluginMinor=${oldPluginMinor}
	    newPluginRelease=${oldPluginRelease}

	    [ ${incrMajor} -gt 0 ] && let newPluginMajor=oldPluginMajor+${incrMajor}
	    [ ${incrMinor} -gt 0 ] && let newPluginMinor=oldPluginMinor+${incrMinor}
	    [ ${incrRelease} -gt 0 ] && let newPluginRelease=oldPluginRelease+${incrRelease}

	    newPluginVersion="${newPluginMajor}.${newPluginMinor}.${newPluginRelease}"

            # Bump version number in manifest
	    sed -i -e "s/Bundle-Version: \(.\+\)/Bundle-Version: ${newPluginVersion}/" ${manifest}
	done

	echo "Adding new feature version to site.xml..."
	echo "   <feature url=\"features/${feature}_${newVersion}.jar\" id=\"${feature}\" version=\"${newVersion}\">" >> site.xml
	echo "      <category name=\"Language Meta-Tools\"/>" >> site.xml
	echo "   </feature>" >> site.xml
    done

    # Now remove the old closing "</site>" (which is now in the middle of the file) and add a new one at the end.
    sed -i -e 's/<\/site>//' site.xml
    echo "</site>" >> site.xml
}

main() {
    setDefaults
    getArgs "$@"
    checkAll

    echo ""
    echo '######################'
    echo "All checks succeeded."
    echo '######################'

    [ -n "${checkOnly}" ] && exit 0

    echo '#################################################'
    echo "Proceeding to increment feature/plugin versions."
    echo '#################################################'

    incrAll

    echo ""
    echo "Done with all features."
}

main "$@"
exit 0
