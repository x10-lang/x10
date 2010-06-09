#!/bin/bash -x

#### M = 16; verification ####

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16 false -procs 16 -hostfile cyclic_mf4 2>&1 | tee -a fft-M16-log-4x4.out

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16 -procs 16 -hostfile cyclic_mf16 2>&1 | tee -a fft-M16-log-1x16.out

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16 -procs 32 -hostfile cyclic_mf16 2>&1 | tee -a fft-M16-log-2x16.out

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16 -procs 64 -hostfile cyclic_mf16 2>&1 | tee -a fft-M16-log-4x16.out

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16 -procs 128 -hostfile cyclic_mf16 2>&1 | tee -a fft-M16-log-8x16.out
 
X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16 -procs 256 -hostfile cyclic_mf16 2>&1 | tee -a fft-M16-log-16x16.out

#### M = 17; no verification ####

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17 false -procs 16 -hostfile cyclic_mf16 2>&1 | tee -a fft-M17-log-1x16.out

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17 false -procs 32 -hostfile cyclic_mf16 2>&1 | tee -a fft-M17-log-2x16.out

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17 false -procs 64 -hostfile cyclic_mf16 2>&1 | tee -a fft-M17-log-4x16.out

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17 false -procs 128 -hostfile cyclic_mf16 2>&1 | tee -a fft-M17-log-8x16.out

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17 false -procs 256 -hostfile cyclic_mf16 2>&1 | tee -a fft-M17-log-16x16.out


######### M = 16; verify #######

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16 -procs 256 -hostfile cyclic_mf32 2>&1 | tee -a fft-M16-log-8x32.out

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 16 -procs 512 -hostfile cyclic_mf32 2>&1 | tee -a fft-M16-log-16x32.out

######### M = 17; verify #######

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17 -procs 256 -hostfile cyclic_mf32 2>&1 | tee -a fft-M17-log-8x32.out

X10_NTHREADS=1 X10_STATIC_THREADS=true poe ./fft-opt 17 -procs 512 -hostfile cyclic_mf32 2>&1 | tee -a fft-M17-log-16x32.out
