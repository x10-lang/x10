#!/bin/bash -x

PGASRT_CPUMAP=hosts/map_block_mf16 poe ./lu-opt 65600 200 1 1 false false true -procs 1 -hostfile hosts/one_34 2>&1 | tee -a out/lu/block-1x1.out
