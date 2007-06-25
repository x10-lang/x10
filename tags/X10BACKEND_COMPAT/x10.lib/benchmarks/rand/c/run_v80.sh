#! /usr/bin/ksh

#
# (c) Copyright IBM Corporation 2007
#
# $Id: run_v80.sh,v 1.1 2007-04-27 12:54:53 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Run script for "v80n01.pbm.ihost.com" SMP system.

export MP_MSG_API=mpi,lapi
export MP_EUILIB=us
export MP_EUIDEVICE=sn_all

echo "Running GUPS on \"v80n01.pbm.ihost.com\" .....\n"
echo "poe ./gups -s 19 -procs 2 -hostfile ../../../hostfiles/host_v80.list"
poe ./gups -s 19 -procs 2 -hostfile ../../../hostfiles/host_v80.list
echo "\n..... done"
