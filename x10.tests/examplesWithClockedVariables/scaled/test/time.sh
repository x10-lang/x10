if [ $# == 1 ]
then
 example=($1)
else
 example=( AllReduceParallel Pipeline NQueensPar MontyPiParallel KMeansScalar Histogram MergeSort Stream Prefix UTS IDEA SOR Stencil Series RayTrace LUFact SparseMatMul)
fi
echo "Sno. Application Original(ms) Determinized(ms) Speed-up"
rm out.txt
COUNT=10
WORKERS=4
for ((i = 0; i < ${#example[@]}; i++))
do
	DIFF1=0
	DIFF2=0
	../../../../x10.dist/bin/x10c -SAFE_PARALLELIZATION_CHECK=true ../${example[$i]}.x10 >> out.txt 
	for ((j = 0; j < $COUNT; j++))
	do
		../../../../x10.dist/bin/x10 -INIT_THREADS=$WORKERS  ${example[$i]}.x10 >> out.txt 2>time.txt
	read line < time.txt
	DIFF1=$(($DIFF1+$line))
	done


	../../../../x10.dist/bin/x10c  ../${example[$i]}Orig.x10 >> out.txt 
	for ((j = 0; j < $COUNT; j++))
	do
		../../../../x10.dist/bin/x10 -INIT_THREADS=$WORKERS ${example[$i]}Orig.x10 >> out.txt 2>time.txt 
	read line < time.txt
	DIFF2=$(($DIFF2+$line))
	done
 echo ""|awk -v sn=$i -v app=${example[$i]} -v n1=$DIFF2 -v n2=$DIFF1 '{print sn+1, app, n1, n2, n1/n2}'
	
done

rm *.java *.class

