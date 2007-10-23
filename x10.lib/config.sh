#! /usr/bin/ksh

#
# (c) Copyright IBM Corporation 2007
#
# $Id: config.sh,v 1.6 2007-10-23 17:29:12 ipeshansky Exp $
# This file is part of X10 Runtime System.
#

## Configure script for building X10Lib.

# create target include and lib dirs.
MKDIR=mkdir
INCDIR=include
if [ ! -d ${INCDIR} ]
then
	echo "${MKDIR} ${INCDIR}"
	${MKDIR} ${INCDIR}
fi

X10IDIR=${INCDIR}/x10
if [ ! -d ${X10IDIR} ]
then
	echo "${MKDIR} ${X10IDIR}"
	${MKDIR} ${X10IDIR}
fi
X10XWSDIR=${INCDIR}/x10/xws
if [ ! -d ${X10XWSDIR} ]
then
	echo "${MKDIR} ${X10XWSDIR}"
	${MKDIR} ${X10XWSDIR}
fi

LIBDIR=lib
if [ ! -d ${LIBDIR} ]
then
	echo "${MKDIR} ${LIBDIR}"
	${MKDIR} ${LIBDIR}
fi

# create header file links.
TOPDIR=`pwd`
SRCDIR=`cd src && pwd`
ARRAYDIR=${SRCDIR}/array
SCHEDDIR=${SRCDIR}/sched
LN=ln
FIND=find
echo "cd ${X10IDIR}"
cd "${X10IDIR}"
for i in "${SRCDIR}" "${ARRAYDIR}"
do
	${FIND} "${i}" -maxdepth 1 -name CVS -prune -o -type f '(' -name '*.h' -o -name '*.tcc' ')' -exec ${LN} -s -f {} . ';'
done
cd ${TOPDIR}
echo "cd ${X10XWSDIR}"
cd ${X10XWSDIR}
for i in ${SCHEDDIR}
do
	${FIND} "${i}" -maxdepth 1 -name CVS -prune -o -type f '(' -name '*.h' -o -name '*.tcc' ')' -exec ${LN} -s -f {} . ';'
done
echo "cd ${TOPDIR}"
cd ${TOPDIR}
