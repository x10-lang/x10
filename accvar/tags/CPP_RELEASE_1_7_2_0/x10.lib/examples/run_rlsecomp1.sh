#! /usr/bin/ksh

#
# (c) Copyright IBM Corporation 2007
#
# $Id: run_rlsecomp1.sh,v 1.4 2007-12-10 17:22:27 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Run script for "rlsecomp1.watson.ihost.com" SMP system.

export MP_MSG_API=mpi,lapi
export MP_EUILIB=ip

echo "Running Example 1 (hello.cc).....\n"
echo "poe ./hello_cc -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list"
poe ./hello_cc -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list
echo "\n.....done"

echo "\nRunning Example 2 (hello.c).....\n"
echo "poe ./hello_c -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list"
poe ./hello_c -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list
echo "\n.....done"

echo "\nRunning Example 3 (addr_c.c).....\n"
echo "poe ./addr_c -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list"
poe ./addr_c -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list
echo "\n.....done"

echo "\nRunning Example 4 (recv_c.c).....\n"
echo "poe ./recv_c -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list"
poe ./recv_c -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list
echo "\n.....done"

echo "\nRunning Example 5 (addr_cc.cc).....\n"
echo "poe ./addr_cc -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list"
poe ./addr_cc -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list
echo "\n.....done"

echo "\nRunning Example 6 (recv_cc.cc).....\n"
echo "poe ./recv_cc -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list"
poe ./recv_cc -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list
echo "\n.....done"

echo "\nRunning Example 7 (acc_ret_c.c).....\n"
echo "poe ./acc_ret_c -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list"
poe ./acc_ret_c -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list
echo "\n.....done"

echo "\nRunning Example 8 (acc_ret_cc.cc).....\n"
echo "poe ./acc_ret_cc -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list"
poe ./acc_ret_c -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list
echo "\n.....done"

echo "\nRunning Example 9 (async_cc.cc).....\n"
echo "poe ./async_cc -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list"
poe ./async_cc -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list
echo "\n.....done"

echo "\nRunning Example 10 (dist_array_onesided_cc.cc).....\n"
echo "poe ./dist_array_onesided_cc -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list"
poe ./dist_array_onesided_cc -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list
echo "\n.....done"

echo "\nRunning Example 11 (dist_array_spmd_cc.cc).....\n"
echo "poe ./dist_array_spmd_cc -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list"
poe ./dist_array_spmd_cc -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list
echo "\n.....done"

echo "\nRunning Example 12 (finish_cc.cc).....\n"
echo "poe ./finish_cc -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list"
poe ./finish_cc -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list
echo "\n.....done"

echo "\nRunning Example 13 (reduce_cc.cc).....\n"
echo "poe ./reduce_cc -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list"
poe ./reduce_cc -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list
echo "\n.....done"

echo "\nRunning Example 14 (region_cc.cc).....\n"
echo "poe ./region_cc -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list"
poe ./region_cc -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list
echo "\n.....done"

echo "\nRunning Example 15 (async_array_put_cc.cc).....\n"
echo "poe ./async_array_put_cc -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list"
poe ./async_array_put_cc -procs 2 -hostfile ../hostfiles/host_rlsecomp1.list
echo "\n.....done"
