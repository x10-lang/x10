#!/bin/sh
X10_EXT=".x10"
JAVA_EXT=".java"
OUT_EXT=".java.hprof.txt"
NAME=$1$X10_EXT
CLASS=$1$JAVA_EXT
OUTPUT=$1"_"$2$OUT_EXT
#echo $CLASS
if [ ! -f $CLASS ]
then
	echo "compile "$NAME" ..."
	x10c $NAME
fi
echo "run "$OUTPUT" ... "
x10 -J-agentlib:hprof=heap=sites,file=$OUTPUT -NUMBER_OF_LOCAL_PLACES=$2 $1
