#!/usr/bin/ksh

#
# (c) IBM Corportation 2007
#
# $Id: run.torus.sh,v 1.1 2008-01-18 15:09:16 srkodali Exp $
# Interactive script for running span (traversal).
#

prog_name=CWS-BFS.Torus
. ../config/run.header

MAX_SIZE=2700
MIN_SIZE=300
MAX_PROC=$num_proc
MIN_PROC=1

CMD=java

seq=1
while [[ $seq -le $MAX_RUNS ]]
do
	printf "#\n# Run: %d\n#\n" $seq 2>&1| tee -a $OUT_FILE
	
	size=$MIN_SIZE
	if [ $num_proc -eq 64 ]
	then
		mem_mult=4
	else
		mem_mult=1
	fi
	while [[ $size -le $MAX_SIZE ]]
	do
		printf "\n## Size: %d\n" $size 2>&1| tee -a $OUT_FILE
		nproc=$MIN_PROC
		while [[ $nproc -le $MAX_PROC ]]
		do
			XOPTS="-Xms${mem_mult}G -Xmx${mem_mult}G"
			printf "\n### nproc: %d\n" $nproc 2>&1| \
				tee -a $OUT_FILE
			printf "${CMD} -d64 ${XOPTS} -cp ../xws.jar BFSRef $nproc T $size\n" \
					2>&1| tee -a $OUT_FILE
			${CMD} -d64 ${XOPTS} -cp ../xws.jar BFSRef $nproc T $size \
					2>&1| tee -a $OUT_FILE
			let 'nproc = nproc * 2'
		done
		let 'size = size * 3'
		let 'mem_mult = mem_mult * 4'
	done
	let 'seq = seq + 1'
done

. ../config/run.footer
