#!/bin/bash

# exit if anything goes wrong
set -e

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

    -dir)
        workdir=$2
	shift
    ;;

    -nobuild)
        export SKIP_X10_BUILD=1
    ;;

    -nodebug)
	  SKIP_DEBUG_BUILD=1
    ;;

    -rpm)
	  BUILD_RPM=1
    ;;

    -gml)
	  BUILD_GML=1
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

UNAME=`uname -smp | sed -e 's/ /,/g'`
case "$UNAME" in
  CYGWIN*,i*86,*) 
      X10_PLATFORM='cygwin_x86'
      SKIP_DEBUG_BUILD=1
      ;;
  Linux,*86_64*,*) 
      SHORT_HOSTNAME=`hostname -s`
      if [[ "$SHORT_HOSTNAME" == "triloka1" ]]; then 
          EXTRA_X10RT_BUILD_ARG="-DX10RT_PAMI=true"
      fi
      X10_PLATFORM='linux_x86_64'
      ;;
  Linux,*86*,*) X10_PLATFORM='linux_x86';;
  Linux,ppc64*,*) X10_PLATFORM='linux_ppc64'
      SHORT_HOSTNAME=`hostname -s`
      if [[ "$SHORT_HOSTNAME" == "loginf1c3" || "$SHORT_HOSTNAME" == "loginf1c9" ]]; then 
          EXTRA_X10RT_BUILD_ARG="-DX10RT_PAMI=true -DX10RT_PAMI_IS_DEFAULT=true"
          export USE_XLC=1
      fi
      if [[ "$SHORT_HOSTNAME" == "bgqfen1" ]]; then 
          EXTRA_X10RT_BUILD_ARG="-DCROSS_COMPILE_BGQ=true"
	  X10_PLATFORM='linux_bgq'
	  SKIP_DEBUG_BUILD=1
      fi
      ;;
  Darwin,*,i*86) 
      X10_PLATFORM='macosx_x86'
      export USE_32BIT=true
      export USE_64BIT=true
      SKIP_DEBUG_BUILD=1
      ;;
    
  *) echo "Unrecognized platform: '$UNAME'"; exit 1;;
esac

date

distdir=$workdir/x10-$X10_VERSION

echo
echo cleaning $workdir
rm -rf $workdir
mkdir -p $workdir
mkdir -p $workdir/x10-$X10_VERSION

(
cd $workdir/x10-$X10_VERSION

echo
echo getting distrib
for i in \
        apgas \
        apgas.examples \
        apgas.impl \
        apgas.tests \
        apgas.cpp \
        apgas.cpp.examples \
	x10.common \
	x10.compiler \
	x10.constraints \
	x10.dist \
	x10.doc \
	x10.gml \
	x10.network \
	x10.runtime \
	x10.tests \
	x10.wala
do
    svn $svn_command -q svn://svn.code.sourceforge.net/p/x10/code/tags/$X10_TAG/$i
done
)

echo "The distribution is now exported to the directory $workdir"

if [[ -z "$SKIP_X10_BUILD" ]]; then
    echo "Building distribution"
    cd $distdir/x10.dist
    ant $EXTRA_X10RT_BUILD_ARG -Doptimize=true -Dx10.version=$X10_VERSION testtar
    ant $EXTRA_X10RT_BUILD_ARG -Doptimize=true -Dx10.version=$X10_VERSION srctar
    ant $EXTRA_X10RT_BUILD_ARG -Doptimize=true -Davailable.procs=4 dist
    if [[ -z "$SKIP_DEBUG_BUILD" ]]; then
        ant $EXTRA_X10RT_BUILD_ARG -Ddebug=true -Davailable.procs=4 dist-cpp
    fi 
    ant xrx-xdoc
    $distdir/x10.dist/releng/packageRelease.sh -version $X10_VERSION -platform $X10_PLATFORM
    echo "Platform specific distribution tarball created"
    if [[ "$BUILD_RPM" == 1 ]]; then
	$distdir/x10.dist/releng/packageRPM.sh -version $X10_VERSION -platform $X10_PLATFORM
	echo "Platform specific distribution rpm created"
    fi
    if [[ "$BUILD_GML" == 1 ]]; then
	cd $distdir/x10.gml && make srctar
	echo "GML source tarball created"
    fi
fi
