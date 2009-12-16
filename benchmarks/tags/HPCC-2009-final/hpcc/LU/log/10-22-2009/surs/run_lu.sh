#!/bin/bash -x

. ~/.bashrc
. ~/x10/x10-env.sh

## Copy executable to all nodes

for((a=33;a<=48;a++)); do ssh v20n$a "mkdir -p /bench1/surs/run"; done
for((a=33;a<=48;a++)); do scp ./lu-opt surs@v20n$a:/bench1/surs/run; done

(./run_16.sh)&
(./run_1.sh)&
(./run_2.sh)&
#(./run_64.sh)&

# too big
# PGASRT_CPUMAP=hosts/map_block_32 poe ./lu-opt 371200 200 16 32 false false true -procs 512 -hostfile hosts/block_32 2>&1 | tee -a out/lu/block-32x16.out

# later
# PGASRT_CPUMAP=hosts/map_block_32 poe ./lu-opt 65600 200 4 4 false false true -procs 16 -hostfile hosts/block_32 2>&1 | tee -a out/lu/block-1x16.out
# PGASRT_CPUMAP=hosts/map_block_32 poe ./lu-opt 92800 200 4 8 false false true -procs 32 -hostfile hosts/block_32 2>&1 | tee -a out/lu/block-2x16.out
# PGASRT_CPUMAP=hosts/map_block_32 poe ./lu-opt 65600 200 1 1 false false true -procs 1 -hostfile hosts/block_32 2>&1 | tee -a out/lu/block-1x1.out
