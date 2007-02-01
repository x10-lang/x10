. tests.files
for i in $FILES
do
	echo '=========='
	echo Generating Sample Output for $i
	echo '=========='
	../src/cccc < $i.txt > $i.out 2>&1
done
