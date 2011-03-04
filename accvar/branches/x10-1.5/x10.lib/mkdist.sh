#!/usr/bin/ksh

#
# (c) Copyright IBM Corporation 2007
#
# $Id: mkdist.sh,v 1.4 2007-06-27 18:08:19 srkodali Exp $
# Script for making distribution.
#

# required commands
PWD=`which pwd`
TAR=`which tar`
GZIP=`which gzip`
MKDIR=`which mkdir`
RM=`which rm`
BASENAME=`which basename`
MV=`which mv`
CP=`which cp`
CAT=`which cat`

# version
MAJOR_VERSION=0
MINOR_VERSION=5

# tmp dir
TMP=`${PWD}`/tmp

# src dirs
SRC_INC_DIR=${SRC_TOP}/include/x10
SRC_LIB_DIR=${SRC_TOP}/lib
SRC_DOC_DIR=${SRC_TOP}/doc

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

mkDist() {

    # check for src dirs and files
    if [ ! -d ${SRC_INC_DIR} -o ! -d ${SRC_LIB_DIR} ]
    then
    	echo "Run \'\$./config.sh\' in ${SRC_TOP} before you run this script..."
		return 1
    fi
    
    if [ ! -f ${SRC_LIB_DIR}/${X10LIB} ]
    then
    	echo "Run \'\$make all\' followed by \"\$make install\' in ${SRC_TOP} before you run this script..."
    	return 1
    fi
    
    # create tmp dir
    if [ ! -d ${TMP} ]
    then
    	echo "${MKDIR} -p ${TMP}"
    	${MKDIR} -p ${TMP}
    fi
    
    # create dest dirs
    if [ ! -d ${DEST_TOP} ]
    then
    	echo "${MKDIR} -p ${DEST_TOP}"
    	${MKDIR} -p ${DEST_TOP}
    fi
    
    if [ ! -d ${DEST_INC_DIR} ]
    then
    	echo "${MKDIR} -p ${DEST_INC_DIR}"
    	${MKDIR} -p ${DEST_INC_DIR}
    fi
    
    if [ ! -d ${DEST_LIB_DIR} ]
    then
    	echo "${MKDIR} -p ${DEST_LIB_DIR}"
    	${MKDIR} -p ${DEST_LIB_DIR}
    fi
    
    if [ ! -d ${DEST_DOC_DIR} ]
    then
    	echo "${MKDIR} -p ${DEST_DOC_DIR}"
    	${MKDIR} -p ${DEST_DOC_DIR}
    fi
    
    # copy distrib files to dest dirs
    for i in ${SRC_INC_DIR}/*.h ${SRC_INC_DIR}/*.tcc
    do
    	BASEFILE=`${BASENAME} ${i}`
    	echo "${CP} -p ${i} ${DEST_INC_DIR}/${BASEFILE}"
    	${CP} -p ${i} ${DEST_INC_DIR}/${BASEFILE}
    done
    
    echo "${CP} -p ${SRC_LIB_DIR}/${X10LIB} ${DEST_LIB_DIR}/${X10LIB}"
    ${CP} -p ${SRC_LIB_DIR}/${X10LIB} ${DEST_LIB_DIR}/${X10LIB}
    
    for i in ${SRC_DOC_DIR}/*.txt ${SRC_DOC_DIR}/*.pdf
    do
    	BASEFILE=`${BASENAME} ${i}`
    	echo "${CP} -p ${i} ${DEST_DOC_DIR}/${BASEFILE}"
    	${CP} -p ${i} ${DEST_DOC_DIR}/${BASEFILE}
    done
    
    #echo "${CP} -p ${SRC_TOP}/RELNOTES ${DEST_TOP}/RELNOTES"
    #${CP} -p ${SRC_TOP}/RELNOTES ${DEST_TOP}/RELNOTES
    #echo "${CP} -p ${SRC_TOP}/LICENSE ${DEST_TOP}/LICENSE"
    #${CP} -p ${SRC_TOP}/LICENSE ${DEST_TOP}/LICENSE
    
    # create distrib arch file
    ( \
		echo "cd ${TMP}" ; \
    	cd ${TMP} ; \
    	echo "${TAR} -c -v -f ${DIST_TAR} ${DIST_PREF}/* "; \
    	${TAR} -c -v -f ${DIST_TAR} ${DIST_PREF}/* ; \
    	echo "${GZIP} -9 ${DIST_TAR}" ; \
    	${GZIP} -9 ${DIST_TAR} ; \
    )
    
    # move distrib to final location
    if [ ! -d ${DIST_TOP} ]
    then
    	echo "${MKDIR} -p ${DIST_TOP}"
    	${MKDIR} -p ${DIST_TOP}
    fi
    echo "${MV} -f ${TMP}/${DIST_NAME} ${DIST_TOP}"
    ${MV} -f ${TMP}/${DIST_NAME} ${DIST_TOP}
    
    # clean and remove tmp files and dirs
    echo "${RM} -r -f ${TMP}"
    ${RM} -r -f ${TMP}

	return 0
}
