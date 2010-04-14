#!/usr/bin/ksh

#
# (c) Copyright IBM Corporation 2007
# $Id: run.sh,v 1.1 2007-08-02 13:09:59 srkodali Exp $
# This file is part of X10 Applications.
#

## Master script for running all variants of RandomAccess
## Benchmark in one go.

# source environment settings
. ./environ.sh


### hpcc (Base Run)

export MP_MSG_API=mpi

_OUTDIR=hpcc.run
CMD=../hpcc
seq=1
while [[ $seq -le $RA_MAX_RUNS ]]
do
	OUTDIR="${_OUTDIR}${seq}"
	if [ ! -d ${OUTDIR} ]
	then
		echo "mkdir -p ${OUTDIR}"
		mkdir -p ${OUTDIR}
	fi
	echo "cd ${OUTDIR}"
	cd ${OUTDIR}
	task=2
	while [[ $task -le $RA_MAX_TASKS ]]
	do
		echo "../run_hpcc.pl "${CMD}" ${task} ${RA_TABLE_SIZE}"
		../run_hpcc.pl "${CMD}" ${task} ${RA_TABLE_SIZE}
		let "task = task * 2"
	done
	echo "cd .."
	cd ..
	let "seq = seq + 1"
done


### hpcc (Optimized Run)

export MP_MSG_API=mpi

_OUTDIR=hpcc_opt.run
CMD=../hpcc_opt
seq=1
while [[ $seq -le $RA_MAX_RUNS ]]
do
	OUTDIR="${_OUTDIR}${seq}"
	if [ ! -d ${OUTDIR} ]
	then
		echo "mkdir -p ${OUTDIR}"
		mkdir -p ${OUTDIR}
	fi
	echo "cd ${OUTDIR}"
	cd ${OUTDIR}
	task=2
	while [[ $task -le $RA_MAX_TASKS ]]
	do
		echo "../run_hpcc.pl "${CMD}" ${task} ${RA_TABLE_SIZE}"
		../run_hpcc.pl "${CMD}" ${task} ${RA_TABLE_SIZE}
		let "task = task * 2"
	done
	echo "cd .."
	cd ..
	let "seq = seq + 1"
done


### gups (LAPI)

export MP_MSG_API=lapi

_OUTDIR=gups.run
CMD=../gups
seq=1
while [[ $seq -le $RA_MAX_RUNS ]]
do
	OUTDIR="${_OUTDIR}${seq}"
	if [ ! -d ${OUTDIR} ]
	then
		echo "mkdir -p ${OUTDIR}"
		mkdir -p ${OUTDIR}
	fi
	echo "cd ${OUTDIR}"
	cd ${OUTDIR}
	task=2
	while [[ $task -le $RA_MAX_TASKS ]]
	do
		echo "poe ${CMD} -a ${RA_TABLE_SIZE} -procs ${task} \\"
		echo "	2>&1| tee ${task}x${RA_TABLE_SIZE}.out"
		poe ${CMD} -a ${RA_TABLE_SIZE} -procs ${task} \
			2>&1| tee ${task}x${RA_TABLE_SIZE}.out
		let "task = task * 2"
	done
	echo "cd .."
	cd ..
	let "seq = seq + 1"
done


### gups (Generated Code)

export MP_MSG_API=lapi

_OUTDIR=x10c.run
CMD=../RandomAccess_Dist
seq=1
while [[ $seq -le $RA_MAX_RUNS ]]
do
	OUTDIR="${_OUTDIR}${seq}"
	if [ ! -d ${OUTDIR} ]
	then
		echo "mkdir -p ${OUTDIR}"
		mkdir -p ${OUTDIR}
	fi
	echo "cd ${OUTDIR}"
	cd ${OUTDIR}
	task=2
	while [[ $task -le $RA_MAX_TASKS ]]
	do
		echo "poe ${CMD} -procs ${task} \\"
		echo "	2>&1| tee ${task}x${RA_TABLE_SIZE}.out"
		poe ${CMD} -procs ${task} \
			2>&1| tee ${task}x${RA_TABLE_SIZE}.out
		let "task = task * 2"
	done
	echo "cd .."
	cd ..
	let "seq = seq + 1"
done


### gups (X10Lib)

export MP_MSG_API=lapi

_OUTDIR=x10lib.run
CMD=../RandomAccess_spmd
seq=1
while [[ $seq -le $RA_MAX_RUNS ]]
do
	OUTDIR="${_OUTDIR}${seq}"
	if [ ! -d ${OUTDIR} ]
	then
		echo "mkdir -p ${OUTDIR}"
		mkdir -p ${OUTDIR}
	fi
	echo "cd ${OUTDIR}"
	cd ${OUTDIR}
	task=2
	while [[ $task -le $RA_MAX_TASKS ]]
	do
		echo "poe ${CMD} -a ${RA_TABLE_SIZE} -procs ${task} \\"
		echo "	2>&1| tee ${task}x${RA_TABLE_SIZE}.out"
		poe ${CMD} -a ${RA_TABLE_SIZE} -procs ${task} \
			2>&1| tee ${task}x${RA_TABLE_SIZE}.out
		let "task = task * 2"
	done
	echo "cd .."
	cd ..
	let "seq = seq + 1"
done
