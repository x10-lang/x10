#!/bin/bash

#
# (c) Copyright IBM Corporation 2009-2014
#
# Harness to compile and execute a set of X10 test cases
# 

# display command-line help
# usage: printUsage excode detail
function printUsage {
    printf "\n=====> X10 Test Harness\n\n"
    printf "Usage: runTest.sh [-native] [-managed] [-opt] [-noopt] [-debug]\n"
    printf "    [-t|-timeOut [secs]]\n"
    printf "    [-report_dir directory]\n"
    printf "    [-l|-list \"test1 test2 ... testn\"]]\n"
    printf "    [-h|-help]\n\n"
    if [[ $2 > 0 ]]; then
	printf "Invoked with no arguments, this script runs the X10 test cases\n"
	printf " in the current directory and its subdirectories.\n\n"
	printf " Optional command line arguments include:\n"
        printf -- "-native\n"
	printf "  Run tests with Native X10 (default)\n\n"
        printf -- "-managed\n"
	printf "  Run tests with Managed X10\n\n"
        printf -- "-managed-cp <path>\n"
	printf "  Pass -cp <path> as an argument to the x10 script\n\n"
        printf -- "-managed-lib <path>\n"
	printf "  Pass -libpath <path> as an argument to the x10 script\n\n"
        printf -- "-nopt\n"
	printf "  Compile the test cases without optimization\n\n"
        printf -- "-opt\n"
	printf "  Compile the test cases with optimization (-O)\n\n"
        printf -- "-debug\n"
	printf "  Compile the test cases with debug support (-DEBUG)\n\n"
        printf -- "-resilient\n"
	printf "  Execute test cases across all resilient modes\n\n"
	printf -- "-t | -timeOut [secs]\n"
	printf "  Set timeout option for test case execution. This"
	printf " overrides\nthe default timeout value of 60 seconds.\n\n"
	printf -- "-report_dir dir\n"
	printf "  For each test case, generate an xml test result file in dir\n\n"
	printf -- "-listFile file\n"
	printf "  Test cases are found recursively starting at the top"
	printf " test\n directory, and are executed in the same order as"
	printf " they are found.\nThis option indicates a file containing"
	printf " a list of shell file\npatterns denoting the tests to run."
	printf "  The test list file can\nalso contain comment lines"
	printf " beginning with a \"#\" character,\nand blank lines for"
	printf " clarity.\n\n"
	printf -- "-l | -list test1 test2 ... testn\n"
	printf "  Alternatively, use this option to indicate the file"
	printf " pattern(s)\nfor the tests to run.  With these options,"
	printf " only the specified tests\nare run.\n\n"
	printf -- "-h | -help\n"
	printf "  Print this help message.\n\n"
    fi
    exit $1
}

# parse command line options and arguments
# exit code: 1
# usage: parseCmdLine args
function parseCmdLine {
    while (( $# > 0 )); do
	if [[ "$1" == "-timeOut" || "$1" == "-t" ]]; then
	    tctimeout=1
	    shift
	    if (( $# >= 1 )); then
		typeset tmp=$(echo $1 | cut -c 1)
		if [[ "$tmp" != "-" ]]; then
					# this takes care of non-integer argument
		    (( tctoutval = $1 ))
		    if (( $? != 0 || $tctoutval <= 0 )); then
			printf -- "\n[${prog}: err]: Timeout value $tctoutval is invalid\n\n"
			printUsage 1 0
		    fi
		    shift
		fi
	    fi
	elif [[ "$1" == "-report_dir" && $# -ge 2 ]]; then
	    tcreportdir=$2
	    shift 2
	elif [[ "$1" == "-debug" ]]; then
	    tccompiler_options="$tccompiler_options -DEBUG"
	    shift
	elif [[ "$1" == "-noopt" ]]; then
            # nothing to do
	    shift
	elif [[ "$1" == "-opt" ]]; then
	    tccompiler_options="$tccompiler_options -O"
	    shift
	elif [[ "$1" == "-x10lib" ]]; then
	    if (( $# >= 2 )); then
		tccompiler_options="$tccompiler_options -x10lib $2"
		shift 2
	    else
		printf "\n[${prog}: err]: Option $1 needs argument\n\n"
		printUsage 1 0
	    fi
	elif [[ "$1" == "-managed-cp" ]]; then
	    if (( $# >= 2 )); then
		managed_x10_extra_args="$managed_x10_extra_args -cp $2"
		shift 2
	    else
		printf "\n[${prog}: err]: Option $1 needs argument\n\n"
		printUsage 1 0
	    fi
	elif [[ "$1" == "-managed-lib" ]]; then
	    if (( $# >= 2 )); then
		managed_x10_extra_args="$managed_x10_extra_args -libpath $2"
		shift 2
	    else
		printf "\n[${prog}: err]: Option $1 needs argument\n\n"
		printUsage 1 0
	    fi
	elif [[ "$1" == "-resilient" ]]; then
	    tcresilient_modes="$tc_all_resilient_modes"
	    shift
	elif [[ "$1" == "-native" ]]; then
	    tcbackend="native"
	    shift
	elif [[ "$1" == "-managed" ]]; then
	    tcbackend="managed"
	    shift
	elif [[ "$1" == "-allow_zero_tests" ]]; then
	    tcallowzerotests="true"
	    shift
	elif [[ "$1" == "-listFile" && $# -ge 2 ]]; then
	    if [[ ! -r "$2" ]]; then
		printf "\n[${prog}: err]: List file $2 must exist & be readable\n"
		exit 1
	    fi
	    tcpatfile=$2
	    shift 2
	elif [[ "$1" == "-list" || "$1" == "-l" ]]; then
	    if (( $# >= 2 )); then
		tcpatlist="$2"
		shift 2
	    else
		printf "\n[${prog}: err]: Option $1 needs argument\n\n"
		printUsage 1 0
	    fi
	elif [[ "$1" == "-help" || "$1" == "-h" ]]; then
	    printUsage 0 1
	elif [[ "$1" == "-listFile" || "$1" == "-l" || "$1" == "-list" || "$1" == "-report_dir" ]]; then
	    printf "\n[${prog}: err]: Option $1 needs argument\n\n"
	    printUsage 1 0
	elif [[ "$1" == -* ]]; then
	    printf "\n[${prog}: err]: Unrecognized option $1\n\n"
	    printUsage 1 0
	else
	    printf "\n[${prog}: err]: Extraneous argument(s) $1\n\n"
	    printUsage 1 0
	fi
    done
    
    # need either test pattern list or file
    if [[ ! -z "$tcpatlist" && ! -z "$tcpatfile" ]]; then
	printf "\n[${prog}: err]: Conflicting test pattern specifications...\n\n"
	printUsage 1 0
    fi
    
    # extract pattern list from file
    if [[ ! -z "$tcpatfile" ]]; then
	cat $tcpatfile | while read -r line; do
	    line=$(echo $line | sed -e 's;^[ \t]+;;')
	    if [[ ! -z "$line" && "$line" != \#* ]]; then
		tcpatlist="$tcpatlist $line"
	    fi
	done
    fi
    
    # neither pattern file or list present
    # choose every valid x10 file under current directory
    if [[ -z "$tcpatlist" ]]; then
	tcpatlist="."
    fi
}

# generate list of tests for the specified pattern(s)
# usage: findTests testpat(s)
function findTests {
    local testPat="$1"
    local findpat=""
    local dirlist

    #eval "find $1 -type d -name '.svn' -prune -o -type f -name '*.x10' -print"
    if [[ -n "$testPat" && "${testPat}" != "." ]]; then
	eval "find $testPat -type d -name '.svn' -prune -o -type f -name '*.x10'  -print | sort"
    else
	dirlist="$(eval "find -type d -name '.svn' -prune -o -type d -name '*' -print | sort")"
	dirlist="$(echo $dirlist | tr '\n' ' ')"
	eval "find $dirlist -maxdepth 1 -type f -name '*.x10' -print"
    fi
}

# check whether the specified test case file is valid
# usage: isTestCase file.x10
function isTestCase {
    typeset file=$(basename $1)
    
    if [[ "${file#${file%\.*}}" != ".x10" ]]; then
	printf "\n[$prog: err]: ${file} need an .x10 extension\n"
	return 1
    fi

    if [[ ! -r $1 ]]; then
	printf "\n[$prog: err]: ${file} must exist & is readable\n"
	return 1
    fi

    ${EGREP} -q 'public[ ]+static[ ]+def[ ]+main' $1
    if [[ $? != 0 ]]; then
	printf "\n[$prog: err]: no \"public static def main\" method in ${file}\n"
	return 1
    fi

    if [[ "$tcbackend" == "native" ]]; then 
	${EGREP} -q 'MANAGED_X10_ONLY' $1
	if [[ $? == 0 ]]; then
	    printf "\n[$prog]: ${file} contains MANAGED_X10_ONLY directive\n"
	    return 1
	fi
    fi

    if [[ "$tcbackend" == "managed" ]]; then 
	${EGREP} -q 'NATIVE_X10_ONLY' $1
	if [[ $? == 0 ]]; then
	    printf "\n[$prog]: ${file} contains NATIVE_X10_ONLY directive\n"
	    return 1
	fi
    fi

    return 0
}

# usage: resolveParams
function resolveParams {
    # validation code
    case "${tctarget}" in
	*_MustFailCompile)
	    tcvcode=FAIL_COMPILE
	    ;;
	*_MustFailTimeout)
	    tcvcode=FAIL_TIMEOUT
	    ;;
	*)
	    tcvcode=SUCCEED
	    ;;
    esac
    if [[ "$tcbackend" == "managed" ]]; then 
	${EGREP} -q 'SKIP_MANAGED_X10' $1
	if [[ $? == 0 ]]; then
	    tcvcode=SKIPPED
	fi
    fi
    if [[ "$tcbackend" == "native" ]]; then 
	${EGREP} -q 'SKIP_NATIVE_X10' $1
	if [[ $? == 0 ]]; then
	    tcvcode=SKIPPED
	fi
    fi
    ${EGREP} -q 'RESILIENT_X10_ONLY' $1
    if [[ $? == 0 ]]; then
        tcresilient_x10_only=1
        if [[ "$tcresilient_modes" == "0" ]]; then
            tcvcode=SKIPPED
        fi
    else 
        tcresilient_x10_only=0
    fi

    # update expected counters
    case "${tcvcode}" in
	"SUCCEED")
	    let 'xtcpasscnt += 1'
	    ;;
	"FAIL_COMPILE")
	    let 'xtcfcompcnt += 1'
	    ;;
	"FAIL_TIMEOUT")
	    let 'xtcftoutcnt += 1'
	    ;;
	"SKIPPED")
	    let 'xtcskipcnt += 1'
	    ;;
    esac
}

# execute the given command line with timelimit
# usage: execTimeOut val cmd
function execTimeOut {
    typeset timeout=$1
    shift
    typeset outfile=$1
    shift
    printf "\n===> $@ >> $outfile &\n\n" 1>&2
    printf "\n" >> $outfile
    "$MYDIR"/newpgrp "$@" >> $outfile 2>&1 &
    typeset cmd_pid=$!
    "$MYDIR"/newpgrp "sleep $timeout && kill -9 -$cmd_pid && echo 'Timeout' >> $outfile" >/dev/null 2>&1 &
    typeset sleep_pid=$!
    wait $cmd_pid >/dev/null 2>&1
    typeset rc=$?
    kill -9 -$sleep_pid >/dev/null 2>&1
    return $rc
}

# the following needs to be defined outside main
MYDIR=$(cd $(dirname $0) && pwd)
X10_HOME=$X10_HOME
if [[ -z "$X10_HOME" ]]; then
    export X10_HOME=$(cd $MYDIR/../..; pwd)
fi

# defined separately from X10_HOME to simplify cygpath/windows path logic
if [[ "$(uname -s)" == CYGWIN* ]]; then
    tmp_test_root=$(cygpath -am $X10_HOME/x10.tests)
else
    tmp_test_root="$X10_HOME/x10.tests"
fi
export X10_TEST_DIR="$tmp_test_root"

# program name
prog=runTest.sh

# platform independent abstraction for certain commands
EGREP=egrep
egrep --version 2>/dev/null 1>/dev/null
if [[ $? == 0 && $(uname -s) != CYGWIN* && $(uname -s) != Linux* ]]; then
    EGREP="egrep -E"
fi

# generate unique timestamp (tctimestamp)
tcdate=$(date '+%Y-%m-%d')
tctime=$(date '+%H:%M:%S')
tctimestamp="$(echo $tcdate | sed -e 's;-;;g').$(echo $tctime | sed -e 's;:;;g')"

# unique name for temporary directory (tctmpdir)
if [[ -z "${TMPDIR}" ]]; then
    TMPDIR=/tmp
fi
tctmpdir=${TMPDIR}/${prog}.$$.${tctimestamp}
mkdir -p $tctmpdir

tcreportdir=$tctmpdir

# default values
DEFAULT_TIMEOUT=360
DEFAULT_LOGPATH="log"
DEFAULT_NPLACES=2

# test case globals

# test harness current run state
# could be one of:
# PARSING_CMDLINE, LIST_PREPARATION, TEST_PROCESSING,
# REPORT_GENERATION, UNKNOWN_STATE
thrunstate="UNKNOWN_STATE"

# backend: either native or managed. Default to native
tcbackend="native"

# resiliency modes
tc_all_resilient_modes="0 1 12 22 99"
tc_default_resilient_mode="0"
tcresilient_modes="$tc_default_resilient_mode"
typeset -i tcresilient_x10_only=0

# enable/disable timeout option
# default: enable
typeset -i tctimeout=1

# default timeout value, if timeout is enabled
typeset -i tctoutval=$DEFAULT_TIMEOUT
typeset -i tccomptout=300

# default log path, where log file will be created
typeset tclogpath=$DEFAULT_LOGPATH

# extra x10c/x10c++ options 
typeset tccompiler_options=""

# test case pattern file
# default: none
typeset tcpatfile=""

# test case: ok to find no tests?
# default: false
typeset tcallowzerotests="false"

# test pattern list
# default: none
typeset tcpatlist=""

# various micro counters
# total number of test case files considered
tctotalcnt=0
# number of test cases passed
tcpasscnt=0
# number of test cases failed
tcfailcnt=0
# number of test case files processed so far
tcproccnt=0
# number of test cases having invalid validation code
tcfvcodecnt=0

# total number of valid test cases
tcvalidcnt=0

# number of test cases skipped compiled
tcskipcnt=0

# number of test cases successfully compiled
tccompcnt=0
# number of expected compilation failures
xtcfcompcnt=0
# number of actual compilation failures
tcfcompcnt=0

# number of test cases successfully executed
tcexeccnt=0
# number of test cases skipped
tcsexeccnt=0
# number of actual execution failures
tcfexeccnt=0

# number of expected timeout failures
xtcftoutcnt=0
# number of actual timeout failures
tcftoutcnt=0
# number of expected successes
xtcpasscnt=0
# number of expected skips
xtcskipcnt=0

# initialize test case globals
# usage: init args
function init {
    # parse command-line arguments
    thrunstate=PARSING_CMDLINE
    parseCmdLine "$@"

    # ensure that the reportdir exists
    mkdir -p $tcreportdir
}

function cleanup {
    if [[ -d ${tctmpdir} ]]; then
	rm -rf ${tctmpdir}
    fi
}

function junitLog {
    __jen_test_end_time=$(perl -e 'print time;')
    let '__jen_test_duration = __jen_test_end_time - __jen_test_start_time'
    let '__jen_compile_duration = __jen_compile_end_time - __jen_compile_start_time'
    let '__jen_total_duration = __jen_compile_duration + __jen_test_duration'

    # testsuite header
    let '__jen_test_id += 1'
    JUFILE="${tcreportdir}/test.${__jen_test_id}.xml"
    printf "<testsuite\n" > $JUFILE
    printf "\tid=\"${__jen_test_id}\"\n" >> $JUFILE
    printf "\tpackage=\"${__jen_current_group}\"\n" >> $JUFILE
    printf "\tname=\"${__jen_test_name}\"\n" >> $JUFILE
    printf "\ttimestamp=\"${__jen_test_timestamp}\"\n" >> $JUFILE
    printf "\thostname=\"${__jen_hostname}\"\n" >> $JUFILE
    printf "\ttime=\"${__jen_total_duration}\"\n" >> $JUFILE
    printf "\ttests=\"1\"\n" >> $JUFILE

    case "${__jen_test_result}" in
	SUCCESS)
	    printf "\tfailures=\"0\"\n" >> $JUFILE
	    printf "\tskipped=\"0\"\n" >> $JUFILE
	    ;;
	SKIPPED)
	    printf "\tfailures=\"0\"\n" >> $JUFILE
	    printf "\tskipped=\"1\"\n" >> $JUFILE
	    ;;
	*)
	    printf "\tfailures=\"1\"\n" >> $JUFILE
	    printf "\tskipped=\"0\"\n" >> $JUFILE
	    ;;
    esac
    printf "\terrors=\"0\" >\n" >> $JUFILE
    printf "\t<properties></properties>\n" >> $JUFILE

    # testcase (trivial...1 per test suite)
    printf "\t<testcase classname=\"${__jen_test_name}\" name=\"${1}\" time=\"${__jen_test_duration}\">\n" >> $JUFILE
    case "${__jen_test_result}" in
	SUCCESS)
	    ;;
	SKIPPED)
	    printf "\t\t<skipped type=\"${__jen_test_result}\" message=\"${__jen_test_result_explanation}\"/>\n" >> $JUFILE
	    ;;
	*)
	    printf "\t\t<failure type=\"${__jen_test_result}\" message=\"${__jen_test_result_explanation}\"/>\n" >> $JUFILE
	    ;;
    esac
    printf "\t</testcase>\n" >> $JUFILE

    printf "\t<system-out>\n" >> $JUFILE
    perl -pe 's/&/\&amp;/g;
	        s/</\&lt;/g;
	        s/>/\&gt;/g;
	        s/"/\&quot;/g;
                s/'"'"'/\&apos;/g;
	        s/([^[:print:]\t\n\r])/sprintf("\&#x%04x;", ord($1))/eg' $2 $3 >> $JUFILE
    if [[ "${__jen_test_exit_code}" != "0" ]]; then
	printf "\n\tTest exited with non-zero return code ${__jen_test_exit_code}\n\n"  >> $JUFILE
    fi
    printf "\t</system-out>\n" >> $JUFILE
    # TODO: include system-err in file
    printf "\t<system-err></system-err>\n" >> $JUFILE
    printf "</testsuite>\n" >> $JUFILE
}

# main routine that invokes the rest
function main {
    # log invocation options
    printf "\n<<Invocation Options>>\n"
    printf "\nGlobal Timeout: "
    if [[ $tctimeout == 1 ]]; then
	printf "Enabled\n"
    else
	printf "Disabled\n"
    fi
    printf "Global Timeout Value: $tctoutval\n"
    printf "\nTestcase Pattern List: $tcpatlist\n"

    # set test case build environment
    X10CPP=$X10CPP
    if [[ -z "$X10CPP" ]]; then
	X10CPP=$X10_HOME/x10.dist/bin/x10c++
    fi
    if [[ ! -f $X10CPP ]]; then
	printf "\n[$prog: err]: unable to locate x10c++ compiler!\n"
	exit 2
    fi

    X10C=$X10C
    if [[ -z "$X10C" ]]; then
	X10C=$X10_HOME/x10.dist/bin/x10c
    fi
    if [[ ! -f $X10C ]]; then
	printf "\n[$prog: err]: unable to locate x10c compiler!\n"
	exit 2
    fi

    RUN_X10=$RUN_X10
    if [[ -z "$RUN_X10" ]]; then
	RUN_X10=$X10_HOME/x10.dist/bin/runx10
    fi
    if [[ ! -f $RUN_X10 ]]; then
	printf "\n[$prog: err]: unable to locate runx10 script!\n"
	exit 2
    fi

    if [[ ! -f $X10_HOME/x10.dist/stdlib/libx10.properties ]]; then
	printf "\n[$prog: err]: unable to libx10.properties!\n"
	exit 2
    fi

    # prepare test case list
    # exit code: 3
    thrunstate=LIST_PREPARATION
    printf "\n<<Testcase List Preparation>>\n"
    declare -a tclist=($(findTests "$tcpatlist"))
    if [[ ${#tclist[*]} == 0 ]]; then
	if [[ "$tcallowzerotests" == "true" ]]; then
	    printf "\n[$prog] zero tests found under $(basename $(pwd))\n"
	    exit 0
	else
	    printf "\n[$prog: err]: zero tests found under $(basename $(pwd))\n"
	    exit 3
	fi
    fi
    # printf "\n===> ${tclist[*]}\n\n"
    tctotalcnt=${#tclist[*]}


    thrunstate=TEST_PROCESSING
    printf "\n<<Testcase Processing>>\n"
    printf "\n# Legend:\n"
    printf "#\tC - compilation step\n"
    printf "#\tE - execution step\n\n"
    printf "#\tX - test failed\n"
    printf "#\tY - test passed\n"
    printf "#\tS - test skipped\n"

    for tc in ${tclist[*]}; do
	let 'tcproccnt += 1'
	printf "\n((${tcproccnt} of ${tctotalcnt})) [$(basename $(dirname $tc)):$(basename $tc)]"
	printf "\n===>((${tcproccnt})) [$(basename $tc)]\n\n" 1>&2
	# pre-validate the test case
	isTestCase $tc
	if [[ $? != 0 ]]; then
	    continue
	fi
	let 'tcvalidcnt += 1'

	__jen_compile_start_time=$(perl -e 'print time;')
	__jen_test_timestamp=$(date "+%FT%T")

	# create the test root
	tctarget=$(basename $tc | sed -e 's;.x10;;')
	local tPkg=$(sed -ne 's|\.|/|g' -e 's|^[[:space:]]*package \([^;]*\);|\1|p' "$tc")

	local className="${tPkg}/${tctarget}"
	className=${className#\.\/}
	className=`echo "$className" | sed -e 's/\//\./g'`
	className=${className#\.}
	__jen_test_name="$className"
	local testDir=$(dirname $tc)
	local tDirSlash=${testDir%/$tPkg}
	local tDir=${tDirSlash#\.\/}
	tDir=`echo "$tDir" | sed -e 's/\//\./g'`
	__jen_current_group="$tDir"
	if [[ -d ${tcroot} ]]; then
	    rm -rf ${tcroot}
	fi
	tcroot=$tctmpdir/$tctarget
	mkdir -p $tcroot

	# resolve test case parameters
	# tcvcode
	resolveParams $tc

	if [[ "$tcvcode" == "SKIPPED" ]]; then
	    skip_reason=$(sed -ne 's|^[[:space:]]*//[[:space:]]*SKIP.*\:[[:space:]]*\(.*\)|\1|p' $tc)
	    __jen_test_result_explanation="${className} Skipped. ${skip_reason}"
	    __jen_test_result="SKIPPED"
	    printf " +S [SKIPPED] ${skip_reason}"
	    junitLog "main" /dev/null /dev/null
	    let 'tcskipcnt += 1'
	    continue
	fi

	# try & generate sources
	printf " +C [COMPILATION]"
	extra_opts="$(sed -ne 's|^[[:space:]]*//[[:space:]]*OPTIONS*\:[[:space:]]*\(.*\)|\1|p' $tc)"
	extra_sourcepath="$(sed -ne 's|^[[:space:]]*//[[:space:]]*SOURCEPATH*\:[[:space:]]*\(.*\)|\1|p' $tc)"
	if [[ -n "$extra_sourcepath" ]]; then
	    if [[ "$(uname -s)" == CYGWIN* ]]; then
		extra_sourcepath=$(cygpath -am $X10_HOME/$extra_sourcepath)
		extra_sourcepath_arg="-sourcepath \"$extra_sourcepath\""
	    else
		extra_sourcepath_arg="-sourcepath $X10_HOME/$extra_sourcepath"
	    fi
	else
	    extra_sourcepath_arg=""
	fi
	__jen_test_x10c_sourcepath="$tcroot"
	__jen_test_x10c_classpath="${EXTRA_CLASSPATH}"
	__jen_test_x10c_directory="$testDir"
	if [[ "$(uname -s)" == CYGWIN* ]]; then
	    if [[ "$tcbackend" == "native" ]]; then
		comp_cmd="${X10CPP} $extra_opts $tccompiler_options -t -v -report postcompile=1 -CHECK_INVARIANTS=true -MAIN_CLASS=$className -o \"$(cygpath -am $tcroot)/$tctarget\" -sourcepath \"$(cygpath -am $X10_HOME/x10.tests/tests/$tDirSlash)\" -sourcepath \"$(cygpath -am $X10_HOME/x10.tests/tests/$testDir)\" -sourcepath \"$(cygpath -am $X10_HOME/x10.tests/tests/x10lib)\" $extra_sourcepath_arg -d \"$(cygpath $tcroot)\" $tc"
	    else
		comp_cmd="${X10C} $extra_opts $tccompiler_options -t -v -report postcompile=1 -CHECK_INVARIANTS=true -MAIN_CLASS=$className -sourcepath \"$(cygpath -am $X10_HOME/x10.tests/tests/$tDirSlash)\" -sourcepath \"$(cygpath -am $X10_HOME/x10.tests/tests/$testDir)\" -sourcepath \"$(cygpath -am $X10_HOME/x10.tests/tests/x10lib)\"  $extra_sourcepath_arg -d \"$(cygpath -am $tcroot)\" $tc"
	    fi
	else
	    if [[ "$tcbackend" == "native" ]]; then
		comp_cmd="${X10CPP} $extra_opts $tccompiler_options -t -v -report postcompile=1 -CHECK_INVARIANTS=true -MAIN_CLASS=$className -o $tcroot/$tctarget -sourcepath $X10_HOME/x10.tests/tests/$tDirSlash -sourcepath $X10_HOME/x10.tests/tests/$testDir -sourcepath $X10_HOME/x10.tests/tests/x10lib  $extra_sourcepath_arg -d $tcroot $tc"
	    else
		comp_cmd="${X10C} $extra_opts $tccompiler_options -t -v -report postcompile=1 -CHECK_INVARIANTS=true -MAIN_CLASS=$className -sourcepath $X10_HOME/x10.tests/tests/$tDirSlash -sourcepath $X10_HOME/x10.tests/tests/$testDir -sourcepath $X10_HOME/x10.tests/tests/x10lib  $extra_sourcepath_arg -d $tcroot $tc"
	    fi
	fi
	tccompdat=${tcroot}/${tctarget}.comp
	printf "\n****** $tDir $className ******\n\n" >> $tccompdat

	__jen_test_x10_command=""
	execTimeOut $tccomptout $tccompdat "${comp_cmd}"
	rc=$?
	cat ${tccompdat} 1>&2
	if [[ $rc != 0 && "$tcvcode" == "FAIL_COMPILE" ]]; then
	    let 'tcfcompcnt += 1'
	    __jen_test_exit_code=$rc
            ${EGREP} "Exception in thread" $tccompdat >/dev/null 2>&1
            if [[ $? == 0 ]]; then
                printf " *** X ***"
                let 'tcfailcnt += 1'
                printf "\n[$prog: err]: compile time exception for ${className}\n"
                __jen_test_result_explanation="${className} did not meet expectation: expected=MustFailCompile actual=FailCompileWithException (exception in thread main)."
                __jen_test_result="FAILURE"
                printf "\n****** $tDir $className failed: compile time exception\n" >> $tccompdat
            else
		printf " *** Y ***"
		let 'tcpasscnt += 1'
		__jen_test_result_explanation="${className} met expectation: MustFailCompile."
		__jen_test_result="SUCCESS"
		printf "\n****** $tDir $className succeeded.\n" >> $tccompdat
            fi
	    junitLog "main" $tccompdat /dev/null
	    continue
	elif [[ $rc == 0 && "$tcvcode" == "FAIL_COMPILE" ]]; then
	    printf " *** X ***"
	    let 'tcfvcodecnt += 1'
	    let 'tcfailcnt += 1'
	    printf "\n[$prog: err]: invalid validation code for ${className}\n"
	    __jen_test_result_explanation="${className} did not meet expectation: expected=MustFailCompile actual=Succeed (invalid validation code)."
	    __jen_test_result="FAILURE"
	    __jen_test_exit_code=42
	    printf "\n****** $tDir $className failed: compile\n" >> $tccompdat
	    junitLog "main" $tccompdat /dev/null
	    continue
	elif [[ $rc != 0 && "$tcvcode" != "FAIL_COMPILE" ]]; then
	    let 'tcfcompcnt += 1'
	    let 'tcfailcnt += 1'
	    printf " *** X ***"
	    printf "\n[$prog: err]: can't compile ${className}\n"
	    __jen_test_result_explanation="${className} did not meet expectation: expected=Succeed actual=FailCompile (compilation failed)."
	    __jen_test_result="FAILURE"
	    __jen_test_exit_code=$rc
	    printf "\n****** $tDir $className failed: compile\n" >> $tccompdat
	    junitLog "main" $tccompdat /dev/null
	    continue
	fi
	printf "\n++++++ Compilation succeeded.\n" >> $tccompdat
	let 'tccompcnt += 1'

	__jen_compile_end_time=$(perl -e 'print time;')

	# extract additional execution details, if available
	numplaces_annotation="$(sed -ne 's|^[[:space:]]*//[[:space:]]*NUM_PLACES*\:[[:space:]]*\(.*\)|\1|p' $tc)"
	my_nplaces=$DEFAULT_NPLACES
	if [[ -n "$numplaces_annotation" ]]; then
	    my_nplaces=$numplaces_annotation
	fi

	# DAVE: 1/20/14 -- disabling this code block as it isn't clear to me why 
	#       it is needed and I'm wondering if it is causing spurious timeout 
	#       failures to be reported on the MacOS automated testing...
	# if [[ "$(uname -s)" == "Darwin" ]]; then
        #     pid_list=`/usr/sbin/lsof -t -i ':21053'`
	#     if (( $? == 0 )); then
	# 	for pid in $pid_list
	# 	do
	# 	    kill -9 $pid 2>/dev/null
	# 	done
	#     fi
	# fi

        # run the target for each resiliency mode
	for jen_resiliency_mode in $tcresilient_modes
	do
	    case "${jen_resiliency_mode}" in
		0)
		    mode_name="main"
		    ;;
		1) 
		    mode_name="resilient_x10"
		    ;;
		11) 
		    mode_name="place_zero_resilient_finish"
		    ;;
		12) 
		    mode_name="hc_resilient_finish"
		    ;;
		22) 
		    mode_name="hc_opt_resilient_finish"
		    ;;
		99) 
		    mode_name="resilient_x10rt"
		    ;;
		*)
		    mode_name="resilient_mode_${jen_resiliency_mode}"
		    ;;
	    esac

	    if [[ $tcresilient_x10_only == 1 && ( "$mode_name" == "main" || "$mode_name" == "resilient_x10rt" ) ]]; then
		printf "\nSupressing execution of RESILIENT_X10_ONLY test case in mode $mode_name\n";
		continue;
	    fi

            if [[ "$tcbackend" == "native" && ( "$mode_name" == "hc_resilient_finish" || "$mode_name" == "hc_opt_resilient_finish" ) ]]; then
		printf "\nSkipping hazelcast-based mode for Native X10\n";
		continue;
	    fi

	    __jen_test_start_time=$(perl -e 'print time;')

	    # the actual output will be logged here
	    tcoutdat=${tcroot}/${tctarget}.${jen_resiliency_mode}.out

	    if [[ "$tcbackend" == "native" ]]; then
		if [[ "$(uname -s)" == CYGWIN* ]]; then
		    run_cmd="X10_RESILIENT_MODE=${jen_resiliency_mode} X10_NPLACES=${my_nplaces} X10_HOSTLIST=localhost $RUN_X10 ./${tctarget}.exe"
		else
		    run_cmd="X10_RESILIENT_MODE=${jen_resiliency_mode} X10_NPLACES=${my_nplaces} X10_HOSTLIST=localhost ./${tctarget}"
		fi
	    else
		managed_x10_extra_resiliency_args=""
		if [[ "$jen_resiliency_mode" != "0" ]]; then
		    if [[ ( "$jen_resiliency_mode" == "12" || "$jen_resiliency_mode" == "22" ) ]]; then
			managed_x10_extra_resiliency_args="-DX10RT_IMPL=JavaSockets -DX10RT_DATASTORE=Hazelcast"
		    else
			managed_x10_extra_resiliency_args="-DX10RT_IMPL=JavaSockets"
		    fi
		fi
		run_cmd="X10_RESILIENT_MODE=${jen_resiliency_mode} X10_NPLACES=${my_nplaces} X10_HOSTLIST=localhost $X10_HOME/x10.dist/bin/x10 -ms128M -mx512M ${managed_x10_extra_resiliency_args} ${managed_x10_extra_args} -t -v -J-ea ${className}"
	    fi
	    printf "\n${run_cmd}\n" >> $tcoutdat

	    __jen_test_x10_timeout="$tctoutval"
	    if [[ $tctimeout == 0 ]]; then
		__jen_test_x10_command="$(echo $run_cmd >> $tcoutdat)"
	    else
		__jen_test_x10_command="$(echo execTimeOut $tctoutval $tcoutdat \"${run_cmd}\")"
	    fi

	    printf "\n ++ E [EXECUTION]"
	    ( \
		cd $tcroot; \
		if [[ $tctimeout == 0 ]]; then \
		printf "\n===> $run_cmd >> $tcoutdat\n\n" 1>&2; \
		$run_cmd >> $tcoutdat; \
		else \
		execTimeOut $tctoutval $tcoutdat "${run_cmd}"; \
		fi;
	    )
	    rc=$?
	    if [[ $rc == 0 && $tcvcode == "SUCCEED" ]]; then
		let 'tcexeccnt += 1'
		let 'tcpasscnt += 1'
		printf " *** Y ***"
		__jen_test_result_explanation="${className} met expectation: Succeed."
		__jen_test_result="SUCCESS"
		__jen_test_exit_code=$rc
		printf "\n****** $tDir $className succeeded.\n" >> $tcoutdat
		junitLog $mode_name $tccompdat $tcoutdat
		continue
	    fi
	    if [[ $tctimeout == 0 ]]; then
		if [[ $rc > 0 && $tcvcode == "SUCCEED" ]]; then
		    let 'tcfailcnt += 1'
		    let 'tcfexeccnt += 1'
		    printf " *** X ***"
		    printf "\n[$prog: err]: failed to execute ${className}\n"
		    __jen_test_result_explanation="${className} did not meet expectation: expected=Succeed actual=FailRun."
		    __jen_test_result="FAILURE"
		    __jen_test_exit_code=$rc
		    printf "\n****** $tDir $className failed: run\n" >> $tcoutdat
		    junitLog $mode_name $tccompdat $tcoutdat
		    continue
		else
		    printf " *** X ***"
		    let 'tcfailcnt += 1'
		    let 'tcfvcodecnt += 1'
		    printf "\n[$prog: err]: invalid validation code ${className}\n"
		    __jen_test_result_explanation="${className} did not meet expectation: expected=Succeed actual=FailRun (invalid validation code)."
		    __jen_test_result="FAILURE"
		    __jen_test_exit_code=42
		    printf "\n****** $tDir $className failed: run\n" >> $tcoutdat
		    junitLog $mode_name $tccompdat $tcoutdat
		    continue
		fi
	    else
		if [[ $rc > 128 && $tcvcode == "SUCCEED" ]]; then
		    printf " *** X ***"
		    let 'tcfailcnt += 1'
		    let 'tcftoutcnt += 1'
		    let 'tcfexeccnt += 1'
		    printf "\n[$prog: err]: ${className} is killed due to timeout\n"
		    __jen_test_result_explanation="${className} did not meet expectation: expected=Succeed actual=TimeOut (killed due to timeout)."
		    __jen_test_result="FAILURE"
		    __jen_test_exit_code=$rc
		    printf "\n****** $tDir $className failed: timeout\n" >> $tcoutdat
		    junitLog $mode_name $tccompdat $tcoutdat
		    continue
		elif [[ $rc > 0 && $tcvcode == "SUCCEED" ]]; then
		    printf " *** X ***"
		    let 'tcfailcnt += 1'
		    let 'tcfexeccnt += 1'
		    printf "\n[$prog: err]: failed to execute ${className}\n"
		    __jen_test_result_explanation="${className} did not meet expectation: expected=Succeed actual=FailRun (execution failed)."
		    __jen_test_result="FAILURE"
		    __jen_test_exit_code=$rc
		    printf "\n****** $tDir $className failed: run\n" >> $tcoutdat
		    junitLog $mode_name $tccompdat $tcoutdat
		    continue
		elif [[ $rc == 0 && $tcvcode == "FAIL_TIMEOUT" ]]; then
		    printf " *** X ***"
		    let 'tcfailcnt += 1'
		    let 'tcfvcodecnt += 1'
		    printf "\n[$prog: err]: invalid validation code ${tcvcode}\n"
		    __jen_test_result_explanation="${className} did not meet expectation: expected=MustFailTimeOut actual=Succeed (invalid validation code)."
		    __jen_test_result="FAILURE"
		    __jen_test_exit_code=42
		    printf "\n****** $tDir $className failed: run\n" >> $tcoutdat
		    junitLog $mode_name $tccompdat $tcoutdat
		    continue
		elif [[ $rc > 128 && $tcvcode == "FAIL_TIMEOUT" ]]; then
		    printf " *** Y ***"
		    let 'tcftoutcnt += 1'
		    let 'tcfexeccnt += 1'
		    let 'tcpasscnt += 1'
		    __jen_test_result_explanation="${className} met expectation: MustFailTimeOut."
		    __jen_test_result="SUCCESS"
		    __jen_test_exit_code=0
		    printf "\n****** $tDir $className succeeded.\n" >> $tcoutdat
		    junitLog $mode_name $tccompdat $tcoutdat
		    continue
		elif [[ $rc > 0 && $tcvcode == "FAIL_TIMEOUT" ]]; then
		    printf " *** X ***"
		    let 'tcfailcnt += 1'
		    let 'tcftoutcnt += 1'
		    let 'tcfexeccnt += 1'
		    printf "\n[$prog: err]: failed to execute ${className}\n"
		    __jen_test_result_explanation="${className} did not meet expectation: expected=MustFailTimeOut actual=FailRun (execution failed)."
		    __jen_test_result="FAILURE"
		    __jen_test_exit_code=$rc
		    printf "\n****** $tDir $className failed: run\n" >> $tcoutdat
		    junitLog $mode_name $tccompdat $tcoutdat
		    continue
		fi
	    fi
	done
    done

    # Simple summary report (visible at end of console output on test job)
    thrunstate=REPORT_GENERATION
    printf "\n\n<<Report Generation>>\n"
    printf "\n\n======================================================================\n\n"
    printf "                     X10 Test Harness :: Run Report\n\n"
    printf "\n**QUEUE          : "
    printf "${tcvalidcnt}\n"

    printf "\n**SKIPPED        : "
    printf "${xtcskipcnt} Expected Skipped"
    printf " / ${tcskipcnt} Actual Skipped\n"

    printf "\n**COMPILATION    : "
    printf "${tccompcnt} Successes / ${xtcfcompcnt} Expected Failures"
    printf " / ${tcfcompcnt} Actual Failures\n"

    printf "\n**EXECUTION      : "
    printf "${tcexeccnt} Successes"
    printf " / ${tcsexeccnt} Skipped"
    printf " / ${tcfexeccnt} Actual Failures\n"

    printf "\n**TIME-OUT       : "
    printf "${xtcftoutcnt} Expected Failures / ${tcftoutcnt} Actual"
    printf " Failures\n"

    printf "\n**MISCELLANEOUS  : "
    printf "${tcfvcodecnt} Invalid Validation Codes\n"

    printf "\n**CONCLUSION     : "
    printf "${tcpasscnt} Successes / ${tcskipcnt} Skipped / ${tcfailcnt} Failures\n"
    printf "\n======================================================================\n\n"
}

__jen_test_id=0
__jen_test_name=""
__jen_test_x10_command=""
__jen_test_parameters=""
__jen_test_exit_code=""
__jen_test_start_time=""
__jen_test_end_time=""
__jen_test_duration=""
__jen_test_result=""
__jen_test_result_explanation=""
__jen_test_output=""
__jen_test_x10c_sourcepath=""
__jen_test_x10c_classpath=""
__jen_test_x10c_directory=""
__jen_test_x10_timeout=""
__jen_hostname=$(hostname)
__jen_compile_start_time=""
__jen_compile_end_time=""
__jen_compile_duration=""

init "$@"
main 
cleanup
exit 0

# vim:tabstop=4:shiftwidth=4:expandtab
