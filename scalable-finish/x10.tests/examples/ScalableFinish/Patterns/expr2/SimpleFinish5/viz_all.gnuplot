#Gnuplot script file for plotting data in file "pipe_mem.dat"
set term post eps color font "Times-Roman"
set output 'SimpleFinish5_All.eps'
#set log y
set size 0.6, 0.4
set xlabel "Number of Places"
set ylabel "Memory Consumption (KB)"
set xrange [0:16]
set yrange [0:150000]
#set key 1000,5
set noborder
set tics nomirror
plot "all.dat" using 1:2 title 'Old Finish' with linespoints lc 1, \
     "all.dat" using 1:3 title 'New Finish' with linespoints lc 3
