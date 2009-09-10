CXX             = g++ -ansi -pedantic -Wno-long-long
MPICXX          = mpicxx
CXXFLAGS        = -O3 -g -I..
LDFLAGS         = -Llib -lpthread

BASE_TESTS      = test/x10rt_basic

default: tests

include mpi/mpi.mk
include standalone/standalone.mk

install: $(LIBS) $(PROPERTIES)

tests: $(TESTS)

clean:
	${RM} -f */*.o lib/*.a */*~ *~ core*

# vim: ts=8:sw=8:noet
