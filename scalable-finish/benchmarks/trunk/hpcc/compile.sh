#!/bin/sh
cd LU/opt-gen
pwd
g++ -O3 -g -I/home/blshao/scalable-finish/x10.dist/include -Iopt-gen -I. -O2 -DNDEBUG -DNO_PLACE_CHECKS -finline-functions -DNO_CHECKS -Wno-long-long -Wno-unused-parameter -pthread -msse2 -mfpmath=sse -o /home/blshao/workspace/x10-branch/benchmarks/trunk/hpcc/LU/lu-opt rc7/BlockedArray__ArrayView.cc rc7/BlockedArray.cc rc7/BlockedArray__Block.cc rc7/essl_natives.cc rc7/LU.cc util/Comm.cc rc7/Timer.cc util/Comm__Integer.cc -L/home/blshao/scalable-finish/x10.dist/lib -lx10 -DX10_USE_BDWGC -lgc -lx10rt_pgas_sockets -lpthread -ldl -lm -lpthread -Wl,--rpath -Wl,/home/blshao/scalable-finish/x10.dist/lib -Wl,-export-dynamic -lrt ~/atlas/lib/*.so
