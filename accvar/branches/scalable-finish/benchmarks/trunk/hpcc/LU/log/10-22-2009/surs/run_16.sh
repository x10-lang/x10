#!/bin/bash -x

PGASRT_CPUMAP=hosts/map_block_mf16 poe ./lu-opt 65600 200 4 4 false false true -procs 16 -hostfile hosts/block_33 2>&1 | tee -a out/lu/block-1x16.out
