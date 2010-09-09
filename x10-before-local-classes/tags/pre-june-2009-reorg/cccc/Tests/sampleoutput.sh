. tests.files
pgm=${1-cccc}
for i in $FILES
do
	echo '=========='
	echo Generating Sample Output for $i
	echo '=========='
	../src/$pgm < $i.txt > $i.$pgm.out 2>&1
done
