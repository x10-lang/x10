#!/bin/bash -x


######################## CYCLIC RUNS ###############################

# M = 16; verification #
# 8x16 M=16 true, 4x16 M-16 false, 2x16 M=15 true, 1x16 M15 false

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16       -procs 128 -hostfile hosts/cyclic_mf_37_44 2>&1 | tee -a out/fft/cyclic-M16-log-8x16.out

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16 false -procs 64 -hostfile hosts/cyclic_mf_37_40 2>&1 | tee -a out/fft/cyclic-M16-log-4x16.out

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 15       -procs 32 -hostfile hosts/cyclic_mf_45_46 2>&1 | tee -a out/fft/cyclic-M15-log-2x16.out

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 15 false -procs 16 -hostfile hosts/cyclic_mf_46 2>&1 | tee -a out/fft/cyclic-M15log-1x16.out
