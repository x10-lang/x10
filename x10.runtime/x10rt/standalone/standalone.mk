TESTS += $(patsubst test/%,test/%.standalone,$(BASE_TESTS))

STANDALONE_DYNLIB = lib/$(LIBPREFIX)x10rt_standalone$(LIBSUFFIX)
LIBS += $(STANDALONE_DYNLIB)

PROPERTIES += etc/x10rt_standalone.properties

%.standalone: %.cc $(STANDALONE_DYNLIB)
	$(CXX) $(CXXFLAGS) $< -o $@ $(LDFLAGS) -lx10rt_standalone $(X10RT_TEST_LDFLAGS)

$(STANDALONE_DYNLIB): standalone/x10rt_standalone.o $(COMMON_OBJS)
	$(CXX) $(CXXFLAGS) $(CXXFLAGS_SHARED) -o $@ $^

etc/x10rt_standalone.properties:
	@echo "CXX=$(CXX)" > $@
	@echo "CXXFLAGS=" >> $@
	@echo "LDFLAGS=$(CUDA_LDFLAGS)" >> $@
	@echo "LDLIBS=-lx10rt_standalone $(CUDA_LDLIBS)" >> $@

.PRECIOUS: etc/x10rt_standalone.properties

# vim: ts=8:sw=8:noet
