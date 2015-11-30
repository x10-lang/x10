#!/bin/bash

# exit if anything goes wrong
set -e

workdir=/tmp/x10-apps-dist

while [ $# != 0 ]; do

  case $1 in
    -version)
	export X10_VERSION=$2
	shift
    ;;

    -tag)
	export X10_TAG=$2
	shift
    ;;

    -dir)
        workdir=$2
	shift
    ;;
   esac
   shift
done

if [[ -z "$X10_VERSION" ]]; then
    echo "usage: $0 must give X10 version as -version <version>"
    exit 1
fi

if [[ -z "$X10_TAG" ]]; then
    echo "usage: $0 must give X10 tag as -tag <git tag>"
    exit 1
fi

date

distdir=$workdir/x10-applications-$X10_VERSION
repodir=$workdir/x10-apps-git

echo
echo cleaning $workdir
rm -rf $workdir
mkdir -p $workdir || exit 1
mkdir -p $workdir/x10-applications-$X10_VERSION

echo
echo cloning x10-applications git repo
cd $workdir
git clone --depth 1 https://github.com/x10-lang/x10-applications.git $repodir

echo
echo extracting applications from repo
cd $repodir
git archive --format=tar $X10_TAG CoMD lulesh2 MCCK | (cd $distdir && tar xf - )

tarfile="x10-applications-$X10_VERSION"".tar.bz2"
echo "The applications are now exported to the directory $workdir"

cd $workdir
eval tar -cjf "$tarfile" x10-applications-$X10_VERSION

