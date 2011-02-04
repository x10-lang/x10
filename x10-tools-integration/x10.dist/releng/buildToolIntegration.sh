#!/bin/bash

# Dave Grove

workdir=/tmp/x10-tib-$USER

CLEAN="true"

while [ $# != 0 ]; do

  case $1 in
    -dir)
        workdir=$2
	shift
    ;;

    -rev)
        rev=$2
	shift
    ;;

    -noclean)
        CLEAN=""
        ;;

   esac
   shift
done

UNAME=`uname -smp | sed -e 's/ /,/g'`
case "$UNAME" in
  CYGWIN*,i*86,*) X10_PLATFORM='cygwin_x86';;
  Linux,*86_64*,*) X10_PLATFORM='linux_x86_64';;
  Linux,*86*,*) X10_PLATFORM='linux_x86';;
  Linux,ppc*,*) X10_PLATFORM='linux_ppc';;
  AIX,*,powerpc) X10_PLATFORM='aix_ppc';;
  Darwin,*,i*86) X10_PLATFORM='macosx_x86'
      export USE_32BIT=true
      export USE_64BIT=true
   ;;
    
  *) echo "Unrecognized platform: '$UNAME'"; exit 1;;
esac

distdir=$workdir/x10
projectList="x10.common x10.compiler x10.constraints x10.dist x10.runtime x10.tests x10.wala"
MAX_RETRIES=5

if [ ! -z "$CLEAN" ]; then
    echo
    echo cleaning $workdir
    rm -rf $workdir
    mkdir -p $workdir || exit 1
    mkdir -p $workdir/x10

    (
        cd $distdir

        echo
        echo Getting X10 source distribution...
	projectsToDo="$projectList"
	count=1
	while [ -n "$projectsToDo" ]; do
	    failedProjects=""
            for proj in $projectsToDo
            do
		# N.B.: SVN 1.4 export doesn't report errors for non-existent projects/tags
		svn export --force http://x10.svn.sourceforge.net/svnroot/x10/branches/x10-tools-integration/$proj
		if [ $? != 0 ]; then
                    failedProjects="$failedProjects $proj"
		fi
            done
	    let count++
	    projectsToDo="$failedProjects"
	    if [ -n "$projectsToDo" ]; then
		if [ $count -le $MAX_RETRIES ]; then
		    echo "Errors retrieving $projectsToDo; initiating retry $count of 5..."
		else
		    echo "Reached retry limit; aborting source retrieval."
		    break
		fi
	    fi
	done
    )

    if [ -n "$projectsToDo" ]; then
        echo "Errors retrieving X10 source; aborting."
        exit 1
    else
        echo "The distribution is now exported to the directory $workdir"
    fi
fi

echo "Building distribution"
cd $distdir/x10.dist

ant dist -Doptimize=true

if [ $? != 0 ]; then
    echo "Ant build failed; aborting build for this platform."
    exit 1
fi

$distdir/x10.dist/releng/packageCPPRelease.sh -version tib_$rev -platform $X10_PLATFORM
echo "Platform specific distribution tarball created"
