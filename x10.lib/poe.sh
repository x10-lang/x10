#!/bin/bash

# A variant of POE for STANDALONE mode.
# This simplifies the setup process
# and execution in STANDALONE mode.

echo "poe <executable_name> #procs"

for n in `seq 1 $(($2-1))`;
do
  ssh localhost -f "$PWD/poe_internal.sh \"$PWD/$1\" $n $2"
done

ssh localhost  "$PWD/poe_internal.sh  \"$PWD/$1\" 0 $2 " 
