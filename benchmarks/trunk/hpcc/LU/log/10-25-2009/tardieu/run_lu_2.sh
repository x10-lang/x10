#!/bin/bash -x

PGASRT_CPUMAP=hosts/map_block_32 poe ./lu-opt 64000 200 4 8 false false true -procs 32 -hostfile hosts/block_32_2 2>&1 | tee -a block-2x16.out
