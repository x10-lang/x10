set autoscale
unset log 
unset label 
set xtic 1
set ytic auto
set title "Number of nodes/second versus Number of processes"
set xlabel "Number of processes"
set ylabel "Number of nodes per second (in Millions)"
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
