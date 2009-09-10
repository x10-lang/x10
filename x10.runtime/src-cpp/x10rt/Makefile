CXX             = g++ -ansi -pedantic -Wno-long-long
MPICXX          = mpicxx
CXXFLAGS        = -O3 -g -I..
LDFLAGS         = -Llib -lpthread

BASE_TESTS      = test/x10rt_basic

X10_HOME        = ../../..

default: all

include mpi/mpi.mk
include standalone/standalone.mk

install: $(LIBS) $(PROPERTIES)
	cp $(LIBS) $(X10_HOME)/x10.dist/lib
	cp $(PROPERTIES) $(X10_HOME)/x10.dist/etc

tests: $(TESTS)

all: $(TESTS) $(PROPERTIES) $(LIBS)

clean:
	${RM} -f */*.o $(LIBS) $(PROPERTIES) */*~ *~ core*

# vim: ts=8:sw=8:noet
