TESTS += $(patsubst test/%,test/%.standalone,$(BASE_TESTS))

LIBS += lib/libx10rt_standalone.a

PROPERTIES += etc/x10rt_standalone.properties

%.standalone: %.cc lib/libx10rt_standalone.a
	${CXX} ${CXXFLAGS} $< -o $@ ${LDFLAGS} -lx10rt_standalone

lib/libx10rt_standalone.a: standalone/x10rt_standalone.o
	${AR} $(ARFLAGS) $@ $^

etc/x10rt_standalone.properties:
	echo "CXX=$(CXX)" > $@
	echo "CXXFLAGS=" >> $@
	echo "LDFLAGS=" >> $@
	echo "LDLIBS=-lx10rt_standalone" >> $@

.PRECIOUS: etc/x10rt_standalone.properties

# vim: ts=8:sw=8:noet
