#!/bin/bash

# Dave Grove

workdir=/tmp/x10-tools-int-$USER

# TODO: parse svn info URL | grep Revision | ... to extract revision number automatically.
rev="13749"

while [ $# != 0 ]; do

  case $1 in
    -dir)
        workdir=$2
	shift
    ;;

   esac
   shift
done

UNAME=`uname -smp | sed -e 's/ /,/g'`
case "$UNAME" in
  CYGWIN*,i*86,*) export X10_PLATFORM='cygwin_x86';;
  Linux,*86_64*,*) export X10_PLATFORM='linux_x86_64';;
  Linux,*86*,*) export X10_PLATFORM='linux_x86';;
  Linux,ppc*,*) export X10_PLATFORM='linux_ppc';;
  AIX,*,powerpc) export X10_PLATFORM='aix_ppc';;
  Darwin,*,i*86) export X10_PLATFORM='macosx_x86';;
  *) echo "Unrecognized platform: '$UNAME'"; exit 1;;
esac

distdir=$workdir/x10-$rev

echo
echo cleaning $workdir
rm -rf $workdir
mkdir -p $workdir || exit 1
mkdir -p $workdir/x10-$rev

(
cd $workdir/x10-$rev

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
    svn export https://x10.svn.sourceforge.net/svnroot/x10/branches/x10-tools-integration/$i
done
)

echo "The distribution is now exported to the directory $workdir"

echo "Building distribution"
cd $distdir/x10.dist
ant dist -Doptimize=true
$distdir/x10.dist/releng/packageCPPRelease.sh -version tib_$rev -platform $X10_PLATFORM
echo "Platform specific distribuiton tarball created"

scp x10-tib_$rev_$X10_PLATFROM.tgz orquesta:/var/www/localhost/htdocs/x10dt/x10-builds/$rev
