#!/usr/bin/sh


#
# (c) Copyright IBM Corporation 2007
#
# $Id: autorun2.sh,v 1.1 2007-06-05 14:18:59 srkodali Exp $
# Toplevel script for running periodically autorun.sh.
#

X10LIB_HOME=/u/srkodali/src/work/x10.lib
AUTORUN=${X10LIB_HOME}/autorun.sh

if [ -d ${X10LIB_HOME} -a -f ${AUTORUN} ]
then
	cd ${X10LIB_HOME}
	${AUTORUN}
fi
