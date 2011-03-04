#!/bin/bash

# Dave Grove

svn_command=export
workdir=/tmp/x10-distribution

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

    -branch)
        svn_command=co
    ;;

    -platform)
        export X10_PLATFORM=$2
	shift
    ;;

    -dir)
        workdir=$2
	shift
    ;;

    -nobuild)
        export SKIP_X10_BUILD=1
    ;;

   esac
   shift
done

if [[ -z "$X10_VERSION" ]]; then
    echo "usage: $0 must give X10 version as -version <version>"
    exit 1
fi

if [[ -z "$X10_TAG" ]]; then
    echo "usage: $0 must give X10 tag as -tag <svn tag>"
    exit 1
fi

if [[ -z "$X10_PLATFORM" ]]; then
    echo "usage: $0 must give X10 platform as -platform <platform>"
    exit 1
fi

date

distdir=$workdir/x10-$X10_VERSION

echo
echo cleaning $workdir
rm -rf $workdir
mkdir -p $workdir || exit 1
mkdir -p $workdir/x10-$X10_VERSION

(
cd $workdir/x10-$X10_VERSION

echo
echo getting distrib
for i in \
	x10.common \
	x10.compiler \
	x10.constraints \
	x10.dist \
	x10.runtime \
	x10.tests
do
    svn $svn_command https://x10.svn.sourceforge.net/svnroot/x10/tags/$X10_TAG/$i
done
)

echo "The distribution is now exported to the directory $workdir"

if [[ -z "$SKIP_X10_BUILD" ]]; then
    echo "Building distribution"
    cd $distdir/x10.dist
    ant -Doptimize=true -Dzip.version=$X10_VERSION testzip
    ant -Doptimize=true -Dzip.version=$X10_VERSION srczip
    ant -Doptimize=true -Dzip.version=$X10_VERSION zip
    ant dist -Doptimize=true
    $distdir/x10.dist/releng/packageCPPRelease.sh -version $X10_VERSION -platform $X10_PLATFORM
    echo "Platform specific distribuiton tarball created"
fi
