#!/bin/bash

# Dave Grove
# Robert M. Fuhrer

# Following are the platforms for which we need to build a platform-specific tarball
# for use with the X10DT. These tarballs are only used for "local" builds, i.e., on
# the same host as the one running the X10DT. So we only need such tarballs for
# platforms that the X10DT supports.
# For more info on what machines can be used to build for each platform, see
#  http://xj.watson.ibm.com/twiki/bin/view/Main/XtenTestMachines

# Mac OS/x86 + x86_64:
# This host needs to have Xcode and must be configured so that the default g++ is
# the right version (currently, 4.2 or higher). For now, it should be a 32-bit
# Leopard host, so that the build works on both 32- and 64-bit Macs running
# Leopard (10.5) or Snow Leopard (10.6).
macX86Host=condor

# Linux/x86_64:
# This host needs to have g++ version 4.2 or higher.
linuxX86_64Host=triloka3

# Linux/x86 (32-bit):
# This host needs to have g++ version 4.2 or higher.
linuxX86Host="bellatrix"

# Cygwin/x86:
# This host needs to have cygwin 1.7 (for gcc to work right).
cygwinHost=nashira

# We don't presently support Linux/Power as a development host for the X10DT
# Linux/Power:
linuxPowerHost=""

# We don't presently support AIX as a development host for the X10DT
# AIX/power:
aixPowerHost=""

hosts="$macX86Host $linuxX86_64Host $linuxX86Host $cygwinHost $linuxPowerHost $aixPowerHost"
platforms="macosx linux-x86 linux-x86_64 cygwin"

noClean=""
userID="$USER"

usageMsg="Usage: $0 [--no-clean] [--platforms platformList] [--hosts hostList] [--user remoteUserID] [--show-hosts] [--show-platforms] [--no-transfer]"

while [ $# != 0 ]; do
  case $1 in
    --hosts)
        hosts=$2
	shift
        ;;

    --user)
        userID=$2
	shift
        ;;

    --platforms)
        platforms="$2"
        hosts=""
        for platform in $platforms; do
            case $platform in
                macosx)
                    host="$macX86Host"
                    ;;
                linux-x86)
                    host="$linuxX86Host"
                    ;;
                linux-x86_64)
                    host="$linuxX86_64Host"
                    ;;
                cygwin)
                    host="$cygwinHost"
                    ;;
                *)
                    echo "No such platform: $platform"
                    exit 1
                    ;;
            esac
            hosts="$hosts $host"
        done
        shift
        ;;

    --show-platforms)
        echo "$platforms"
        exit 0
        ;;

    --show-hosts)
        echo "Host list: $hosts"
        echo "Mac OS x86 host:   $macX86Host"
        echo "Linux x86 host:    $linuxX86Host"
        echo "Linux x86_64 host: $linuxX86_64Host"
        echo "Cygwin host:       $cygwinHost"
        echo "Linux/Power:       unsupported"
        echo "AIX/Power:         unsupported"
        exit 0
        ;;

    --no-clean)
        noClean="-noclean"
        ;;

    --no-transfer)
        noTransfer="true"
        ;;

    -h)
        echo "$usageMsg"
        exit 0
        ;;

    -*)
        echo "$usageMsg"
        exit 1
        ;;
    esac
    shift
done

branchName="x10-tools-integration"
tibURL="http://x10.svn.sourceforge.net/svnroot/x10/branches/${branchName}/"
tarballDest=/var/www/localhost/htdocs/x10dt/x10-builds
remoteTmpDir=/tmp/x10-tib-$userID

#############################################################################
# Make sure we can get to all the remote hosts without a password challenge #
#############################################################################
echo "Testing prompt-free host access via ssh..."
hostsWithErrors=""
for host in $hosts orquesta
do
    echo -n "  Testing $host... "
    ssh -o 'BatchMode=yes' ${userID}@${host} "echo ok"
    if [[ $? != 0 ]]; then
        hostsWithErrors="$hostsWithErrors $host"
    fi
done

if [[ ! -z "$hostsWithErrors" ]]; then
    echo "Errors when attempting to log onto$hostsWithErrors; aborting."
    exit 1
fi

###########################################
# Determine the right SVN revision to use #
###########################################
echo "Determining current revision of ${branchName}..."

rev=$(svn info $tibURL | grep 'Last Changed Rev' | sed -E 's/Last Changed Rev: ([0-9]+)/\1/')

if [[ -z "$rev" ]]; then
    echo "Error: Unable to determine revision from 'svn info' on URL $tibURL"
    exit 1
fi

echo "Using revision $rev"
echo "Building on hosts: $hosts"
echo "Using user ID $userID"

########################
# Now do the real work #
########################

ssh ${userID}@orquesta.watson.ibm.com "mkdir -p $tarballDest/$rev; chmod go+rx $tarballDest/$rev"

if [[ $? != 0 ]]; then
    echo "Unable to create destination directory on orquesta for platform tarballs; aborting."
    exit 1
fi

for host in $hosts
do
    logFile=/tmp/x10-tib-$host.log
    echo "=================================================="
    echo "=== Launching buildToolIntegration.sh on $host ==="
    echo "=== Log is in ${logFile} ==="
    echo "=================================================="
    (
    scp buildToolIntegration.sh ${userID}@$host:/tmp 
    ssh ${userID}@$host "bash -l -c 'cd /tmp; ./buildToolIntegration.sh ${noClean} -dir $remoteTmpDir -rev $rev'"
    rc=$?
    ssh ${userID}@$host "(cd /tmp; rm ./buildToolIntegration.sh)"
    if [[ $rc == 0 && -z "$noTransfer" ]]; then
        echo "Transferring file from $host to localhost..."
        scp "${userID}@$host:$remoteTmpDir/x10/x10.dist/x10-tib*.tgz" .

        echo "Transferring from localhost to orquesta..."
        scp x10-tib_*.tgz ${userID}@orquesta.watson.ibm.com:$tarballDest/$rev

	echo "Transfer complete."

	echo -n "Setting tarball permissions..."
	ssh ${userID}@orquesta.watson.ibm.com "chmod go+r $tarballDest/$rev/*.tgz"
	echo "done."

        rm x10-tib_*.tgz
        #ssh $host rm -rf $remoteTmpDir
    fi ) > ${logFile} 2>&1 &
done
wait

ssh ${userID}@orquesta.watson.ibm.com ls -l $tarballDest/$rev
