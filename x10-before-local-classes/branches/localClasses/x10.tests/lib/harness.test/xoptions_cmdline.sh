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

function parseCmdLineMain() {
__x10th_progname=x10th
initOptions
case "$1" in
    args1)
        printOptions timeLimit
        echo "(( $1: parseCmdLine -timeLimit 120 ))"
        parseCmdLine -timeLimit 120
        printOptions timeLimit
        ;;
    args2)
        printOptions timeLimit
        echo "(( $1: parseCmdLine -timeLimit -45 ))"
        parseCmdLine -timeLimit -45
        printOptions timeLimit
        ;;
    args3)
        printOptions logPath
        echo "(( $1: parseCmdLine -logPath /tmp/x10.logs ))"
        parseCmdLine -logPath /tmp/x10.logs
        printOptions logPath
        ;;
    args4)
        printOptions logPath
        echo "(( $1: mkdir -p /tmp/x10.logs; parseCmdLine -logPath /tmp/x10.logs ))"
        mkdir -p /tmp/x10.logs
        parseCmdLine -logPath /tmp/x10.logs
        printOptions logPath
        rm -rf /tmp/x10.logs
        ;;
    args5)
        printOptions workRoot testRoot runFile
        echo "(( $1: parseCmdLine -workRoot /tmp/x10.work ))"
        parseCmdLine -workRoot /tmp/x10.work
        printOptions workRoot testRoot runFile
        ;;
    args6)
        printOptions workRoot testRoot runFile
        echo "(( $1: mkdir -p /tmp/x10.work; parseCmdLine -workRoot /tmp/x10.work ))"
        mkdir -p /tmp/x10.work
        parseCmdLine -workRoot /tmp/x10.work
        printOptions workRoot testRoot runFile
        rm -rf /tmp/x10.work
        ;;
    args7)
        printOptions testRoot
        echo "(( $1: parseCmdLine -testRoot /tmp/x10.work/x10.tests ))"
        parseCmdLine -testRoot /tmp/x10.work/x10.tests
        printOptions testRoot
        ;;
    args8)
        printOptions testRoot
        echo "(( $1: mkdir -p /tmp/x10.work/x10.tests; parseCmdLine -testRoot /tmp/x10.work/x10.tests ))"
        mkdir -p /tmp/x10.work/x10.tests
        parseCmdLine -testRoot /tmp/x10.work/x10.tests
        printOptions testRoot
        rm -rf /tmp/x10.work/x10.tests
        ;;
    args9)
        printOptions listFile
        echo "(( $1: parseCmdLine -listFile /tmp/test.lst ))"
        parseCmdLine -listFile /tmp/test.lst
        printOptions listFile
        ;;
    args10)
        printOptions listFile
        echo "(( $1: touch /tmp/test.lst; parseCmdLine -listFile /tmp/test.lst ))"
        touch /tmp/test.lst
        parseCmdLine -listFile /tmp/test.lst
        printOptions listFile
        rm -f /tmp/test.lst
        ;;
    args11)
        printOptions list
        echo "(( $1: parseCmdLine ./Constructs/For/*.x10 ./Misc/*.x10 ))"
        parseCmdLine './Constructs/For/*.x10' './Misc/*.x10'
        printOptions list
        ;; 
    args12)
        printOptions runFile
        echo "(( $1: parseCmdLine -runFile /tmp/tesrun.dat ))"
        parseCmdLine -runFile /tmp/testrun.dat
        printOptions runFile
        ;;
    args13)
        printOptions runFile
        echo "(( $1: touch /tmp/testrun.dat; parseCmdLine -runFile /tmp/testrun.dat ))"
        touch /tmp/testrun.dat
        parseCmdLine -runFile /tmp/testrun.dat
        printOptions runFile
        rm -f /tmp/testrun.dat
        ;;
    args14)
        printOptions revision
        echo "(( $1: parseCmdLine -revision 55555 ))"
        parseCmdLine -revision 55555
        printOptions revision
        ;;
    args15)
        printOptions revision
        echo "(( $1: parseCmdLine -revision {2009-06-06} ))"
        parseCmdLine -revision "{2009-06-06}"
        printOptions revision
        ;;
    args16)
        printOptions polyglot
        echo "(( $1: parseCmdLine -polyglot 55555 ))"
        parseCmdLine -polyglot 55555
        printOptions polyglot
        ;;
    args17)
        printOptions polyglot
        echo "(( $1: parseCmdLine -polyglot 2571 ))"
        parseCmdLine -polyglot 2571
        printOptions polyglot
        ;;
    args18)
        printOptions rptFormat
        echo "(( $1: parseCmdLine -rptFormat html ))"
        parseCmdLine -rptFormat html
        printOptions rptFormat
        ;;
    args19)
        printOptions rptFormat
        echo "(( $1: parseCmdLine -rptFormat xml ))"
        parseCmdLine -rptFormat xml
        printOptions rptFormat
        ;;
    args20)
        printOptions mailList
        echo "(( $1: parseCmdLine -mailList 'x10test@rlsecomp1 regression@legato' ))"
        parseCmdLine -mailList "x10test@rlsecomp1 regression@legato"
        printOptions mailList
        ;;
    args21)
        printOptions backend
        echo "(( $1: parseCmdLine -backend cpp ))"
        parseCmdLine -backend cpp
        printOptions backend
        ;;
    args22)
        printOptions backend
        echo "(( $1: parseCmdLine -backend c++ ))"
        parseCmdLine -backend c++
        printOptions backend
        ;;
    args23)
        printOptions java
        echo "(( $1: parseCmdLine -java $HOME/bin/myjava ))"
        parseCmdLine -java $HOME/bin/myjava
        printOptions java
        ;;
    args24)
        printOptions java
        echo "(( $1: parseCmdLine -java $(which java) ))"
        parseCmdLine -java "$(which java)"
        printOptions java
        ;;
    args25)
        printOptions compiler
        echo "(( $1: parseCmdLine -compiler mycc ))"
        parseCmdLine -compiler mycc
        printOptions compiler
        ;;
    args26)
        printOptions compiler
        echo "(( $1: parseCmdLine -compiler xlc ))"
        parseCmdLine -compiler xlc
        printOptions compiler
        ;;
    args27)
        printOptions compopts
        echo "(( $1: parseCmdLine -compopts '-m32 -O3 -g' ))"
        parseCmdLine -compopts "-m32 -O3 -g"
        printOptions compopts
        ;;
    args28)
        printOptions pgas
        echo "(( $1: parseCmdLine -pgas /tmp/pgas2 ))"
        parseCmdLine -pgas /tmp/pgas2
        printOptions pgas
        ;;
    args29)
        printOptions pgas
        echo "(( $1: mkdir -p /tmp/pgas2; parseCmdLine -pgas /tmp/pgas2 ))"
        mkdir -p /tmp/pgas2
        parseCmdLine -pgas /tmp/pgas2
        printOptions pgas
        rm -rf /tmp/pgas2
        ;;
    args30)
        printOptions transport
        echo "(( $1: parseCmdLine -transport smp ))"
        parseCmdLine -transport smp
        printOptions transport
        ;;
    args31)
        printOptions transport
        echo "(( $1: parseCmdLine -transport lapi ))"
        parseCmdLine -transport lapi
        printOptions transport
        ;;
    args32)
        printOptions runLevel
        echo "(( $1: parseCmdLine -runLevel 0 ))"
        parseCmdLine -runLevel 0
        printOptions runLevel
        ;;
    args33)
        printOptions runLevel
        echo "(( $1: parseCmdLine -runLevel stage2 ))"
        parseCmdLine -runLevel stage2
        printOptions runLevel
        ;;
    args34)
        printOptions runLevel
        echo "(( $1: parseCmdLine -runLevel 7 ))"
        parseCmdLine -runLvel 7
        printOptions runLevel
        ;;
    args35)
        printOptionsAll
        echo "(( $1: parseCmdLine -timeLimit 120 ))"
        echo "(( $1: mkdir -p /tmp/x10.logs; parseCmdLine -logPath /tmp/x10.logs ))"
        mkdir -p /tmp/x10.logs
        echo "(( $1: mkdir -p /tmp/x10.work; parseCmdLine -workRoot /tmp/x10.work ))"
        mkdir -p /tmp/x10.work
        echo "(( $1: mkdir -p /tmp/x10.work/x10.tests; parseCmdLine -testRoot /tmp/x10.work/x10.tests ))"
        mkdir -p /tmp/x10.work/x10.tests
        echo "(( $1: touch /tmp/test.lst; parseCmdLine -listFile /tmp/test.lst ))"
        touch /tmp/test.lst
        echo "(( $1: setting test list to './Constructs/For/*.x10 ./Misc/*.x10'... ))"
        export X10TH_LIST="./Constructs/For/*.x10 ./Misc/*.x10"
        echo "(( $1: touch /tmp/testrun.dat; parseCmdLine -runFile /tmp/testrun.dat... ))"
        touch /tmp/testrun.dat
        echo "(( $1: parseCmdLine -revision {2009-06-06} ))"
        echo "(( $1: parseCmdLine -polyglot 2571 ))"
        echo "(( $1: parseCmdLine -rptFormat xml ))"
        echo "(( $1: parseCmdLine -mailList 'x10test@rlsecomp1 regression@legato' ))"
        echo "(( $1: parseCmdLine -backend c++ ))"
        echo "(( $1: parseCmdLine -java $(which java) ))"
        echo "(( $1: parseCmdLine -compiler xlc ))"
        echo "(( $1: parseCmdLine -compopts '-m32 -O3 -g' ))"
        echo "(( $1: mkdir -p /tmp/pgas2; parseCmdLine -pgas /tmp/pgas2 ))"
        mkdir -p /tmp/pgas2
        echo "(( $1: parseCmdLine -transport lapi ))"
        echo "(( $1: parseCmdLine -runLevel 7 ))"
        parseCmdLine -timeLimit 120 -logPath /tmp/x10.logs -workRoot /tmp/x10.work \
            -testRoot /tmp/x10.work/x10.tests -listFile /tmp/test.lst \
            -runFile /tmp/testrun.dat -revision '{2009-06-06}' -polyglot 2571 \
            -rptFormat xml -mailList 'x10test@rlsecomp1 regression@legato' \
            -backend 'c++' -java "$(which java)" -compiler xlc \
            -compopts '-m32 -O3 -g' -pgas /tmp/pgas2 -transport lapi -runLevel 7 \
            './Constructs/For/*.x10' './Misc/*.x10'
        printOptionsAll
        rm -rf /tmp/pgas2
        rm -f /tmp/testrun.dat
        rm -f /tmp/test.lst
        rm -rf /tmp/x10.work/x10.tests
        rm -rf /tmp/x10.work
        rm -rf /tmp/x10.logs
        ;;
    args36)
        printOptions backend rptFormat transport backend runLevel
        echo "(( $1: parseCmdLine -backend c++ ))"
        echo "(( $1: parseCmdLine -rptFormat xml ))"
        echo "(( $1: parseCmdLine -transport lapi ))"
        echo "(( $1: parseCmdLine -backend x10cpp ))"
        echo "(( $1: parseCmdLine -runLevel 7 ))"
        parseCmdLine -backend 'c++' -rptFormat xml -transport lapi \
            -backend x10cpp -runLevel 7
        printOptions backend rptFormat transport backend runLevel
        ;;
esac
}

parseCmdLineMain $1 3>&2

# vim:tabstop=4:shiftwidth=4:expandtab:nu
