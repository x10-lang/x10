#!/bin/bash

PGAS_DIR=../../../pgas2/common/work/lib

CXX=mpCC_r
CXXFLAGS="$@  -I .. -g -O3 -q64 -qrtti=all -qsuppress=1540-0809:1500-029:1540-1101"
LDFLAGS="-ldl -lm -lpthread -bbigtoc -lptools_ptr"

for BACKEND in sockets lapi ; do

    LIB="${PGAS_DIR}/libxlpgas_${BACKEND}.a"

    if [ -e "$LIB" ] ; then
        for TEST in basic gups ; do
            $CXX $CXXFLAGS  "test/x10rt_${TEST}.cc" -o test/x10rt_${TEST}.pgas.${BACKEND} $LDFLAGS "$LIB"
        done
    fi

done
