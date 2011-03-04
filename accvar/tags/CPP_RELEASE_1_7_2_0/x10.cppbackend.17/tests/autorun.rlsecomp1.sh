#!/usr/bin/sh

#
# (c) Copyright IBM Corporation 2008
#
# $Id: autorun.rlsecomp1.sh,v 1.1.1.1 2009-02-19 14:32:54 pvarma Exp $
# This file is part of X10/C++ Test Harness.


# X10_HOME, X10_PLATFORM, JAVA_HOME, ANT_HOME
. ~/.profile
cd $X10_HOME/x10.backend/tests
# Just a copy of testScript.sh in case cvs update overrides
./xtestScript.sh
cd ~
