#!/bin/bash -x

X10_NTHREADS=1 X10_STATIC_THREADS=true ./ra-opt -m 29 -u 1 -procs 8 -hostfile hosts/block_32_8 2>&1 | tee -a out/ra/block-log-1x8.out

X10_NTHREADS=1 X10_STATIC_THREADS=true ./ra-opt -m 29 -u 1 -procs 16 -hostfile hosts/block_32_8 2>&1 | tee -a out/ra/block-log-2x8.out

X10_NTHREADS=1 X10_STATIC_THREADS=true ./ra-opt -m 29 -u 1 -procs 32 -hostfile hosts/block_32_8 2>&1 | tee -a out/ra/block-log-4x8.out

X10_NTHREADS=1 X10_STATIC_THREADS=true ./ra-opt -m 29 -u 1 -procs 64 -hostfile hosts/block_32_8 2>&1 | tee -a out/ra/block-log-8x8.out

X10_NTHREADS=1 X10_STATIC_THREADS=true ./ra-opt -m 29 -u 1 -procs 128 -hostfile hosts/block_32_8 2>&1 | tee -a out/ra/block-log-16x8.out

X10_NTHREADS=1 X10_STATIC_THREADS=true ./ra-opt -m 29 -u 1 -procs 256 -hostfile hosts/block_32_8 2>&1 | tee -a out/ra/block-log-32x8.out
