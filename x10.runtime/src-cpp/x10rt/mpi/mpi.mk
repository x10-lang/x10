TESTS += $(patsubst test/%,test/%.mpi,$(BASE_TESTS))

LIBS += lib/libx10rt_mpi.a

PROPERTIES += etc/libx10rt_mpi.properties

%.mpi: %.cc lib/libx10rt_mpi.a
	${MPICXX} ${CXXFLAGS} $< -o $@ ${LDFLAGS} -lx10rt_mpi

mpi/x10rt_mpi.o: mpi/x10rt_mpi.cc
	${MPICXX} ${CXXFLAGS} -c $< -o $@

lib/libx10rt_mpi.a: mpi/x10rt_mpi.o
	${AR} rcu $@ $^

etc/x10rt_mpi.properties:

# vim: ts=8:sw=8:noet
