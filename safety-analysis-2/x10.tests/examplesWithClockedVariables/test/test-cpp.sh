if [ $# == 1 ]
then
 example=($1)
else
example=( AllReduceParallel Pipeline Convolve NQueensPar MontyPiParallel KMeansScalar Histogram MergeSort Stream Prefix UTS IDEA SOR Stencil Series RayTrace LUFact SparseMatMul)
fi
export X10RT_STANDALONE_NUMPLACES=1
for ((i = 0; i < ${#example[@]}; i++))
do
	
	echo ""
	echo "----${example[$i]}: Clocked Code -----"
	echo ""
	../../../x10.dist/bin/x10c++ -SAFE_PARALLELIZATION_CHECK=true  -x10rt standalone ../${example[$i]}.x10 > out.txt 
	time  ./a.out 

	echo ""
	echo "----${example[$i]}Orig: Original Code -----"
	echo ""
	../../../x10.dist/bin/x10c++  -x10rt standalone ../${example[$i]}Orig.x10 >> out.txt 
	time  ./a.out

	echo ""
	echo "----${example[$i]}Orig: Original Code with safe parallelization check -----"
	echo ""
	../../../x10.dist/bin/x10c++ -x10rt standalone -SAFE_PARALLELIZATION_CHECK=true ../${example[$i]}Orig.x10 >> out.txt 
done

%rm *.h *.inc *.class

