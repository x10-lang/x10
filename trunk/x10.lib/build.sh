#!/usr/bin/ksh

#
# $Id: build.sh,v 1.1 2007-06-01 14:24:50 srkodali Exp $
# Script for building the library.
#

SRC_TOP=`pwd`
LOGFILE=${SRC_TOP}/build.log
CONFIGURE=${SRC_TOP}/config.sh
MAKE=`which make`
LIBNAME=libx10.a

bailOut() {
	if [ $1 -ne 0 ]
	then
		echo "\n...failed" >> ${LOGFILE}
		exit 1
	else
		echo "\n...done" >> ${LOGFILE}
	fi
}

# start logging the build process
echo "\n>> Building ${LIBNAME} on `date`...\n" > ${LOGFILE}

# clean the distribution
echo "\n>>> Preparing the distribution for configuration..." >> ${LOGFILE}
${MAKE} realclean 2>&1 >> ${LOGFILE}
bailOut $?

# run configure on the dir hier
echo "\n>>> Configuring the source..." >> ${LOGFILE}
${CONFIGURE} 2>&1 >> ${LOGFILE}
bailOut $?

# build and install the library in local dir hier
echo "\n>>> Building the library..." >> ${LOGFILE}
${MAKE} all 2>&1 >> ${LOGFILE}
bailOut $?
echo "\n>>> Copying out the library..." >> ${LOGFILE}
${MAKE} install 2>&1 >> ${LOGFILE}
bailOut $?

# build example programs and benchmarks
echo "\n>>> Building the example programs..." >> ${LOGFILE}
${MAKE} samples 2>&1 >> ${LOGFILE}
bailOut $?
echo "\n>>> Building the benchmarks..." >> ${LOGFILE}
${MAKE} bench 2>&1 >> ${LOGFILE}
bailOut $?

# build the test programs
echo "\n>> Building the test programs..." >> ${LOGFILE}
${MAKE} test 2>&1 >> ${LOGFILE}
bailOut $?

echo "\n...successfully completed on `date`." >> ${LOGFILE}
exit 0
