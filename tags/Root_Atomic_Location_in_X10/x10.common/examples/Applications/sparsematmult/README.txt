[ This description of the benchmark is taken from 
  http://www.epcc.ed.ac.uk/javagrande/threads/s2contents.html ]

SparseMatmult 

This uses an unstructured sparse matrix stored in
compressed-row format with a prescribed sparsity structure. This
kernel exercises indirection addressing and non-regular memory
references. A N x N sparse matrix is used for 200 iterations.

The principle computation involves an outer loop over iterations and
an inner loop over the size of the principal arrays. The simplest
parallelisation mechanism is to divided the loop over the array length
between threads. Parallelising this loop creates the potential for
more than one thread to up-date the same element of the result
vector. To avoid this the non zero elements are sorted by their row
value. The loop has then been parallelised by dividing the iterations
into blocks, which are approximately equal, but adjusted to ensure
that no row is access by more than one thread.