#!/bin/bash -x

PGASRT_CPUMAP=hosts/map_block_32 poe ./lu-opt 262400 200 16 16 -procs 256 -hostfile hosts/block_16 2>&1 | tee -a block-large-16x16.out
