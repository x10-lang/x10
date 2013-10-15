-----------------------
Notes on resilient X10:
-----------------------

Resilient X10 programs are compiled as normal.

They can be executed as normal as well, but will not actually be resilient
unless X10_RESILIENT_PLACE_ZERO=1 is specified, in which case the resilient
finish is turned on.  This implementation of finish is slower than the default
finish, which is why it is not enabled by default.


How to run each sample app:

./ResilientMontePi
./ResilientKMeans [num_points] [num_clusters]
./ResilientHeatTransfer [size]


MatVecMult assumes data exists on disk in a particular format.  This data is
not publically available, although the format is simple and can be understood
by reading thes ource code.  The app is included in SVN because it is an
interesting example to read, but it is not expected that anyone will try to
run it.

