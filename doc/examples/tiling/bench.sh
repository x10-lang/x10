#!/bin/sh

x10c++ -O -o Jacobi2D Jacobi2D.x10

echo "nthreads, time" > bench.out
X10_NTHREADS=1 ./Jacobi2D >> bench.out
for n in `seq 2 2 512`; do
    echo $n
    X10_NTHREADS=$n ./Jacobi2D >> bench.out
done
