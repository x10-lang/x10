
CXX 		?= g++
MPICXX          = mpicxx
CXXFLAGS        += -g -I..
LDFLAGS         += -Llib -lpthread

BASE_TESTS      = test/x10rt_basic test/x10rt_gups

X10_HOME        = ../../..

default: all

ifdef ENABLE_X10RT_MPI
  include mpi/mpi.mk
endif

include standalone/standalone.mk

install: $(LIBS) $(PROPERTIES)
	cp $(LIBS) $(X10_HOME)/x10.dist/lib
	cp $(PROPERTIES) $(X10_HOME)/x10.dist/etc

tests: $(TESTS)

all: $(TESTS) $(PROPERTIES) $(LIBS)

clean:
	${RM} -f */*.o $(LIBS) $(PROPERTIES) */*~ *~ core*

# vim: ts=8:sw=8:noet
