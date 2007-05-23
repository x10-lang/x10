#! /usr/bin/ksh


make all;

export MP_MSG_API=lapi
export MP_EUILIB=us

echo "Syntax: ./run.sh <numprocs> <hostfile>"

poe ./Test_array.exe -procs $1 -hostfile $2
poe ./Test_array_async.exe -procs $1 -hostfile $2
poe ./Test_async.exe -procs $1 -hostfile $2
poe ./Test_async_agg.exe -procs $1 -hostfile $2
poe ./Test_dist.exe -procs $1 -hostfile $2
poe ./Test_point.exe -procs $1 -hostfile $2
poe ./Test_region.exe -procs $1 -hostfile $2
poe ./Test_tiled_region.exe -procs $1 -hostfile $2
