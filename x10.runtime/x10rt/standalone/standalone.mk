#
#  This file is part of the X10 project (http://x10-lang.org).
#
#  This file is licensed to You under the Eclipse Public License (EPL);
#  You may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#      http://www.opensource.org/licenses/eclipse-1.0.php
#
#  (C) Copyright IBM Corporation 2006-2014.
#

TESTS += $(patsubst test/%,test/%.standalone,$(BASE_TESTS))

LIB_FILE_STANDALONE = lib/$(LIBPREFIX)x10rt_standalone$(LIBSUFFIX)
LIBS += $(LIB_FILE_STANDALONE)

PROPERTIES += etc/x10rt_standalone.properties

MOV_LDFLAGS_STANDALONE = $(MOV_LDFLAGS)
MOV_LDLIBS_STANDALONE = $(MOV_LDLIBS)
SO_LDFLAGS_STANDALONE = $(SO_LDFLAGS)
SO_LDLIBS_STANDALONE = $(SO_LDLIBS)
APP_LDFLAGS_STANDALONE = $(APP_LDFLAGS)
APP_LDLIBS_STANDALONE = $(APP_LDLIBS) -lx10rt_standalone

ifdef X10_STATIC_LIB
  APP_LDFLAGS_STANDALONE += $(MOV_LDFLAGS_STANDALONE)
  APP_LDLIBS_STANDALONE += $(MOV_LDLIBS_STANDALONE)
else
  SO_LDFLAGS_STANDALONE += $(MOV_LDFLAGS_STANDALONE)
  SO_LDLIBS_STANDALONE += $(MOV_LDLIBS_STANDALONE)
endif

%.standalone: %.cc $(LIB_FILE_STANDALONE)
	$(CXX) $(CXXFLAGS) $< $(APP_LDFLAGS_STANDALONE) $(APP_LDLIBS_STANDALONE) $(X10RT_TEST_LDFLAGS) -o $@

$(LIB_FILE_STANDALONE): standalone/x10rt_standalone.o $(COMMON_OBJS)
ifdef X10_STATIC_LIB
	$(AR) $(ARFLAGS) $@ $^
else
	$(LINKER_PROG) $(CXXFLAGS) $^ $(CXXFLAGS_SHARED) $(SO_LDFLAGS_STANDALONE) $(SO_LDLIBS_STANDALONE) -o $@
endif

etc/x10rt_standalone.properties:
	@echo "X10LIB_PLATFORM=$(X10RT_PLATFORM)" > $@
	@echo "X10LIB_CXX=$(CXX)" >> $@
	@echo "X10LIB_CXXFLAGS=$(X10RT_PROPS_CXXFLAGS)" >> $@
	@echo "X10LIB_LDFLAGS=$(APP_LDFLAGS_STANDALONE)" >> $@
	@echo "X10LIB_LDLIBS=$(APP_LDLIBS_STANDALONE)" >> $@

.PRECIOUS: etc/x10rt_standalone.properties

# vim: ts=8:sw=8:noet
