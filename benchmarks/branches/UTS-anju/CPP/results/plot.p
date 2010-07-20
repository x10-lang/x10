set terminal jpeg
set output "plot.jpg"
set grid
set title "UTS: Plot of nodes processed per second. k = number of nodes stolen per steal."
set label "Triloka, 16 Quad-core nodes" at 0.5,51
set label "GCC 4.3.2-7, CXXFLAGS=-O3 -fomit-framepointer" at 0.5,49
set label "X10RT with MPI backend (OpenMPI 1.4.2)" at 0.5,47
set xlabel "Number of processes"
set ylabel "Number of nodes (in millions) per second"
set style histogram clustered gap 5 title offset 0,-10
set style fill solid 1.0 border -1
set style data histograms
plot 'par_uts.1.log' using 5:xtic(1) title 2, \
     'par_uts.2.log' using 5:xtic(1) title 2, \
     'par_uts.4.log' using 5:xtic(1) title 2, \
     'par_uts.8.log' using 5:xtic(1) title 2, \
     'par_uts.16.log' using 5:xtic(1) title 2, \
     'par_uts.32.log' using 5:xtic(1) title 2, \
     'par_uts.64.log' using 5:xtic(1) title 2, \
     'par_uts.128.log' using 5:xtic(1) title 2, \
     'par_uts.256.log' using 5:xtic(1) title 2, \
     'par_uts.512.log' using 5:xtic(1) title 2
