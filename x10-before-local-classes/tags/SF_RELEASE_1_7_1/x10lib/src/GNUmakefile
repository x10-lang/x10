HEADERS = x10lib.h

default: clean

install:
	cp $(HEADERS) ../include

docs:
	doxygen

clean:
	rm -f *~
	rm -f *.o
	rm -f *.x
	rm -rf ../docs/doxygen/html
	rm -f ../include/*.h