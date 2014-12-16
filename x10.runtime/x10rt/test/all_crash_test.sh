#!/bin/bash

echo -e "All runs: `date`\n"

for X10_NPLACES in 1 2 ; do
    for CRASH_MODE in segfault abort exit ; do
        export X10_NPLACES CRASH_MODE
        echo "== Test: standalone X10_NPLACES=$X10_NPLACES CRASH_MODE=$CRASH_MODE"
        ./x10rt_crash.standalone 2>&1
        echo -e "== Test exit code: $?\n"

        echo "== Test: sockets with fork X10_NPLACES=$X10_NPLACES CRASH_MODE=$CRASH_MODE"
        ./x10rt_crash.sockets 2>&1
        echo -e "== Test exit code: $?\n"

        echo "== Test: sockets with ssh X10_NPLACES=$X10_NPLACES CRASH_MODE=$CRASH_MODE"
        X10_HOSTLIST=expensive ./x10rt_crash.sockets 2>&1
        echo -e "== Test exit code: $?\n"

        echo "== Test: mpirun_rsh X10_NPLACES=$X10_NPLACES CRASH_MODE=$CRASH_MODE"
        mpirun_rsh -np 2 localhost localhost ./x10rt_crash.mpi 2>&1
        echo -e "== Test exit code: $?\n"
    done
done

wait
