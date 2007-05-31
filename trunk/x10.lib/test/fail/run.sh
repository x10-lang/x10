#! /usr/bin/ksh

export MP_MSG_API=lapi,mpi
export MP_EUILIB=ip

if [ $# -ne 2 ]
then
	echo "Syntax: ./run.sh <numprocs> <hostfile>"
	exit 1
fi

poe ./Test_point.exe -procs $1 -hostfile $2
poe ./Test_region.exe -procs $1 -hostfile $2
