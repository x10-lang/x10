#!/usr/bin/ksh

#
# (c) IBM Corporation 2008
#
# $Id: mk.plot.2.sh,v 1.2 2008-06-27 09:24:05 srkodali Exp $
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

out_file=bfs_random_moxie_color.jpg
tmp_file=/tmp/mk.plot.gnu.$$
printf "set terminal jpeg medium size 800,200\n" > $tmp_file
printf "set output \"${out_file}\"\n" >> $tmp_file
printf "set size 1,1\n" >> $tmp_file
printf "set grid xtics\n" >> $tmp_file
printf "set style data linespoints\n" >> $tmp_file
printf "set xtics 0,4,32\n" >> $tmp_file
printf "set xrange [1:32]\n" >> $tmp_file
printf "set yrange [0:60]\n" >> $tmp_file

printf "set multiplot\n" >> $tmp_file

printf "set rmargin 0\n" >> $tmp_file
printf "set ytics\n" >> $tmp_file
printf "set ytics nomirror\n" >> $tmp_file
printf "set ylabel \"MEdges/Sec\"\n" >> $tmp_file
printf "set xlabel \" \"\n" >> $tmp_file
printf "set title \"[BFS/Random/Moxie] 250K\"\n" >> $tmp_file
printf "set origin 0,0\n" >> $tmp_file
printf "set size 0.25,1\n" >> $tmp_file
printf "plot \"${java_tbl}\" index 0 using 3:5 title \"\" with linespoints lw 2 pt 4, \\" >> $tmp_file
printf "\n" >> $tmp_file
printf "\"${c_tbl}\" index 0 using 3:5 title \"\" with linespoints lw 2 pt 8, \\" >> $tmp_file
printf "\n" >> $tmp_file
printf "\"${cilk_tbl}\" index 0 using 3:5 title \"\" with linespoints lw 2 pt 2" >> $tmp_file
printf "\n" >> $tmp_file

printf "set lmargin 0\n" >> $tmp_file
printf "set title \"Size: 1M\"\n" >> $tmp_file
printf "set noylabel\n" >> $tmp_file
printf "set noytics\n" >> $tmp_file
printf "set xlabel \" \"\n" >> $tmp_file
printf "set origin 0.25,0\n" >> $tmp_file
printf "set size 0.25,1\n" >> $tmp_file
printf "plot \"${java_tbl}\" index 1 using 3:5 title \"\" with linespoints lw 2 pt 4, \\" >> $tmp_file
printf "\n" >> $tmp_file
printf "\"${c_tbl}\" index 1 using 3:5 title \"\" with linespoints lw 2 pt 8, \\" >> $tmp_file
printf "\n" >> $tmp_file
printf "\"${cilk_tbl}\" index 1 using 3:5 title \"\" with linespoints lw 2 pt 2" >> $tmp_file
printf "\n" >> $tmp_file

printf "set xlabel \" \"\n" >> $tmp_file
printf "set title \"Size: 4M\"\n" >> $tmp_file
printf "set origin 0.5,0\n" >> $tmp_file
printf "set size 0.25,1\n" >> $tmp_file
printf "plot \"${java_tbl}\" index 2 using 3:5 title \"\" with linespoints lw 2 pt 4, \\" >> $tmp_file
printf "\n" >> $tmp_file
printf "\"${c_tbl}\" index 2 using 3:5 title \"\" with linespoints lw 2 pt 8, \\" >> $tmp_file
printf "\n" >> $tmp_file
printf "\"${cilk_tbl}\" index 2 using 3:5 title \"\" with linespoints lw 2 pt 2" >> $tmp_file
printf "\n" >> $tmp_file


printf "set xlabel \"Processes\"\n" >> $tmp_file
printf "unset rmargin\n" >> $tmp_file
printf "set title \"Size: 9M\"\n" >> $tmp_file
printf "set origin 0.75,0\n" >> $tmp_file
printf "set size 0.25,1\n" >> $tmp_file
printf "plot \"${java_tbl}\" index 3 using 3:5 title \"XWS\" with linespoints lw 2 pt 4, \\" >> $tmp_file
printf "\n" >> $tmp_file
printf "\"${c_tbl}\" index 3 using 3:5 title \"C\" with linespoints lw 2 pt 8, \\" >> $tmp_file
printf "\n" >> $tmp_file
printf "\"${cilk_tbl}\" index 3 using 3:5 title \"Cilk\" with linespoints lw 2 pt 2" >> $tmp_file
printf "\n" >> $tmp_file

printf "unset multiplot\n" >> $tmp_file
gnuplot $tmp_file
rm -f $tmp_file
