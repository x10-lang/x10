#!/bin/bash -x

PGASRT_CPUMAP=hosts/map_block_32 poe ./lu-opt 131200 200 8 8 -procs 64 -hostfile hosts/block_8 2>&1 | tee -a block-large-4x16.out
