#!/bin/bash -x

PGASRT_CPUMAP=hosts/map_block_32 poe ./lu-opt 92800 200 4 8 -procs 32 -hostfile hosts/block_4  2>&1 | tee -a block-large-2x16.out
