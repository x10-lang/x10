%.mpi: %.cc lib/libx10rt_mpi.a
	${MPICXX} ${CXXFLAGS} $< -o $@ ${LDFLAGS} -lx10rt_mpi

mpi/x10rt_mpi.o: mpi/x10rt_mpi.cc
	${MPICXX} ${CXXFLAGS} -c $< -o $@

lib/libx10rt_mpi.a: mpi/x10rt_mpi.o
	${AR} rcu $@ $^

CLEAN += */*.mpi

# vim: ts=8:sw=8:noet
