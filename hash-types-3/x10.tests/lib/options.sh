#!/bin/bash

#
# (c) Copyright IBM Corporation 2009
#
# $Id$
# This file is part of X10 v 2.0 Unified Test Harness.

# these are global variables declared and set here
# TODO: rewrite once bash supports associative arrays
declare -i __x10th_timeLimt
declare __x10th_logPath
declare __x10th_testRoot
declare __x10th_workRoot
declare __x10th_listFile
declare __x10th_list
declare __x10th_runFile
declare __x10th_revision
declare __x10th_polyglot
declare __x10th_rptFormat
declare __x10th_mailList
declare __x10th_backend
declare __x10th_java
declare __x10th_compiler
declare __x10th_compopts
declare __x10th_pgas
declare __x10th_transport
declare -i __x10th_runLevel
declare __x10th_do_checkout
declare __x10th_do_build
declare __x10th_do_makedist
declare __x10th_do_regress
declare __x10th_do_report
declare __x10th_do_mail
declare __x10th_do_ver17
declare __x10th_cxx
declare __x10th_x10_svnroot
declare __x10th_polyglot_svnroot
declare __x10th_x10_modules
declare __x10th_polyglot_modules
declare __x10th_module_common
declare __x10th_module_compiler
declare __x10th_module_constraints
declare __x10th_module_cppbackend
declare __x10th_module_dist
declare __x10th_module_runtime
declare __x10th_module_tests
declare __x10th_module_polyglot
declare -i __x10th_checkout_attempts

# print all the options and their values
# usage: printOptionsAll
function printOptionsAll() {
    printf >&3 "
timeLimit = $__x10th_timeLimit
logPath = $__x10th_logPath
workRoot = $__x10th_workRoot
testRoot = $__x10th_testRoot
listFile = $__x10th_listFile
list = $__x10th_list
runFile = $__x10th_runFile
revision = $__x10th_revision
polyglot = $__x10th_polyglot
rptFormat = $__x10th_rptFormat
mailList = $__x10th_mailList
backend = $__x10th_backend
java = $__x10th_java
compiler = $__x10th_compiler
compopts = $__x10th_compopts
pgas = $__x10th_pgas
transport = $__x10th_transport
runLevel = $__x10th_runLevel
"
}

# print value(s) for the specified option(s)
# usage: printOptions option(s)
function printOptions() {
    while (( $# > 0 )); do
	    case "$1" in
	        timeLimit)
	            printf >&3 "timeLimit = $__x10th_timeLimit\n"
	            ;;
	        logPath)
	            printf >&3 "logPath = $__x10th_logPath\n"
	            ;;
	        workRoot)
	            printf >&3 "workRoot = $__x10th_workRoot\n"
	            ;;
	        testRoot)
	            printf >&3 "testRoot = $__x10th_testRoot\n"
	            ;;
	        listFile)
	            printf >&3 "listFile = $__x10th_listFile\n"
	            ;;
            list)
                printf >&3 "list = $__x10th_list\n"
                ;;
	        runFile)
	            printf >&3 "runFile = $__x10th_runFile\n"
	            ;;
	        revision)
	            printf >&3 "revision = $__x10th_revision\n"
	            ;;
	        polyglot)
	            printf >&3 "polyglot = $__x10th_polyglot\n"
	            ;;
	        rptFormat)
	            printf >&3 "rptFormat = $__x10th_rptFormat\n"
	            ;;
	        mailList)
	            printf >&3 "mailList = $__x10th_mailList\n"
	            ;;
	        backend)
	            printf >&3 "backend = $__x10th_backend\n"
	            ;;
	        java)
	            printf >&3 "java = $__x10th_java\n"
	            ;;
	        compiler)
	            printf >&3 "compiler = $__x10th_compiler\n"
	            ;;
	        compopts)
	            printf >&3 "compopts = $__x10th_compopts\n"
	            ;;
	        pgas)
	            printf >&3 "pgas = $__x10th_pgas\n"
	            ;;
	        transport)
	            printf >&3 "transport = $__x10th_transport\n"
	            ;;
	        runLevel)
	            printf >&3 "runLevel = $__x10th_runLevel\n"
	            ;;
        esac
        shift
    done
}

# set the option defaults
# usage: initOptions
# global variables used:
# TMPDIR
function initOptions() {
    let '__x10th_timeLimit = 60'
    __x10th_logPath=$(pwd)
    if [[ -n $TMPDIR && -d $TMPDIR ]]; then
        __x10th_workRoot=${TMPDIR}/x10th.unified
    else
        __x10th_workRoot=/tmp/x10th.unified
    fi
    # reset the following whenever working directory changes
    __x10th_testRoot=${__x10th_workRoot}/x10.tests/examples
    __x10th_listFile=""
    __x10th_list=""
    # reset the following whenever working directory changes
    __x10th_runFile=${__x10th_workRoot}/x10.tests/data/testrun.dat
    __x10th_revision=HEAD
    __x10th_polyglot=HEAD
    __x10th_rptFormat=text
    __x10th_mailList=""
    __x10th_backend=java
    __x10th_java=java
    if [[ $(uname -s) == "AIX" ]]; then
        __x10th_compiler=xlc
    else
        __x10th_compiler=gcc
    fi
    __x10th_compopts=""
    __x10th_pgas=""
    if [[ $(uname -s) == "AIX" ]]; then
        __x10th_transport=lapi
    else
        __x10th_transport=sockets
    fi
    let '__x10th_runLevel = 6'
    # these don't belong here; should moved to a separate file
    __x10th_do_checkout=no
    __x10th_do_build=no
    __x10th_do_makedist=no
    __x10th_do_regress=no
    __x10th_do_report=no
    __x10th_do_mail=no
    __x10th_do_ver17=no
    if [[ $(uname -s) == "AIX" ]]; then
        __x10th_cxx=mpCC_r
    else
        __x10th_cxx=g++
    fi
    __x10th_x10_svnroot="https://x10.svn.sf.net/svnroot/x10/trunk"
    __x10th_polyglot_svnroot="http://polyglot-compiler.googlecode.com/svn/trunk"
    __x10th_module_common="x10.common"
    __x10th_module_compiler="x10.compiler"
    __x10th_module_constraints="x10.constraints"
    __x10th_module_cppbackend=""
    __x10th_module_dist="x10.dist"
    __x10th_module_runtime="x10.runtime"
    __x10th_module_tests="x10.tests"
    __x10th_x10_modules="$__x10th_module_common $__x10th_module_compiler \
            $__x10th_module_constraints $__x10th_module_cppbackend $__x10th_module_dist \
            $__x10th_module_runtime $__x10th_module_tests"
    __x10th_module_polyglot="polyglot"
    __x10th_polyglot_modules="$__x10th_module_polyglot"
    let '__x10th_checkout_attempts = 1'
}

# scan the test harness run environment and update options
# usage: scanEnv
# global variables used:
function scanEnv() {
    local -i tmpInt

    if [[ -n $X10TH_TIMELIMIT ]]; then
        (( tmpInt = $X10TH_TIMELIMIT ))
        if (( $? != 0 || $tmpInt < 0 )); then
            printf >&3 "${__x10th_progname}:Error ## X10TH_TIMELIMIT - "
            printf >&3 "time limit $X10TH_TIMELIMIT is invalid!\n"
            exit 1
        else
            __x10th_timeLimit=$tmpInt
        fi
    fi
    if [[ -n $X10TH_LOGPATH ]]; then
        if [[ ! -d $X10TH_LOGPATH ]]; then
            printf >&3 "${__x10th_progname}:Error ## X10TH_LOGPATH - "
            printf >&3 "directory $X10TH_LOGPATH must exist!\n"
            exit 1
        else
            __x10th_logPath=$X10TH_LOGPATH
        fi
    fi
    if [[ -n $X10TH_WORKROOT ]]; then
        if [[ ! -d $X10TH_WORKROOT ]]; then
            printf >&3 "${__x10th_progname}:Error ## X10TH_WORKROOT - "
            printf >&3 "directory $X10TH_WORKROOT must exist!\n"
            exit 1
        else
            __x10th_workRoot=$X10TH_WORKROOT
            __x10th_testRoot=${__x10th_workRoot}/x10.tests/examples
            __x10th_runFile=${__x10th_workRoot}/x10.tests/data/testrun.dat
        fi
    fi
    if [[ -n $X10TH_TESTROOT ]]; then
        if [[ ! -d $X10TH_TESTROOT ]]; then
            printf >&3 "${__x10th_progname}:Error ## X10TH_TESTROOT - "
            printf >&3 "directory $X10TH_TESTROOT must exist!\n"
            exit 1
        else
            __x10th_testRoot=$X10TH_TESTROOT
        fi
    fi
    if [[ -n $X10TH_LISTFILE ]]; then
        if [[ ! -r $X10TH_LISTFILE ]]; then
            printf >&3 "${__x10th_progname}:Error ## X10TH_LISTFILE - "
            printf >&3 "file $X10TH_LISTFILE must exist and readable!\n"
            exit 1
        else
            __x10th_listFile=$X10TH_LISTFILE
        fi
    fi
    if [[ -n "$X10TH_LIST" ]]; then
        __x10th_list="$X10TH_LIST"
    fi
    if [[ -n $X10TH_RUNFILE ]]; then
        if [[ ! -r $X10TH_RUNFILE ]]; then
            printf >&3 "${__x10th_progname}:Error ## X10TH_RUNFILE - "
            printf >&3 "file $X10TH_RUNFILE must exist and redable!\n"
            exit 1
        else
            __x10th_runFile=$X10TH_RUNFILE
        fi
    fi
    if [[ -n $X10TH_REVISION ]]; then
        svn list -r "$X10TH_REVISION" $__x10th_x10_svnroot >/dev/null 2>&1
        if (( $? != 0 )); then
            printf >&3 "${__x10th_progname}:Error ## X10TH_REVISION - "
            printf >&3 "keyword $X10TH_REVISION is invalid!\n"
            exit 1
        else
            __x10th_revision=$X10TH_REVISION
        fi
    fi
    if [[ -n $X10TH_POLYGLOT ]]; then
        svn list -r "$X10TH_POLYGLOT" $__x10th_polyglot_svnroot >/dev/null 2>&1
        if (( $? != 0 )); then
            printf >&3 "${__x10th_progname}:Error ## X10TH_POLYGLOT - "
            printf >&3 "keyword $X10TH_POLYGLOT is invalid!\n"
            exit 1
        else
            __x10th_polyglot=$X10TH_POLYGLOT
        fi
    fi
    if [[ -n $X10TH_RPTFORMAT ]]; then
        if [[ $X10TH_RPTFORMAT == "text" || $X10TH_RPTFORMAT == "xml" ]]; then
            __x10th_rptFormat=$X10TH_RPTFORMAT
        else
            printf >&3 "${__x10th_progname}:Error ## X10TH_RPTFORMAT - "
            printf >&3 "format $X10TH_RPTFORMAT is invalid!\n"
            exit 1
        fi
    fi
    if [[ -n "$X10TH_MAILLIST" ]]; then
        # TODO: validate mail addresses
        __x10th_mailList="$X10TH_MAILLIST"
    fi
    if [[ -n $X10TH_BACKEND ]]; then
        if [[ $X10TH_BACKEND == "java" || $X10TH_BACKEND == "c++" ]]; then
            __x10th_backend=$X10TH_BACKEND
        else
            printf >&3 "${__x10th_progname}:Error ## X10TH_BACKEND - "
            printf >&3 "backend $X10TH_BACKEND is invalid!\n"
            exit 1
        fi
    fi
    if [[ -n $X10TH_JAVA ]]; then
        "$X10TH_JAVA" -version >/dev/null 2>&1
        if (( $? != 0 )); then
            printf >&3 "${__x10th_progname}:Error ## X10TH_JAVA - "
            printf >&3 "command $X10TH_JAVA is invalid!\n"
            exit 1
        else
            __x10th_java="$X10TH_JAVA"
        fi
    fi
    if [[ -n $X10TH_COMPILER ]]; then
        if [[ $X10TH_COMPILER == "xlc" || $X10TH_COMPILER == "gcc" ]]; then
            __x10th_compiler=$X10TH_COMPILER
        else
            printf >&3 "${__x10th_progname}:Error ## X10TH_COMPILER - "
            printf >&3 "compiler $X10TH_COMPILER is invalid!\n"
            exit 1
        fi
    fi
    if [[ -n "$X10TH_COMPOPTS" ]]; then
        __x10th_compopts="$X10TH_COMPOPTS"
    fi
    if [[ -n $X10TH_PGAS ]]; then
        if [[ ! -d $X10TH_PGAS ]]; then
            printf >&3 "${__x10th_progname}:Error ## X10TH_PGAS - "
            printf >&3 "directory $X10TH_PGAS must exist!\n"
            exit 1
        else
            __x10th_pgas=$X10TH_PGAS
        fi
    fi
    if [[ -n $X10TH_TRANSPORT ]]; then
        if [[ $X10TH_TRANSPORT == "sockets" || $X10TH_TRANSPORT == "lapi" ]]; then
            __x10th_transport=$X10TH_TRANSPORT
        else
            printf >&3 "${__x10th_progname}:Error ## X10TH_TRANSPORT - "
            printf >&3 "transport $X10TH_TRANSPORT is invalid!\n"
            exit 1
        fi
    fi
    if [[ -n $X10TH_RUNLEVEL ]]; then
        (( tmpInt = $X10TH_RUNLEVEL ))
        if (( $? != 0 || $tmpInt < 1 || $tmpInt > 9 )); then
            printf >&3 "${__x10th_progname}:Error ## X10TH_RUNLEVEL - "
            printf >&3 "runlevel $X10TH_RUNLEVEL is invalid!\n"
            exit 1
        else
            __x10th_runLevel=$tmpInt
        fi
    fi
    # environment variables that don't have any command line equivalents
    if [[ -n $X10TH_DO_VER17 ]]; then
        __x10th_do_ver17=yes
        __x10th_x10_svnroot="https://x10.svn.sf.net/svnroot/x10/branches/x10-1.7"
        __x10th_module_common="x10.common.17"
        __x10th_module_compiler="x10.compiler.p3"
        __x10th_module_cppbackend="x10.cppbackend.17"
        __x10th_module_runtime="x10.runtime.17"
        __x10th_x10_modules="$__x10th_module_common $__x10th_module_compiler \
                $__x10th_module_constraints $__x10th_module_cppbackend $__x10th_module_dist \
                $__x10th_module_runtime $__x10th_module_tests"
    fi
    if [[ -n $X10TH_CHECKOUT_ATTEMPTS ]]; then
        (( tmpInt = $X10TH_CHECKOUT_ATTEMPTS ))
        if (( $? == 0 )); then
            let '__x10th_checkout_attempts = tmpInt'
        fi
    fi
}

# parse the command line and update options
# usage: parseCmdLine args
# global variables used:
function parseCmdLine() {
    local -i tmpInt

    # extract the command-line option(s) and their values
    while (( $# > 0 )); do
        if [[ "$1"  == "-timeLimit" && $# -ge 2 ]]; then
            # this should take care of non-integer argument
            (( tmpInt = $2 ))
            if (( $? != 0 || $tmpInt < 0 )); then
                printf >&3 "${__x10th_progname}:Error ## time limit $2 is invalid!\n"
                printUsage 1 0
            else
                __x10th_timeLimit=$tmpInt
                shift 2
            fi
        elif [[ "$1" == "-logPath" && $# -ge 2 ]]; then
            if [[ ! -d $2 ]]; then
                printf >&3 "${__x10th_progname}:Error ## log directory $2 must exist!\n"
                printUsage 1 0
            else
                __x10th_logPath=$2
                shift 2
            fi
        elif [[ "$1" == "-workRoot" && $# -ge 2 ]]; then
            if [[ ! -d $2 ]]; then
                printf >&3 "${__x10th_progname}:Error ## work directory $2 must exist!\n"
                printUsage 1 0
            else
                __x10th_workRoot=$2
                __x10th_testRoot=${__x10th_workRoot}/x10.tests/examples
                __x10th_runFile=${__x10th_workRoot}/x10.tests/data/testrun.dat
                shift 2
            fi
        elif [[ "$1" == "-testRoot" && $# -ge 2 ]]; then
            if [[ ! -d $2 ]]; then
                printf >&3 "${__x10th_progname}:Error ## test directory $2 must exist!\n"
                printUsage 1 0
            else
                __x10th_testRoot=$2
                shift 2
            fi
        elif [[ "$1" == "-listFile" && $# -ge 2 ]]; then
            if [[ ! -r $2 ]]; then
                printf >&3 "${__x10th_progname}:Error ## list file $2 must exist and readable!\n"
                printUsage 1 0
            else
                __x10th_listFile=$2
                shift 2
            fi
        elif [[ "$1" == "-runFile" && $# -ge 2 ]]; then
            if [[ ! -r $2 ]]; then
                printf >&3 "${__x10th_progname}:Error ## run file $2 must exist and redable!\n"
                printUsage 1 0
            else
                __x10th_runFile=$2
                shift 2
            fi
        elif [[ "$1" == "-revision" && $# -ge 2 ]]; then
            svn list -r $2 $__x10th_x10_svnroot >/dev/null 2>&1
            if (( $? != 0 )); then
                printf >&3 "${__x10th_progname}:Error ## revision keyword $2 is invalid!\n"
                printUsage 1 0
            else
                __x10th_revision="$2"
                shift 2
            fi
        elif [[ "$1" == "-polyglot" && $# -ge 2 ]]; then
            svn list -r "$2" $__x10th_polyglot_svnroot >/dev/null 2>&1
            if (( $? != 0 )); then
                printf >&3 "${__x10th_progname}:Error ## polyglot revision keyword $2 is invalid!\n"
                printUsage 1 0
            else
                __x10th_polyglot="$2"
                shift 2
            fi
        elif [[ "$1" == "-rptFormat" && $# -ge 2 ]]; then
            if [[ "$2" == "text" || "$2" == "xml" ]]; then
                __x10th_rptFormat=$2
                shift 2
            else
                printf >&3 "${__x10th_progname}:Error ## report format $2 is invalid!\n"
                printUsage 1 0
            fi
        elif [[ "$1" == "-mailList" && $# -ge 2 ]]; then
            # TODO: validate mail addresses
            __x10th_mailList="$2"
            shift 2
        elif [[ "$1" == "-backend" && $# -ge 2 ]]; then
            if [[ "$2" == "java" || "$2" == "c++" ]]; then
                __x10th_backend=$2
                shift 2
            else
                printf >&3 "${__x10th_progname}:Error ## backend $2 is invalid!\n"
                printUsage 1 0
            fi
        elif [[ "$1" == "-java" && $# -ge 2 ]]; then
            "$2" -version >/dev/null 2>&1
            if (( $? != 0 )); then
                printf >&3 "${__x10th_progname}:Error ## java command $2 is invalid!\n"
                printUsage 1 0
            else
                __x10th_java="$2"
                shift 2
            fi
       elif [[ "$1" == "-compiler" && $# -ge 2 ]]; then
            if [[ "$2" == "xlc" || "$2" == "gcc" ]]; then
                __x10th_compiler=$2
                shift 2
            else
                printf >&3 "${__x10th_progname}:Error ## compiler $2 is invalid!\n"
                printUsage 1 0
            fi 
        elif [[ "$1" == "-compopts" && $# -ge 2 ]]; then
            __x10th_compopts="$2"
            shift 2
        elif [[ "$1" == "-pgas" && $# -ge 2 ]]; then
            if [[ ! -d $2 ]]; then
                printf >&3 "${__x10th_progname}:Error ## pgas directory $2 must exist!\n"
                printUsage 1 0
            else
                __x10th_pgas=$2
                shift 2
            fi
        elif [[ "$1" == "-transport" && $# -ge 2 ]]; then
            if [[ "$2" == "sockets" || "$2" == "lapi" ]]; then
                __x10th_transport=$2
                shift 2
            else
                printf >&3 "${__x10th_progname}:Error ## transport $2 is invalid!\n"
                printUsage 1 0
            fi
        elif [[ "$1" == "-runLevel" && $# -ge 2 ]]; then
            (( tmpInt = $2 ))
            if (( $? != 0 || $tmpInt < 1 || $tmpInt > 9 )); then
                printf >&3 "${__x10th_progname}:Error ## run level $2 is invalid!\n"
                printUsage 1 0
            else
                __x10th_runLevel=$tmpInt
                shift 2
            fi
        elif [[ "$1" == "-h" || "$1" == "-help" || "$1" == "--help" ]]; then
            printUsage 0 1
        elif [[ "$1" == "-v" || "$1" == "-version" || "$1" == "--version" ]]; then
            printVersion 0
        elif [[ "$1" == "-timeLimt" || "$1" == "-logPath" || "$1" == "-workRoot" ||\
                "$1" == "-testRoot" || "$1" == "-listFile" || "$1" == "-runFile" ||\
                "$1" == "-revision" || "$1" == "-polyglot" || "$1" == "-rptFormat" ||\
                "$1" == "-mailList" || "$1" == "-backend" || "$1" == "-java" ||\
                "$1" == "-compiler" || "$1" == "-compopts" || "$1" == "-pgas" ||\
                "$1" == "-transport" || "$1" == "-runLevel" ]]; then
            printf >&3 "${__x10th_progname}:Error ## Option $1 needs argument!\n"
            printUsage 1 0
        elif [[ "$1" == -* ]]; then
            printf >&3 "${__x10th_progname}:Error ## Unrecognized option ${1}!\n"
            printUsage 1 0
        else
            __x10th_list="$@"
            break
        fi
    done
}

# reconcile various options and stop regressing, if there are conflicts
# usage: resolveOptions
function resolveOptions() {
    local -i status
    # turn on various regression stages based on the final run level
    case $__x10th_runLevel in
        1)
            __x10th_do_checkout=yes
            ;;
        2)
            __x10th_do_checkout=yes
            __x10th_do_build=yes
            ;;
        3)
            __x10th_do_checkout=yes
            __x10th_do_build=yes
            __x10th_do_makedist=yes
            ;;
        4)
            __x10th_do_checkout=yes
            __x10th_do_build=yes
            __x10th_do_makedist=yes
            __x10th_do_regress=yes
            ;;
        5)
            __x10th_do_checkout=yes
            __x10th_do_build=yes
            __x10th_do_makedist=yes
            __x10th_do_regress=yes
            __x10th_do_report=yes
            ;;
        6)
            __x10th_do_checkout=yes
            __x10th_do_build=yes
            __x10th_do_makedist=yes
            __x10th_do_regress=yes
            __x10th_do_report=yes
            __x10th_do_mail=yes
            ;;
        7)
            __x10th_do_build=yes
            __x10th_do_regress=yes
            __x10th_do_report=yes
            __x10th_do_mail=yes
            ;;
        8)
            __x10th_do_build=yes
            __x10th_do_regress=yes
            __x10th_do_report=yes
            ;;
        9)
            __x10th_do_regress=yes
            __x10th_do_report=yes
            ;;
    esac

    # empty mailing list?
    if [[ "${__x10th_do_mail}" == "yes" && x"${__x10th_mailList}" == "x" ]]; then
        printf >&3 "${__x10th_progname}: Error ## run level ${__x10th_runLevel} requires mailing list!\n"
        exit 1
    fi

    # do the following checks for c++ backend only
    if [[ "${__x10th_backend}" == "c++" ]]; then
        # validate post-processing compiler
        if [[ "${__x10th_compiler}" == "xlc" &&
                ! ($(uname -s) == "AIX" || $(uname -s) == "Linux") ]]; then
            printf >&3 "${__x10th_progname}: Error ## this platform doesn't support the xlc compiler!\n"
            exit 1
        fi
        case "${__x10th_compiler}" in
            xlc)
                __x10th_cxx=mpCC_r
                ;;
            gcc)
                __x10th_cxx=g++
                ;;
        esac
        # is CXX on the command path?
        which ${__x10th_cxx} >/dev/null 2>&1
        if (( $? != 0 )); then
            printf >&3 "${__x10th_progname}: Error ## can't find ${__x10th_cxx} on the command path!\n"
            exit 1
        fi
        # test compile with the specified options
        echo "
#include <iostream>
using namespace std;
int main(void) {
cout << \"hello, world!\" << endl;
return 0;
}
" > /tmp/__x10th_test.cc
        ${__x10th_cxx} ${__x10th_compopts} /tmp/__x10th_test.cc -o /tmp/__x10th_test 2>/dev/null
        status=$?
        rm -f /tmp/__x10th_test.*
        if (( $status != 0 )); then
            printf >&3 "${__x10th_progname}: Error ## invalid compiler options ${__x10th_compopts}!\n"
            exit 1
        fi

        # validate pgas transport against the platform
        if [[ "${__x10th_transport}" == "lapi" &&
                ! ($(uname -s) == "AIX" || $(uname -s) == "Linux") ]]; then
            printf >&3 "${__x10th_progname}: Error ## this platform doesn't support the lapi transport!\n"
            exit 1
        fi
                
        # validate installed pgas headers and libraries
        # pgas directory not specified?
        if [[ x"${__x10th_pgas}" == "x" ]]; then
            printf >&3 "${__x10th_progname}: Error ## c++ backend requires installed pgas runtime!\n"
            exit 1
        fi
        # TODO: do sample pgas compilation once the x10rt api is fixed
        # check for pgas headers?
        if [[ ! -f "${__x10th_pgas}/include/x10/x10rt_api.h" ]]; then
            printf >&3 "${__x10th_progname}: Error ## can't locate pgas headers under ${__x10th_pgas}!\n"
            exit 1
        fi
        # check for pgas libraries?
        if [[ ! ( -f ${__x10th_pgas}/lib/libxlpgas_${__x10th_transport}.a ||
                -f ${__x10th_pgas}/lib/libxlpgas_${__x10th_transport}.so ) ]]; then
            printf >&3 "${__x10th_progname}: Error ## can't locate pgas libraries under ${__x10th_pgas}!\n"
            exit 1
        fi
    fi
    
}

# vim:tabstop=4:shiftwidth=4:expandtab:nu
