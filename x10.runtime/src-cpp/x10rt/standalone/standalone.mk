TESTS += $(patsubst test/%,test/%.standalone,$(BASE_TESTS))

LIBS += lib/libx10rt_standalone.a

PROPERTIES += etc/x10rt_standalone.properties

%.standalone: %.cc lib/libx10rt_standalone.a
	$(CXX) $(CXXFLAGS) $< -o $@ $(LDFLAGS) -lx10rt_standalone

lib/libx10rt_standalone.a: standalone/x10rt_standalone.o $(COMMON_OBJS)
	$(AR) $(ARFLAGS) $@ $^

etc/x10rt_standalone.properties:
	echo "CXX=$(CXX)" > $@
	echo "CXXFLAGS=" >> $@
	echo "LDFLAGS=$(CUDA_LDFLAGS)" >> $@
	echo "LDLIBS=-lx10rt_standalone $(CUDA_LDLIBS)" >> $@

.PRECIOUS: etc/x10rt_standalone.properties

# vim: ts=8:sw=8:noet
