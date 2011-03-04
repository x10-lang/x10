set size 0.65, 0.85 
set terminal postscript 10 
unset grid
set border 3 
set key spacing 1.2
set log y
set boxwidth 0.2
set key at 6,5
#landscape enhanced monochrome
set style data histogram
set ytics nomirror
#set grid
unset grid
set key at 9,3
set style fill pattern 3 border
#set palette gray
set border 3 # bottom and left only
#set key spacing 1.5
#set key top left
set ylabel "Relative Speed"
set xlabel "Application"
set xtics rotate 90
set xrange [-1:19]
set yrange [0.01:100]
set boxwidth .148
set xtics nomirror
set output 'figures/output.eps'
plot  'figures/java.dat' using -1:xticlabel(2) notitle,  'figures/java.dat'  using ($1-0.9):($5) title "Determinized (Java)" with boxes fs solid 1, 'figures/cpp.dat'  using ($1-0.7):($5) title "Determinized (C++)" with boxes,1 with lines lt 4 title "Original"

set size 0.65, 0.65 
unset log y
set ylabel "Time"
set xlabel "Number of tasks"
set output 'figures/next.eps'
set xrange [0:4]
set yrange [0:1200]
set key at 3,1100
plot  'figures/javaNext.dat' using ($1):($2) title "Java" with lines , 'figures/cppNext.dat'  using ($1):($2) title "C++" with lines 
