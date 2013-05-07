#!/bin/bash

workdir=/tmp/pgas-package

#DATE=20100916
DATE=$(date +%Y%m%d)

mkdir -p $workdir
cd $workdir
mkdir -p lib bin include

get_and_compress() {
    HOST="$1"
    PGASDIR="$2"
    TSP="$3"
    ARCH="$4"
    LAUNCHER="$5"
    NAME="pgas-${DATE}-${ARCH}-${TSP}"
    echo "$NAME"
    scp "${HOST}:${PGASDIR}/common/work/lib/libxlpgas_${TSP}.a" lib
    scp "${HOST}:${PGASDIR}/common/work/include/*.h" include
    if [ "$LAUNCHER" = "yes" ] ; then
        scp "${HOST}:${PGASDIR}/common/work/bin/*" bin
        tar -czf "$NAME.tgz" "include" "lib" "bin"
        rm -f lib/* bin/* include/*
    else
        tar -czf "$NAME.tgz" "include" "lib"
        rm -f lib/* include/*
    fi
}

get_and_compress bgpfen1   pgas2          bgp     bgp_g++4          no
