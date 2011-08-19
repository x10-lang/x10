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

. ../build.sh
. ../checkout.sh
. ../options.sh
. ../usage.sh
. ../version.sh

function buildMain() {
__x10th_progname=x10th
initOptions
    case "$1" in
        args1)
            mkdir -p $__x10th_workRoot
            doCheckOut
            doBuild
            ;;
        args2)
            export X10TH_BACKEND=c++
            export X10TH_PGAS=$HOME/pgas2/common/work
            scanEnv
            mkdir -p /tmp/x10.trunk
            parseCmdLine -workRoot /tmp/x10.trunk
            doCheckOut
            doBuild
            ;;
        args3)
            mkdir -p /tmp/x10.17
            export X10TH_DO_VER17=yes
            export X10TH_WORKROOT=/tmp/x10.17
            scanEnv
            doCheckOut
            doBuild
            ;;
    esac
}

buildMain "$1" 3>&2

# vim:tabstop=4:shiftwidth=4:expandtab:nu
