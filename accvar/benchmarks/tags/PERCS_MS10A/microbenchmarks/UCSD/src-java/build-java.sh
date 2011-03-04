#!/bin/sh
if [ ! -d bin-java ]; then
  mkdir bin-java
fi
javac -d bin-java -O Benchmark.java $*
