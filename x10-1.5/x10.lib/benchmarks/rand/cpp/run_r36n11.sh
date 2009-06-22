#! /usr/bin/ksh

#
# (c) Copyright IBM Corporation 2007
#
# $Id: run_r36n11.sh,v 1.1 2007-04-27 12:54:53 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Run script for "r36n11.pbm.ihost.com" SMP system.

export MP_MSG_API=mpi,lapi
export MP_EUILIB=ip

echo "Running GUPS on \"r36n11.pbm.ihost.com\" .....\n"
echo "poe ./gups 19 -procs 2 -hostfile ../../../hostfiles/host_r36n11.list"
poe ./gups 19 -procs 2 -hostfile ../../../hostfiles/host_r36n11.list
echo "\n..... done"
