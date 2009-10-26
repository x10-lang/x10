#!/bin/bash -x

PGASRT_CPUMAP=hosts/map_block_32 poe ./lu-opt 128000 200 8 16 false false true -procs 128 -hostfile hosts/block_32_8 2>&1 | tee -a block-8x16.out
