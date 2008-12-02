#!/usr/bin/ksh

#
# (c) IBM Corporation 2008
#
# $Id: mk.plot.sh,v 1.1 2008-02-25 11:18:44 srkodali Exp $
#
# Script for generating plots from tables.
#

if [ $# -ne 3 ]
then
	printf "Usage: mk.plot.sh java.tbl c.tbl cilk.tbl\n"
	exit 1
fi

java_tbl=$1
c_tbl=$2
cilk_tbl=$3

out_file=`basename $java_tbl | sed -e 's;.tbl;;'`
tmp_file=/tmp/mk.plot.gnu.$$
printf "set autoscale x\n" > $tmp_file
printf "set autoscale y\n" >> $tmp_file
printf "set xtics 0,2,8\n" >> $tmp_file
printf "set ytics nomirror\n" >> $tmp_file
printf "set grid\n" >> $tmp_file
#printf "set size 1,1\n" >> $tmp_file
printf "set style data linespoints\n" >> $tmp_file
printf "set terminal png\n" >> $tmp_file

printf "set output \"${out_file}.png\"\n" >> $tmp_file
printf "set ylabel \"MEdges/Sec\"\n" >> $tmp_file
printf "set xlabel \"Processes\"\n" >> $tmp_file
printf "set xrange [1:8]\n" >> $tmp_file

printf "set multiplot\n" >> $tmp_file
printf "set title \"BFS:Random [Altair/250K]\"\n" >> $tmp_file
printf "set origin 0,0.5\n" >> $tmp_file
printf "set size 0.5,0.5\n" >> $tmp_file
printf "plot \"${java_tbl}\" index 0 using 3:5 title \"XWS-Adaptive BFS\", \\" >> $tmp_file
printf "\n" >> $tmp_file
printf "\"${c_tbl}\" index 0 using 3:5 title \"C-SpanT\", \\" >> $tmp_file
printf "\n" >> $tmp_file
printf "\"${cilk_tbl}\" index 0 using 3:5 title \"Cilk-BFSBlock\"" >> $tmp_file
printf "\n" >> $tmp_file

printf "set title \"BFS:Random [Altair/1M]\"\n" >> $tmp_file
printf "set origin 0.5,0.5\n" >> $tmp_file
printf "set size 0.5,0.5\n" >> $tmp_file
printf "plot \"${java_tbl}\" index 1 using 3:5 title \"XWS-Adaptive BFS\", \\" >> $tmp_file
printf "\n" >> $tmp_file
printf "\"${c_tbl}\" index 1 using 3:5 title \"C-SpanT\", \\" >> $tmp_file
printf "\n" >> $tmp_file
printf "\"${cilk_tbl}\" index 1 using 3:5 title \"Cilk-BFSBlock\"" >> $tmp_file
printf "\n" >> $tmp_file

printf "set title \"BFS:Random [Altair/4M]\"\n" >> $tmp_file
printf "set origin 0,0\n" >> $tmp_file
printf "set size 0.5,0.5\n" >> $tmp_file
printf "plot \"${java_tbl}\" index 2 using 3:5 title \"XWS-Adaptive BFS\", \\" >> $tmp_file
printf "\n" >> $tmp_file
printf "\"${c_tbl}\" index 2 using 3:5 title \"C-SpanT\", \\" >> $tmp_file
printf "\n" >> $tmp_file
printf "\"${cilk_tbl}\" index 2 using 3:5 title \"Cilk-BFSBlock\"" >> $tmp_file
printf "\n" >> $tmp_file


printf "set title \"BFS:Random [Altair/9M]\"\n" >> $tmp_file
printf "set origin 0.5,0\n" >> $tmp_file
printf "set size 0.5,0.5\n" >> $tmp_file
printf "plot \"${java_tbl}\" index 3 using 3:5 title \"XWS-Adaptive BFS\", \\" >> $tmp_file
printf "\n" >> $tmp_file
printf "\"${c_tbl}\" index 3 using 3:5 title \"C-SpanT\", \\" >> $tmp_file
printf "\n" >> $tmp_file
printf "\"${cilk_tbl}\" index 3 using 3:5 title \"Cilk-BFSBlock\"" >> $tmp_file
printf "\n" >> $tmp_file

printf "unset multiplot\n" >> $tmp_file
gnuplot $tmp_file
rm -f $tmp_file
