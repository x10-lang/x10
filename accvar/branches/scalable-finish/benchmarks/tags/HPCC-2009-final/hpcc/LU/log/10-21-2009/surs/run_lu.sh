#!/bin/bash -x

PGASRT_CPUMAP=hosts/map_block_32 poe ./lu-opt 131200 200 8 8 false false true -procs 64 -hostfile hosts/block_32 2>&1 | tee -a out/lu/block-4x16.out

PGASRT_CPUMAP=hosts/map_block_32 poe ./lu-opt 185600 200 8 16 false false true -procs 128 -hostfile hosts/block_32 2>&1 | tee -a out/lu/block-8x16.out

PGASRT_CPUMAP=hosts/map_block_32 poe ./lu-opt 262400 200 16 16 false false true -procs 256 -hostfile hosts/block_32 2>&1 | tee -a out/lu/block-16x16.out

PGASRT_CPUMAP=hosts/map_block_32 poe ./lu-opt 371200 200 16 32 false false true -procs 512 -hostfile hosts/block_32 2>&1 | tee -a out/lu/block-32x16.out

PGASRT_CPUMAP=hosts/map_block_32 poe ./lu-opt 65600 200 4 4 false false true -procs 16 -hostfile hosts/block_32 2>&1 | tee -a out/lu/block-1x16.out

PGASRT_CPUMAP=hosts/map_block_32 poe ./lu-opt 92800 200 4 8 false false true -procs 32 -hostfile hosts/block_32 2>&1 | tee -a out/lu/block-2x16.out

PGASRT_CPUMAP=hosts/map_block_32 poe ./lu-opt 65600 200 1 1 false false true -procs 1 -hostfile hosts/block_32 2>&1 | tee -a out/lu/block-1x1.out
