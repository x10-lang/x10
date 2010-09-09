#!/usr/bin/ksh

# $Id: xtract.tbl.sh,v 1.1 2008-02-25 08:17:57 srkodali Exp $

if [ $# -ne 1 ]
then
	echo "Usage: xtract.sh file.dat"
	exit 1
fi

in_file=$1
out_file=`echo $in_file | sed -e 's;.dat;.tbl;'`
sed -n '1,7p' $in_file > $out_file
sed -e 's;^../../../../ST;ST;g' $in_file | \
egrep '(^# Run|^ST|^graph|^Running|^## Size)' | \
awk '$0 ~ /^# Run.*/ { seq = $3; \
		printf "\n\n# %10s %10s %5s %10s %10s [Run: %d]\n\n", "VERTICS", "EDGES", "PROCS", "TIME(Sec)", "MEDGES/Sec", seq; \
	} \
	$0 ~ /^## Size.*/ {printf "\n\n";} \
	$0 ~ /^ST.*/ { \
		n_procs = $3; \
	} \
	$0 ~ /^graph.*/ { \
		n_vertics = $6; \
		n_edges = n_vertics * 4; \
	} \
	$0 ~ /^Running.*/ { run_time = $7; \
		printf "  %10d %10d %5d %10.6f %10.6f\n", n_vertics, n_edges, n_procs, run_time, (n_edges/(run_time * 1000000)); \
	}' \
>> $out_file
printf "\n\n" >> $out_file
tail -3 $in_file >> $out_file
