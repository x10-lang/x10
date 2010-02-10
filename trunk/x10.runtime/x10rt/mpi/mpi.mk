TESTS += $(patsubst test/%,test/%.mpi,$(BASE_TESTS))

MPI_DYNLIB = lib/$(LIBPREFIX)x10rt_mpi$(LIBSUFFIX)
LIBS += $(MPI_DYNLIB)

PROPERTIES += etc/x10rt_mpi.properties

%.mpi: %.cc $(MPI_DYNLIB)
	$(MPICXX) $(CXXFLAGS) $< -o $@ $(LDFLAGS) -lx10rt_mpi $(X10RT_TEST_LDFLAGS)

mpi/x10rt_mpi.o: mpi/x10rt_mpi.cc
	$(MPICXX) $(CXXFLAGS) $(CXXFLAGS_SHARED) -c $< -o $@

$(MPI_DYNLIB): mpi/x10rt_mpi.o $(COMMON_OBJS)
	$(MPICXX) $(CXXFLAGS) $(CXXFLAGS_SHARED) -o $@ $^

etc/x10rt_mpi.properties:
	@echo "CXX=$(MPICXX)" > $@
	@echo "CXXFLAGS=" >> $@
	@echo "LDFLAGS=$(CUDA_LDFLAGS)" >> $@
	@echo "LDLIBS=-lx10rt_mpi $(CUDA_LDLIBS)" >> $@

.PRECIOUS: etc/x10rt_mpi.properties

# vim: ts=8:sw=8:noet
