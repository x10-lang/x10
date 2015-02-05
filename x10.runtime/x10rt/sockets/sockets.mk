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

TESTS += $(patsubst test/%,test/%.sockets,$(BASE_TESTS))

LIB_FILE_SOCKETS = lib/$(LIBPREFIX)x10rt_sockets$(LIBSUFFIX)
LIBS += $(LIB_FILE_SOCKETS)

JNILIB_FILE_SOCKETS = lib/$(LIBPREFIX)x10rt_sockets.jnilib
ifndef X10_STATIC_LIB
ifeq ($(subst 64,,$(X10RT_PLATFORM)),darwin)
  LIBS += $(JNILIB_FILE_SOCKETS)
endif
endif

PROPERTIES += etc/x10rt_sockets.properties
LAUNCHER_OBJS = sockets/Launcher_Init.o sockets/DebugHelper.o sockets/Launcher.o sockets/tcp.o 

EXECUTABLES += sockets/X10Launcher

MOV_LDFLAGS_SOCKETS = $(MOV_LDFLAGS) $(SOLARIS_LDLIBS)
MOV_LDLIBS_SOCKETS = $(MOV_LDLIBS) $(SOLARIS_LDLIBS)
SO_LDFLAGS_SOCKETS = $(SO_LDFLAGS)
SO_LDLIBS_SOCKETS = $(SO_LDLIBS)
APP_LDFLAGS_SOCKETS = $(APP_LDFLAGS)
APP_LDLIBS_SOCKETS = $(APP_LDLIBS) -lx10rt_sockets

ifdef X10_STATIC_LIB
  APP_LDFLAGS_SOCKETS += $(MOV_LDFLAGS_SOCKETS)
  APP_LDLIBS_SOCKETS += $(MOV_LDLIBS_SOCKETS)
else
  SO_LDFLAGS_SOCKETS += $(MOV_LDFLAGS_SOCKETS)
  SO_LDLIBS_SOCKETS += $(MOV_LDLIBS_SOCKETS)
endif

%.sockets: %.cc $(LIB_FILE_SOCKETS)
	$(CXX) $(CXXFLAGS) $^ $(APP_LDFLAGS_SOCKETS) $(APP_LDLIBS_SOCKETS) $(X10RT_TEST_LDFLAGS) -o $@


$(LIB_FILE_SOCKETS): sockets/x10rt_sockets.o $(LAUNCHER_OBJS) $(COMMON_OBJS)
ifdef X10_STATIC_LIB
	$(AR) $(ARFLAGS) $@ $^
else
	$(LINKER_PROG) $(CXXFLAGS) $^ $(CXXFLAGS_SHARED) $(SO_LDFLAGS_SOCKETS) $(SO_LDLIBS_SOCKETS) -o $@
endif

$(JNILIB_FILE_SOCKETS): $(LIB_FILE_SOCKETS)
	$(CP) $(LIB_FILE_SOCKETS) $(JNILIB_FILE_SOCKETS)

sockets/X10Launcher: $(LAUNCHER_OBJS) sockets/main.cc
	$(CXX) $(CXXFLAGS) $(LAUNCHER_OBJS) $(LDFLAGS) $(SOLARIS_LDLIBS) sockets/main.cc -o sockets/X10Launcher

etc/x10rt_sockets.properties:
	@echo "X10LIB_PLATFORM=$(X10RT_PLATFORM)" > $@
	@echo "X10LIB_CXX=$(CXX)" >> $@
	@echo "X10LIB_CXXFLAGS=$(X10RT_PROPS_CXXFLAGS)" >> $@
	@echo "X10LIB_LDFLAGS=$(APP_LDFLAGS_SOCKETS)" >> $@
	@echo "X10LIB_LDLIBS=$(APP_LDLIBS_SOCKETS)" >> $@

.PRECIOUS: etc/x10rt_sockets.properties

# vim: ts=8:sw=8:noet
