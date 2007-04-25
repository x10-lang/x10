#! /usr/bin/ksh

#
# (c) Copyright IBM Corporation 2007
#
# $Id: run_v80.sh,v 1.1.1.1 2007-04-25 09:57:46 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Run script for "v80n01.pbm.ihost.com" Cluster.

export MP_MSG_API=mpi,lapi
export MP_EUILIB=us
export MP_EUIDEVICE=sn_all

echo "Running Example 1 (hello.cc).....\n"
echo "poe ./hello_cc -procs 2 -hostfile ./host_v80.list"
poe ./hello_cc -procs 2 -hostfile ./host_v80.list
echo "\n.....done"

echo "Running Example 2 (hello.c).....\n"
echo "poe ./hello_c -procs 2 -hostfile ./host_v80.list"
poe ./hello_c -procs 2 -hostfile ./host_v80.list
echo "\n.....done"
