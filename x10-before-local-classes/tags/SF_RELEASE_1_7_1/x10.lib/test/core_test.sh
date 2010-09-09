#!/usr/bin/ksh

#
# (c) Copyright IBM Corporation 2007
#
# $Id: core_test.sh,v 1.4 2007-10-11 10:55:59 ganeshvb Exp $
# Script for testing the X10Lib's core functionality.
#

coreTest() {
	RC=0
	for i in Test_async Test_async_agg Test_async_c \
			Test_async_agg_c Test_async_agg_func \
                        Test_switch Test_switch_c Test_finish Test_broadcast 
	do
		let "TOTAL_TESTS = TOTAL_TESTS + 1"
		CMD=${SRC_TEST}/${i}
		CMDFILE=${SRC_TEST}/data/${i}.cmd
		DATAFILE=${SRC_TEST}/data/${i}.dat
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
			echo "\nFailed in test ${CMD}...\n"
			echo "<<Expected Output>>\n"
			cat ${DATAFILE}
			echo "<<Test Output>>"
			cat ${RESFILE}
			let "FAILED_TESTS = FAILED_TESTS + 1"
			RC=1
			continue
		fi
		echo "...OK\n"
		let "OK_TESTS = OK_TESTS + 1"
	done
	return $RC
}
