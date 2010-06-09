all: sw run

sw:
	mpicc -O2 -finline-functions -o sw sw.c autobind.c -lm

run:
	mpirun -n 1 sw -s ../data/short9.txt -l ../data/long9.txt -p ../data/baseDNA.txt -r 4
	mpirun -n 2 sw -s ../data/short9.txt -l ../data/long9.txt -p ../data/baseDNA.txt -r 4

clean:
	rm -f sw *.o
