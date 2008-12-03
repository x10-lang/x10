#!/usr/bin/ksh

#
# (c) IBM Corportation 2007
#
# $Id: run.sh,v 1.2 2008-02-25 08:17:57 srkodali Exp $
# Interactive script for running DFS Cilk.
#

TOP=../../../..
prog_name=dfs.cilk.random
. ${TOP}/config/run.header

_CMD_=${TOP}/ST.pwr5
PTHREAD_STACK_MAX=256000000

seq=1
while [[ $seq -le $MAX_RUNS ]]
do
	printf "#\n# Run: %d\n#\n" $seq 2>&1| tee -a $OUT_FILE
	for size in 250000 1000000
	do
		let 'n_edges = size * 4'
		let 'stack_depth = n_edges * 4'
		printf "\n## Size: %d\n" $size 2>&1| tee -a $OUT_FILE
		for nproc in 1 2 4 6 8 10 12 14 16
		do
			let 'stack_size = PTHREAD_STACK_MAX / nproc'
			printf "\n### nproc: %d\n" $nproc 2>&1| tee -a $OUT_FILE
			CMD="${_CMD_} --nproc $nproc --stack $stack_depth --pthread-stacksize $stack_size 0 $size $n_edges"
			printf "${CMD}\n" 2>&1| tee -a $OUT_FILE
			${CMD} 2>&1| tee -a $OUT_FILE
		done
	done
	let 'seq = seq + 1'
done
. ${TOP}/config/run.footer
