#!/bin/bash

export MP_PROCS=$1
shift

for((i=1;i<$MP_PROCS;i++))
do
export MP_CHILD=$i
$1 &
done

export MP_CHILD=0
echo $*
$*
