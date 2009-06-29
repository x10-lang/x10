#!/usr/bin/ksh

#
# (c) IBM Corporation 2008
#
# $Id: run.sh,v 1.2 2008-02-01 11:02:56 srkodali Exp $
#
# Interactive script for benchmarking dfs.java.torus programs.
#

TOP=../../../..
prog_name=dfs.java.torus
. ${TOP}/config/run.header

_CMD_="java"
_CMD_="${_CMD_} -cp ${TOP}/../xwsn.jar"
_CMD_="${_CMD_} -Xms2G -Xmx3G"
_CMD_="${_CMD_} SpanFTROLev"

seq=1
while [[ $seq -le $MAX_RUNS ]]
do
	printf "#\n# Run: %d\n#\n" $seq 2>&1| tee -a $OUT_FILE
	for size in 300 900 2100 2700
	do
		printf "\n## Size: %d\n" $size 2>&1| tee -a $OUT_FILE
		if [ $num_proc -eq 32 ]
		then
			for nproc in 1 2 4 8 16 20 24 30 32
			do
				printf "\n### nproc: %d\n" $nproc 2>&1| tee -a $OUT_FILE
				CMD="${_CMD_} $nproc T $size 4 false false true"
				printf "${CMD}\n" 2>&1| tee -a $OUT_FILE
				${CMD} 2>&1| tee -a $OUT_FILE
				if [ $nproc -eq 30 ]
				then
					printf "\n#<<<< BEGIN BATCHING >>>>\n" 2>&1| tee -a $OUT_FILE
					for bsize in 1 10 20 30 40 50 60 70 80 90 100
					do
						printf "\n#### bsize: %d\n" $bsize 2>&1| tee -a $OUT_FILE
						CMD="${_CMD_} $nproc T $size 4 false false true $bsize"
						printf "${CMD}\n" 2>&1| tee -a $OUT_FILE
						${CMD} 2>&1| tee -a $OUT_FILE
					done
					printf "\n#<<<< END BATCHING >>>>\n" 2>&1| tee -a $OUT_FILE
				fi
			done
		else
			for nproc in 1 2 4 6 8
			do
				printf "\n### nproc: %d\n" $nproc 2>&1| tee -a $OUT_FILE
				CMD="${_CMD_} $nproc T $size 4 false false true"
				printf "${CMD}\n" 2>&1| tee -a $OUT_FILE
				${CMD} 2>&1| tee -a $OUT_FILE
				if [ $nproc -eq 8 ]
				then
					printf "\n#<<<< BEGIN BATCHING >>>>\n" 2>&1| tee -a $OUT_FILE
					for bsize in 1 10 20 30 40 50 60 70 80 90 100
					do
						printf "\n#### bsize: %d\n" $bsize 2>&1| tee -a $OUT_FILE
						CMD="${_CMD_} $nproc T $size 4 false false true $bsize"
						printf "${CMD}\n" 2>&1| tee -a $OUT_FILE
						${CMD} 2>&1| tee -a $OUT_FILE
					done
					printf "\n#<<<< END BATCHING >>>>\n" 2>&1| tee -a $OUT_FILE
				fi
			done
		fi
	done
	let 'seq = seq + 1'
done
. ${TOP}/config/run.footer
