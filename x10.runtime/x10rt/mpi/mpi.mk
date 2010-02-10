TESTS += $(patsubst test/%,test/%.mpi,$(BASE_TESTS))

LIBS += lib/libx10rt_mpi.so

PROPERTIES += etc/x10rt_mpi.properties

%.mpi: %.cc lib/libx10rt_mpi.so
	$(MPICXX) $(CXXFLAGS) $< -o $@ $(LDFLAGS) -lx10rt_mpi $(X10RT_TEST_LDFLAGS)

mpi/x10rt_mpi.o: mpi/x10rt_mpi.cc
	$(MPICXX) $(CXXFLAGS) $(CXXFLAGS_SHARED) -c $< -o $@

lib/libx10rt_mpi.so: mpi/x10rt_mpi.o $(COMMON_OBJS)
	$(MPICXX) $(CXXFLAGS) $(CXXFLAGS_SHARED) -o $@ $^

etc/x10rt_mpi.properties:
	@echo "CXX=$(MPICXX)" > $@
	@echo "CXXFLAGS=" >> $@
	@echo "LDFLAGS=$(CUDA_LDFLAGS)" >> $@
	@echo "LDLIBS=-lx10rt_mpi $(CUDA_LDLIBS)" >> $@

.PRECIOUS: etc/x10rt_mpi.properties

# vim: ts=8:sw=8:noet
