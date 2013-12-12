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
  X10_RESILIENT_MODE=1	-> Place0-based resilient finish 
                            (most stable resilient mode)
  X10_RESILIENT_MODE=2	-> Distributed resilient finish
                            (better scalability, but may not be as stable as 
                             Place0-based resilient finish; under development)
  X10_RESILIENT_MODE=3	-> ZooKeeper-based resilient finish
                            (Not yet ready for external usage; uses 
                             additional code not yet committed to sourceforge)

There will be a paper describing Resilient X10 in the upcoming PPoPP 2014
conference in February. 

How to run each sample app:

./ResilientMontePi
./ResilientKMeans [num_points] [num_clusters]
./ResilientHeatTransfer [size]


MatVecMult assumes data exists on disk in a particular format.  This
data is not publicly available, although the format is simple and
can be understood by reading the source code.  The app is included in
SVN because it is an interesting example to read, but it is not
expected that anyone will try to run it.

