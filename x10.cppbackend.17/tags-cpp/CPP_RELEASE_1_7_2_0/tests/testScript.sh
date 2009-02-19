#!/usr/bin/ksh93

#
# (c) Copyright IBM Corporation 2008
#
# $Id: testScript.sh,v 1.1.1.1 2009-02-19 14:32:54 pvarma Exp $
# This file is part of X10/C++ Test Harness.

# archive both run and error log files
# usage: archiveLogs
function archiveLogs {
	if [[ -d "${X10CPP_LOGLOC}" ]]; then
		# run log
		if [[ -s ${tcrlogfile} ]]; then
			cp -f -p ${tcrlogfile} "${X10CPP_LOGLOC}/${X10CPP_RUNLOG}"
			( \
				typeset mesg="x10/c++ compiler test harness run log file"; \
				mesg="${mesg} @ $(hostname) on ${tcdate} ${tctime} hrs"; \
				cd ${X10CPP_LOGLOC}; \
				cvs -Q -d "${X10CPP_CVSROOT}" add ${X10CPP_RUNLOG} >/dev/null 2>&1;
				cvs -Q -d "${X10CPP_CVSROOT}" commit -l -m "${mesg}" ${X10CPP_RUNLOG} >/dev/null 2>&1; \
			)
		fi
		# error log
		if [[ -s ${tcelogfile} ]]; then
			cp -f -p ${tcelogfile} "${X10CPP_LOGLOC}/${X10CPP_ERRLOG}"
			( \
				typeset mesg="x10/c++ compiler test harness error log file"; \
				mesg="${mesg} @ $(hostname) on ${tcdate} ${tctime} hrs"; \
				cd ${X10CPP_LOGLOC}; \
				cvs -Q -d "${X10CPP_CVSROOT}" add ${X10CPP_ERRLOG} >/dev/null 2>&1;
				cvs -Q -d "${X10CPP_CVSROOT}" commit -l -m "${mesg}" ${X10CPP_ERRLOG} >/dev/null 2>&1; \
			)
		fi
	fi
}

# log file header
# usage: writeLogHead
function writeLogHead {
	printf "############################################################\n"
	printf "# X10/C++ TEST HARNESS\n"
	printf "# (c) Copyright IBM Corporation 2008\n"
	printf "# Host: $(hostname)\n"
	printf "# Start Time: $(date)\n"
	printf "# Launch Directory: $(pwd)\n"
	printf "############################################################\n\n"
}

# log file tail
# usage: writeLogTail
function writeLogTail {
	printf "\n############################################################\n"
	printf "# End Time: $(date)\n"
	printf "############################################################\n\n"
}

# mail results
# usage: mailResults
function mailResults {
	# set mail subject
	case ${thrunstate} in
		"PARSING_CMDLINE")
			mesg="Failed Command Line Parsing"
			;;
		"SANITY_CHECK")
			mesg="Failed Sanity Check"
			;;
		"MODULE_UPDATE")
			mesg="Failed Module Update"
			;;
		"MODULE_BUILD")
			mesg="Failed Module Build"
			;;
		"LIST_PREPARATION")
			mesg="Failed List Preparation"
			;;
		"TEST_PROCESSING")
			mesg="Failed Test Processing"
			;;
		"REPORT_GENERATION")
			mesg="${tctotalcnt} Tests/${tcpasscnt} Passed/${tcfailcnt} Failures"
			;;
		"UNKNOWN_STATE")
			mesg="Unknown Error"
			;;
	esac
	# let's stick with the single timestamp
	mailsubj="X10/C++ Test Harness [Started ${tcdate}"
	mailsubj="${mailsubj} ${tctime} @ $(hostname)]: ${mesg}"
	# send mail
	if [[ $tcverbose == 1 ]]; then
		printf "\nmail -s "${mailsubj}" -c "${tcmaillist}" ${tcmailaddr} < ${tcrlogfile}\n\n"
	fi
	mail -s "${mailsubj}" -c "${tcmaillist}" ${tcmailaddr} < ${tcrlogfile}
}

# display command-line help
# usage: printUsage excode detail
function printUsage {
	printf "\n=====> X10/C++ Test Harness\n\n"
	printf "Usage: testScript.sh [-t|-timeOut [secs]] [-f|-force]\n"
	printf "    [-logPath dir] [[-listFile file]|"
	printf "[-l|-list \"test1 test2 ... testn\"]]\n"
	printf "    [-mailAddr user@host] "
	printf "[-mailList \"user1@host ... usern@host\"]\n"
	printf "    [-v|-verbose] [-h|-help]\n\n"
	if [[ $2 > 0 ]]; then
		printf "The testScript.sh runs the pre-validated *.x10 test cases"
		printf " in the current\ndirectory and its subdirectories, and"
		printf " places the test results in a log\nfile under the"
		printf " specified log directory.\n\n"
		printf -- "-t | -timeOut [secs]\n"
		printf "  Enable timeout option for test case execution. This might"
		printf " get\noverrided by an individual test case through"
		printf " pre-validated data.\nOptionally, the timeout value can be"
		printf " specified in seconds.  This\noverrides the default timeout"
		printf " of 60 seconds.\n\n"
		printf -- "-f | -force\n"
		printf "  Run this script in the top-level directory where the *.x10"
		printf " \ntest cases can be located.  The current directory must"
		printf " have a magic\nfile \".ThisIsAnX10TestDirectory\" present"
		printf " in the directory to launch\nthe harness.  Use this option,"
		printf " to force launch the harness, even\nif the magic file is"
		printf " not present.\n\n"
		printf -- "-logPath dir\n"
		printf "  Specify the directory path where generated log files"
		printf " will be\nstored.  This is also the directory to look for"
		printf " archived log\nfiles from earlier runs.  If not specified,"
		printf " the current path's\n\"log\" entry will be used for this"
		printf " purpose.\n\n"
		printf -- "-listFile file\n"
		printf "  Test cases are found recursively starting at the top"
		printf " test\ndirectory, and are executed in the same order as"
		printf " they are found.\nThis option indicates a file containing"
		printf " a list of shell file\npatterns denoting the tests to run."
		printf "  The test list file can\nalso contain comment lines"
		printf " beginning with a \"#\" character,\nand blank lines for"
		printf " clarity.\n\n"
		printf -- "-l | -list test1 test2 ... testn\n"
		printf "  Alternatively, use this option to indicate the file"
		printf " pattern(s)\nfor the tests to run.  With these options,"
		printf " only the specified tests\nare run.\n\n"
		printf -- "-mailAddr user@host\n"
		printf "  Test harness administrator mail address.\n\n"
		printf -- "-mailList user1@host ... usern@host\n"
		printf "  List of users who should receive the run log.\n\n"
		printf -- "-local\n"
		printf "  Work with already checked out sources.\n\n"
		printf -- "-v | -verbose\n"
		printf "  Use this option to dislay the output of the tests that"
		printf " were executed.\n\n"
		printf -- "-h | -help\n"
		printf "  Print this help message.\n\n"
	fi
	cleanUpExit $1
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
						printf -- "\n[${prog}:err]: Timeout value $tctoutval is invalid\n\n"
						printUsage 1 0
					fi
					shift
				fi
			fi
		elif [[ "$1" == "-force" || "$1" == "-f" ]]; then
			tcforce=1
			shift
		elif [[ "$1" == "-testRun" ]]; then
			tctestrun=1
			shift
		elif [[ "$1" == "-logPath" && $# -ge 2 ]]; then
			if [[ ! -d "$2" ]]; then
				printf "\n[${prog}:err]: Log directory $2 must exist\n"
				cleanUpExit 1
			fi
			tclogpath=$2
			shift 2
		elif [[ "$1" == "-listFile" && $# -ge 2 ]]; then
			if [[ ! -r "$2" ]]; then
				printf "\n[${prog}:err]: List file $2 must exist & be readable\n"
				cleanUpExit 1
			fi
			tcpatfile=$2
			shift 2
		elif [[ "$1" == "-list" || "$1" == "-l" ]]; then
			if (( $# >= 2 )); then
				tcpatlist="$2"
				shift 2
			fi
		elif [[ "$1" == "-mailAddr" ]]; then
			if (( $# >= 2 )); then
				tcmailaddr="$2"
				shift 2
			fi
		elif [[ "$1" == "-mailList" ]]; then
			if (( $# >= 2 )); then
				tcmaillist="$2"
				shift 2
			fi
		elif [[ "$1" == "-local" ]]; then
			tclocal=1
			shift
		elif [[ "$1" == "-verbose" || "$1" == "-v" ]]; then
			tcverbose=1
			shift
		elif [[ "$1" == "-help" || "$1" == "-h" ]]; then
			printUsage 0 1
		elif [[ "$1" == "-logPath" || "$1" == "-listFile" || "$1" == "-l" || "$1" == "-list" || "$1" == "-mailAddr" || "$1" == "-mailList" ]]; then
			printf "\n[${prog}:err]: Option $1 needs argument\n\n"
			printUsage 1 0
		elif [[ "$1" == -* ]]; then
			printf "\n[${prog}:err]: Unrecognized option $1\n\n"
			printUsage 1 0
		else
			printf "\n[${prog}:err]: Extraneous argument(s) $1\n\n"
			printUsage 1 0
		fi
	done
	
	# need either test pattern list or file
	if [[ ! -z "$tcpatlist" && ! -z "$tcpatfile" ]]; then
		printf "\n[${prog}:err]: Conflicting test pattern specifications...\n\n"
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


# check the environment for specified variable
# exit code: 2
# usage: checkEnviron var opt-type
# simple value - no type
# file - type F
# directory - type D
function checkEnviron {
	typeset -n var="$1"

	if [[ "x$var" == "x" ]]; then
		printf "\n[$prog:err]: Environ variable $1 not set\n"
		cleanUpExit 2
	fi

	case "$2" in
		"F")
			if [[ ! -r "$var" ]]; then
				printf "\n[$prog:err]: File $1 either doesn't exist or not readable\n"
				cleanUpExit 2
			fi
			;;
		"D")
			if [[ ! -d "$var" ]]; then
				printf "\n[$prog:err]: Directory $1 doesn't exist\n"
				cleanUpExit 2
			fi
			;;
	esac
}

# check whether required commands are available on the default path
# exit code: 3
# usage: checkReqCmds
function checkReqCmds {
	for cmd in ${REQ_CMDS}; do
		printf " $cmd"
		which "$cmd" > /dev/null 2>&1
		if [[ $? != 0 ]]; then
			printf "\n[$prog:err]: can't locate command $cmd on the PATH\n"
			cleanUpExit 3
		fi
	done
	return
}

# check out or update the specified module
# exit code: 4
# usage: cvsUpdate module_path cvsroot
function cvsUpdate {
	# No actual update while working with already checked out sources
	if [[ $tclocal == 1 ]]; then
		if [[ ! -d $1 ]]; then
			printf "\n[$prog:err]: Module $(basename $1) doesn't exist\n"
			cleanUpExit 4
		fi
		return
	fi
	if [[ ! -d $1 ]]; then
		( \
			if [[ $tcverbose == 1 ]]; then \
				printf "\ncd $(dirname $1)\n"; \
				printf "\ncvs -Q -d \"$2\" co $(basename $1) >/dev/null\n\n"; \
			fi; \
			printf "\n===> cd $(dirname $1)\n" 1>&2; \
			printf "\n===> cvs -Q -d \"$2\" co $(basename $1) >/dev/null\n\n" 1>&2; \
			cd $(dirname $1); \
			cvs -Q -d "$2" co $(basename $1) >/dev/null 2>&1; \
			exit $?; \
		)
		if [[ $? != 0 ]]; then
			printf "\n[$prog:err]: Checkout failed for module $(basename $1)\n"
			cleanUpExit 4
		fi
	else
		( \
			cd $1; \
			if [[ $tcverbose == 1 ]]; then \
				printf "\ncvs -Q -d \"$2\" update -d >/dev/null\n\n"; \
			fi; \
			printf "\n===> cvs -Q -d \"$2\" update -d >/dev/null\n\n" 1>&2; \
			cvs -Q -d "$2" update -d >/dev/null; \
			exit $?; \
		)
		if [[ $? != 0 ]]; then
			printf "\n[$prog:err]: Update failed for module $(basename $1)\n"
			cleanUpExit 4
		fi
	fi
}

# build the specified x10 component
# exit code: 5
# usage: buildX10Module module_path type
function buildX10Module {
	case "$2" in
		"polyglot")
			( \
				if [[ $tcverbose == 1 ]]; then \
					printf "\ncd $1\n"; \
					printf "\nant -q clean jar >/dev/null\n\n"; \
				fi; \
				printf "\n===> cd $1\n" 1>&2; \
				printf "\n===> ant -q clean jar >/dev/null\n\n" 1>&2; \
				cd $1; \
				ant -q clean jar >/dev/null; \
				exit ?; \
			)
			;;
		"x10")
			( \
				if [[ $tcverbose == 1 ]]; then \
					printf "\ncd $1\n"; \
					printf "\nant -q clean dist >/dev/null\n\n"; \
				fi; \
				printf "\n===> cd $1\n" 1>&2; \
				printf "\n===> ant -q clean dist >/dev/null\n\n" 1>&2; \
				cd $1; \
				ant -q clean dist >/dev/null; \
				exit $?; \
			)
			;;
		"x10lib")
			( \
				if [[ $tcverbose == 1 ]]; then \
					printf "\ncd $1\n"; \
					printf "\n${GMAKE} -s realclean >/dev/null\n"; \
					printf "\n./configure >/dev/null\n"; \
					printf "\n${GMAKE}\n"; \
				fi; \
				printf "\n===> cd $1\n" 1>&2; \
				printf "\n===> ${GMAKE} -s realclean >/dev/null\n" 1>&2; \
				printf "\n===> ./configure >/dev/null\n" 1>&2; \
				printf "\n===> ${GMAKE}\n" 1>&2; \
				cd $1; \
				${GMAKE} -s realclean >/dev/null; \
				./configure >/dev/null; \
				${GMAKE} -s >/dev/null; \
				exit $?; \
			)
			;;
		"x10lang")
			( \
				if [[ $tcverbose == 1 ]]; then \
					printf "\ncd $1\n"; \
					printf "\n${GMAKE} clean >/dev/null\n"; \
					printf "\n${GMAKE} all >/dev/null\n"; \
				fi; \
				printf "\n===> cd $1\n" 1>&2; \
				printf "\n===> ${GMAKE} clean >/dev/null\n" 1>&2; \
				printf "\n===> ${GMAKE} all >/dev/null\n" 1>&2; \
				cd $1; \
				${GMAKE} -s clean >/dev/null; \
				${GMAKE} -s all >/dev/null; \
				exit $?; \
			)
			;;
	esac
	if [[ $? != 0 ]]; then
		printf "\n[$prog:err]: X10 component build failed for $(basename $1)\n"
		cleanUpExit 5
	fi
}

# generate list of tests for the specified pattern(s)
# usage: findTests testpat(s)
function findTests {
	eval "find $1 -type d -name CVS -prune -o -type f -name '*.x10' -print"
}

# check whether the specified test case file is valid
# usage: isTestCase file.x10
function isTestCase {
	typeset file=$(basename $1)
	
	if [[ "${file#${file%\.*}}" != ".x10" ]]; then
		printf "\n[$prog:err]: ${file} need an .x10 extension\n"
		return 1
	fi

	if [[ ! -r $1 ]]; then
		printf "\n[$prog:err]: ${file} must exist & is readable\n"
		return 1
	fi

	if [[ $tcverbose == 1 ]]; then
		printf "\negrep -q '(public[ ]+static[ ]+void[ ]+main|static[ ]+public[ ]+void[ ]+main)' $1\n\n"
	fi
	printf "\n===> egrep -q '(public[ ]+static[ ]+void[ ]+main|static[ ]+public[ ]+void[ ]+main)' $1\n\n" 1>&2
	egrep -q '(public[ ]+static[ ]+void[ ]+main|static[ ]+public[ ]+void[ ]+main)' $1
	if [[ $? != 0 ]]; then
		printf "\n[$prog:err]: no \"public static void main\" method in ${file}\n"
		return 1
	fi
	return 0
}

# extract the specified paramenter value from a given test case
# usage: xtractParams file.x10 vlist type
function xtractParams {
	typeset -n vlist=$2
	unset -v vlist

	if [[ "${3}" != "DATA" ]]; then
		vlist=($(egrep '^//@@X101X@@'${3}'@@' $1 | \
						sed -e 's;^//@@X101X@@'${3}'@@;;'))
	else
		egrep '^//@@X101X@@'${3}'@@' $1 | \
				sed -e 's;^//@@X101X@@'${3}'@@;;' > $tcrefdat
	fi
}

# usage: resolveParams file.x10
function resolveParams {
	typeset tmplist
	
	# validation code
	xtractParams $1 tmplist VCODE
	if [[ ${#tmplist[*]} == 0 ]]; then
		case "${tctarget}" in
			*_MustFailCompile)
				tcvcode=FAIL_COMPILE
				;;
			*_MustFailRun)
				tcvcode=FAIL_RUN
				;;
			*_MustFailTimeout)
				tcvcode=FAIL_TIMEOUT
				;;
			*)
				tcvcode=SUCCEED
				;;
		esac
	else
		tcvcode=${tmplist[0]}
	fi

	# update expected counters
	case "${tcvcode}" in
		"SUCCEED")
			let 'xtcpasscnt += 1'
			;;
		"FAIL_COMPILE")
			let 'xtcfcompcnt += 1'
			;;
		"FAIL_BUILD")
			let 'xtcfbuildcnt += 1'
			;;
		"FAIL_RUN")
			let 'xtcfexeccnt += 1'
			;;
		"FAIL_TIMEOUT")
			let 'xtcftoutcnt += 1'
			;;
	esac

	# flags
	xtractParams $1 tmplist FLAGS
	set -A tcflags ${tmplist[*]}

	# timeout enablement & value
	xtractParams $1 tmplist TOUT
	if [[ ${#tmplist[*]} == 0 ]]; then
		xtctimeout=$tctimeout
		xtctoutval=$tctoutval
	elif [[ ${#tmplist[*]} == 1 ]]; then
		xtctimeout=${tmplist[0]}
		xtctoutval=$tctoutval
	else
		xtctimeout=${tmplist[0]}
		xtctoutval=${tmplist[1]}
	fi

	# nplaces
	xtractParams $1 tmplist NPLACES
	if [[ ${#tmplist[*]} == 0 ]]; then
		tcnplaces=$DEFAULT_NPLACES
	else
		tcnplaces=${tmplist[0]}
	fi

	# input arguments
	xtractParams $1 tmplist ARGS
	set -A tcparams ${tmplist[*]}

	# expected output data
	tcrefdat=$tcroot/$tctarget.ref
	xtractParams $1 tmplist DATA
}

# execute the given command line with timelimit
# usage: execTimeOut val cmd
function execTimeOut {
	typeset timeout=$1
	shift
	typeset outfile=$1
	shift
	if [[ $tcverbose == 1 ]]; then
		printf "\n$@ > $outfile &\n\n"
	fi
	printf "\n===> $@ > $outfile &\n\n" 1>&2
	"$@" > $outfile &
	typeset cmd_pid=$!
	if [[ $tcverbose == 1 ]]; then
		printf "\nsleep $timeout && kill -KILL $cmd_pid 2>/dev/null &\n\n"
	fi
	printf "\n===> sleep $timeout && kill -KILL $cmd_pid 2>/dev/null &\n\n" 1>&2
	sleep $timeout && kill -KILL $cmd_pid 2>/dev/null &
	typeset sleep_pid=$!
	if [[ $tcverbose == 1 ]]; then
		printf "\nwait $cmd_pid 2>/dev/null\n\n"
	fi
	printf "\n===> wait $cmd_pid 2>/dev/null\n\n" 1>&2
	wait $cmd_pid 2>/dev/null
	typeset rc=$?
	if [[ $tcverbose == 1 ]]; then
		printf "\nkill -KILL $sleep_pid 2>/dev/null\n\n"
	fi
	printf "\n===> kill -KILL $sleep_pid 2>/dev/null\n\n" 1>&2
	kill -KILL $sleep_pid 2>/dev/null
	return $rc
}

# the following needs to be defined outside main
# program name
#prog=$(basename $0)
prog="x10c++th"

# generate unique timestamp (tctimestamp)
tcdate=$(date '+%Y-%d-%m')
tctime=$(date '+%H:%M:%S')
tctimestamp="$(echo $tcdate | sed -e 's;-;;g').$(echo $tctime | sed -e 's;:;;g')"
#tctimestamp=$(date '+%Y%d%m.%H%M%S')

# unique name for temporary directory (tctmpdir)
TMPDIR=/tmp
tctmpdir=${TMPDIR}/${prog}.$$.${tctimestamp}
mkdir -p $tctmpdir

# cleanup & exit if aborted
# usage: cleanUpExit excode
function cleanUpExit {
	if [[ -d ${tctmpdir} ]]; then
		if [[ -f ${tctmprlog} && ! -z ${tcrlogfile} ]]; then
			if [[ ! -d ${tclogpath} ]]; then
				mkdir -p ${tclogpath}
			fi
			writeLogTail >> ${tctmprlog} 2>>${tctmpelog}
			mv -f ${tctmprlog} ${tcrlogfile}
			mv -f ${tctmpelog} ${tcelogfile}
			# mail results
			if [[ $tctestrun == 0 ]]; then
				mailResults
				archiveLogs
			fi
		fi
		rm -rf $tctmpdir
	fi
	exit $1
}

# exit gracefully upon receiving these signals
trap 'cleanUpExit 1' INT QUIT TERM

# default values
DEFAULT_TIMEOUT=60
DEFAULT_LOGPATH="log"
DEFAULT_NPLACES=1
DEFAULT_MAILADDR="srkodali@in.ibm.com"
DEFAULT_MAILLIST="vsaraswa@us.ibm.com igorp@us.ibm.com nvkrishna@in.ibm.com pvarma@in.ibm.com"

# list of required commands
if [[ $(hostname) == "rlsecomp1.watson.ibm.com" ]]; then
	GMAKE=make-3.81
else
	GMAKE=gmake
fi

REQ_CMDS="mail cvs make ${GMAKE} find xargs egrep awk sed mpCC_r poe x10c++"

# these are currently hard coded - should be sourced from environ
export CVS_RSH=ssh
export X10_CVSROOT=":ext:x10.cvs.sf.net:/cvsroot/x10"
if [[ $(hostname) == "rlsecomp1.watson.ibm.com" ]]; then
	export X10CPP_CVSROOT=":ext:orquesta.watson.ibm.com:/usr/src/cvs/X10_FLASH"
else
	export X10CPP_CVSROOT=":ext:localhost:/usr/src/cvs/X10_FLASH"
fi
export POLYGLOT_CVSROOT=":pserver:anonymous@gforge.cis.cornell.edu:/cvsroot/polyglot"

# the value will be set later
export X10CPP_LOGLOC=""
export X10CPP_RUNLOG="${prog}.run.log"
export X10CPP_ERRLOG="${prog}.err.log"

# test case globals

# test harness current run state
# could be one of:
# PARSING_CMDLINE, SANITY_CHECK, MODULE_UPDATE, MODULE_BUILD,
# LIST_PREPARATION, TEST_PROCESSING, REPORT_GENERATION,
# UNKNOWN_STATE
thrunstate="UNKNOWN_STATE"

# debug variable
tctestrun=0

# enable/disable timeout option
# default: disable
typeset -i tctimeout=0

# default timeout value, if timeout is enabled
typeset -i tctoutval=$DEFAULT_TIMEOUT

# run tests even if the script is launched in a non-test dir
# default: no
typeset -i tcforce=0

# default log path, where log file will be created
typeset tclogpath=$DEFAULT_LOGPATH

# test case pattern file
# default: none
typeset tcpatfile=""

# test pattern list
# default: none
typeset tcpatlist=""

# if turned on, displays all the commands that are being executed
# default: off
typeset -i tcverbose=0

# are we using local sources
typeset -i tclocal=0

# target mail address and mailing list
typeset tcmailaddr="${DEFAULT_MAILADDR}"
typeset tcmaillist="${DEFAULT_MAILLIST}"

# path to temporary log file(s)
# run and error logs
# need to capture text even before command-line parsing is done
tctmprlog=${tctmpdir}/${prog}.run.${tctimestamp}.log
tctmpelog=${tctmpdir}/${prog}.err.${tctimestamp}.log
# final log(s) will be available here
tcrlogfile=""
tcelogfile=""


# total number of test cases
tctotalcnt=0
# number of test cases passed
tcpasscnt=0
# number of test cases failed
tcfailcnt=0


# initialize test case globals
# usage: init args
function init {

	# parse command-line arguments
	thrunstate=PARSING_CMDLINE
	parseCmdLine "$@"

	# validate the test launch directory
	# exit code: 1
	if [[ $tcforce != 1 && ! -e .ThisIsAnX10TestDirectory ]]; then
		printf "\n[$prog:err]: $(basename $(pwd)) not a valid X10 launch dir\n"
		cleanUpExit 1
	fi

	# set final log destination(s)
	tcrlogfile=${tclogpath}/${prog}.run.${tctimestamp}.log
	tcelogfile=${tclogpath}/${prog}.err.${tctimestamp}.log
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
	printf "Run Log File: $tcrlogfile\n"
	printf "Archived Run Log File: x10.backend/tests/log/${X10CPP_RUNLOG}\n"
	printf "Error Log File: $tcelogfile\n"
	printf "Archived Error Log File: x10.backend/tests/log/${X10CPP_ERRLOG}\n"
	printf "Verbose: "
	if [[ $tcverbose == 1 ]]; then
		printf "On\n"
	else
		printf "Off\n"
	fi
	printf "Testcase Pattern List: $tcpatlist\n"

	# see whether necessary environment variables are set
	thrunstate=SANITY_CHECK
	printf "\n<<Sanity Check : Environ>>\n"
	printf "\n+ X10_HOME"
	checkEnviron "X10_HOME" D
	printf " ++ X10_PLATFORM"
	checkEnviron "X10_PLATFORM"
	printf " +++ JAVA_HOME"
	checkEnviron "JAVA_HOME" D
	printf " ++++ ANT_HOME"
	checkEnviron "ANT_HOME" D

	# see whether all the required command are available
	printf "\n\n<<Sanity Check : Commands>>\n\n"
	checkReqCmds

	# update or check out the required x10 modules
	thrunstate=MODULE_UPDATE
	printf "\n\n<<Module Update>>\n"
	POLYGLOT_HOME=$X10_HOME/polyglot
	printf "\n+ $(basename $POLYGLOT_HOME)"
	cvsUpdate $POLYGLOT_HOME $POLYGLOT_CVSROOT
	X10_COMMON=$X10_HOME/x10.common
	printf " ++ $(basename $X10_COMMON)"
	cvsUpdate $X10_COMMON $X10_CVSROOT
	X10_RUNTIME=$X10_HOME/x10.runtime
	printf " +++ $(basename $X10_RUNTIME)"
	cvsUpdate $X10_RUNTIME $X10_CVSROOT
	X10_COMPILER=$X10_HOME/x10.compiler
	printf " ++++ $(basename $X10_COMPILER)"
	cvsUpdate $X10_COMPILER $X10_CVSROOT
	X10_LIB=$X10_HOME/x10.lib
	printf " +++++ $(basename $X10_LIB)"
	cvsUpdate $X10_LIB $X10_CVSROOT
	X10CPP_BACKEND=$X10_HOME/x10.backend
	printf " ++++++ $(basename $X10CPP_BACKEND)\n"
	cvsUpdate $X10CPP_BACKEND $X10CPP_CVSROOT

	# set after x10.backend is properly updated at least once
	# repository location for archiving of logs
	export X10CPP_LOGLOC=$X10CPP_BACKEND/tests/log

	# build all the modules
	thrunstate=MODULE_BUILD
	printf "\n<<Module Build>>\n"
	printf "\n+ $(basename $POLYGLOT_HOME)"
	buildX10Module $POLYGLOT_HOME polyglot
	export LOCAL_POLYGLOT_JAR=$POLYGLOT_HOME/lib/polyglot.jar
	printf " ++ $(basename $X10_COMMON)" 
	buildX10Module $X10_COMMON x10
	printf " +++ $(basename $X10_RUNTIME)"
	buildX10Module $X10_RUNTIME x10
	printf " ++++ $(basename $X10_COMPILER)"
	buildX10Module $X10_COMPILER x10
	printf " +++++ $(basename $X10_LIB)"
	buildX10Module $X10_LIB x10lib
	printf " ++++++ $(basename $X10CPP_BACKEND)"
	buildX10Module $X10CPP_BACKEND x10
	X10CPP_X10LANG=$X10CPP_BACKEND/x10lang
	printf " +++++++ x10lang\n"
	buildX10Module $X10CPP_X10LANG x10lang

	# set test case build environment
	export CFLAGS="-q64"
	export INCLUDES="-I. -I$X10CPP_BACKEND/x10lang -I$X10_LIB/include"
	export LIBS="-lx10lang -lx10"
	export LIBDIRS="-L$X10CPP_BACKEND/x10lang -L$X10_LIB/lib"

	# prepare test case list
	# exit code: 6
	thrunstate=LIST_PREPARATION
	printf "\n<<Testcase List Preparation>>\n"
	set -A tclist $(findTests '$tcpatlist')
	if [[ ${#tclist[*]} == 0 ]]; then
		printf "\n[$prog:err]: zero tests found under $(basename $(pwd))\n"
		cleanUpExit 6
	fi
	if [[ $tcverbose == 1 ]]; then
		printf "\n${tclist[*]}\n\n"
	fi
	printf "\n===> ${tclist[*]}\n\n" 1>&2
	tctotalcnt=${#tclist[*]}

	# various micro counters
	# number of test cases processed so far
	tcproccnt=0
	# number of test cases having invalid validation code
	tcfvcodecnt=0

	# number of test cases passed pre-validity check
	tcvalidcnt=0
	# number of in appropriate test cases
	tcfvalidcnt=0

	# number of test cases successfully compiled
	tccompcnt=0
	# number of expected compilation failures
	xtcfcompcnt=0
	# number of actual compilation failures
	tcfcompcnt=0

	# number of test cases successfully build
	tcbuildcnt=0
	# number of expected build failures
	xtcfbuildcnt=0
	# number of actual build failures
	tcfbuildcnt=0

	# number of test cases successfully executed
	tcexeccnt=0
	# number of expected execution failures
	xtcfexeccnt=0
	# number of actual execution failures
	tcfexeccnt=0

	# number of expected timeout failures
	xtcftoutcnt=0
	# number of actual timeout failures
	tcftoutcnt=0
	# number of expected successes
	xtcpasscnt=0

	thrunstate=TEST_PROCESSING
	printf "\n<<Testcase Processing>>\n"
	printf "\n# Legend:\n"
	printf "#\tV - pre-validation step\n"
	printf "#\tR - parameters resolution step\n"
	printf "#\tC - compilation step\n"
	printf "#\tB - build step\n"
	printf "#\tE - execution step\n\n"
	printf "#\tX - test failed\n"
	printf "#\tY - test passed\n"

	for tc in ${tclist[*]}; do
		let 'tcproccnt += 1'
		printf "\n((${tcproccnt})) [$(basename $(dirname $tc)):$(basename $tc)]"
		printf "\n===>((${tcproccnt})) [$(basename $tc)]\n\n" 1>&2
		# pre-validate the test case
		printf " + V"
		isTestCase $tc
		if [[ $? != 0 ]]; then
			printf " *** X ***"
			let 'tcfvalidcnt += 1'
			let 'tcfailcnt += 1'
			printf "\n[$prog:err]: $(basename $tc) is not a valid test case\n"
			continue
		fi
		let 'tcvalidcnt += 1'

		# create the test root
		tctarget=$(basename $tc | sed -e 's;.x10;;')
		if [[ -d ${tcroot} ]]; then
			rm -rf ${tcroot}
		fi
		tcroot=$tctmpdir/$tctarget
		if [[ $tcverbose == 1 ]]; then
			printf "\nmkdir -p $tcroot\n\n"
		fi
		printf "\n===> mkdir -p $tcroot\n\n" 1>&2
		mkdir -p $tcroot

		# resolve test case parameters
		# tcvcode, tcnplaces, tcflags, tcargs, tcrefdat
		# xtctimeout, xtctoutval
		printf " ++ R"
		resolveParams $tc
		
		# try & generate sources
		printf " +++ C"
		if [[ $tcverbose == 1 ]]; then
			printf "\n${X10CPP_BACKEND}/bin/x10c++ -c -d $tcroot $tc >/dev/null\n\n" 
		fi
		printf "===> \n${X10CPP_BACKEND}/bin/x10c++ -c -d $tcroot $tc >/dev/null\n\n" 1>&2
		${X10CPP_BACKEND}/bin/x10c++ -c -d $tcroot $tc >/dev/null
		rc=$?
		if [[ $rc != 0 && "$tcvcode" == "FAIL_COMPILE" ]]; then
			let 'tcfcompcnt += 1'
			let 'tcpasscnt += 1'
			printf " *** Y ***"
			continue
		elif [[ $rc == 0 && "$tcvcode" == "FAIL_COMPILE" ]]; then
			printf " *** X ***"
			let 'tcfvcodecnt += 1'
			let 'tcfailcnt += 1'
			printf "\n[$prog:err]: invalid validation code for ${tctarget}\n"
			continue
		elif [[ $rc != 0 && "$tcvcode" != "FAIL_COMPILE" ]]; then
			let 'tcfcompcnt += 1'
			let 'tcfailcnt += 1'
			printf " *** X ***"
			printf "\n[$prog:err]: can't generate c++ sources for ${tctarget}\n"
			continue
		fi
		let 'tccompcnt += 1'

		# list of all the generated cc files (tcsrclist)
		set -A tcsrclist $(cd $tcroot; ls *.cc)
		if [[ $tcverbose == 1 ]]; then
			printf "\n${tcsrclist[*]}\n\n"
		fi
		printf "\n===> ${tcsrclist[*]}\n\n" 1>&2

		# try & build the target
		printf " ++++ B"
		( \
			if [[ $tcverbose == 1 ]]; then \
				printf "\ncd $tcroot\n"; \
				printf "\nmpCC_r ${CFLAGS} ${INCLUDES} -o $tctarget ${LIBDIRS} ${LIBS} ${tcsrclist[@]} >/dev/null\n\n"; \
			fi; \
			printf "\n===> cd $tcroot\n" 1>&2; \
			printf "\n===> mpCC_r ${CFLAGS} ${INCLUDES} -o $tctarget ${LIBDIRS} ${LIBS} ${tcsrclist[@]} >/dev/null\n\n" 1>&2; \
			cd $tcroot; \
			mpCC_r ${CFLAGS} ${INCLUDES} -o $tctarget ${LIBDIRS} ${LIBS} ${tcsrclist[@]} >/dev/null; \
		)
		rc=$?
		if [[ $rc != 0 && "$tcvcode" == "FAIL_BUILD" ]]; then
			let 'tcfbuildcnt += 1'
			let 'tcpasscnt += 1'
			printf " *** Y ***"
			continue
		elif [[ $rc == 0 && "$tcvcode" == "FAIL_BUILD" ]]; then
			printf " *** X ***"
			let 'tcfailcnt += 1'
			let 'tcfvcodecnt += 1'
			printf "\n[$prog:err]: invalid validation code for ${tctarget}\n"
			continue
		elif [[ $rc != 0 && "$tcvcode" != "FAIL_BUILD" ]]; then
			let 'tcfailcnt += 1'
			let 'tcfbuildcnt += 1'
			printf " *** X ***"
			printf "\n[$prog:err]: can't build target ${tctarget}\n"
			continue
		fi
		let 'tcbuildcnt += 1'

		# try & run the target	
		# create a temporary hostfile (tchostfile)
		tchostfile=$tcroot/host.list
		for (( i = 0; i < tcnplaces; i++ )); do
			printf "$(hostname)\n" >> $tchostfile
		done

		run_cmd="poe ./${tctarget} ${tcargs} -procs ${tcnplaces} -msg_api lapi -hostfile $tchostfile"
		# the actual output will be logged here
		tcoutdat=${tcroot}/${tctarget}.out
		printf " +++++ E"
		( \
			if [[ $tcverbose == 1 ]]; then
				printf "\ncd $tcroot\n"; \
			fi; \
			printf "\n===> cd $tcroot\n" 1>&2; \
			cd $tcroot; \
			if [[ $xtctimeout == 0 ]]; then \
				if [[ $tcverbose == 1 ]]; then \
					printf "\n$run_cmd > $tcoutdat\n\n"; \
				fi; \
				printf "\n===> $run_cmd > $tcoutdat\n\n" 1>&2; \
				$run_cmd > $tcoutdat; \
			else \
				execTimeOut $xtctoutval $tcoutdat ${run_cmd}; \
			fi;
		)
		rc=$?
		let 'sig = rc - 128'
		if [[ $rc == 0 && $tcvcode == "SUCCEED" ]]; then
			if [[ ! -s ${tcrefdat} ]]; then
				printf " *** Y ***"
				printf "\n[$prog:warn]: no reference data to compare\n"
				let 'tcexeccnt += 1'
				let 'tcpasscnt += 1'
				continue
			fi
			diff ${tcoutdat} ${tcrefdat} > /dev/null 2>&1
			rc2=$?
			if [[ $rc2 != 0  ]]; then
				typeset -i limitation
				echo ${tcflags[*]} | egrep -q LIMITATION
				if [[ $? == 0 ]]; then
					limitation=1
				else
					limitation=0
				fi
				if [[ $limitation == 1 ]]; then
					printf " *** Y ***"
					printf "\n[$prog:warn]: actual & expected outcomes don't match *** LIMITATION ***\n"
					let 'tcexeccnt += 1'
					let 'tcpasscnt += 1'
				else
					let 'tcfailcnt += 1'
					let 'tcexeccnt += 1'
					printf " *** X ***"
					printf "\n[$prog:err]: actual & expected outcomes don't match\n"
				fi
			else
				printf " *** Y ***"
				let 'tcexeccnt += 1'
				let 'tcpasscnt += 1'
			fi
			continue
		fi
		if [[ $xtctimeout == 0 ]]; then
			if [[ $rc > 0 && $tcvcode == "SUCCEED" ]]; then
				let 'tcfailcnt += 1'
				let 'tcfexeccnt += 1'
				printf " *** X ***"
				printf "\n[$prog:err]: failed to execute ${tctarget}\n"
				continue
			elif [[ $rc == 0 && $tcvcode == "FAIL_RUN" ]]; then
				printf " *** X ***"
				let 'tcfailcnt += 1'
				let 'tcfvcodecnt += 1'
				printf "\n[$prog:err]: invalid validation code for ${tctarget}\n"
				continue
			elif [[ $rc > 0 && $tcvcode == "FAIL_RUN" ]]; then
				printf " *** Y ***"
				let 'tcfexeccnt += 1'
				let 'tcpasscnt += 1'
				continue
			else
				printf " *** X ***"
				let 'tcfailcnt += 1'
				let 'tcfvcodecnt += 1'
				printf "\n[$prog:err]: invalid validation code ${tctarget}\n"
				continue
			fi
		else
			if [[ $rc > 128 && $sig == 9 && $tcvcode == "SUCCEED" ]]; then
				printf " *** X ***"
				let 'tcftoutcnt += 1'
				let 'tcfexeccnt += 1'
				printf "\n[$prog:err]: ${tctarget} is killed due to timeout\n"
				continue
			elif [[ $rc > 0 && $tcvcode == "SUCCEED" ]]; then
				printf " *** X ***"
				let 'tcfailcnt += 1'
				let 'tcfexeccnt += 1'
				printf "\n[$prog:err]: failed to execute ${tctarget}\n"
				continue
			elif [[ $rc == 0 && $tcvcode == "FAIL_RUN" ]]; then
				printf " *** X ***"
				let 'tcfailcnt += 1'
				let 'tcfvcodecnt += 1'
				printf "\n[$prog:err]: invalid validation code ${tcvcode}\n"
				continue
			elif [[ $rc > 128 && $sig == 9 && $tcvcode == "FAIL_RUN" ]]; then
				printf " *** X ***"
				let 'tcfailcnt += 1'
				let 'tcftoutcnt += 1'
				let 'tcfexeccnt += 1'
				printf "\n[$prog:err]: ${tctarget} is killed due to timeout\n"
				continue
			elif [[ $rc > 0 && $tcvcode == "FAIL_RUN" ]]; then
				printf " *** Y ***"
				let 'tcfexeccnt += 1'
				let 'tcpasscnt += 1'
				continue
			elif [[ $rc == 0 && $tcvcode == "FAIL_TIMEOUT" ]]; then
				printf " *** X ***"
				let 'tcfailcnt += 1'
				let 'tcfvcodecnt += 1'
				printf "\n[$prog:err]: invalid validation code ${tcvcode}\n"
				continue
			elif [[ $rc > 128 && $sig == 9 && $tcvcode == "FAIL_TIMEOUT" ]]; then
				printf " *** Y ***"
				let 'tcftoutcnt += 1'
				let 'tcfexeccnt += 1'
				let 'tcpasscnt += 1'
				continue
			elif [[ $rc > 0 && $tcvcode == "FAIL_TIMEOUT" ]]; then
				printf " *** X ***"
				let 'tcfailcnt += 1'
				let 'tcftoutcnt += 1'
				let 'tcfexeccnt += 1'
				printf "\n[$prog:err]: failed to execute ${tctarget}\n"
				continue
			fi
		fi
	done

	# prepare the report
	# simple summary for the moment
	thrunstate=REPORT_GENERATION
	printf "\n\n<<Report Generation>>\n"
	printf "\n\n======================================================================\n\n"
	printf "                     X10/C++ Test Harness :: Run Report\n\n"
	printf "\n**QUEUE          : "
	printf "${tctotalcnt} Total / ${tcproccnt} Processed\n"

	printf "\n**PRE-VALIDATION : "
	printf "${tcvalidcnt} Successes / ${tcfvalidcnt} Failures\n"

	printf "\n**COMPILATION    : "
	printf "${tccompcnt} Successes / ${xtcfcompcnt} Expected Failures"
	printf " / ${tcfcompcnt} Actual Failures\n"

	printf "\n**BUILD          : "
	printf "${tcbuildcnt} Successes / ${xtcfbuildcnt} Expected Failures"
	printf " / ${tcfbuildcnt} Actual Failures\n"

	printf "\n**EXECUTION      : "
	printf "${tcexeccnt} Successes / ${xtcfexeccnt} Expected Failures"
	printf " / ${tcfexeccnt} Actual Failures\n"

	printf "\n**TIME-OUT       : "
	printf "${xtcftoutcnt} Expected Failures / ${tcftoutcnt} Actual"
	printf " Failures\n"

	printf "\n**MISCELLANEOUS  : "
	printf "${tcfvcodecnt} Invalid Validation Codes\n"

	printf "\n**CONCLUSION     : "
	printf "${tcpasscnt} Successes / ${tcfailcnt} Failures\n"
	printf "\n======================================================================\n\n"
}

init "$@"
writeLogHead > ${tctmprlog} 2>${tctmpelog}
main >> ${tctmprlog} 2>>${tctmpelog}
cleanUpExit 0
