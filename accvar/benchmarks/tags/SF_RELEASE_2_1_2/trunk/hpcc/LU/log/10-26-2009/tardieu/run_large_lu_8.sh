#!/bin/bash -x

PGASRT_CPUMAP=hosts/map_block_32 poe ./lu-opt 185600 200 8 16 -procs 128 -hostfile hosts/block_16 2>&1 | tee -a block-large-8x16.out
