#!/usr/bin/bash

source /vol/x10/scripts/environ.sh
source ./env.sh

RESULT_DIR=dist-1d/results;

`mkdir  -p $RESULT_DIR`;

size=$2;
for nprocs in  64 32 16 8 4 2 1
do
  date_qualifier=`date +"%d-%m-%y"`;
  procs_qualifier=`printf "%04d"  $nprocs`;
  echo $procs_qualifier; 
  log=`echo $nprocs | awk '{printf "%d\n",log($1)/log(2)}'`;
  if [ $6 -gt 0 ] ; then size=$((log + $2)) ;fi
  `poe $1 $size $3 $4 true true  false $5 -procs $nprocs 2>  $RESULT_DIR/log.$2.$date_qualifier.$procs_qualifier | cat > $RESULT_DIR/ssca2.$2.$date_qualifier.$procs_qualifier`;
done
