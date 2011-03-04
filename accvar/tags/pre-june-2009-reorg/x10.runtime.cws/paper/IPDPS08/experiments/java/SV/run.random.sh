#!/usr/bin/ksh

#
# (c) IBM Corportation 2007
#
# $Id: run.random.sh,v 1.1 2008-01-18 15:09:16 srkodali Exp $
# Interactive script for running SV (Java).
#

prog_name=CWS-SV.Random
. ../config/run.header

MAX_SIZE=100000
MIN_SIZE=100000
MAX_PROC=$num_proc
MIN_PROC=$num_proc

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
			printf "${CMD} -d64 ${XOPTS} -cp ../xws.jar SV $nproc E $size\n" \
					2>&1| tee -a $OUT_FILE
			${CMD} -d64 ${XOPTS} -cp ../xws.jar SV \
					$nproc E $size 2>&1| tee -a $OUT_FILE
			let 'nproc = nproc * 2'
		done
		let 'size = size * 10'
		let 'mem_mult = mem_mult * 4'
	done
	let 'seq = seq + 1'
done

. ../config/run.footer
