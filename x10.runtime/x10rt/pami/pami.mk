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

TESTS += $(patsubst test/%,test/%.pami,$(BASE_TESTS))

PAMI_DYNLIB = lib/$(LIBPREFIX)x10rt_pami$(LIBSUFFIX)
LIBS += $(PAMI_DYNLIB)

PROPERTIES += etc/x10rt_pami.properties

%.pami: %.cc $(PAMI_DYNLIB)
	$(CXX) $(CXXFLAGS) $< -o $@ $(LDFLAGS) -lx10rt_pami $(X10RT_TEST_LDFLAGS)

pami/x10rt_pami.o: pami/x10rt_pami.cc
	$(CXX) $(CXXFLAGS) $(CXXFLAGS_SHARED) -c $< -o $@

ifdef X10_STATIC_LIB
$(PAMI_DYNLIB): pami/x10rt_pami.o $(COMMON_OBJS)
	$(AR) $(ARFLAGS) $@ $^
else
$(PAMI_DYNLIB): pami/x10rt_pami.o $(COMMON_OBJS)
	$(CXX) $(CXXFLAGS) $(CXXFLAGS_SHARED) $(LDFLAGS_SHARED) -o $@ $^
endif

etc/x10rt_pami.properties:
	@echo "CXX=$(CXX)" > $@
	@echo "CXXFLAGS=" >> $@
	@echo "LDFLAGS=$(CUDA_LDFLAGS)" >> $@
	@echo "LDLIBS=-lx10rt_pami $(CUDA_LDLIBS)" >> $@

.PRECIOUS: etc/x10rt_pami.properties

# vim: ts=8:sw=8:noet
