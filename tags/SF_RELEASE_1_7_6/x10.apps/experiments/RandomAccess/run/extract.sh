#
# (c) Copyright IBM Corporation 2007
# $Id: extract.sh,v 1.1 2007-08-02 13:09:59 srkodali Exp $
# This file is part of X10 Applications.
#

## Script for extracting plot data from raw output files.

tabsize=${RA_TABLE_SIZE}
OUT_DATA=plot.${tabsize}.dat
for _dir in hpcc.run hpcc_opt.run
do
	seq=1
	while [[ $seq -le $RA_MAX_RUNS ]]
	do
		dir="${_dir}${seq}"
		echo "cd $dir"
		cd $dir

		printf "#Table Size : ${tabsize} [Run $seq]\n" \
			> ${OUT_DATA}
		printf "#%6s %8s %8s\n" "Tasks" "GUPS" "Seconds" >> ${OUT_DATA}

		tasks=2
		while [[ $tasks -le $RA_MAX_TASKS ]]
		do
			file="${tasks}x${tabsize}.out"
			time=`egrep '^CPU time used' $file | awk '{print $5}'`
			gups=`egrep 'Billion\(10\^9\) Updates    ' $file | \
						awk '{print $1}'`
			printf "%6d %8.6f %8.1f\n" $tasks $gups $time \
				>> ${OUT_DATA}
			let "tasks = tasks * 2"
		done
		echo "cd .."
		cd ..
		let "seq = seq + 1"
	done
done

for _dir in gups.run
do
	seq=1
	while [[ $seq -le $RA_MAX_RUNS ]]
	do
		dir="${_dir}${seq}"
		echo "cd $dir"
		cd $dir

		printf "#Table Size : ${tabsize} [Run $seq]\n" \
			> ${OUT_DATA}
		printf "#%6s %8s %8s\n" "Tasks" "GUPS" "Seconds" >> ${OUT_DATA}

		tasks=2
		while [[ $tasks -le $RA_MAX_TASKS ]]
		do
			export NUM_TASKS=${tasks}
			file="${tasks}x${tabsize}.out"
			awk 'NR == 1 { \
				printf "%6d %8.6f %8.1f\n",ENVIRON["NUM_TASKS"],$5,$7 \
			}' $file >> ${OUT_DATA}
			let "tasks = tasks * 2"
		done
		echo "cd .."
		cd ..
		let "seq = seq + 1"
	done
done

for _dir in x10c.run x10lib.run
do
	seq=1
	while [[ $seq -le $RA_MAX_RUNS ]]
	do
		dir="${_dir}${seq}"
		echo "cd $dir"
		cd $dir

		printf "#Table Size : ${tabsize} [Run $seq]\n" \
			> ${OUT_DATA}
		printf "#%6s %8s %8s\n" "Tasks" "GUPS" "Seconds" >> ${OUT_DATA}

		tasks=2
		while [[ $tasks -le $RA_MAX_TASKS ]]
		do
			file="${tasks}x${tabsize}.out"
			time=`egrep 'CPU time used' $file | awk '{print $5}'`
			gups=`egrep 'Billion[ ]*\(10\^9\) Updates' $file | \
						awk '{print $1}'`
			printf "%6d %8.6f %8.1f\n" $tasks $gups $time \
				>> ${OUT_DATA}
			let "tasks = tasks * 2"
		done
		echo "cd .."
		cd ..
		let "seq = seq + 1"
	done
done
