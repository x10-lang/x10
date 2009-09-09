CXX             = g++ -ansi -pedantic -Wno-long-long
MPICXX             = mpicxx
CXXFLAGS          = -O3 -g -I include -pthread
LDFLAGS         = -L lib

TESTS           = test/x10rt_basic

all: test/x10rt_basic.mpi test/x10rt_basic.standalone

%.mpi: %.cc lib/libxlpgas_mpi.a
	${MPICXX} ${CXXFLAGS} $< -o $@ ${LDFLAGS} -lxlpgas_mpi

mpi/x10rt_mpi.o: mpi/x10rt_mpi.cc
	${MPICXX} ${CXXFLAGS} -c $< -o $@

lib/libxlpgas_mpi.a: mpi/x10rt_mpi.o
	${AR} rcu $@ $^


%.standalone: %.cc lib/libxlpgas_standalone.a
	${CXX} ${CXXFLAGS} $< -o $@ ${LDFLAGS} -lxlpgas_standalone

lib/libxlpgas_standalone.a: standalone/x10rt_standalone.o
	${AR} rcu $@ $^


clean:
	${RM} -f */*.o */*.mpi */*.standalone lib/*.a */*~ *~ core*

# vim: ts=8:sw=8:noet
