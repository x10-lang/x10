#!/bin/bash -x


######################## CYCLIC RUNS ###############################

# M = 16; verification #

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16       -procs 16 -hostfile hosts/cyclic_mf16 2>&1 | tee -a out/fft/cyclic-M16-log-16x1.out

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16       -procs 32 -hostfile hosts/cyclic_mf16 2>&1 | tee -a out/fft/cyclic-M16-log-16x2.out

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16       -procs 64 -hostfile hosts/cyclic_mf16 2>&1 | tee -a out/fft/cyclic-M16-log-16x4.out

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16       -procs 128 -hostfile hosts/cyclic_mf16 2>&1 | tee -a out/fft/cyclic-M16-log-16x8.out
 
X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16       -procs 256 -hostfile hosts/cyclic_mf16 2>&1 | tee -a out/fft/cyclic-M16-log-16x16.out

# X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16       -procs 256 -hostfile hosts/cyclic_mf32 2>&1 | tee -a out/fft/cyclic-M16-log-32x8.out

# X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16       -procs 384 -hostfile hosts/cyclic_mf32 2>&1 | tee -a out/fft/cyclic-M16-log-32x12.out

# X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16       -procs 512 -hostfile hosts/cyclic_mf32 2>&1 | tee -a out/fft/cyclic-M16-log-32x16.out

# M = 17; no verification #

# doesn't work X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17 false -procs 16 -hostfile hosts/cyclic_mf16 2>&1 | tee -a out/fft/cyclic-M17-log-16x1.out

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17 false -procs 32 -hostfile hosts/cyclic_mf16 2>&1 | tee -a out/fft/cyclic-M17-log-16x2.out

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17 false -procs 64 -hostfile hosts/cyclic_mf16 2>&1 | tee -a out/fft/cyclic-M17-log-16x4.out

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17 false -procs 128 -hostfile hosts/cyclic_mf16 2>&1 | tee -a out/fft/cyclic-M17-log-16x8.out

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17 false -procs 256 -hostfile hosts/cyclic_mf16 2>&1 | tee -a out/fft/cyclic-M17-log-16x16.out


# M = 17; verification #

# X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17       -procs 256 -hostfile hosts/cyclic_mf32 2>&1 | tee -a out/fft/cyclic-M17-log-32x8.out

# X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17       -procs 384 -hostfile hosts/cyclic_mf32 2>&1 | tee -a out/fft/cyclic-M17-log-32x12.out

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17       -procs 512 -hostfile hosts/cyclic_mf32 2>&1 | tee -a out/fft/cyclic-M17-log-32x16.out

######################## BLOCK RUNS ###############################

# M = 16; verification #

# X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16 -procs 16 -hostfile hosts/block_32 2>&1 | tee -a out/fft/block-M16-log-1x16.out

# X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16 -procs 32 -hostfile hosts/block_32 2>&1 | tee -a out/fft/block-M16-log-2x16.out

# X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16 -procs 64 -hostfile hosts/block_32 2>&1 | tee -a out/fft/block-M16-log-4x16.out

# X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16 -procs 128 -hostfile hosts/block_32 2>&1 | tee -a out/fft/block-M16-log-8x16.out
 
# X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16 -procs 256 -hostfile hosts/block_32 2>&1 | tee -a out/fft/block-M16-log-16x16.out

# X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16 -procs 512 -hostfile hosts/block_32 2>&1 | tee -a out/fft/block-M16-log-32x16.out

# M = 17; no verification #

# X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17 false -procs 16 -hostfile hosts/block_32 2>&1 | tee -a out/fft/block-M17-log-1x16.out

# X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17 false -procs 32 -hostfile hosts/block_32 2>&1 | tee -a out/fft/block-M17-log-2x16.out

# X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17 false -procs 64 -hostfile hosts/block_32 2>&1 | tee -a out/fft/block-M17-log-4x16.out

# X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17 false -procs 128 -hostfile hosts/block_32 2>&1 | tee -a out/fft/block-M17-log-8x16.out

# X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17 false -procs 256 -hostfile hosts/block_32 2>&1 | tee -a out/fft/block-M17-log-16x16.out

# M = 17; verification #

# X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17 -procs 256 -hostfile hosts/block_32 2>&1 | tee -a out/fft/block-M17-log-32x8.out

# X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17 -procs 512 -hostfile hosts/block_32 2>&1 | tee -a out/fft/block-M17-log-32x16.out
