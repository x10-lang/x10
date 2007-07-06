#!/bin/bash

echo "===============RandomAccess -- Table Size = $1 NPROCS = $2==================" 

echo "***************************EXPT1******************************" 
poe "x10lib/RandomAccess_spmd   -o  -m $1" -procs $2 

echo "***************************EXPT2********************************" 
poe "x10c/RandomAccess_Dist -o  -m $1"  -procs $2  

echo "***************************EXPT3********************************" 
cd hpcc-opt;
rm host.list;
export MP_MSG_API=mpi;
export | grep MP_MSG;
ln -s ../host.list;
./script.pl $2 $1  ;
cd ..;

