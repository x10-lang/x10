#!/bin/bash

#
# (c) Copyright IBM Corporation 2009
#
# $Id$
# This file is part of X10 v 2.0 Unified Test Harness.

if (( $# != 1 )); then
    printf "usage: $0 argsN\n"
    exit 1
fi

. ../options.sh
. ../usage.sh
. ../version.sh

function envCmdLineMain() {
__x10th_progname=x10th
initOptions
case "$1" in
    args1)
        printOptions timeLimit runFile revision backend compiler transport list
        echo "(( $1: setting time limit to 120... ))"
        export X10TH_TIMELIMIT=120
        echo "(( $1: setting revision to {2009-06-06}... ))"
        export X10TH_REVISION="{2009-06-06}"
        echo "(( $1: setting compiler to xlc... ))"
        export X10TH_COMPILER=xlc
        scanEnv
        echo "(( $1: parseCmdLine -runFile /tmp/testrun.dat ))"
        touch /tmp/testrun.dat
        echo "(( $1: parseCmdLine -backend c++ ))"
        echo "(( $1: parseCmdLine -transport lapi ))"
        echo "(( $1: parseCmdLine ./Constructs/For/*.x10 ./Misc/*.x10 ))"
        parseCmdLine -runFile /tmp/testrun.dat -backend 'c++' \
            -transport lapi './Constructs/For/*.x10' './Misc/*.x10'
        printOptions timeLimit runFile revision backend compiler transport list
        rm -f /tmp/testrun.dat
        ;;
    args2)
        printOptions revision backend compiler transport
        echo "(( $1: setting revision to {2009-06-06}... ))"
        export X10TH_REVISION="{2009-06-06}"
        echo "(( $1: setting compiler to icc... ))"
        export X10TH_COMPILER=icc
        echo "(( $1: parseCmdLine -backend c++ ))"
        echo "(( $1: parseCmdLine -transport lapi ))"
        scanEnv
        parseCmdLine -backend 'c++' -transport lapi
        printOptions revision backend compiler transport
        ;;
    args3)
        printOptions revision backend compiler transport
        echo "(( $1: setting revision to {2009-06-06}... ))"
        export X10TH_REVISION="{2009-06-06}"
        echo "(( $1: setting compiler to xlc... ))"
        export X10TH_COMPILER=xlc
        echo "(( $1: parseCmdLine -backend x10cpp ))"
        echo "(( $1: parseCmdLine -transport lapi ))"
        scanEnv
        parseCmdLine -backend x10cpp -transport lapi
        printOptions revision backend compiler transport
        ;;
esac
}

envCmdLineMain $1 3>&2

# vim:tabstop=4:shiftwidth=4:expandtab:nu
