#!/bin/bash

if [ -z $1 ]; then
  echo "Usage: test_scaling.sh MAX_PLACES PARTITION"
  exit 1
fi
MAX_PLACES=$1

SHELL=`uname -s`

if [ $SHELL == "Linux" ]; then
    MACHINE=`uname -m`
    if [ $MACHINE == "ppc64" ]; then
        # Blue Gene
        if [ -z $2 ]; then
          echo "Usage: test_scaling.sh MAX_PLACES PARTITION"
          exit 1
        fi
        PARTITION=$2
        MPIARGS="-env X10RT_PGAS_COLLECTIVES=true -env BG_COREDUMPONEXIT=1 -env X10_NTHREADS=1 -env X10_STATIC_THREADS=true -noallocate -nofree -partition $PARTITION -mode VN"
    else
        MPIARGS="-x X10_NTHREADS=1 -x X10_STATIC_THREADS=true"
    fi
fi

run_test() {
  for j in 1 2 3
  do
    echo "$1"
    #echo "$(eval $1)"
    echo ""
  done
}

i=1
while [ $i -le $MAX_PLACES ]
do
  run_test "mpirun $MPIARGS -np $i bin/periodicFmm3d 51396 60 10 10 -verbose"
  i=$(( i*2 ))
done
