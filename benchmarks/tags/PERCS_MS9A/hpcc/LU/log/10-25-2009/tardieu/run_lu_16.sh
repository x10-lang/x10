#!/bin/bash -x

PGASRT_CPUMAP=hosts/map_block_32 poe ./lu-opt 184320 256 16 16 false false true -procs 256 -hostfile hosts/block_32_16 2>&1 | tee -a block-16x16.out
