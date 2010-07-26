example=( AllReduceParallel Pipeline Convolve NQueensPar MontyPiParallel KMeansScalar Histogram MergeSort Stream Prefix UTS IDEA SOR Stencil Series RayTrace LUFact SparseMatMul)
for ((i = 0; i < ${#example[@]}; i++))
do
	
	echo ""
	echo "----${example[$i]}: Clocked Code -----"
	echo ""
	../../../x10.dist/bin/x10c++  -x10rt ../${example[$i]}.x10 > out.txt 
	time  ../../../x10.dist/bin/runx10 a.out

	echo ""
	echo "----${example[$i]}Orig: Original Code -----"
	echo ""
	../../../x10.dist/bin/x10c++  -x10rt ../${example[$i]}Orig.x10 > out.txt 
	time  ../../../x10.dist/bin/runx10 a.out

	echo ""
	echo "----${example[$i]}Orig: Original Code with safe parallelization check -----"
	echo ""
	../../../x10.dist/bin/x10c++  -SAFE_PARALLELIZATION_CHECK=true ../${example[$i]}Orig.x10 > out.txt 
done

rm *.java *.class

