TESTS += $(patsubst test/%,test/%.mpi,$(BASE_TESTS))

AR ?= ar

LIBS += lib/libx10rt_mpi.la

PROPERTIES += etc/x10rt_mpi.properties

%.mpi: %.cc lib/libx10rt_mpi.la
	libtool --mode=link --tag=CXX $(MPICXX) $(CXXFLAGS) $< -o $@ $(LDFLAGS) -lx10rt_mpi

mpi/x10rt_mpi.lo: mpi/x10rt_mpi.cc
	libtool --mode=compile --tag=CXX $(MPICXX) $(CXXFLAGS) -c $< -o $@

lib/libx10rt_mpi.la: mpi/x10rt_mpi.lo $(COMMON_OBJS)
	libtool --mode=link --tag=CXX $(MPICXX) -o $@ $^ -rpath ${X10_HOME}/x10.dist/lib

etc/x10rt_mpi.properties:
	@echo "CXX=$(MPICXX)" > $@
	@echo "CXXFLAGS=" >> $@
	@echo "LDFLAGS=$(CUDA_LDFLAGS)" >> $@
	@echo "LDLIBS=-lx10rt_mpi $(CUDA_LDLIBS)" >> $@

.PRECIOUS: etc/x10rt_mpi.properties

# vim: ts=8:sw=8:noet
