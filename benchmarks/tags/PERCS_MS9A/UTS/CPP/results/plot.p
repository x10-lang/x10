set terminal jpeg
set output "plot.jpg"
set grid
set autoscale
set title "UTS: Plot of nodes processed per second. k = number of nodes stolen per steal."
set xlabel "Number of processes"
set ylabel "Number of nodes (in millions) per second"
set style fill solid 1.0 border -1
plot "par_uts.1.log" using 5:xtic(1) title "k=1" with histogram, \
     "par_uts.2.log" using 5:xtic(1) title "k=2" with histogram, \
     "par_uts.4.log" using 5:xtic(1) title "k=4" with histogram, \
     "par_uts.8.log" using 5:xtic(1) title "k=8" with histogram, \
     "par_uts.16.log" using 5:xtic(1) title "k=16" with histogram, \
     "par_uts.32.log" using 5:xtic(1) title "k=32" with histogram, \
     "par_uts.64.log" using 5:xtic(1) title "k=64" with histogram, \
     "par_uts.128.log" using 5:xtic(1) title "k=128" with histogram, \
     "par_uts.256.log" using 5:xtic(1) title "k=256" with histogram, \
     "par_uts.512.log" using 5:xtic(1) title "k=512" with histogram, \
     "par_uts.1024.log" using 5:xtic(1) title "k=1024" with histogram
