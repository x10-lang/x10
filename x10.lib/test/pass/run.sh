#! /usr/bin/ksh

export MP_MSG_API=lapi
export MP_EUILIB=us

if [ $# -ne 2 ]
then
	echo "Syntax: ./run.sh <numprocs> <hostfile>"
	exit 1
fi

poe ./Test_array.exe -procs $1 -hostfile $2
poe ./Test_array_async.exe -procs $1 -hostfile $2
poe ./Test_async.exe -procs $1 -hostfile $2
poe ./Test_async_agg.exe -procs $1 -hostfile $2
poe ./Test_dist.exe -procs $1 -hostfile $2
poe ./Test_point.exe -procs $1 -hostfile $2
poe ./Test_region.exe -procs $1 -hostfile $2
poe ./Test_tiled_region.exe -procs $1 -hostfile $2

poe ./Test_async_c.exe -procs $1 -hostfile $2
poe ./Test_async_agg_c.exe -procs $1 -hostfile $2
