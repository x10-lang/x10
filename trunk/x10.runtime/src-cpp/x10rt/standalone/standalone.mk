TESTS += $(patsubst test/%,test/%.standalone,$(BASE_TESTS))

%.standalone: %.cc lib/libx10rt_standalone.a
	${CXX} ${CXXFLAGS} $< -o $@ ${LDFLAGS} -lx10rt_standalone

lib/libx10rt_standalone.a: standalone/x10rt_standalone.o
	${AR} rcu $@ $^

# vim: ts=8:sw=8:noet
