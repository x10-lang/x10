#!/usr/bin/ksh93

#
# (c) Copyright IBM Corporation 2008
#
# $Id: tcPreSubmit.sh,v 1.1 2009-02-19 14:32:54 pvarma Exp $
# This file is part of X10/C++ Test Harness.

## Interactive Test Case Pre-Submission Script.


# ====== Step 1: Check Usage =====
# exit code: 1
# x10 test case file (tcfile)
# args check
if [[ $# == 1 ]]; then
	tcfile=$1
elif [[ $# > 1 ]]; then
	printf "Usage: tcPreSubmit.sh [ file.x10 ]\n"
	exit 1
else
	tcfile=""
fi
prog=$(basename $0)

# generate unique timestamp (tctimestamp)
tctimestamp=$(date '+%Y%d%m.%H%M%S')

# unique name for temporary directory (tctmpdir)
TMPDIR=/tmp
tctmpdir=${TMPDIR}/${prog}.$$.${tctimestamp}

# cleanup & exit if aborted
# usage: cleanUpExit excode
function cleanUpExit {
	if [[ -d ${tctmpdir} ]]; then
		rm -rf $tctmpdir
	fi
	exit $1
}

# exit gracefully upon receiving these signals
trap cleanUpExit INT QUIT TERM


# ====== Step 2: Prologue =====
# exit code: 2
printf "============================================================\n\n"
printf "  X10/C++ Test Harness Interactive Pre-Submission Facility\n\n"
printf "                (c) IBM Corporation 2008\n\n"
printf "============================================================\n\n"
printf "This program facilitates you to pre-validate & prepare the\n"
printf "specified test case for final submission to the x10/c++ test\n"
printf "harness.  You will be required to answer few questions as it\n"
printf "takes you through a series of steps.  At any time you can\n"
printf "quit the process safely by hitting <ctrl+c> or <ctrl+\\\> key\n"
printf "combinations.  Until you confirm, no modifications will be\n"
printf "done to the test case file.\n\n"

# usage: yesNo message
function yesNo {
	typeset -i status
	typeset tmp

	let 'status = 0'
	until [[ $status == 1 ]]; do
		printf "\n"
		read tmp?"${1} [y|n]: "
		if [[ $tmp == "y" || $tmp == "yes" ]]; then
			return 1
		elif [[ $tmp == "n" || $tmp == "no" ]]; then
			return 0
		else
			continue
		fi
	done
}

yesNo "Do you want to proceed with the session?"
if [[ $? == 0 ]]; then
	exit 2
fi


# ===== Step 3: Sanity Check =====
# exit code: 3
# check for command existence 
REQ_CMDS="awk egrep sed mpCC_r poe x10c++"
printf "\nDoing sanity check....."
for cmd in ${REQ_CMDS}; do
	which "$cmd" > /dev/null 2>&1
	if [[ $? != 0 ]]; then
		printf "\n[$prog]: can't locate command $cmd\n"
		exit 3
	fi
done
printf "done.\n"


# ===== Step 4: Get Test Case (If Req) =====
if [[ -z "${tcfile}" ]]; then
	let 'status = 0'
	until [[ $status == 1 ]]; do
		printf "\n"
		read tcfile?"Enter full path to the test case file: "
		if [[ -z "${tcfile}" ]]; then
			continue
		fi
		let 'status = 1'
	done
fi


# ===== Step 5: Pre-Validate Test Case =====
# exit code: 4
# is this valid test case file
printf "\nPre-validating the test case file \"$(basename $tcfile)\"....."
if [[ ! -r $tcfile || ! -w $tcfile ]]; then
	printf "\n[$prog]: \"${tcfile}\" must exist & is readable/writable\n"
	exit 4
fi
if [[ "${tcfile#${tcfile%\.*}}" != ".x10" ]]; then
	printf "\n[$prog]: \"${tcfile}\" need an .x10 extension\n"
	exit 4
fi
egrep -q '(public[ ]+static[ ]+void[ ]+main|static[ ]+public[ ]+void[ ]+main)' $tcfile
if [[ $? != 0 ]]; then
	printf "\n[$prog]: no \"public static void main\" method in \"${tcfile}\"\n"
	exit 4
fi
printf "done.\n"

# derive target name (tctarget)
tctarget=$(basename $tcfile | sed -e 's;.x10;;')

# this is the directory where all intermediate files will be located
printf "\nCreating the temporary output directory....."
mkdir $tctmpdir
printf "done.\n"


# ===== Step 6: Check & Get Timeout Value =====
# usage: readUInt message dflt
function readUInt {
	typeset -i status
	typeset -i rval
	typeset tmp

	let 'status = 0'
	until [[ $status == 1 ]]; do
		printf "\n"
		read tmp?"${1} [${2}]: "
		if [[ -z "$tmp" ]]; then
			return $2
		fi
		# this takes care of discarding non-integer argument
		let 'rval = tmp'
		if [[ $? != 0 || $rval == 0 || $rval < 0 ]]; then
			printf -- "[$prog]: $tmp is invalid\n"
			continue
		fi
		return $rval
	done
}

# see whether timeout option is needed (tctimeout, tctoutval)
DEFAULT_TIMEOUT=60
typeset -i tctoutval
yesNo "Do you want timeout option enabled for \"${tctarget}\"?"
if [[ $? == 1 ]]; then
	tctimeout=1
	readUInt "Enter timeout in seconds for \"${tctarget}\"" $DEFAULT_TIMEOUT
	tctoutval=$?
else
	tctimeout=0
	tctoutval=$DEFAULT_TIMEOUT
fi



# ===== Step 7: Get Validation Code =====
# get test case validation code (tcvcode)
header="<<Test Case Validation Codes>>"
printf "\n${header}\n"
PS3="How to validate the test case \"${tctarget}\"? "
options="SUCCEED FAIL_COMPILE FAIL_BUILD FAIL_RUN"
if [[ $tctimeout == 1 ]]; then
	options="${options} FAIL_TIMEOUT"
fi
select tcvcode in $options; do
	if [[ -z "${tcvcode}" ]]; then
		REPLY=""
		printf "\n${header}\n"
	else
		break
	fi
done



# ===== Step 14: Update Test Case =====
# exit code: 8, 0
# usage: updateTest
function updateTest {
	typeset id="//@@X101X@@"
	typeset x10cpp_ver
	typeset polyglot_ver

	# display results and get confirmation
	printf "\nX10/C++ Test Harness Pre-Validation Summary\n\n"
	printf "Test Case: ${tctarget}\n"
	printf "Validation Code: ${tcvcode}\n"
	printf "Timeout Enabled: ${tctimeout}\n"
	printf "Timeout Value: ${tctoutval}\n"
	if [[ $tcvcode != "FAIL_COMPILE" ]]; then
		printf "CC Sources: ${tcsrclist[*]}\n"
	fi
	if [[ $tcvcode != "FAIL_COMPILE" && $tcvcode != "FAIL_BUILD" ]]; then
		printf "Number Places: ${tcnplaces}\n"
		printf "Flags: ${tcflags}\n"
		printf "Input Arguments: ${tcargs}\n"
	fi
	if [[ $tcvcode == "FAIL_TIMEOUT" || $tcvcode == "SUCCEED" ]]; then
		printf "Output Data:\n"
		cat $tcoutf
	fi
	yesNo "Do you confirm with the outcome?"
	if [[ $? == 0 ]]; then
		printf "[$prog]: test case \"${tctarget}\" updation abandoned\n"
		cleanUpExit 8
	fi

	printf "\nDoing the final test case updation....."
	typeset tctmpfile=${tctmpdir}/$(basename ${tcfile}).tmp
	# remove old entries, if any
	sed '/^\/\/@@X101X@@/d' $tcfile > $tctmpfile
	printf "\n${id}========== X10/C++ TEST HARNESS ==========<<START>>\n" >> $tctmpfile
	x10cpp_ver=$(x10c++ -version | egrep x10c | awk '{print $3}')
	polyglot_ver=$(x10c++ -version | egrep Polyglot | awk '{print $5}')
	printf "${id}VER@@${x10cpp_ver} [POLYGLOT ${polyglot_ver}]\n" >> $tctmpfile
	printf "${id}TSTAMP@@${tctimestamp}\n" >> $tctmpfile
	printf "${id}TCASE@@${tctarget}\n" >> $tctmpfile
	printf "${id}VCODE@@${tcvcode}\n" >> $tctmpfile
	printf "${id}TOUT@@${tctimeout} ${tctoutval}\n" >> $tctmpfile
	if [[ $tcvcode != "FAIL_COMPILE" ]]; then
		printf "${id}SRCS@@${tcsrclist[*]}\n" >> $tctmpfile
	fi
	if [[ $tcvcode != "FAIL_COMPILE" && $tcvcode != "FAIL_BUILD" ]]; then
		printf "${id}NPLACES@@${tcnplaces}\n" >> $tctmpfile
		printf "${id}FLAGS@@${tcflags}\n" >> $tctmpfile
		printf "${id}ARGS@@${tcargs}\n" >> $tctmpfile
	fi
	if [[ $tcvcode == "FAIL_TIMEOUT" || $tcvcode == "SUCCEED" ]]; then
		cat $tcoutf | sed -e 's;^;'${id}'DATA@@;g' >> $tctmpfile
	fi
	printf "${id}========== X10/C++ TEST HARNESS ==========<<END>>\n" >> $tctmpfile
	mv -f $tcfile $tcfile.bak
	mv -f $tctmpfile $tcfile
	printf "done.\n"
	cleanUpExit 0
}


# ===== Step 8: Compile Test Case =====
# exit code: 5
# try & generate sources
printf "\nCompiling the test case \"${tctarget}\"....."
x10c++ -c -d $tctmpdir $tcfile 2>/dev/null
rc=$?
printf "done.\n"
if [[ $rc != 0 ]]; then
	if [[ $tcvcode == "FAIL_COMPILE" ]]; then
		updateTest
	else
		printf "[$prog]: can't generate c++ sources for \"${tctarget}\"\n"
		cleanUpExit 5
	fi
else
	if [[ $tcvcode == "FAIL_COMPILE" ]]; then
		printf "[$prog]: invalid validation code \"${tcvcode}\"\n"
		cleanUpExit 5
	fi
fi

# make list of all the generated cc files (tcsrclist)
set -A tcsrclist $(cd $tctmpdir; ls *.cc)


# ===== Step 9: Build Test Case =====
# exit code: 6

# setup paths for test case build
# this shouldn't fail with x10c++ availability
X10CPP_BACKEND=$(cd $(dirname $(which x10c++))/..; pwd)
X10_HOME=$(cd ${X10CPP_BACKEND}/..; pwd)
X10_LIB=$(cd ${X10CPP_BACKEND}/../x10.lib; pwd)

INCLUDES="-I. -I${X10CPP_BACKEND}/x10lang -I${X10_LIB}/include"
LIBDIRS="-L. -L${X10CPP_BACKEND}/x10lang -L${X10_LIB}/lib"
LIBS="-lx10lang -lx10"
CFLAGS="-q64"

# try and build the target
printf "\nBuilding the test case \"${tctarget}\"....."
( \
	cd $tctmpdir; \
	mpCC_r ${CFLAGS} ${INCLUDES} -o $tctarget ${LIBDIRS} ${LIBS} ${tcsrclist[@]} 2>/dev/null; \
)
rc=$?
printf "done.\n"
if [[ $rc != 0 ]]; then
	if [[ $tcvcode == "FAIL_BUILD" ]]; then
		updateTest
	else
		printf "[$prog]: can't build target \"${tctarget}\"\n"
		cleanUpExit 6
	fi
else
	if [[ $tcvcode == "FAIL_BUILD" ]]; then
		printf "[$prog]: invalid validation code \"${tcvcode}\"\n"
		cleanUpExit 6
	fi
fi


# ===== Step 10: Get Number Of Places =====
# get number of places to run (tcnplaces)
typeset -i tcnplaces
DEFAULT_NPLACES=1
readUInt "How many places \"${tctarget}\" to run for?" $DEFAULT_NPLACES
tcnplaces=$?

# create a temporary hostfile (tchostfile)
printf "\nCreating temporary hostfile....."
tchostfile=$tctmpdir/host.list
for (( i = 0; i < tcnplaces; i++ )); do
	printf "$(hostname)\n" >> $tchostfile
done
printf "done.\n"


# ===== Step 11: Check Implementation Limitation =====
# is the test case an implementation limitation (tcflags)
yesNo "Is \"${tctarget}\" an implementation limitation?"
if [[ $? == 1 ]]; then
	tcflags=LIMITATION
else
	tcflags=""
fi


# ===== Step 12: Get Input Arguments =====
# get test case input arguments, if any (tcargs)
yesNo "Does \"${tctarget}\" need any input arguments?"
if [[ $? == 1 ]]; then
	printf "\n"
	read tcargs?"Enter input arguments for \"${tctarget}\": "
else
	tcargs=""
fi

# test case output data file (tcoutf)
tcoutf=${tctmpdir}/${tctarget}.dat


# ===== Step 13: Run Test Case =====
# exit code: 7

# usage: timeOut val cmd
function timeOut {
	typeset timeout=$1
	shift
	typeset outfile=$1
	shift
	"$@" > $outfile &
	typeset cmd_pid=$!
	sleep $timeout && kill -KILL $cmd_pid 2>/dev/null &
	typeset sleep_pid=$!
	wait $cmd_pid 2>/dev/null
	typeset rc=$?
	kill -KILL $sleep_pid 2>/dev/null
	return $rc
}

# try and run the test case & capture output
run_cmd="poe ./${tctarget} ${tcargs} -procs ${tcnplaces} -msg_api lapi -hostfile $tchostfile"
printf "\nRunning the test case \"${tctarget}\"....."
( \
	cd $tctmpdir; \
	if [[ $tctimeout == 0 ]]; then \
		$run_cmd > $tcoutf; \
	else \
		timeOut $tctoutval $tcoutf ${run_cmd}; \
	fi; \
)
rc=$?
printf "done.\n"
let 'sig = 128 - rc'
if [[ $tctimeout == 0 && $rc == 0 && $tcvcode == "SUCCEED" ]]; then
	updateTest
elif [[ $tctimeout == 0 && $rc > 0 && $tcvcode == "SUCCEED" ]]; then
	printf "[$prog]: failed to execute \"${tctarget}\"\n"
	cleanUpExit 7
elif [[ $tctimeout == 0 && $rc == 0 && $tcvcode == "FAIL_RUN" ]]; then
	printf "[$prog]: invalid validation code \"${tcvcode}\"\n"
	cleanUpExit 7
elif [[ $tctimeout == 0 && $rc > 0 && $tcvcode == "FAIL_RUN" ]]; then
	updateTest
elif [[ $tctimeout == 1 && $rc == 0 && $tcvcode == "SUCCEED" ]]; then
	updateTest
elif [[ $tctimeout == 1 && $rc > 128 && $sig == 9 && $tcvcode == "SUCCEED" ]]; then
	printf "[$prog]: \"${tctarget}\" is killed due to timeout\n"
	cleanUpExit 7
elif [[ $tctimeout == 1 && $rc > 0 && $tcvcode == "SUCCEED" ]]; then
	printf "[$prog]: failed to execute \"${tctarget}\"\n"
	cleanUpExit 7
elif [[ $tctimeout == 1 && $rc == 0 && $tcvcode == "FAIL_RUN" ]]; then
	printf "[$prog]: invalid validation code \"${tcvcode}\"\n"
	cleanUpExit 7
elif [[ $tctimeout == 1 && $rc > 128 && $sig == 9 && $tcvcode == "FAIL_RUN" ]]; then
	printf "[$prog]: \"${tctarget}\" is killed due to timeout\n"
	cleanUPExit 7
elif [[ $tctimeout == 1 && $rc > 0 && $tcvcode == "FAIL_RUN" ]]; then
	updateTest
elif [[ $tctimeout == 1 && $rc == 0 && $tcvcode == "FAIL_TIMEOUT" ]]; then
	printf "[$prog]: invalid validation code \"${tcvcode}\"\n"
	cleanUpExit 7
elif [[ $tctimeout == 1 && $rc > 128 && $sig == 9 && $tcvcode == "FAIL_TIMEOUT" ]]; then
	updateTest
elif [[ $tctimeout == 1 && $rc > 0 && $tcvcode == "FAIL_TIMEOUT" ]]; then
	printf "[$prog]: failed to execute \"${tctarget}\"\n"
	cleanUpExit 7
fi
