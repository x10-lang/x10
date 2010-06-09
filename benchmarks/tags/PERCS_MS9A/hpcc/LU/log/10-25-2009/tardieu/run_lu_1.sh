#!/bin/bash -x

PGASRT_CPUMAP=hosts/map_block_32 poe ./lu-opt 45600 200 4 4 false false true -procs 16 -hostfile hosts/block_32_1 2>&1 | tee -a block-1x16.out
