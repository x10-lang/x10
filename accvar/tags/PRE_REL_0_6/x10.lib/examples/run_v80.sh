#! /usr/bin/ksh

#
# (c) Copyright IBM Corporation 2007
#
# $Id: run_v80.sh,v 1.5 2007-06-27 14:48:33 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Run script for "v80n01.pbm.ihost.com" Cluster.

export MP_MSG_API=lapi
export MP_EUILIB=us
export MP_EUIDEVICE=sn_all

echo "Running Example 1 (hello.cc).....\n"
echo "poe ./hello_cc -procs 2 -hostfile ../hostfiles/host_v80.list"
poe ./hello_cc -procs 2 -hostfile ../hostfiles/host_v80.list
echo "\n.....done"

echo "\nRunning Example 2 (hello.c).....\n"
echo "poe ./hello_c -procs 2 -hostfile ../hostfiles/host_v80.list"
poe ./hello_c -procs 2 -hostfile ../hostfiles/host_v80.list
echo "\n.....done"

echo "\nRunning Example 3 (addr_c.c).....\n"
echo "poe ./addr_c -procs 2 -hostfile ../hostfiles/host_v80.list"
poe ./addr_c -procs 2 -hostfile ../hostfiles/host_v80.list
echo "\n.....done"

echo "\nRunning Example 4 (recv_c.c).....\n"
echo "poe ./recv_c -procs 2 -hostfile ../hostfiles/host_v80.list"
poe ./recv_c -procs 2 -hostfile ../hostfiles/host_v80.list
echo "\n.....done"

echo "\nRunning Example 5 (addr_cc.cc).....\n"
echo "poe ./addr_cc -procs 2 -hostfile ../hostfiles/host_v80.list"
poe ./addr_cc -procs 2 -hostfile ../hostfiles/host_v80.list
echo "\n.....done"

echo "\nRunning Example 6 (recv_cc.cc).....\n"
echo "poe ./recv_cc -procs 2 -hostfile ../hostfiles/host_v80.list"
poe ./recv_cc -procs 2 -hostfile ../hostfiles/host_v80.list
echo "\n.....done"

echo "\nRunning Example 7 (acc_ret_c.c).....\n"
echo "poe ./acc_ret_c -procs 2 -hostfile ../hostfiles/host_v80.list"
poe ./acc_ret_c -procs 2 -hostfile ../hostfiles/host_v80.list
echo "\n.....done"

echo "\nRunning Example 8 (acc_ret_cc.cc).....\n"
echo "poe ./acc_ret_cc -procs 2 -hostfile ../hostfiles/host_v80.list"
poe ./acc_ret_c -procs 2 -hostfile ../hostfiles/host_v80.list
echo "\n.....done"
