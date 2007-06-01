#!/usr/bin/ksh

#
# $Id: autorun.sh,v 1.1 2007-06-01 15:14:21 srkodali Exp $
# Script for automating the build and testing process.
#

SRC_TOP=`pwd`
LOGFILE=${SRC_TOP}/autorun.log
BUILD=${SRC_TOP}/build.sh
BUILD_LOG=${SRC_TOP}/build.log
MKDIST=${SRC_TOP}/mkdist.sh
MKDIST_LOG=${SRC_TOP}/mkdist.log

#mail results
mailResults() {
	cat ${LOGFILE} | \
	mail -s "!!! X10Lib autorun results on `date` !!!" \
	vsaraswa@us.ibm.com
}

# start logging the autorun process
echo "##### X10Lib - The X10 Runtime System #####\n" > ${LOGFILE}
echo "\n***** Initiating automatic build and testing at `hostname` on `date`...\n" >> ${LOGFILE} 
# do build
echo "\n*** Build: ${BUILD}\n" >> ${LOGFILE}
${BUILD} 2>&1 >> ${LOGFILE}
status=$?
cat ${BUILD_LOG} >> ${LOGFILE}
if [ $status -ne 0 ]
then
	echo "\n...failed" >> ${LOGFILE}
	mailResults
	exit 1
fi

# make distribution
echo "\n*** MkDist: ${MKDIST}\n" >> ${LOGFILE}
${MKDIST} 2>&1 >> ${LOGFILE}
status=$?
cat ${MKDIST_LOG} >> ${LOGFILE}
if [ $status -ne 0 ]
then
	echo "\n...failed" >> ${LOGFILE}
	mailResults
	exit 1
fi

echo "\n...done successfully autorun on `date`." >> ${LOGFILE}
mailResults
exit 0
