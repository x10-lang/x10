
rm *.java *.class

echo ""
echo "-----AllReduceParallel-----"
echo ""
../../../x10.dist/bin/x10c -SAFE_PARALLELIZATION_CHECK=true ../AllReduceParallel.x10 > out.txt 
../../../x10.dist/bin/x10  AllReduceParallel.x10 
echo ""
echo "-----Pipeline-----"
echo ""
../../../x10.dist/bin/x10c -SAFE_PARALLELIZATION_CHECK=true  ../Pipeline.x10 > out.txt
../../../x10.dist/bin/x10  Pipeline.x10 
echo ""
echo "-----Convolve----"
echo ""
../../../x10.dist/bin/x10c -SAFE_PARALLELIZATION_CHECK=true  ../Convolve.x10 > out.txt
../../../x10.dist/bin/x10   Convolve.x10 
echo ""
echo "-----NQueensPar-----"
echo ""
../../../x10.dist/bin/x10c -SAFE_PARALLELIZATION_CHECK=true  ../NQueensPar.x10 > out.txt
../../../x10.dist/bin/x10  NQueensPar.x10 
echo ""
echo "----MontiPiParallel-----"
echo ""
../../../x10.dist/bin/x10c -SAFE_PARALLELIZATION_CHECK=true  ../MontyPiParallel.x10 > out.txt
../../../x10.dist/bin/x10  MontyPiParallel.x10 
echo ""
echo "-----KMeansScalar-----"
echo ""
../../../x10.dist/bin/x10c -SAFE_PARALLELIZATION_CHECK=true  ../KMeansScalar.x10 > out.txt
../../../x10.dist/bin/x10  KMeansScalar.x10 
echo ""
echo "-----Histogram-----"
echo ""
../../../x10.dist/bin/x10c -SAFE_PARALLELIZATION_CHECK=true  ../Histogram.x10 > out.txt
../../../x10.dist/bin/x10  Histogram.x10 
echo ""
echo "-----MergeSort-----"
echo ""
../../../x10.dist/bin/x10c -SAFE_PARALLELIZATION_CHECK=true  ../MergeSort.x10 > out.txt
../../../x10.dist/bin/x10  MergeSort.x10 


