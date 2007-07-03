#!/bin/bash

echo "===============RandomAccess -- Table Size = $1 NPROCS = $2==================" 

echo "***************************X10LIB VERSION ******************************" 
poe "x10lib/RandomAccess_spmd -o  -m $1" -procs $2 

echo "***************************X10C VERSION ********************************" 
poe "x10c/RandomAccess_Dist -o  -m $1"  -procs $2  

echo "***************************HPCC VERSION ********************************" 
cd hpcc-opt;
rm host.list;
ln -s ../host.list;
./script.pl $2 $1   
cd ..;

