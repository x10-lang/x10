#!/bin/bash -x

PGASRT_CPUMAP=hosts/map_block_32 poe ./lu-opt 91200 200 8 8 false false true -procs 64 -hostfile hosts/block_32_4 2>&1 | tee -a block-4x16.out
