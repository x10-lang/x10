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

function resolveMain() {
__x10th_progname=x10th
initOptions
case "$1" in
    args1)
        printOptions timeLimit revision backend compiler transport list
        echo "(( $1: setting time limit to 120... ))"
        export X10TH_TIMELIMIT=120
        echo "(( $1: setting revision to {2009-06-06}... ))"
        export X10TH_REVISION="{2009-06-06}"
        echo "(( $1: setting compiler to xlc... ))"
        export X10TH_COMPILER=xlc
        scanEnv
        echo "(( $1: parseCmdLine -backend c++ ))"
        echo "(( $1: parseCmdLine -transport lapi ))"
        echo "(( $1: parseCmdLine ./Constructs/For/*.x10 ./Misc/*.x10 ))"
        parseCmdLine -backend 'c++' -transport lapi \
            './Constructs/For/*.x10' './Misc/*.x10'
        printOptions timeLimit revision backend compiler transport list
        resolveOptions
        ;;
    args2)
        printOptions timeLimit mailList revision backend compiler transport list
        echo "(( $1: setting time limit to 120... ))"
        export X10TH_TIMELIMIT=120
        echo "(( $1: setting revision to {2009-06-06}... ))"
        export X10TH_REVISION="{2009-06-06}"
        echo "(( $1: setting compiler to xlc... ))"
        export X10TH_COMPILER=xlc
        scanEnv
        echo "(( $1: parseCmdLine -mailList 'x10test@rlsecomp1 regression@legato' ))"
        echo "(( $1: parseCmdLine -backend c++ ))"
        echo "(( $1: parseCmdLine -transport lapi ))"
        echo "(( $1: parseCmdLine ./Constructs/For/*.x10 ./Misc/*.x10 ))"
        parseCmdLine -backend 'c++' -transport lapi \
            -mailList 'x10test@rlsecomp1 regression@legato' \
            './Constructs/For/*.x10' './Misc/*.x10'
        printOptions timeLimit mailList revision backend compiler transport list
        resolveOptions
        ;;
    args3)
        printOptions timeLimit mailList revision backend compiler transport list
        echo "(( $1: setting time limit to 120... ))"
        export X10TH_TIMELIMIT=120
        echo "(( $1: setting revision to {2009-06-06}... ))"
        export X10TH_REVISION="{2009-06-06}"
        echo "(( $1: setting compiler to gcc... ))"
        export X10TH_COMPILER=gcc
        scanEnv
        echo "(( $1: parseCmdLine -mailList 'x10test@rlsecomp1 regression@legato' ))"
        echo "(( $1: parseCmdLine -backend c++ ))"
        echo "(( $1: parseCmdLine -transport lapi ))"
        echo "(( $1: parseCmdLine ./Constructs/For/*.x10 ./Misc/*.x10 ))"
        parseCmdLine -backend 'c++' -transport lapi \
            -mailList 'x10test@rlsecomp1 regression@legato' \
            './Constructs/For/*.x10' './Misc/*.x10'
        printOptions timeLimit mailList revision backend compiler transport list
        resolveOptions
        ;;
    args4)
        printOptions timeLimit mailList revision pgas backend compiler transport list
        echo "(( $1: setting time limit to 120... ))"
        export X10TH_TIMELIMIT=120
        echo "(( $1: setting revision to {2009-06-06}... ))"
        export X10TH_REVISION="{2009-06-06}"
        echo "(( $1: setting compiler to gcc... ))"
        export X10TH_COMPILER=gcc
        echo "(( $1: setting pgas root to /tmp/pgas2... ))"
        mkdir -p /tmp/pgas2
        export X10TH_PGAS=/tmp/pgas2
        scanEnv
        echo "(( $1: parseCmdLine -mailList 'x10test@rlsecomp1 regression@legato' ))"
        echo "(( $1: parseCmdLine -backend c++ ))"
        echo "(( $1: parseCmdLine -transport lapi ))"
        echo "(( $1: parseCmdLine ./Constructs/For/*.x10 ./Misc/*.x10 ))"
        parseCmdLine -backend 'c++' -transport lapi \
            -mailList 'x10test@rlsecomp1 regression@legato' \
            './Constructs/For/*.x10' './Misc/*.x10'
        printOptions timeLimit mailList revision pgas backend compiler transport list
        resolveOptions
        rm -rf /tmp/pgas2
        ;;
    args5)
        printOptions timeLimit mailList revision pgas backend compiler transport list
        echo "(( $1: setting time limit to 120... ))"
        export X10TH_TIMELIMIT=120
        echo "(( $1: setting revision to {2009-06-06}... ))"
        export X10TH_REVISION="{2009-06-06}"
        echo "(( $1: setting compiler to gcc... ))"
        export X10TH_COMPILER=gcc
        echo "(( $1: setting pgas root to /tmp/pgas2... ))"
        mkdir -p /tmp/pgas2
        export X10TH_PGAS=/tmp/pgas2
        scanEnv
        echo "(( $1: parseCmdLine -mailList 'x10test@rlsecomp1 regression@legato' ))"
        echo "(( $1: parseCmdLine -backend c++ ))"
        echo "(( $1: parseCmdLine -transport lapi ))"
        echo "(( $1: parseCmdLine ./Constructs/For/*.x10 ./Misc/*.x10 ))"
        parseCmdLine -backend 'c++' -transport lapi \
            -mailList 'x10test@rlsecomp1 regression@legato' \
            './Constructs/For/*.x10' './Misc/*.x10'
        printOptions timeLimit mailList revision pgas backend compiler transport list
        mkdir -p /tmp/pgas2/include/x10
        touch /tmp/pgas2/include/x10/x10rt_api.h
        resolveOptions
        rm -rf /tmp/pgas2
        ;;
    args6)
        printOptions timeLimit mailList revision pgas backend compiler transport list
        echo "(( $1: setting time limit to 120... ))"
        export X10TH_TIMELIMIT=120
        echo "(( $1: setting revision to {2009-06-06}... ))"
        export X10TH_REVISION="{2009-06-06}"
        echo "(( $1: setting compiler to gcc... ))"
        export X10TH_COMPILER=gcc
        echo "(( $1: setting pgas root to /tmp/pgas2... ))"
        mkdir -p /tmp/pgas2
        export X10TH_PGAS=/tmp/pgas2
        scanEnv
        echo "(( $1: parseCmdLine -mailList 'x10test@rlsecomp1 regression@legato' ))"
        echo "(( $1: parseCmdLine -backend c++ ))"
        echo "(( $1: parseCmdLine -transport lapi ))"
        echo "(( $1: parseCmdLine ./Constructs/For/*.x10 ./Misc/*.x10 ))"
        parseCmdLine -backend 'c++' -transport lapi \
            -mailList 'x10test@rlsecomp1 regression@legato' \
            './Constructs/For/*.x10' './Misc/*.x10'
        printOptions timeLimit mailList revision pgas backend compiler transport list
        mkdir -p /tmp/pgas2/include/x10
        touch /tmp/pgas2/include/x10/x10rt_api.h
        mkdir -p /tmp/pgas2/lib
        touch /tmp/pgas2/lib/libxlpgas_sockets.so
        resolveOptions
        rm -rf /tmp/pgas2
        ;;
    args7)
        printOptions timeLimit mailList revision pgas backend compiler transport list
        echo "(( $1: setting time limit to 120... ))"
        export X10TH_TIMELIMIT=120
        echo "(( $1: setting revision to {2009-06-06}... ))"
        export X10TH_REVISION="{2009-06-06}"
        echo "(( $1: setting compiler to gcc... ))"
        export X10TH_COMPILER=gcc
        echo "(( $1: setting pgas root to /tmp/pgas2... ))"
        mkdir -p /tmp/pgas2
        export X10TH_PGAS=/tmp/pgas2
        scanEnv
        echo "(( $1: parseCmdLine -mailList 'x10test@rlsecomp1 regression@legato' ))"
        echo "(( $1: parseCmdLine -backend c++ ))"
        echo "(( $1: parseCmdLine -transport lapi ))"
        echo "(( $1: parseCmdLine ./Constructs/For/*.x10 ./Misc/*.x10 ))"
        parseCmdLine -backend 'c++' -transport lapi \
            -mailList 'x10test@rlsecomp1 regression@legato' \
            './Constructs/For/*.x10' './Misc/*.x10'
        printOptions timeLimit mailList revision pgas backend compiler transport list
        mkdir -p /tmp/pgas2/include/x10
        touch /tmp/pgas2/include/x10/x10rt_api.h
        mkdir -p /tmp/pgas2/lib
        touch /tmp/pgas2/lib/libxlpgas_lapi.so
        resolveOptions
        rm -rf /tmp/pgas2
        ;;
    args8)
        printOptions timeLimit mailList compopts revision pgas backend compiler transport list
        echo "(( $1: setting time limit to 120... ))"
        export X10TH_TIMELIMIT=120
        echo "(( $1: setting revision to {2009-06-06}... ))"
        export X10TH_REVISION="{2009-06-06}"
        echo "(( $1: setting compiler to gcc... ))"
        export X10TH_COMPILER=gcc
        echo "(( $1: setting pgas root to /tmp/pgas2... ))"
        mkdir -p /tmp/pgas2
        export X10TH_PGAS=/tmp/pgas2
        scanEnv
        echo "(( $1: parseCmdLine -mailList 'x10test@rlsecomp1 regression@legato' ))"
        echo "(( $1: parseCmdLine -compopts '-m32 -O2 -g' ))"
        echo "(( $1: parseCmdLine -backend c++ ))"
        echo "(( $1: parseCmdLine -transport lapi ))"
        echo "(( $1: parseCmdLine ./Constructs/For/*.x10 ./Misc/*.x10 ))"
        parseCmdLine -backend 'c++' -transport lapi -compopts '-m32 -O2 -g' \
            -mailList 'x10test@rlsecomp1 regression@legato' \
            './Constructs/For/*.x10' './Misc/*.x10'
        printOptions timeLimit mailList compopts revision pgas backend compiler transport list
        mkdir -p /tmp/pgas2/include/x10
        touch /tmp/pgas2/include/x10/x10rt_api.h
        mkdir -p /tmp/pgas2/lib
        touch /tmp/pgas2/lib/libxlpgas_lapi.so
        resolveOptions
        rm -rf /tmp/pgas2
        ;;
esac
}

resolveMain $1 3>&2

# vim:tabstop=4:shiftwidth=4:expandtab:nu
