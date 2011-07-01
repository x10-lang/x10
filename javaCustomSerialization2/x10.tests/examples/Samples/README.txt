This directory contains x10Test harnesses for 
the programs that are found in x10.dist/samples.

The default idiom is to invoke the main method
of the program from in x10.dist/samples from
the run method of the Test program.  In some
cases, instead of calling the main method,
a different entry point in the Sample program
is called to enable verification of the program
result.

The point of this directory is to enable robust testing
of the programs in x10.dist/samples without obscuring the
sample code with any extra boilerplate related to being 
an X10 regression test.
