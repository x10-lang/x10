#! /bin/bash

# Igor Peshansky

# Note: assumes everything is built
# FIXME: check out afresh

while [ $# != 0 ]; do

  case $1 in
    -version)
	export X10_VERSION=$2
	shift
    ;;

    -platform)
	export PLATFORM=$2
	shift
    ;;
   esac
   shift
done

if [[ -z "$X10_VERSION" ]]; then
    echo "usage: $0 must give X10 version as -version <version>"
    exit 1
fi

if [[ -z "$PLATFORM" ]]; then
    echo "usage: $0 must give target platform as -platform <platform>"
    exit 1
fi

tarfile="x10-$X10_VERSION""_$PLATFORM.tgz"

mydir="`dirname "$0"`"
top="`cd "$mydir"/.. && pwd`"
cdir="`pwd`"
[ "$cdir" = "/" ] && cdir="$cdir."
cd "$top"
tar -cvzf "$cdir/$tarfile" INSTALL.txt README.txt RELEASE.NOTES.txt bin/{runx10,setupX10,x10,x10c,x10c++,mpirunx10,launcher,manager,daemon} epl-v10.html etc include lib/{lib*,*.jar,*.dll} samples/*.x10

