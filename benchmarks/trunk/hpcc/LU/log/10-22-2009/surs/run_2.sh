#!/bin/bash -x

PGASRT_CPUMAP=hosts/map_block_mf16 poe ./lu-opt 92800 200 4 8 false false true -procs 32 -hostfile hosts/block_35_36 2>&1 | tee -a out/lu/block-2x16.out
