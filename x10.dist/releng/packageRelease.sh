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

#
# PLAT_LIBPAT is a comma-separated list of shell file-globbing patterns that
# identifies the libraries to be included in the tarball from the 'lib' directory.
#
case $PLATFORM in
    cygwin_*)
        PLAT_LIBPAT="{lib*.*,*.dll}"
        ;;
    linux_*)
        PLAT_LIBPAT="lib*.*"
        ;;
    aix_*)
        PLAT_LIBPAT="lib*.*"
        ;;
    macosx_*)
        PLAT_LIBPAT="lib*.*"
        ;;
esac

mydir="`dirname "$0"`"
top="`cd "$mydir"/.. && pwd`"
cdir="`pwd`"
[ "$cdir" = "/" ] && cdir="$cdir."
cd "$top"

eval tar -cvzf "$cdir/$tarfile" --exclude=.svn INSTALL.txt README.txt RELEASE.NOTES.txt bin/{x10,runx10,x10c,x10cj,x10c++,X10Launcher,x10doc} epl-v10.html etc include stdlib lib/*.jar lib/logging.properties lib/${PLAT_LIBPAT} samples
