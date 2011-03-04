#! /usr/bin/ksh

#
# (c) Copyright IBM Corporation 2007
#
# $Id: run_r36n11.sh,v 1.2 2007-04-27 12:54:54 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Run script for "r36n11.pbm.ihost.com" SMP system.

export MP_MSG_API=mpi,lapi
export MP_EUILIB=ip

echo "Running Example 1 (hello.cc).....\n"
echo "poe ./hello_cc -procs 2 -hostfile ../hostfiles/host_r36n11.list"
poe ./hello_cc -procs 2 -hostfile ../hostfiles/host_r36n11.list
echo "\n.....done"

echo "Running Example 2 (hello.c).....\n"
echo "poe ./hello_c -procs 2 -hostfile ../hotfiles/host_r36n11.list"
poe ./hello_c -procs 2 -hostfile ../hostfiles/host_r36n11.list
echo "\n.....done"
