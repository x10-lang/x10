#!/usr/bin/ksh

#
# (c) Copyright IBM Corporation 2007
#
# $Id: sched_test.sh,v 1.1 2007-06-08 13:51:45 srkodali Exp $
# Script for testing the X10Lib's scheduler functionality.
#

schedTest() {
	RC=0
ENABLE=0
if [ ${ENABLE} -eq 1 ]
then
	for i in Test_sched
	do
		let "TOTAL_TESTS = TOTAL_TESTS + 1"
		CMD=${SRC_TEST}/sched/${i}
		CMDFILE=${SRC_TEST}/sched/data/${i}.cmd
		DATAFILE=${SRC_TEST}/sched/data/${i}.dat
		echo "\nTesting ${CMD}...\n"
		echo "${KSH} ${CMDFILE} ${CMD} > ${RESFILE}\n"
		${KSH} ${CMDFILE} ${CMD} > ${RESFILE}
		status=$?
		if [ $status -ne 0 ]
		then
			echo "Error in executing ${CMD}...\n"
			let "FAILED_TESTS = FAILED_TESTS + 1"
			RC=1
			continue
		fi
		echo "${DIFF} ${DATAFILE} ${RESFILE}\n"
		${DIFF} ${DATAFILE} ${RESFILE}
		status=$?
		if [ $status -ne 0 ]
		then
			echo "Failed in test ${CMD}...\n"
			echo "<<Expected Output>>\n"
			cat ${DATAFILE}
			echo "<<Tested Output>>\n"
			cat ${RESFILE}
			let "FAILED_TESTS = FAILED_TESTS + 1"
			RC=1
			continue
		fi
		echo "...OK\n"
		let "OK_TESTS = OK_TESTS + 1"
	done
fi
	return $RC
}
