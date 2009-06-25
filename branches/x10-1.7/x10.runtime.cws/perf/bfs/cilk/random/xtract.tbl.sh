#!/usr/bin/ksh

if [ $# -ne 1 ]
then
	echo "Usage: xtract.tbl.sh file.dat"
	exit 1
fi

in_file=$1
out_file=`echo $in_file | sed -e 's;.dat;.tbl;'`
sed -n '1,7p' $in_file > $out_file
egrep '(^# Run|^n_vertics|^ running|^Running|^## Size)' $in_file | \
awk '$0 ~ /^# Run.*/ { seq = $3; \
		printf "\n\n# %10s %10s %5s %10s %10s [Run: %d]\n\n", "VERTICS", "EDGES", "PROCS", "TIME(Sec)", "MEDGES/Sec", seq; \
	} \
	$0 ~ /^n_vertics.*/ { \
		n_vertics = substr($1, 11, (length($1) - 11)); \
		n_edges = substr($2, 9, (length($2) - 9)); \
	} \
	$0 ~ /^## Size.*/ { \
		printf "\n\n"; \
	} \
	$0 ~ /^ running.*/ { n_procs = $3; } \
	$0 ~ /^Running.*/ { run_time = $4; \
		printf "  %10d %10d %5d %10.6f %10.6f\n", n_vertics, n_edges, n_procs, run_time, (n_edges/(run_time * 1000000)); \
	}' \
>> $out_file
printf "\n\n" >> $out_file
tail -3 $in_file >> $out_file
