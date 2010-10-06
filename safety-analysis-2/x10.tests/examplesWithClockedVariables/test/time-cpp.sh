export X10RT_STANDALONE_NUMPLACES=1

if [ $# == 1 ]
then
 example=($1)
else
example=( AllReduceParallel Pipeline Convolve NQueensPar MontyPiParallel KMeansScalar Histogram MergeSort Stream Prefix UTS IDEA SOR Stencil Series RayTrace LUFact SparseMatMul)
fi
rm out.txt
COUNT=10
for ((i = 0; i < ${#example[@]}; i++))
do
	../../../x10.dist/bin/x10c++ -SAFE_PARALLELIZATION_CHECK=true -x10rt standalone ../${example[$i]}.x10 >> out.txt 
	START=`perl -MTime::HiRes=gettimeofday -e 'print
int(1000*gettimeofday()).qq(\n);'` 
	for ((j = 0; j < $COUNT; j++))
	do
		./a.out >> out.txt
	done
	END=`perl -MTime::HiRes=gettimeofday -e 'print
int(1000*gettimeofday()).qq(\n);'` 
	DIFF1=$(( $END - $START ))


	../../../x10.dist/bin/x10c++ -x10rt standalone ../${example[$i]}Orig.x10 >> out.txt 
	START=`perl -MTime::HiRes=gettimeofday -e 'print
int(1000*gettimeofday()).qq(\n);'` 
	for ((j = 0; j < $COUNT; j++))
	do
		./a.out  >> out.txt 
	done
	END=`perl -MTime::HiRes=gettimeofday -e 'print
int(1000*gettimeofday()).qq(\n);'` 
	DIFF2=$(( $END - $START ))
 echo ""|awk -v sn=$i -v app=${example[$i]} -v n1=$DIFF2 -v n2=$DIFF1 '{print sn+1, app, n1, n2, n1/n2}'
	
done

rm *.cc *.h *.inc

