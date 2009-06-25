#!/usr/bin/bash

export MP_MSG_API=lapi
export MP_EUILIB=us

make 

echo "syntax: ./run.sh log_table_size num_procs host_XXX.list (e.g. XXX=r36n11)"

poe ./RandomAccess_spmd.exe -o  -m $1 -procs $2 >> out.$1.$2

#poe @versions/RandomAccess_spmd.v01.exe -o  -m $1 -procs $2  >> out.$1.$2
poe @versions/RandomAccess_spmd.v02.exe -o  -m $1 -procs $2 >> out.$1.$2
poe @versions/RandomAccess_spmd.v03.exe -o  -m $1 -procs $2  >> out.$1.$2

a=$(($1+2))
poe ./gups.exe -a $1 $a -procs $2  >> out.$1.$2

