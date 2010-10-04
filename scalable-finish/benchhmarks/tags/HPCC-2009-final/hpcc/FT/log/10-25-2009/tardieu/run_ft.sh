#!/bin/bash -x

# X10_NTHREADS=1 and X10_STATIC_THREADS=true must be set in the env

poe ./fft-opt 16       -procs 16 -hostfile hosts/cyclic_mf16 2>&1 | tee -a cyclic-M16-log-16x1.out

poe ./fft-opt 16       -procs 32 -hostfile hosts/cyclic_mf16 2>&1 | tee -a cyclic-M16-log-16x2.out

poe ./fft-opt 16       -procs 64 -hostfile hosts/cyclic_mf16 2>&1 | tee -a cyclic-M16-log-16x4.out

poe ./fft-opt 16       -procs 128 -hostfile hosts/cyclic_mf16 2>&1 | tee -a cyclic-M16-log-16x8.out

poe ./fft-opt 16       -procs 256 -hostfile hosts/cyclic_mf16 2>&1 | tee -a cyclic-M16-log-16x16.out

poe ./fft-opt 16       -procs 256 -hostfile hosts/cyclic_mf32 2>&1 | tee -a cyclic-M16-log-32x8.out

poe ./fft-opt 16       -procs 512 -hostfile hosts/cyclic_mf32 2>&1 | tee -a cyclic-M16-log-32x16.out
