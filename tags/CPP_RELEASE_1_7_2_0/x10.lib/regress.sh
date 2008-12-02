#!/usr/bin/ksh

#
# (c) Copyright IBM Corporation 2007
#
# $Id: regress.sh,v 1.5 2007-11-13 05:28:49 ganeshvb Exp $
# Script for starting the test harness.
#

SRC_TEST=${SRC_TOP}/test
CORE_TEST=${SRC_TEST}/core_test.sh
ARRAY_TEST=${SRC_TEST}/array/array_test.sh
SCHED_TEST=${SRC_TEST}/sched/sched_test.sh
HOSTFILE=host.list
TMP=/tmp
RESFILE=${TMP}/results.$$.dat
FAILED_CORE_TESTS=0
FAILED_ARRAY_TESTS=0
FAILED_SCHED_TESTS=0
KSH=`which ksh`
DIFF=`which diff`

regress() {
	export MP_MSG_API=lapi
	echo "\n>> Testing X10Lib's Core Functionality...\n"
	echo "Using ${HOSTFILE}...\n"
	export MP_HOSTFILE=${HOSTFILE}
	. ${CORE_TEST}
	coreTest
	status=$?
	let "FAILED_CORE_TESTS = FAILED_TESTS"
	if [ $status -ne 0 ]
	then
		echo "*****  ${FAILED_CORE_TESTS} CORE TESTS FAILED  *****"
	fi
	echo "\n...done.\n"
	
	echo "\n>> Testing X10Lib's Array Functionality...\n"
	. ${ARRAY_TEST}
	arrayTest
	status=$?
	let "FAILED_ARRAY_TESTS = FAILED_TESTS - FAILED_CORE_TESTS"
	if [ $status -ne 0 ]
	then
		echo "***** ${FAILED_ARRAY_TESTS} ARRAY TESTS FAILED *****"
	fi
	echo "\n...done.\n"
	
	echo "\n>> Testing X10Lib's Scheduler Functionality...\n"
	. ${SCHED_TEST}
	schedTest
	status=$?
	let "FAILED_SCHED_TESTS = FAILED_TESTS - FAILED_ARRAY_TESTS - FAILED_CORE_TESTS"
	if [ $status -ne 0 ]
	then
		echo "***** ${FAILED_SCHED_TESTS} SCHED TESTS FAILED *****"
	fi
	echo "\n...done.\n"

	rm -f ${RESFILE}
	if [ $FAILED_TESTS -ne 0 ]
	then
		return 1
	fi
	return 0
}

