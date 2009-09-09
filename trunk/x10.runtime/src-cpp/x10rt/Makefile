CXX             = mpicxx
CXXFLAGS          = -O3 -g -I include -DENABLE_ASSERT -pthread
LDFLAGS         = -L lib

TESTS           = test/x10rt_basic

all: test/x10rt_basic.mpi

%.mpi: %.cc lib/libxlpgas_mpi.a
	${CXX} ${CXXFLAGS} $< -o $@ -L lib -lxlpgas_mpi

lib/libxlpgas_mpi.a: mpi/x10rt_mpi.o
	${AR} rcu $@ $^


%.standalone: %.cc lib/libxlpgas_standalone.a
	${CXX} ${CXXFLAGS} $< -o $@ -L lib -lxlpgas_mpi

lib/libxlpgas_standalone.a: standalone/x10rt_standalone.o
	${AR} rcu $@ $^


clean:
	${RM} mpi/*.o test/*.o x10rt_basic *~ core*

# vim: ts=8:sw=8:noet
