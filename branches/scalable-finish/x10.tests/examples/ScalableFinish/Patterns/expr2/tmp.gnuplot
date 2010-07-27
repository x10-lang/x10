#Gnuplot script file for plotting data in file "pipe_mem.dat"
set term post eps color font "Times-Roman"
set output 'pipe_mem.eps'
set log y
set size 0.6, 0.4
set xlabel "Number of processes"
set ylabel "Memory Consumption (MB)"
set xrange [5:1000]
set yrange [0.5:1000]
set key 1000,5
set noborder
set tics nomirror
plot "pipe_mem.dat" using 1:2 title 'NuSMV' with linespoints , \
     "pipe_mem.dat" using 1:3 title 'Compositional analysis' with linespoints
