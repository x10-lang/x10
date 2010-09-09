#!/bin/sh
Files="ManyLocalFinish1 ManyLocalFinish2 SimpleFinish1 SimpleFinish2 SimpleFinish3 SimpleFinish4 SimpleFinish5 ManyLocalFinish1_old ManyLocalFinish2_old SimpleFinish1_old SimpleFinish2 SimpleFinish3 SimpleFinish4 SimpleFinish5_old"
for n in $Files
do
	for i in $1
	do
		./run.sh $n $i
	done
done
