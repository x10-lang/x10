for i in \
	1-Dekker.txt \
	2-IRIW.txt \
	3-CC.txt \
	4-DC.txt \
	fail1.txt \
	fail2.txt \
	fail3.txt \
	lock.txt \
	test1.txt \
	test2.txt \
	test3.txt
do
	echo '=========='
	echo Testing $i
	echo '=========='
	../cccc < $i
done
