#!/usr/bin/ksh

#
# $Id: mkdist.sh,v 1.1 2007-06-01 12:58:25 srkodali Exp $
# Script for making distribution.
#

# commands
MKDIR=`which mkdir`
TAR=`which tar`
RM=`which rm`
BASENAME=`which basename`
GZIP=`which gzip`
PWD=`which pwd`
ECHO=`which echo`
MV=`which mv`
CP=`which cp`
CAT=`which cat`

# version
MAJOR_VERSION=0
MINOR_VERSION=1

# tmp dir
TMP=`${PWD}`/tmp

# src dirs
SRC_TOP=`${PWD}`
SRC_INC_DIR=${SRC_TOP}/include/x10
SRC_LIB_DIR=${SRC_TOP}/lib
SRC_DOC_DIR=${SRC_TOP}/doc

# log file
LOGFILE=${SRC_TOP}/mkdist.log

# distrib vars
LIB_PREF=libx10
DIST_PREF=${LIB_PREF}-${MAJOR_VERSION}.${MINOR_VERSION}
DIST_TAR=${DIST_PREF}.tar
DIST_NAME=${DIST_TAR}.gz
DIST_TOP=${SRC_TOP}/distfiles

# dest dirs
DEST_TOP=${TMP}/${DIST_PREF}
DEST_INC_DIR=${DEST_TOP}/include
DEST_LIB_DIR=${DEST_TOP}/lib
DEST_DOC_DIR=${DEST_TOP}/doc

# distrib files
X10LIB=libx10.a

# start logging
echo "Making ${DIST_NAME} on `date`...\n" >> ${LOGFILE}

# check for src dirs and files
if [ ! -d ${SRC_INC_DIR} -o ! -d ${SRC_LIB_DIR} ]
then
	${ECHO} "Run \'\$ ./config.sh\' followed by \'\$ make all\' in ${SRC_TOP} before you run this script..." >> ${LOGFILE}
	exit 1
fi

if [ ! -f ${SRC_LIB_DIR}/${X10LIB} ]
then
	${ECHO} "Run \'\$ make all\' in ${SRC_TOP} before you run this script..." >> ${LOGFILE}
	exit 1
fi

# create tmp dir
if [ ! -d ${TMP} ]
then
	echo "${MKDIR} -p ${TMP}" >> ${LOGFILE}
	${MKDIR} -p ${TMP}
fi

# create dest dirs
if [ ! -d ${DEST_TOP} ]
then
	echo "${MKDIR} -p ${DEST_TOP}" >> ${LOGFILE}
	${MKDIR} -p ${DEST_TOP}
fi

if [ ! -d ${DEST_INC_DIR} ]
then
	echo "${MKDIR} -p ${DEST_INC_DIR}" >> ${LOGFILE}
	${MKDIR} -p ${DEST_INC_DIR}
fi

if [ ! -d ${DEST_LIB_DIR} ]
then
	echo "${MKDIR} -p ${DEST_LIB_DIR}" >> ${LOGFILE}
	${MKDIR} -p ${DEST_LIB_DIR}
fi

if [ ! -d ${DEST_DOC_DIR} ]
then
	echo "${MKDIR} -p ${DEST_DOC_DIR}" >> ${LOGFILE}
	${MKDIR} -p ${DEST_DOC_DIR}
fi

# copy distrib files to dest dirs
for i in ${SRC_INC_DIR}/*.h ${SRC_INC_DIR}/*.tcc
do
	BASEFILE=`${BASENAME} ${i}`
	echo "${CP} -p ${i} ${DEST_INC_DIR}/${BASEFILE}" >> ${LOGFILE}
	${CP} -p ${i} ${DEST_INC_DIR}/${BASEFILE}
done

echo "${CP} -p ${SRC_LIB_DIR}/${X10LIB} ${DEST_LIB_DIR}/${X10LIB}" >> ${LOGFILE}
${CP} -p ${SRC_LIB_DIR}/${X10LIB} ${DEST_LIB_DIR}/${X10LIB}

for i in ${SRC_DOC_DIR}/*.txt ${SRC_DOC_DIR}/*.pdf
do
	BASEFILE=`${BASENAME} ${i}`
	echo "${CP} -p ${i} ${DEST_DOC_DIR}/${BASEFILE}" >> ${LOGFILE}
	${CP} -p ${i} ${DEST_DOC_DIR}/${BASEFILE}
done

#echo "${CP} -p ${SRC_TOP}/RELNOTES ${DEST_TOP}/RELNOTES" >> ${LOGFILE}
#${CP} -p ${SRC_TOP}/RELNOTES ${DEST_TOP}/RELNOTES
#echo "${CP} -p ${SRC_TOP}/LICENSE ${DEST_TOP}/LICENSE" >> ${LOGFILE}
#${CP} -p ${SRC_TOP}/LICENSE ${DEST_TOP}/LICENSE

# create distrib arch file
(echo "cd ${TMP}" >> ${LOGFILE}; \
cd ${TMP}; \
echo "${TAR} -c -v -f ${DIST_TAR} ${DIST_PREF}/* " >> ${LOGFILE}; \
${TAR} -c -v -f ${DIST_TAR} ${DIST_PREF}/* 2>&1 >> ${LOGFILE}; \
echo "${GZIP} -9 ${DIST_TAR}" >> ${LOGFILE}; \
${GZIP} -9 ${DIST_TAR} 2>&1 >> ${LOGFILE}; \
)

# move distrib to final location
if [ ! -d ${DIST_TOP} ]
then
	echo "${MKDIR} -p ${DIST_TOP}" >> ${LOGFILE}
	${MKDIR} -p ${DIST_TOP}
fi
echo "${MV} -f ${TMP}/${DIST_NAME} ${DIST_TOP}" >> ${LOGFILE}
${MV} -f ${TMP}/${DIST_NAME} ${DIST_TOP}

# clean and remove tmp files and dirs
echo "${RM} -r -f ${TMP}" >> ${LOGFILE}
${RM} -r -f ${TMP}

# out completion statement
${ECHO} "\n...successfully created ${DIST_NAME} under ${DIST_TOP} on `date`." >> ${LOGFILE}
exit 0
