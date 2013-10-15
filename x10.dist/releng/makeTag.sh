#! /bin/bash

# Author: Dave Grove
#
# Simple script to use svn copy to tag a X10 release
# 
# Usage: makeTag -rev <svn revision number> -tag <revision name>
#

DO_CODE=0
DO_MAN=0
DO_X10DT=0

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

	-code)
	    DO_CODE=1;
	    ;;

	-man)
	    DO_MAN=1;
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

if [[ $DO_CODE == 1 ]]; then
    svn copy -r $REVISION https://svn.code.sourceforge.net/p/x10/code/trunk/ \
        https://svn.code.sourceforge.net/p/x10/code/tags/$TAG \
        -m "Tagging trunk revision $REVISION as $TAG release of X10"

    svn copy -r $REVISION https://svn.code.sourceforge.net/p/x10/code/benchmarks/trunk/ \
        https://svn.code.sourceforge.net/p/x10/code/benchmarks/tags/$TAG \
        -m "Tagging benchmarks trunk revision $REVISION as $TAG release of X10"
fi

if [[ $DO_MAN == 1 ]]; then
    svn copy -r $REVISION https://svn.code.sourceforge.net/p/x10/code/documentation/trunk/ \
        https://svn.code.sourceforge.net/p/x10/code/documentation/tags/$TAG \
        -m "Tagging documentation trunk revision $REVISION as $TAG release of X10"
fi

if [[ $DO_X10DT == 1 ]]; then
    svn copy -r $REVISION https://svn.code.sourceforge.net/p/x10/code/x10dt/trunk/ \
        https://svn.code.sourceforge.net/p/x10/code/x10dt/tags/$TAG \
        -m "Tagging X10DT trunk revision $REVISION as $TAG release of X10DT"
fi


