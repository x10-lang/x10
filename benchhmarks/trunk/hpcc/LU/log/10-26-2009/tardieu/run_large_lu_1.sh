#!/bin/bash -x

PGASRT_CPUMAP=hosts/map_block_32 poe ./lu-opt 65600 200 4 4 -procs 16 -hostfile hosts/block_2  2>&1 | tee -a block-large-1x16.out
