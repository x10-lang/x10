#!/usr/bin/ksh

#
# (c) IBM Corportation 2007
#
# $Id: run.sh,v 1.1 2008-02-18 10:01:57 srkodali Exp $
# Interactive script for running BFS Cilk.
#

TOP=../../../..
prog_name=bfs.cilk.kgraph
. ${TOP}/config/run.header

export LD_LIBRARY_PATH=/home/dl/amdcilk/lib/
_CMD_=${TOP}/BFSBlock.amd64

seq=1
while [[ $seq -le $MAX_RUNS ]]
do
	printf "#\n# Run: %d\n#\n" $seq 2>&1| tee -a $OUT_FILE
	for size in 250000 1000000 4000000 9000000
	do
		printf "\n## Size: %d\n" $size 2>&1| tee -a $OUT_FILE
		if [ $num_proc -eq 32 ]
		then
			for nproc in 1 2 4 8 16 20 24 30 32
			do
				printf "\n### nproc: %d\n" $nproc 2>&1| tee -a $OUT_FILE
				CMD="${_CMD_} --nproc $nproc 2 $size 4 64"
				printf "${CMD}\n" 2>&1| tee -a $OUT_FILE
				${CMD} 2>&1| tee -a $OUT_FILE
			done
		else
			for nproc in 1 2 4 6 8
			do
				printf "\n### nproc: %d\n" $nproc 2>&1| tee -a $OUT_FILE
				CMD="${_CMD_} --nproc $nproc 2 $size 4 64"
				printf "${CMD}\n" 2>&1| tee -a $OUT_FILE
				${CMD} 2>&1| tee -a $OUT_FILE
			done
		fi
	done
	let 'seq = seq + 1'
done
. ${TOP}/config/run.footer
