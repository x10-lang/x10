#!/usr/bin/ksh

#
# (c) IBM Corporation 2008
#
# $Id: standrun.sh,v 1.1 2008-02-12 12:44:36 srkodali Exp $
#
# Interactive script for benchmarking xws programs.
#

if [ $# -ne 4 ]
then
	printf "Usage: standrun.sh [bfs|dfs] [random|kgraph|torus] [on|off] path_to_jar\n"
	exit 1
fi

algo=$1
inp_class=$2
batching=$3
path_to_jar=$4

# specify maximum number of times the benchmark should run
MAX_RUNS=$MAX_RUNS
if [ -z "${MAX_RUNS}" ]
then
	MAX_RUNS=1
fi

# specify the directory where the data file could be created
DATA_DIR="data"
if [ ! -d "${DATA_DIR}" ]
then
	echo "mkdir -p ${DATA_DIR}"
	mkdir -p "${DATA_DIR}"
fi

# specify the log file prefix following the listed pattern
# prog_name={algo}.{impl_lang}.{inp_class}
prog_name=${algo}.java.${inp_class}

# specify the fixed command path
host_name=`hostname`
if [ "$host_name" = "moxie" ]
then
	_CMD_="/home/dl/1.7.0/j2se/martin/promoted/solaris-sparcv9/bin/java"
	num_proc=32
else
	_CMD_="/home/dl/1.7.0/j2se/martin/promoted/solaris-amd64/bin/java"
	num_proc=8
fi

_CMD_="${_CMD_} -server -Xbootclasspath/p:/home/dl/jsr166/build/lib/jsr166.jar"
_CMD_="${_CMD_} -cp ${path_to_jar}"
_CMD_="${_CMD_} -Xms2G -Xmx3G"

# specify the class name
if [ "${algo}" = "bfs" ]
then
	_CMD_="${_CMD_} graph.BFS"
else
	_CMD_="${_CMD_} graph.DFS"
fi

date_time=`date +"%m-%d-%Y.%H-%M-%S"`
host_name=`hostname`
if [ "$host_name" = "moxie" ]
then
	num_proc=32
else
	num_proc=8
fi

# specify input sizes based on class
if [ "${inp_class}" = "torus" ]
then
	sizes="300 900 2100 2700"
else
	sizes="100000 1000000 5000000 10000000"
fi

OUT_FILE=${DATA_DIR}/${prog_name}.`hostname`.${num_proc}.${date_time}.dat

seq=1
while [[ $seq -le $MAX_RUNS ]]
do
	printf "#\n# Run: %d\n#\n" $seq 2>&1| tee -a $OUT_FILE
	for size in ${sizes}
	do
		printf "\n## Size: %d\n" $size 2>&1| tee -a $OUT_FILE
		if [ $num_proc -eq 32 ]
		then
			for nproc in 1 2 4 8 16 20 24 30 32
			do
				printf "\n### nproc: %d\n" $nproc 2>&1| tee -a $OUT_FILE
				if [ "${inp_class}" = "torus" ]
				then
					CMD="${_CMD_} $nproc T $size 4 false false true"
				elif [ "${inp_class}" = "kgraph" ]
				then
					CMD="${_CMD_} $nproc K $size 4 false false true"
				else
					CMD="${_CMD_} $nproc E $size 4 false false true"
				fi
				printf "${CMD}\n" 2>&1| tee -a $OUT_FILE
				${CMD} 2>&1| tee -a $OUT_FILE
				if [ $nproc -eq 30 -a "${batching}" = "on" ]
				then
					printf "\n#<<<< BEGIN BATCHING >>>>\n" 2>&1| tee -a $OUT_FILE
					for bsize in 1 10 20 30 40 50 60 70 80 90 100
					do
						printf "\n#### bsize: %d\n" $bsize 2>&1| tee -a $OUT_FILE
						if [ "${inp_class}" = "torus" ]
						then
							CMD="${_CMD_} $nproc T $size 4 false false true $bsize"
						elif [ "${inp_class}" = "kgraph" ]
						then
							CMD="${_CMD_} $nproc K $size 4 false false true $bsize"
						else
							CMD="${_CMD_} $nproc E $size 4 false false true $bsize"
						fi
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
				if [ "${inp_class}" = "torus" ]
				then
					CMD="${_CMD_} $nproc T $size 4 false false true"
				elif [ "${inp_class}" = "kgraph" ]
				then
					CMD="${_CMD_} $nproc K $size 4 false false true"
				else
					CMD="${_CMD_} $nproc E $size 4 false false true"
				fi
				printf "${CMD}\n" 2>&1| tee -a $OUT_FILE
				${CMD} 2>&1| tee -a $OUT_FILE
				if [ $nproc -eq 8 -a "${batching}" = "on" ]
				then
					printf "\n#<<<< BEGIN BATCHING >>>>\n" 2>&1| tee -a $OUT_FILE
					for bsize in 1 10 20 30 40 50 60 70 80 90 100
					do
						printf "\n#### bsize: %d\n" $bsize 2>&1| tee -a $OUT_FILE
						if [ "${inp_class}" = "torus" ]
						then
							CMD="${_CMD_} $nproc T $size 4 false false true $bsize"
						elif [ "${inp_class}" = "kgraph" ]
						then
							CMD="${_CMD_} $nproc K $size 4 false false true $bsize"
						else
							CMD="${_CMD_} $nproc E $size 4 false false true $bsize"
						fi
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
