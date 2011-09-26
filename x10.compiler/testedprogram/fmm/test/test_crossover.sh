#!/bin/bash

PARTITION=$1
DENSITY=$2

i=5000
while [ $i -le 250000 ]
do
    echo "mpirun -env X10RT_PGAS_COLLECTIVES=true -env BG_COREDUMPONEXIT=1 -env X10_NTHREADS=1 -env X10_STATIC_THREADS=true -noallocate -nofree -partition $PARTITION -mode VN -np 256 bin/periodicFmm3d $i $DENSITY"
    mpirun -env X10RT_PGAS_COLLECTIVES=true -env BG_COREDUMPONEXIT=1 -env X10_NTHREADS=1 -env X10_STATIC_THREADS=true -noallocate -nofree -partition $PARTITION -mode VN -np 256 bin/periodicFmm3d $i $DENSITY
  i=$(( i+5000 ))
done
