#!/bin/bash

PGAS_DIR=../../../pgas2/common/work/lib

CXX=/bgsys/drivers/ppcfloor/gnu-linux/bin/powerpc-bgp-linux-g++

CXXFLAGS="$@  -I .. -g -O3"
LDFLAGS="-L/bgsys/drivers/ppcfloor/comm/lib -L/bgsys/drivers/ppcfloor/runtime/SPI -ldcmf.cnk -ldcmfcoll.cnk -lSPI.cna -lpthread -lrt -lm"

for BACKEND in sockets lapi bgp ; do

    LIB="${PGAS_DIR}/libxlpgas_${BACKEND}.a"

    if [ -e "$LIB" ] ; then
        for TEST in basic gups ; do
            echo $CXX $CXXFLAGS  "test/x10rt_${TEST}.cc" -o test/x10rt_${TEST}.pgas.${BACKEND} $LDFLAGS "$LIB"
            $CXX $CXXFLAGS  "test/x10rt_${TEST}.cc" -o test/x10rt_${TEST}.pgas.${BACKEND} "$LIB" $LDFLAGS 
        done
    fi

done
