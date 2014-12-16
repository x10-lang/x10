#! /bin/bash

# Author: Dave Grove
#
# Simple script to use svn copy to create a 
# long-term development branch for X10 or X10DT
# 
# Usage: makeBranch.sh -rev <svn rev #> -branch <branch name> [-x10] [-x10dt] [-torTest]
#

DO_TORTEST=0
DO_X10=0
DO_X10DT=0

while [ $# != 0 ]; do

    case $1 in
	-rev)
	    export REVISION=$2
	    shift
	    ;;

	-branch)
	    export TAG=$2
	    shift
	    ;;

	-torTest)
	    DO_TORTEST=1;
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
    echo "usage: $0 must give branch name as -branch <name>"
    exit 1
fi

if [[ $DO_TORTEST == 1 ]]; then
    svn copy -r $REVISION svn+ssh://triloka1.pok.ibm.com/gsa/yktgsa/projects/x/x10/svn-torontoTests/trunk/ \
        svn+ssh://triloka1.pok.ibm.com/gsa/yktgsa/projects/x/x10/svn-torontoTests/branches/$TAG \
        -m "Copying internal tests trunk revision $REVISION as $TAG branch of internal tests"
fi

if [[ $DO_X10 == 1 ]]; then
    svn copy -r $REVISION https://svn.code.sourceforge.net/p/x10/code/trunk/ \
        https://svn.code.sourceforge.net/p/x10/code/branches/$TAG \
        -m "Copying X10 trunk revision $REVISION as $TAG branch of X10"
fi

if [[ $DO_X10DT == 1 ]]; then
    svn copy -r $REVISION https://svn.code.sourceforge.net/p/x10/code/x10dt/trunk/ \
        https://svn.code.sourceforge.net/p/x10/code/x10dt/branches/$TAG \
        -m "Copying X10DT trunk revision $REVISION as $TAG branch of X10DT"
fi
