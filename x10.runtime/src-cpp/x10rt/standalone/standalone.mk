TESTS += $(patsubst test/%,test/%.standalone,$(BASE_TESTS))

LIBS += lib/libx10rt_standalone.la

PROPERTIES += etc/x10rt_standalone.properties

%.standalone: %.cc lib/libx10rt_standalone.la
	libtool --mode=link $(CXX) $(CXXFLAGS) $< -o $@ $(LDFLAGS) -lx10rt_standalone

lib/libx10rt_standalone.la: standalone/x10rt_standalone.lo $(COMMON_OBJS)
	libtool --mode=link $(CXX) -o $@ $^ -rpath ${X10_HOME}/x10.dist/lib

etc/x10rt_standalone.properties:
	@echo "CXX=$(CXX)" > $@
	@echo "CXXFLAGS=" >> $@
	@echo "LDFLAGS=$(CUDA_LDFLAGS)" >> $@
	@echo "LDLIBS=-lx10rt_standalone $(CUDA_LDLIBS)" >> $@

.PRECIOUS: etc/x10rt_standalone.properties

# vim: ts=8:sw=8:noet
