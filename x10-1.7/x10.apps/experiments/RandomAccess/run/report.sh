#
# (c) Copyright IBM Corporation 2007
# $Id: report.sh,v 1.1 2007-08-02 13:09:59 srkodali Exp $
# This file is part of X10 Applications.
#

## Script for generating comparison tables for each run.

tabsize=${RA_TABLE_SIZE}
IN_DATA=plot.${tabsize}.dat

seq=1
while [[ $seq -le $RA_MAX_RUNS ]]
do
	OUT_FILE=report${seq}.txt
	printf "#Table Size : ${tabsize} [Run $seq]\n" > ${OUT_FILE}
	printf "#%6s %8s %8s %8s %8s %8s %8s %8s %8s %8s %8s\n" \
			"" "MPI" "" "MPI-OPT" "" "LAPI" "" \
			"X10LIB" "" "X10C" "" >> ${OUT_FILE}
	printf "#%6s %8s %8s %8s %8s %8s %8s %8s %8s %8s %8s\n" \
			"Tasks" "GUPS" "Seconds" "GUPS" "Seconds" \
			"GUPS" "Seconds" "GUPS" "Seconds" "GUPS" "Seconds" \
				>> ${OUT_FILE}
	tasks=2
	while [[ $tasks -le $RA_MAX_TASKS ]]
	do
		# hpcc
		file=hpcc.run${seq}/${IN_DATA}
		hpcc_gups=`egrep '^[ ]*'${tasks}' ' $file | \
					awk '{print $2}'`
		hpcc_secs=`egrep '^[ ]*'${tasks}' ' $file | \
					awk '{print $3}'`

		# hpcc_opt
		file=hpcc_opt.run${seq}/${IN_DATA}
		hpcc_opt_gups=`egrep '^[ ]*'${tasks}' ' $file | \
					awk '{print $2}'`
		hpcc_opt_secs=`egrep '^[ ]*'${tasks}' ' $file | \
					awk '{print $3}'`
	
		# gups
		file=gups.run${seq}/${IN_DATA}
		lapi_gups=`egrep '^[ ]*'${tasks}' ' $file | \
					awk '{print $2}'`
		lapi_secs=`egrep '^[ ]*'${tasks}' ' $file | \
					awk '{print $3}'`
	
		# x10lib
		file=x10lib.run${seq}/${IN_DATA}
		x10lib_gups=`egrep '^[ ]*'${tasks}' ' $file | \
					awk '{print $2}'`
		x10lib_secs=`egrep '^[ ]*'${tasks}' ' $file | \
					awk '{print $3}'`

		# x10c
		file=x10c.run${seq}/${IN_DATA}
		x10c_gups=`egrep '^[ ]*'${tasks}' ' $file | \
					awk '{print $2}'`
		x10c_secs=`egrep '^[ ]*'${tasks}' ' $file | \
					awk '{print $3}'`

		printf "%6d %8.6f %8.1f %8.6f %8.1f %8.6f %8.1f %8.6f %8.1f %8.6f %8.1f\n" \
			${tasks} ${hpcc_gups} ${hpcc_secs} \
			${hpcc_opt_gups} ${hpcc_opt_secs} \
			${lapi_gups} ${lapi_secs} ${x10lib_gups} \
			${x10lib_secs} ${x10c_gups} ${x10c_secs} >> ${OUT_FILE}

		let "tasks = tasks * 2"
	done
	let "seq = seq + 1"
done
