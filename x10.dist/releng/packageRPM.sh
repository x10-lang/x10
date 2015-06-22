#! /bin/bash

# Benjamin Herta
# Note: assumes that we already have a .tgz of the build in the local directory

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

if [[ -e "${HOME}/rpmbuild" ]]; then
    echo "${HOME}/rpmbuild must not already exist!  Please delete it before running this script"
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
    macosx_*)
        PLAT_LIBPAT="lib*.*"
        ;;
esac

mydir="`dirname "$0"`"
top="`cd "$mydir"/.. && pwd`"
cdir="`pwd`"
[ "$cdir" = "/" ] && cdir="$cdir."
cd "$top"

case "$PLATFORM" in
    *_x86) 
	RPM_PLATFORM="i686"
	;;
    *_x86_64)
	RPM_PLATFORM="x86_64"
	;;
    *_ppc64)
	RPM_PLATFORM="ppc64"
	;;
    *) echo "Unrecognized platform: '$UNAME'"; exit 1;;
esac    
	
bindir="${HOME}/rpmbuild/BUILDROOT/x10-${X10_VERSION}-1.${PLATFORM}/opt/ibm/x10/${X10_VERSION}/"
mkdir -p ${HOME}/rpmbuild/BUILD ${HOME}/rpmbuild/BUILDROOT ${HOME}/rpmbuild/RPMS ${HOME}/rpmbuild/SOURCES ${HOME}/rpmbuild/SPECS ${HOME}/rpmbuild/SRPMS/x10

# put an empty tgz into the sources folder
eval tar -C ${HOME}/rpmbuild/SRPMS -czf ${HOME}/rpmbuild/SOURCES/$tarfile x10
rmdir ${HOME}/rpmbuild/SRPMS/x10

spec="${HOME}/rpmbuild/SPECS/x10.spec"
echo "Name: x10" >> ${spec}
echo "Version: ${X10_VERSION}" >> ${spec}
echo "Release: 1" >> ${spec}
echo "License: Eclipse Public Licence" >> ${spec}
echo "Group: Applications/Programming" >> ${spec}
echo "Source: http://sourceforge.net/projects/x10/files/x10/${X10_VERSION}/${tarfile}" >> ${spec}
echo "URL: http://x10-lang.org" >> ${spec}
echo "Vendor: IBM" >> ${spec}
echo "Summary:The X10 Programming language compiler and Runtime" >> ${spec}
echo "#Requires: " >> ${spec}
echo "Autoreq: 0" >> ${spec}
echo "" >> ${spec}
echo "%description" >> ${spec}
echo "X10 is a programming language that aims to make you 10 times more productive when programming modern computers," >> ${spec}
echo "from multi-core desktops, to GPUs, on up to large clusters with thousands of processors.  If you are familiar" >> ${spec}
echo "with C++ or Java, then you'll quickly become comfortable with the basic syntax of X10.  On top of that, we add" >> ${spec}
echo "features that make expressing computational parallelism, and the movement of data across memory spaces, as" >> ${spec}
echo "elegant as standard sequential program features.  X10 developers have access to a full eclipse-based IDE called" >> ${spec}
echo "X10DT, and a community of developers to help you get started." >> ${spec}
echo "" >> ${spec}
echo "%prep" >> ${spec}
echo "" >> ${spec}
echo "%build" >> ${spec}
echo "" >> ${spec}
echo "%install" >> ${spec}
echo "mkdir -p ${bindir}" >> ${spec}
echo "tar -xzf "$cdir/$tarfile" -C ${bindir}" >> ${spec}
echo "" >> ${spec}
echo "%files" >> ${spec}
sedarg="s/^/\/opt\/ibm\/x10\/${X10_VERSION}\//"
eval tar -tzf "$cdir/$tarfile" | sed -e '/\/$/ d' -e ${sedarg} >> ${spec}

eval rpmbuild --buildroot ${HOME}/rpmbuild/BUILDROOT/x10-${X10_VERSION}-1.${PLATFORM} -bb --target ${RPM_PLATFORM} ${spec}
cp ${HOME}/rpmbuild/RPMS/${RPM_PLATFORM}/x10-${X10_VERSION}-1.${RPM_PLATFORM}.rpm ${cdir}/x10-${X10_VERSION}-1.${PLATFORM}.rpm
rm -rf ${HOME}/rpmbuild
echo "RPM built.  Type \"rpm -qpil ${cdir}/x10-${X10_VERSION}-1.${PLATFORM}.rpm\" to see details"
