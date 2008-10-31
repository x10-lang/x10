. tests.files
for i in $FILES
do
	echo '=========='
	echo Testing $i
	echo '=========='
	../src/cccc < $i.txt
done
