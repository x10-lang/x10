all: sw run

sw:
	cc -O2 -finline-functions -o sw sw.c -lm

run:
	./sw -s ../data/short9.txt -l ../data/long9.txt -p ../data/baseDNA.txt -r 4

clean:
	rm -f sw *.o
