#!/usr/bin/bash

export MP_MSG_API=lapi
export MP_EUILIB=us

make 

echo "syntax: ./run.sh log_table_size num_procs host_XXX.list (e.g. XXX=r36n11)"

poe ./RandomAccess_spmd.exe -o -m $1 -procs $2 -hostfile /u/gbikshan/Work2/x10.lib/hostfiles/$3  >> out.$1.$2

poe ./RandomAccess_spmd.1.exe -o -m $1 -procs $2 -hostfile /u/gbikshan/Work2/x10.lib/hostfiles/$3  >> out.$1.$2

poe ./RandomAccess_func.exe -o -m $1 -procs $2 -hostfile /u/gbikshan/Work2/x10.lib/hostfiles/$3  >> out.$1.$2

a=$(($1+2))
poe ./gups.exe -a $1 $a -procs $2 -hostfile /u/gbikshan/Work2/x10.lib/hostfiles/$3  >> out.$1.$2

