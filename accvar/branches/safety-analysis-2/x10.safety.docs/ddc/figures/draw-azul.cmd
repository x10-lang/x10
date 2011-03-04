set size 1, 1 
set terminal postscript 10 landscape enhanced monochrome 
unset grid
set border 3 
set key spacing 1.2
set boxwidth 0.1
set xrange [-0.25:6]
set style data histogram
set ytics nomirror
#set grid
unset grid
#set key 9,23
set style fill pattern 3 border
#set palette gray
set border 3 # bottom and left only
#set key spacing 1.5
#set key top left
set ylabel "Relative Speed"
set xlabel "Application"
set xtics rotate 90
set log y
set xrange [-0.5:19]
set yrange [0.01:100]
set boxwidth .148
set xtics nomirror
set output 'figures/azul.eps'
plot  'figures/azulw1.dat' using -1:xticlabel(2) notitle,   'figures/azulw1.dat'  using ($1-0.9):($5) title "1 worker (Determinized)" with boxes fs solid 1,  'figures/azulw2.dat'  using ($1-0.8):($5) title "2 workers (Determinized)" with boxes ,  'figures/azulw4.dat'  using ($1-0.7):($5) title "4 workers (Determinized)" with boxes ,  'figures/azulw8.dat'  using ($1-0.6):($5) title "8 workers (Determinized)" with boxes ,  'figures/azulw16.dat'  using ($1-0.5):($5) title "16 workers (Determinized)" with boxes , 'figures/azulw32.dat'  using ($1-0.4):($5) title "32 workers (Determinized)" with boxes ,  'figures/azulw64.dat'  using ($1-0.3):($5) title "64 workers (Determinized)" with boxes , 1 with lines lt 4 title "Original"
