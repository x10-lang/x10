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

UNAME=`uname -smp | sed -e 's/ /,/g'`
case "$UNAME" in
  CYGWIN*,i*86,*) X10_PLATFORM='cygwin_x86';;
  Linux,*86_64*,*) 
      SHORT_HOSTNAME=`hostname -s`
      if [[ "$SHORT_HOSTNAME" == "triloka4" ]]; then 
          X10_PLATFORM='linux_rh6_x86_64'
      else
          X10_PLATFORM='linux_x86_64'
      fi
      ;;
  Linux,*86*,*) X10_PLATFORM='linux_x86';;
  Linux,ppc*,*) X10_PLATFORM='linux_ppc';;
  AIX,*,powerpc) X10_PLATFORM='aix_ppc';;
  Darwin,*,i*86) X10_PLATFORM='macosx_x86'
      export USE_32BIT=true
      export USE_64BIT=true
   ;;
    
  *) echo "Unrecognized platform: '$UNAME'"; exit 1;;
esac

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
	x10.doc \
	x10.runtime \
	x10.tests \
	x10.wala
do
    svn $svn_command https://x10.svn.sourceforge.net/svnroot/x10/tags/$X10_TAG/$i
done
)

echo "The distribution is now exported to the directory $workdir"

if [[ -z "$SKIP_X10_BUILD" ]]; then
    echo "Building distribution"
    cd $distdir/x10.dist
    ant -Doptimize=true -Dtar.version=$X10_VERSION testtar
    ant -Doptimize=true -Dtar.version=$X10_VERSION srctar
    ant dist -Doptimize=true
    ant xrx-xdoc
    $distdir/x10.dist/releng/packageCPPRelease.sh -version $X10_VERSION -platform $X10_PLATFORM
    echo "Platform specific distribuiton tarball created"
fi
