CXX             = g++ -ansi -pedantic -Wno-long-long -DENABLE_ASSERT
MPICXX          = mpicxx
CXXFLAGS        = -O3 -g -I..
LDFLAGS         = -L./lib -lpthread

TESTS           = test/x10rt_basic

all: test/x10rt_basic.mpi test/x10rt_basic.standalone

include mpi/mpi.mk
include standalone/standalone.mk

clean:
	${RM} -f */*.o $(CLEAN) lib/*.a */*~ *~ core*

# vim: ts=8:sw=8:noet
