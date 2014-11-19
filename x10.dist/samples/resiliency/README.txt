-----------------------
Notes on Resilient X10:
-----------------------

Resilient X10 programs are compiled as normal. Resilient X10 relies
solely on runtime system (execution time) support that is activated by
setting environment variables when running the program.

They can be executed as normal as well, but will not actually be
resilient unless the environment variable X10_RESILIENT_MODE is set to
a non-zero value.  The currently supported values of
X10_RESILIENT_MODE are:
  X10_RESILIENT_MODE=0	-> Non-resilient X10 (normal execution)
  X10_RESILIENT_MODE=1	-> Most stable resilient mode (now is an alias to 11)
  X10_RESILIENT_MODE=11	-> Place0-based resilient finish
  X10_RESILIENT_MODE=99	-> Non-resilient X10, but resilient/elastic X10RT

We are planning to add various implementations of resilient finish,
but you can choose the most stable one by "X10_RESILIENT_MODE=1".

A paper describing Resilient X10 appeared in PPoPP 2014
(http://dl.acm.org/citation.cfm?doid=2555243.2555248). 

A paper describing how to make X10 applications fault tolerant in X10 2014
(http://x10.sourceforge.net/documentation/papers/X10Workshop2014/x1014-kawachiya.pdf
 http://x10.sourceforge.net/documentation/papers/X10Workshop2014/x1014-kawachiya-slides.pdf).

How to run each sample app:

./ResilientMontePi
./ResilientKMeans [num_points] [num_clusters]
./ResilientHeatTransfer [size]
./ResilientSimpleHeatTransfer [size]
./ResilientPlhHeatTransfer [size]

MatVecMult assumes data exists on disk in a particular format.  This
data is not publicly available, although the format is simple and
can be understood by reading the source code.  The app is included in
SVN because it is an interesting example to read, but it is not
expected that anyone will try to run it.

You need to kill place(s) manually to test the resiliency.  Note that 
place 0 should not be killed in current implementation.

Known limitations:
- Currently, only "X10_RESILIENT_MODE=11" is supported.
- Both of native and managed X10 are supported, but only for sockets
  (default) backend.
- Clocked finish and collecting finish are not resilient yet.
