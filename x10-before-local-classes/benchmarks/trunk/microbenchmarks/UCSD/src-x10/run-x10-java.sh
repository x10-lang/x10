#!/bin/sh
x10 -INIT_THREADS=1 -NO_STEALS=true -NUMBER_OF_LOCAL_PLACES=1 -classpath bin-x10-java -J-da -J-dsa Benchmark $*
