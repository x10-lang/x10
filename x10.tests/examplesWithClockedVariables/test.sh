example=( AllReduceParallel Pipeline Convolve NQueensPar MontyPiParallel KMeansScalar Histogram MergeSort Stream Prefix UTS IDEA )

for ((i = 0; i < 12; i++))
do
	
	echo ""
	echo "----${example[$i]}-----"
	echo ""
	../../../x10.dist/bin/x10c -SAFE_PARALLELIZATION_CHECK=true ../${example[$i]}.x10 > out.txt 
	time ../../../x10.dist/bin/x10  ${example[$i]}.x10 

	echo ""
	echo "----${example[$i]}Orig-----"
	echo ""
	../../../x10.dist/bin/x10c  ../${example[$i]}Orig.x10 > out.txt 
	time ../../../x10.dist/bin/x10  ${example[$i]}Orig.x10 
done

rm *.java *.class

