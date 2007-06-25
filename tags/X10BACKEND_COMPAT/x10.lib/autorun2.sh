#!/usr/bin/sh


#
# (c) Copyright IBM Corporation 2007
#
# $Id: autorun2.sh,v 1.2 2007-06-07 06:35:22 srkodali Exp $
# Toplevel script for running periodically autorun.sh.
#

X10LIB_HOME=/u/srkodali/src/build/auto/x10.lib
AUTORUN=${X10LIB_HOME}/autorun.sh
CVSROOT=:pserver:anonymous@x10.cvs.sourceforge.net:/cvsroot/x10

if [ -d ${X10LIB_HOME} -a -f ${AUTORUN} ]
then
	cd ${X10LIB_HOME}
	cvs update
	${AUTORUN}
fi
