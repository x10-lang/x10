#
# (c) Copyright IBM Corporation 2007
# $Id: environ.sh,v 1.1 2007-08-02 13:09:59 srkodali Exp $
# This file is part of X10 Applications.
#

## Script for setting table size, hostfile, and other
## POE/MPI/LAPI environment variables.

# (RandomAccess) table size
RA_TABLE_SIZE=${RA_TABLE_SIZE}
if [ -z "${RA_TABLE_SIZE}" ]
then
	RA_TABLE_SIZE=26
fi

# (RandomAccess) maximum number of program tasks
RA_MAX_TASKS=${RA_MAX_TASKS}
if [ -z "${RA_MAX_TASKS}" ]
then
	RA_MAX_TASKS=64
fi

# (RandomAccess) maximum number of program runs
RA_MAX_RUNS=${RA_MAX_RUNS}
if [ -z "${RA_MAX_RUNS}" ]
then
	RA_MAX_RUNS=3
fi


## POE - partition manger control

# hostfile
MP_HOSTFILE=${MP_HOSTFILE}
if [ -z "${MP_HOSTFILE}" ]
then
	export MP_HOSTFILE=`pwd`/../hostfiles/host_v20.list
fi

export MP_ADAPTER_USE=dedicated
export MP_CPU_USE=unique
# user space protocol
export MP_EUIDEVICE=sn_all
export MP_EUILIB=us
# ip protocol
#export MP_EUIDEVICE=en0
#export MP_EUILIB=ip

## Set this in run script.
#export MP_MSG_API=mpi


## POE - job specification

export MP_PGMMODEL=spmd
export MP_TASK_AFFINITY=MCM


## POE - i/o control

#export MP_LABELIO=yes


## MPI

# Eager/Rendezvous protocol
#export MP_BUFFER_MEM=64M
#export MP_EAGER_LIMIT=32768

# Time source
#export MP_CLOCK_SOURCE=SWITCH

export MP_CSS_INTERRUPT=no
export MP_SHARED_MEMORY=yes
export MP_WAIT_MODE=poll
export MP_SINGLE_THREAD=yes


## These also apply to LAPI.

#export MP_ACK_THRESH=30
#export MP_POLLING_INTERAL=400000
#export MP_UDP_PACKET_SIZE=8192

# Uncomment the following for RDMA
#export MP_USE_BULK_XFER=yes
#export MP_BULK_MIN_MSG_SIZE=16384


## LAPI

# Enable LAPI's shared memory
export LAPI_USE_SHM=yes
