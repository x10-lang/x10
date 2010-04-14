#!/usr/bin/ksh

if [ $# -ne 4 ]
then
	echo "Usage: ./generate.sh prefix lnode unode procs"
	exit 1
fi

prefix=$1
lnode=$2
unode=$3
procs=$4

OUTFILE="host_${prefix}.list"

rm -f ${OUTFILE}
seq=1
while [[ $seq -le $procs ]]
do
node=$lnode
while [[ $node -le $unode ]]
do
	if [[ $node -lt 10 ]]
	then
		echo "${prefix}n0${node}.pbm.ihost.com" >> ${OUTFILE}
	else
		echo "${prefix}n${node}.pbm.ihost.com" >> ${OUTFILE}
	fi
	let "node = node + 1"
done
let "seq = seq + 1"
done


