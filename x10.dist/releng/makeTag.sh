#! /bin/bash

# Author: Dave Grove
#
# Simple script to use svn copy to tag a X10 release
# 
# Usage: makeTag -rev <svn revision number> -tag <revision name>
#

DO_APP=0
DO_BENCH=0
DO_MAN=0
DO_X10=0
DO_X10DT=0
BRANCH=trunk

while [ $# != 0 ]; do

    case $1 in
	-rev)
	    export REVISION=$2
	    shift
	    ;;

	-tag)
	    export TAG=$2
	    shift
	    ;;

	-branch)
	    export BRANCH="branches/$2"
	    shift
	    ;;

	-app)
	    DO_APP=1;
	    ;;

	-bench)
	    DO_BENCH=1;
	    ;;

	-man)
	    DO_MAN=1;
	    ;;

	-x10)
	    DO_X10=1;
	    ;;

	-x10dt)
	    DO_X10DT=1;
	    ;;


	*)
	    echo "unknown option: '$1'"
	    exit 1
	    ;;
    esac
    shift
done

if [[ -z "$REVISION" ]]; then
    echo "usage: $0 must give svn revision number as -rev <rev>"
    exit 1
fi

if [[ -z "$TAG" ]]; then
    echo "usage: $0 must give tag name as -tag <tag>"
    exit 1
fi

if [[ $DO_APP == 1 ]]; then
    svn copy -r $REVISION https://svn.code.sourceforge.net/p/x10/code/applications/$BRANCH/ \
        https://svn.code.sourceforge.net/p/x10/code/applications/tags/$TAG \
        -m "Tagging applications $BRANCH revision $REVISION as $TAG release of applications"
fi

if [[ $DO_BENCH == 1 ]]; then
    svn copy -r $REVISION https://svn.code.sourceforge.net/p/x10/code/benchmarks/$BRANCH/ \
        https://svn.code.sourceforge.net/p/x10/code/benchmarks/tags/$TAG \
        -m "Tagging benchmarks $BRANCH revision $REVISION as $TAG release of benchmarks"
fi

if [[ $DO_MAN == 1 ]]; then
    svn copy -r $REVISION https://svn.code.sourceforge.net/p/x10/code/documentation/$BRANCH/ \
        https://svn.code.sourceforge.net/p/x10/code/documentation/tags/$TAG \
        -m "Tagging documentation $BRANCH revision $REVISION as $TAG release of documentation"
fi

if [[ $DO_X10 == 1 ]]; then
    svn copy -r $REVISION https://svn.code.sourceforge.net/p/x10/code/$BRANCH/ \
        https://svn.code.sourceforge.net/p/x10/code/tags/$TAG \
        -m "Tagging X10 $BRANCH revision $REVISION as $TAG release of X10"
fi

if [[ $DO_X10DT == 1 ]]; then
    svn copy -r $REVISION https://svn.code.sourceforge.net/p/x10/code/x10dt/$BRANCH/ \
        https://svn.code.sourceforge.net/p/x10/code/x10dt/tags/$TAG \
        -m "Tagging X10DT $BRANCH revision $REVISION as $TAG release of X10DT"
fi
