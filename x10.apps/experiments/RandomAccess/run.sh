#!/bin/bash

export MP_MSG_API=lapi
export MP_TASK_AFFINITY=MCM
export MP_EUIDEVICE=sn_all
export MP_EUILIB=us
export LAPI_USE_SHM=yes
export MP_ADAPTER_USE=dedicated
export MP_CPU_USE=unique
export MP_CSS_INTERRUPT=no
export MP_USE_BULK_XFER=yes
export MP_EAGER_LIMIT=65536
export MP_PULSE=0
export MP_POLLING_INTERVAL=2000000000
export MP_SHARED_MEMORY=yes
export MP_SINGLE_THREAD=yes
export MP_WAIT_MODE=poll 

make;



`rm result.tmp`;
`export  | grep MP >> result.tmp`;
`export  | grep LAPI >> result.tmp`;

for k in `seq 1 $3`
do
for n in `seq $1 $2`
do
./run_internal.sh $4 $((2**$n)) >> result.tmp
done
done

./post.pl result.tmp;

`cat result.tmp >> result.cum`;
