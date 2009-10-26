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
. ../mkdist.sh
. ../params.sh

function mkDistMain() {
__x10th_progname=x10th
initOptions
    case "$1" in
        args1)
            rm -rf $__x10th_workRoot
            mkdir -p $__x10th_workRoot
            doCheckOut
            setWorkRev
            doBuild
            setRelVer
            doMakeDist
            ;;
        args2)
            export X10TH_BACKEND=c++
            export X10TH_PGAS=$HOME/pgas2/common/work
            scanEnv
            rm -rf /tmp/x10.trunk
            mkdir -p /tmp/x10.trunk
            parseCmdLine -workRoot /tmp/x10.trunk
            doCheckOut
            setWorkRev
            doBuild
            setRelVer
            doMakeDist
            ;;
        args3)
            rm -rf /tmp/x10.17/c++
            mkdir -p /tmp/x10.17/c++
            export X10TH_DO_VER17=yes
            export X10TH_BACKEND=c++
            export X10TH_PGAS=$HOME/pgas17/common/work
            export X10TH_WORKROOT=/tmp/x10.17/c++
            scanEnv
            doCheckOut
            setWorkRev
            doBuild
            setRelVer
            doMakeDist
            ;;
        args4)
            rm -rf /tmp/x10.17/java
            mkdir -p /tmp/x10.17/java
            export X10TH_DO_VER17=yes
            export X10TH_WORKROOT=/tmp/x10.17/java
            scanEnv
            doCheckOut
            setWorkRev
            doBuild
            setRelVer
            doMakeDist
            ;;
    esac
}

mkDistMain "$1" 3>&2

# vim:tabstop=4:shiftwidth=4:expandtab:nu
