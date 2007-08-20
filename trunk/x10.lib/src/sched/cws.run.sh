#!/bin/bash

#Subroutine to loop over the various processor and nums.
#This assumes the executable takes exactly three arguments

function benchmark() {
    exe=$1
    nprocs=$2
    nums=$3
    nreps=$4

    echo "exe=$exe procs=$nprocs nums=$nums nreps=$nreps"

    for p in $nprocs
      do
      for n in $nums
	do
	$exe $p $nreps $n
      done
    done
}

#cilk version of the above code. Puts the -nrpoc in args

function cilk_benchmark() {
    exe=$1
    nprocs=$2
    nums=$3
    nreps=$4

    for p in $nprocs
      do
      for n in $nums
	do
	$exe -nproc $p $nreps $n 
      done
    done
}

nReps=5  #best of nReps runs

echo "STAMP: `hostname` `date`"
echo "Best time of $nReps runs"

PROCS="1 2 4 8"
FIBNUMS="20 25 30 35"
QNUMS="8 10 12 14"

##############make with xlc#################

echo "Results with xlC_r"

make clean &> /dev/null
make Fib.x &>/dev/null
make FibStack.x &> /dev/null
make NQueensC.x &> /dev/null
make NQueensCStack_a.x &> /dev/null
make NQueensGStack.x &> /dev/null

#benchmark FibStack.x "$PROCS" "$FIBNUMS" $nReps
#benchmark Fib.x      "$PROCS" "$FIBNUMS" $nReps

benchmark NQueensGStack.x "$PROCS" "$QNUMS" $nReps
benchmark NQueensCStack_a.x "$PROCS" "$QNUMS" $nReps
#benchmark NQueensC.x      "$PROCS" "$QNUMS" $nReps




##############make with gcc#################

echo "Results with g++"

make clean &> /dev/null
make GCC=y Fib.x &>/dev/null
make GCC=y FibStack.x &> /dev/null
make GCC=y NQueensC.x &> /dev/null
make GCC=y NQueensCStack_a.x &> /dev/null
make GCC=y NQueensGStack.x &> /dev/null

#benchmark FibStack.x "$PROCS" "$FIBNUMS" $nReps
#benchmark Fib.x      "$PROCS" "$FIBNUMS" $nReps

benchmark NQueensGStack.x "$PROCS" "$QNUMS" $nReps
benchmark NQueensCStack_a.x "$PROCS" "$QNUMS" $nReps
#benchmark NQueensC.x      "$PROCS" "$QNUMS" $nReps


