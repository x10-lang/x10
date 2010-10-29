 example=(TestNext)
rm out.txt
COUNT=4
for ((i = 0; i < ${#example[@]}; i++))
do
	../../../x10.dist/bin/x10c -SAFE_PARALLELIZATION_CHECK=true ../${example[$i]}.x10 >> out.txt 
	for ((j = 0; j < $COUNT; j+=1))
	do
	START=`perl -MTime::HiRes=gettimeofday -e 'print
int(1000*gettimeofday()).qq(\n);'` 
		../../../x10.dist/bin/x10  ${example[$i]}.x10 $j >> out.txt
	END=`perl -MTime::HiRes=gettimeofday -e 'print
int(1000*gettimeofday()).qq(\n);'` 
	DIFF1=$(( $END - $START ))

	 echo ""|awk -v sn=$j  -v n1=$DIFF1 '{print sn+1, n1}'
	done
	
done

rm *.java *.class

