#! /usr/bin/ksh

#
# (c) Copyright IBM Corporation 2007
#
# $Id: run_rlsecomp1.sh,v 1.1 2007-04-27 12:54:53 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Run script for "rlsecomp1.watson.ibm.com" SMP system.

export MP_MSG_API=mpi,lapi
export MP_EUILIB=ip

echo "Running GUPS on \"rlsecomp1.watson.ibm.com\" .....\n"
echo "poe ./gups -s 19 -procs 2 -hostfile ../../../hostfiles/host_rlsecomp1.list"
poe ./gups -s 19 -procs 2 -hostfile ../../../hostfiles/host_rlsecomp1.list
echo "\n..... done"
