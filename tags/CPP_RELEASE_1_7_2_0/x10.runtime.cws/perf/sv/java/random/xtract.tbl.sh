#!/usr/bin/ksh

#
# (c) IBM Corporation 2008
#
# $Id: xtract.tbl.sh,v 1.1 2008-02-24 10:34:38 srkodali Exp $
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
egrep '(^# Run|^## Size|^Number.*|^N=.* t=.*|^Completed)' | \
	awk '$0 ~ /^# Run.*/ { seq = $3; } \
			$0 ~ /^## Size.*/ { size = $3; \
							printf "\n# %10s %10s %5s %10s %10s [Run: %d]\n\n", "VERTICS", "EDGES", "NPROC", "TIME(Sec)", "MEDGES/Sec", seq; \
			} \
			$0 ~ /^Number.*/ { nproc = substr($3,7); rtime = -1; } \
			$0 ~ /^BATCH_SIZE.*/ { batch = substr($1,12); } \
			$0 ~ /^N=.* t=.*/ { vertics = substr($1,3); edges = 4 * vertics; \
				if (rtime == -1) { \
					rtime = substr($2,3); \
				} else { \
					rtime2 = substr($2,3); \
					if (rtime > rtime2) { \
						rtime = rtime2; \
					} \
				} \
				rtime_sec = rtime / 1000000000; \
				medges_sec = edges / (rtime_sec * 1000000); } \
			$0 ~ /^Completed.*/ { \
							printf "  %10d %10d %5d %10.6f %10.6f\n", vertics, edges, nproc, rtime_sec, medges_sec; }' \
		>> $out_file
printf "\n\n" >> $out_file
tail -3 $in_file >> $out_file
