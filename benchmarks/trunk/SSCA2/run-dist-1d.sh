#!/usr/bin/bash

source /vol/x10/scripts/environ.sh
source ./env.sh

RESULT_DIR=dist-1d/results;

`mkdir  -p $RESULT_DIR`;

for nprocs in  1 2 4 8 16 32 64 128  256
do
  date_qualifier=`date +"%d-%m-%y"`;
  procs_qualifier=`printf "%04d"  $nprocs`;
  echo $procs_qualifier; 
  `poe $1 1 $2 $3 $4 true true -procs $nprocs 2>  $RESULT_DIR/log.$2.$date_qualifier.$procs_qualifier | cat > $RESULT_DIR/ssca2.$2.$date_qualifier.$procs_qualifier`;
done
