#!/usr/bin/ksh

#
# (c) IBM Corporation 2008
#
# $Id: xtract.tbl.sh,v 1.1 2008-02-24 09:47:51 srkodali Exp $
#
# Random table extraction script for Java.
#

if [ $# -ne 1 ]
then
	echo "Usage: xtract.tbl.sh file.dat"
	exit 1
fi

in_file=$1
out_file=`echo $in_file | sed -e 's;.dat;.tbl;'`
sed -n '1,7p' $in_file > $out_file
sed '/^\#<<<< BEGIN/,/^\#<<<< END/d' $in_file | \
egrep '(^# Run|^## Size|^Number.*|^N=|^M=.* t=.*|^Completed)' | \
	awk '$0 ~ /^# Run.*/ { seq = $3; } \
			$0 ~ /^## Size.*/ { size = $3; \
							printf "\n# %10s %10s %5s %10s %10s %8s %8s [Run: %d]\n\n", "VERTICS", "EDGES", "NPROC", "TIME(Sec)", "MEDGES/Sec", "STEALS", "ATTEMPTS", seq; \
			} \
			$0 ~ /^Number.*/ { nproc = substr($3,7); rtime = -1; } \
			$0 ~ /^BATCH_SIZE.*/ { batch = substr($1,12); } \
			$0 ~ /^N=.*/ { inval = substr($1,3); vertics = inval; } \
			$0 ~ /^M=.* t=.*/ { edges = substr($1,3); \
				if (rtime == -1) { \
					rtime = substr($2,3); \
					steals = substr($6,4); \
					attempts = substr($7,4); \
				} else { \
					rtime2 = substr($2,3); \
					if (rtime > rtime2) { \
						rtime = rtime2; \
						steals = substr($6,4); \
						attempts = substr($7,4); \
					} \
				} \
				medges_sec = edges / (rtime * 1000000); } \
			$0 ~ /^Completed.*/ { \
							printf "  %10d %10d %5d %10.6f %10.6f %8d %8d\n", vertics, edges, nproc, rtime, medges_sec, steals, attempts; }' \
		>> $out_file
printf "\n\n" >> $out_file
tail -3 $in_file >> $out_file
