#!/usr/bin/ksh

if [ $# -ne 1 ]
then
	echo "Usage: xtract.sh file.dat"
	exit 1
fi

in_file=$1
out_file=`echo $in_file | sed -e 's;.dat;.tbl;'`
sed -n '1,10p' $in_file > $out_file
egrep '(^# Run|^Number.*|^N=.* t=.*|^D=.*|^Completed)' $in_file | \
	awk '$0 ~ /^# Run.*/ { seq = $3; \
							printf "# =================================================================\n"; \
							printf "# %10s %10s %4s %10s %15s [Run: %d]\n", "VERTICES", "EDGES", "PROC", "TIME(Sec)", "EDGES/Sec", seq; \
							printf "# =================================================================\n"; \
			} \
			$0 ~ /^Number.*/ { nproc = substr($3,7); run_time = -1; }\
			$0 ~ /^D=.*/ { degree = substr($1,3); } \
			$0 ~ /^N=.* t=.*/ { n_vertices = substr($1,3) ; \
							n_edges = degree * n_vertices; \
							if (run_time == -1) \
								run_time = substr($2,3); \
							else {
								run_time2 = substr($2,3); \
								if (run_time > run_time2) \
									run_time = run_time2; \
							} \
							run_time_sec = (run_time / 1E9); \
							edges_sec = n_edges / run_time_sec; }\
			$0 ~ /^Completed.*/ { \
						printf "  %10d %10d %4d %10.6f %15.2f\n", n_vertices, n_edges, nproc, run_time_sec, edges_sec; \
			}' \
		>> $out_file
