#!/usr/bin/ksh

#
# (c) IBM Corporation 2008
#
# $Id: run.sh,v 1.1 2008-02-24 10:34:39 srkodali Exp $
#
# Interactive script for benchmarking sv.java.torus programs.
#

TOP=../../../..
prog_name=sv.java.torus
. ${TOP}/config/run.header

_CMD_="java -d64 -Xdump:heap:none -Xdump:system:none -Xtrace:none"
_CMD_="${_CMD_} -Xdisablejavadump -Xcheck:memory:quick"
_CMD_="${_CMD_} -Xgcpolicy:gencon -Xjit:count=0"
_CMD_="${_CMD_} -cp ${TOP}/../xwsn.jar"
_CMD_="${_CMD_} -Xmx8G"
_CMD_="${_CMD_} SV"

seq=1
while [[ $seq -le $MAX_RUNS ]]
do
	printf "#\n# Run: %d\n#\n" $seq 2>&1| tee -a $OUT_FILE
	for size in 500 1000 2000 3000
	do
		printf "\n## Size: %d\n" $size 2>&1| tee -a $OUT_FILE
		for nproc in 1 2 4 6 8 10 12 14 16
		do
			printf "\n### nproc: %d\n" $nproc 2>&1| tee -a $OUT_FILE
			CMD="${_CMD_} $nproc T $size"
			printf "${CMD}\n" 2>&1| tee -a $OUT_FILE
			${CMD} 2>&1| tee -a $OUT_FILE
		done
	done
	let 'seq = seq + 1'
done
. ${TOP}/config/run.footer
