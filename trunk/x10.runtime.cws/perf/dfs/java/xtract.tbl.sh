#!/usr/bin/ksh

#
# (c) IBM Corporation 2008
#
# $Id: xtract.tbl.sh,v 1.1 2008-02-04 14:47:30 srkodali Exp $
#
# Torus table extraction script for Java.
#

if [ $# -ne 1 ]
then
	echo "Usage: xtract.tbl.sh file.dat"
	exit 1
fi

in_file=$1
out_file=`echo $in_file | sed -e 's;.dat;.tbl;'`
sed -n '1,7p' $in_file > $out_file
#sed '/^\#<<<< BEGIN/,/^\#<<<< END/d' $in_file
egrep '(^# Run|^## Size|^Number.*|^N=|^BATCH_SIZE|^M=.* t=.*|^Completed|^#<<<< BEGIN|^#<<<< END)' $in_file | \
	awk '$0 ~ /^# Run.*/ { seq = $3; } \
			$0 ~ /^## Size.*/ { size = $3; \
							printf "\n# %10s %10s %5s %5s %10s %15s %10s %10s [Run: %d]\n\n", "VERTICS", "EDGES", "NPROC", "BATCH", "TIME(Sec)", "EDGES/Sec", "STEALS", "ATTEMPTS", seq; \
			} \
			$0 ~ /^#<<<< BEGIN.*/ { printf "\n%s\n\n", $0 ; } \
			$0 ~ /^#<<<< END.*/ { printf "\n%s\n\n", $0 ; } \
			$0 ~ /^Number.*/ { nproc = substr($3,7); rtime = -1; } \
			$0 ~ /^BATCH_SIZE.*/ { batch = substr($1,12); } \
			$0 ~ /^N=.*/ { inval = substr($1,3); vertics = inval * inval; } \
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
				edges_sec = edges / rtime; } \
			$0 ~ /^Completed.*/ { \
							printf "  %10d %10d %5d %5d %10.6f %15.2f %10d %10d\n", vertics, edges, nproc, batch, rtime, edges_sec, steals, attempts; }' \
		>> $out_file
printf "\n\n" >> $out_file
tail -3 $in_file >> $out_file
