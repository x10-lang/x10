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

function scanEnvMain() {
__x10th_progname=x10th
initOptions
case "$1" in
    args1)
        printOptions timeLimit
        echo "(( $1: setting time limit to 120... ))"
        export X10TH_TIMELIMIT=120
        scanEnv
        printOptions timeLimit
        ;;
    args2)
        printOptions timeLimit
        echo "(( $1: setting time limit to -45... ))"
        export X10TH_TIMELIMIT=-45
        scanEnv
        printOptions timeLimit
        ;;
    args3)
        printOptions logPath
        echo "(( $1: setting log path to /tmp/x10.logs... ))"
        export X10TH_LOGPATH=/tmp/x10.logs
        scanEnv
        printOptions logPath
        ;;
    args4)
        printOptions logPath
        echo "(( $1: mkdir -p /tmp/x10.logs; setting log path to /tmp/x10.logs... ))"
        mkdir -p /tmp/x10.logs
        export X10TH_LOGPATH=/tmp/x10.logs
        scanEnv
        printOptions logPath
        rm -rf /tmp/x10.logs
        ;;
    args5)
        printOptions workRoot testRoot runFile
        echo "(( $1: setting work root to /tmp/x10.work... ))"
        export X10TH_WORKROOT=/tmp/x10.work
        scanEnv
        printOptions workRoot testRoot runFile
        ;;
    args6)
        printOptions workRoot testRoot runFile
        echo "(( $1: mkdir -p /tmp/x10.work; setting work root to /tmp/x10.work... ))"
        mkdir -p /tmp/x10.work
        export X10TH_WORKROOT=/tmp/x10.work
        scanEnv
        printOptions workRoot testRoot runFile
        rm -rf /tmp/x10.work
        ;;
    args7)
        printOptions testRoot
        echo "(( $1: setting work root to /tmp/x10.work/x10.tests... ))"
        export X10TH_TESTROOT=/tmp/x10.work/x10.tests
        scanEnv
        printOptions testRoot
        ;;
    args8)
        printOptions testRoot
        echo "(( $1: mkdir -p /tmp/x10.work/x10.tests; setting work root to /tmp/x10.work/x10.tests... ))"
        mkdir -p /tmp/x10.work/x10.tests
        export X10TH_TESTROOT=/tmp/x10.work/x10.tests
        scanEnv
        printOptions testRoot
        rm -rf /tmp/x10.work/x10.tests
        ;;
    args9)
        printOptions listFile
        echo "(( $1: setting list file to /tmp/test.lst... ))"
        export X10TH_LISTFILE=/tmp/test.lst
        scanEnv
        printOptions listFile
        ;;
    args10)
        printOptions listFile
        echo "(( $1: touch /tmp/test.lst; setting list file to /tmp/test.lst... ))"
        touch /tmp/test.lst
        export X10TH_LISTFILE=/tmp/test.lst
        scanEnv
        printOptions listFile
        rm -f /tmp/test.lst
        ;;
    args11)
        printOptions list
        echo "(( $1: setting test list to './Constructs/For/*.x10 ./Misc/*.x10'... ))"
        export X10TH_LIST="./Constructs/For/*.x10 ./Misc/*.x10"
        scanEnv
        printOptions list
        ;; 
    args12)
        printOptions runFile
        echo "(( $1: setting run file to /tmp/tesrun.dat... ))"
        export X10TH_RUNFILE=/tmp/testrun.dat
        scanEnv
        printOptions runFile
        ;;
    args13)
        printOptions runFile
        echo "(( $1: touch /tmp/testrun.dat; setting run file to /tmp/testrun.dat... ))"
        touch /tmp/testrun.dat
        export X10TH_RUNFILE=/tmp/testrun.dat
        scanEnv
        printOptions runFile
        rm -f /tmp/testrun.dat
        ;;
    args14)
        printOptions revision
        echo "(( $1: setting x10 revision keyword to 55555... ))"
        export X10TH_REVISION=55555
        scanEnv
        printOptions revision
        ;;
    args15)
        printOptions revision
        echo "(( $1: setting x10 revision keyword to {2009-06-06}... ))"
        export X10TH_REVISION="{2009-06-06}"
        scanEnv
        printOptions revision
        ;;
    args16)
        printOptions polyglot
        echo "(( $1: setting polyglot revision keyword to 55555... ))"
        export X10TH_POLYGLOT=55555
        scanEnv
        printOptions polyglot
        ;;
    args17)
        printOptions polyglot
        echo "(( $1: setting polyglot revision keyword to 2571... ))"
        export X10TH_POLYGLOT=2571
        scanEnv
        printOptions polyglot
        ;;
    args18)
        printOptions rptFormat
        echo "(( $1: setting report format to html... ))"
        export X10TH_RPTFORMAT=html
        scanEnv
        printOptions rptFormat
        ;;
    args19)
        printOptions rptFormat
        echo "(( $1: setting report format to xml... ))"
        export X10TH_RPTFORMAT=xml
        scanEnv
        printOptions rptFormat
        ;;
    args20)
        printOptions mailList
        echo "(( $1: setting mailing list to 'x10test@rlsecomp1 regression@legato'... ))"
        export X10TH_MAILLIST="x10test@rlsecomp1 regression@legato"
        scanEnv
        printOptions mailList
        ;;
    args21)
        printOptions backend
        echo "(( $1: setting backend to cpp... ))"
        export X10TH_BACKEND=cpp
        scanEnv
        printOptions backend
        ;;
    args22)
        printOptions backend
        echo "(( $1: setting backend to c++... ))"
        export X10TH_BACKEND=c++
        scanEnv
        printOptions backend
        ;;
    args23)
        printOptions java
        echo "(( $1: setting java command to $HOME/bin/myjava... ))"
        export X10TH_JAVA=$HOME/bin/myjava
        scanEnv
        printOptions java
        ;;
    args24)
        printOptions java
        echo "(( $1: setting java command to $(which java)... ))"
        export X10TH_JAVA=$(which java)
        scanEnv
        printOptions java
        ;;
    args25)
        printOptions compiler
        echo "(( $1: setting compiler to mycc... ))"
        export X10TH_COMPILER=mycc
        scanEnv
        printOptions compiler
        ;;
    args26)
        printOptions compiler
        echo "(( $1: setting compiler to xlc... ))"
        export X10TH_COMPILER=xlc
        scanEnv
        printOptions compiler
        ;;
    args27)
        printOptions compopts
        echo "(( $1: setting compiler flags to '-m32 -O3 -g'... ))"
        export X10TH_COMPOPTS="-m32 -O3 -g"
        scanEnv
        printOptions compopts
        ;;
    args28)
        printOptions pgas
        echo "(( $1: setting pgas home to /tmp/pgas2... ))"
        export X10TH_PGAS=/tmp/pgas2
        scanEnv
        printOptions pgas
        ;;
    args29)
        printOptions pgas
        echo "(( $1: mkdir -p /tmp/pgas2; setting pgas home to /tmp/pgas2... ))"
        mkdir -p /tmp/pgas2
        export X10TH_PGAS=/tmp/pgas2
        scanEnv
        printOptions pgas
        rm -rf /tmp/pgas2
        ;;
    args30)
        printOptions transport
        echo "(( $1: setting transport to smp... ))"
        export X10TH_TRANSPORT=smp
        scanEnv
        printOptions transport
        ;;
    args31)
        printOptions transport
        echo "(( $1: setting transport to lapi... ))"
        export X10TH_TRANSPORT=lapi
        scanEnv
        printOptions transport
        ;;
    args32)
        printOptions runLevel
        echo "(( $1: setting run level to 0... ))"
        export X10TH_RUNLEVEL=0
        scanEnv
        printOptions runLevel
        ;;
    args33)
        printOptions runLevel
        echo "(( $1: setting run level to stage2... ))"
        export X10TH_RUNLEVEL=stage2
        scanEnv
        printOptions runLevel
        ;;
    args34)
        printOptions runLevel
        echo "(( $1: setting run level to 7... ))"
        export X10TH_RUNLEVEL=7
        scanEnv
        printOptions runLevel
        ;;
    args35)
        printOptionsAll
        echo "(( $1: setting time limit to 120... ))"
        export X10TH_TIMELIMIT=120
        echo "(( $1: mkdir -p /tmp/x10.logs; setting log path to /tmp/x10.logs... ))"
        mkdir -p /tmp/x10.logs
        export X10TH_LOGPATH=/tmp/x10.logs
        echo "(( $1: mkdir -p /tmp/x10.work; setting work root to /tmp/x10.work... ))"
        mkdir -p /tmp/x10.work
        export X10TH_WORKROOT=/tmp/x10.work
        echo "(( $1: mkdir -p /tmp/x10.work/x10.tests; setting work root to /tmp/x10.work/x10.tests... ))"
        mkdir -p /tmp/x10.work/x10.tests
        export X10TH_TESTROOT=/tmp/x10.work/x10.tests
        echo "(( $1: touch /tmp/test.lst; setting list file to /tmp/test.lst... ))"
        touch /tmp/test.lst
        export X10TH_LISTFILE=/tmp/test.lst
        echo "(( $1: setting test list to './Constructs/For/*.x10 ./Misc/*.x10'... ))"
        export X10TH_LIST="./Constructs/For/*.x10 ./Misc/*.x10"
        echo "(( $1: touch /tmp/testrun.dat; setting run file to /tmp/testrun.dat... ))"
        touch /tmp/testrun.dat
        export X10TH_RUNFILE=/tmp/testrun.dat
        echo "(( $1: setting x10 revision keyword to {2009-06-06}... ))"
        export X10TH_REVISION="{2009-06-06}"
        echo "(( $1: setting polyglot revision keyword to 2571... ))"
        export X10TH_POLYGLOT=2571
        echo "(( $1: setting report format to xml... ))"
        export X10TH_RPTFORMAT=xml
        echo "(( $1: setting mailing list to 'x10test@rlsecomp1 regression@legato'... ))"
        export X10TH_MAILLIST="x10test@rlsecomp1 regression@legato"
        echo "(( $1: setting backend to c++... ))"
        export X10TH_BACKEND=c++
        echo "(( $1: setting java command to $(which java)... ))"
        export X10TH_JAVA="$(which java)"
        echo "(( $1: setting compiler to xlc... ))"
        export X10TH_COMPILER=xlc
        echo "(( $1: setting compiler flags to '-m32 -O3 -g'... ))"
        export X10TH_COMPOPTS="-m32 -O3 -g"
        echo "(( $1: mkdir -p /tmp/pgas2; setting pgas home to /tmp/pgas2... ))"
        mkdir -p /tmp/pgas2
        export X10TH_PGAS=/tmp/pgas2
        echo "(( $1: setting transport to lapi... ))"
        export X10TH_TRANSPORT=lapi
        echo "(( $1: setting run level to 7... ))"
        export X10TH_RUNLEVEL=7
        scanEnv
        printOptionsAll
        rm -rf /tmp/pgas2
        rm -f /tmp/testrun.dat
        rm -f /tmp/test.lst
        rm -rf /tmp/x10.work/x10.tests
        rm -rf /tmp/x10.work
        rm -rf /tmp/x10.logs
        ;;
    args36)
        printOptions backend rptFormat transport polyglot runLevel
        echo "(( $1: setting backend to c++... ))"
        export X10TH_BACKEND=c++
        echo "(( $1: setting report format to xml... ))"
        export X10TH_RPTFORMAT=xml
        echo "(( $1: setting transport to lapi... ))"
        export X10TH_TRANSPORT=lapi
        echo "(( $1: setting polyglot revision keyword to 55555... ))"
        export X10TH_POLYGLOT=55555
        echo "(( $1: setting run level to 7... ))"
        export X10TH_RUNLEVEL=7
        scanEnv
        printOptions backend rptFormat transport polyglot runLevel
        ;;
esac
}

scanEnvMain $1 3>&2

# vim:tabstop=4:shiftwidth=4:expandtab:nu
