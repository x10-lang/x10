To test an example:
--------------------

1. Right click on the x10 file
2. Run Configurations -> x10c -> Run

Type Checking for clocked variables:
------------------------------------

Working examples: (I've ignored effect sets for now)

AllReduceParallel.x10 
Histogram.x10
Convolve.x10 
KMeansScalar.x10
MergeSort.x10
Pipeline.x10
Stream.x10

Things I am stuck at:
----------------------

TestArray.x10 /* Annotations are lost for arrays */
TestRail.x10 /* Annotation is lost if the it is specified in Rail.make */
KMeans.x10 /* Place error during compilation */
NQueensPar.x10 /* Place error during compilation */
MontiPiParallel.x10 /* Type compatibility error */


I am looking at:
-------------------------

Effects Sets  - I'll start fixing the code.
RandomAccess.x10





