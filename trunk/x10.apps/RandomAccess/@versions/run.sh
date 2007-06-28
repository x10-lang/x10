#!/usr/bin/bash

export MP_MSG_API=lapi
export MP_EUILIB=us

make 

echo "syntax: ./run.sh log_table_size num_procs host_XXX.list (e.g. XXX=r36n11)"

poe ./RandomAccess_spmd.v01.exe -o -m $1 -procs $2 -hostfile /u/gbikshan/Work2/x10.lib/hostfiles/$3  >> out.$1.$2
poe ./RandomAccess_spmd.v02.exe -o -m $1 -procs $2 -hostfile /u/gbikshan/Work2/x10.lib/hostfiles/$3  >> out.$1.$2
poe ./RandomAccess_spmd.v03.exe -o -m $1 -procs $2 -hostfile /u/gbikshan/Work2/x10.lib/hostfiles/$3  >> out.$1.$2


