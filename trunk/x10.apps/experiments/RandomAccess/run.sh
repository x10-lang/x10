#!/bin/bash

export MP_MSG_API=lapi,mpi
export MP_TASK_AFFINITY=MCM
export MP_EUIDEVICE=sn_all
export MP_ADAPTER_USE=dedicated
export MP_EUILIB=us
export MP_CPU_USE=unique
export MP_CSS_INTERRUPT=no
export MP_USE_BULK_XFER=yes
export MP_EAGER_LIMIT=65535
export MP_PULSE=0
export MP_POLLING_INTERVAL=2000000000
export MP_SHARED_MEMORY=yes
export MP_SINGLE_THREAD=yes
export MP_WAIT_MODE=poll

make

echo "Syntax: ./run.sh" 

`rm tmp.out`;
for n in `seq 0 $1`
do
./run_internal.sh 26 $((2**$n)) >> tmp.out
done

./post.pl tmp.out

