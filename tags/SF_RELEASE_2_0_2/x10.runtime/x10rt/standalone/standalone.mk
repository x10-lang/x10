#
#  This file is part of the X10 project (http://x10-lang.org).
#
#  This file is licensed to You under the Eclipse Public License (EPL);
#  You may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#      http://www.opensource.org/licenses/eclipse-1.0.php
#
#  (C) Copyright IBM Corporation 2006-2010.
#

TESTS += $(patsubst test/%,test/%.standalone,$(BASE_TESTS))

STANDALONE_DYNLIB = lib/$(LIBPREFIX)x10rt_standalone$(LIBSUFFIX)
LIBS += $(STANDALONE_DYNLIB)

PROPERTIES += etc/x10rt_standalone.properties

%.standalone: %.cc $(STANDALONE_DYNLIB)
	$(CXX) $(CXXFLAGS) $< -o $@ $(LDFLAGS) -lx10rt_standalone $(X10RT_TEST_LDFLAGS)

ifdef X10_STATIC_LIB
$(STANDALONE_DYNLIB): standalone/x10rt_standalone.o $(COMMON_OBJS)
	$(AR) $(ARFLAGS) $@ $^
else
$(STANDALONE_DYNLIB): standalone/x10rt_standalone.o $(COMMON_OBJS)
	$(CXX) $(CXXFLAGS) $(CXXFLAGS_SHARED) -o $@ $^
endif

etc/x10rt_standalone.properties:
	@echo "CXX=$(CXX)" > $@
	@echo "CXXFLAGS=" >> $@
	@echo "LDFLAGS=$(CUDA_LDFLAGS)" >> $@
	@echo "LDLIBS=-lx10rt_standalone $(CUDA_LDLIBS)" >> $@

.PRECIOUS: etc/x10rt_standalone.properties

# vim: ts=8:sw=8:noet
