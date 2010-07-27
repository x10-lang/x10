#Gnuplot script file for plotting data in file "pipe_mem.dat"
set term post eps color font "Times-Roman"
set output 'SimpleFinish2_Root.eps'
#set log y
set size 0.6, 0.4
set xlabel "Number of Places"
set ylabel "Memory Consumption (KB)"
set xrange [0:16]
set yrange [0:500]
#set key 1000,5
set noborder
set tics nomirror
plot "root.dat" using 1:2 title 'Old RootFinish' with linespoints lc 1, \
     "root.dat" using 1:3 title 'New RootFinish' with linespoints lc 3
