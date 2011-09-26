#!/bin/bash

run_test() {
  for j in 1 2 3
  do
    echo "$1"
    echo "$(eval $1)"
    echo ""
  done
}

export X10_NTHREADS=4
unset X10_STATIC_THREADS
i=5
while [ $i -le 30 ]
do
#  run_test "bin/fmm3d 10000 60 $i"
  i=$(( i+5 ))
done

run_test "bin/fmm3d 51396"
run_test "mpiexec -n 2 -x X10_NTHREADS=2 bin/fmm3d 51396"
run_test "mpiexec -n 4 -x X10_NTHREADS=1 bin/fmm3d 51396"
run_test "mpiexec -n 4 -x X10_NTHREADS=1 -x X10_STATIC_THREADS=true bin/fmm3d 51396"
run_test "bin/fmm3d 100000"
run_test "bin/fmm3d 250000 64"

