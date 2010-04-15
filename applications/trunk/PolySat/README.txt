COMPILING:

x10c++ -d out_dir PolySat.x10 -o PolySat



RUNNING:

If built with pgas sockets, you can run from two terminals like this:

Termainal1$ X10_NTHREADS=1 BE=0 OF=2 runx10 ./PolySat example_medium.cnf

Termainal2$ X10_NTHREADS=1 BE=1 OF=2 runx10 ./PolySat

(It can also be run with launcher/manager/mpi/etc)


DATA FILE:

example.cnf taken from http://www.satcompetition.org/
2009 competition
random.7z
RANDOM/MEDIUM/3SAT/SATISFIABLE/360/unif-k3-r4.25-v360-c1530-S1293537826-039.cnf
