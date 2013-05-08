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

X10_VERSION=svn head
#X10_VERSION=2.3.1
VERSION=20130508

#WPLATFORM is the string used to identify the platform in the web tarballs
PGAS_BGP_TGZ = pgas-$(VERSION)-$(WPLATFORM)-bgp.tgz

# defaults
PLATFORM_SUPPORTS_PGAS_BGP := no

# MOV: flags that are either used at app or so compile time depending on X10_STATIC_LIB
# SO: flags that are always used at so compile time
# APP: flags that are always used at app compile time

# configuration common to all platforms

MOV_LDFLAGS_PGAS_BGP    = $(MOV_LDFLAGS)
MOV_LDLIBS_PGAS_BGP     = $(MOV_LDLIBS)
SO_LDFLAGS_PGAS_BGP     = $(SO_LDFLAGS)
SO_LDLIBS_PGAS_BGP      = $(SO_LDLIBS) 
APP_LDFLAGS_PGAS_BGP     = $(APP_LDFLAGS)
APP_LDLIBS_PGAS_BGP      = $(APP_LDLIBS) -lx10rt_pgas_bgp
ifdef X10_STATIC_LIB
  APP_LDFLAGS_PGAS_BGP += $(MOV_LDFLAGS_PGAS_BGP)
  APP_LDLIBS_PGAS_BGP += $(MOV_LDLIBS_PGAS_BGP)
else
  SO_LDFLAGS_PGAS_BGP += $(MOV_LDFLAGS_PGAS_BGP)
  SO_LDLIBS_PGAS_BGP += $(MOV_LDLIBS_PGAS_BGP)
endif

#configuration specific to each platform
ifeq ($(X10RT_PLATFORM), bgp)
  WPLATFORM      := bgp_g++4
  PLATFORM_SUPPORTS_PGAS_BGP        := yes
  MOV_LDFLAGS_PGAS_BGP    += -L/bgsys/drivers/ppcfloor/comm/lib -L/bgsys/drivers/ppcfloor/runtime/SPI
  MOV_LDLIBS_PGAS_BGP     += -ldcmf.cnk -ldcmfcoll.cnk -lSPI.cna -lpthread -lrt -lm
endif

#{{{ Deciding what underlying PGAS builds are available
ifdef CUSTOM_PGAS
include/pgasrt.h: $(CUSTOM_PGAS)/include/pgasrt.h
	$(CP) $(CUSTOM_PGAS)/include/*.h include

  ifeq ($(shell test -r $(CUSTOM_PGAS)/lib/libxlpgas_bgp.a && printf hi),hi)
    XLPGAS_BGP_EXISTS := yes
  else
    XLPGAS_BGP_EXISTS := no
  endif
else
  # if the platform supports it, it can be found in the website tarball for that platform
  XLPGAS_BGP_EXISTS := $(PLATFORM_SUPPORTS_PGAS_BGP)
endif
#}}}

#LIB_FILE_PGAS_BGP = lib/$(LIBPREFIX)x10rt_pgas_bgp$(LIBSUFFIX) # dynamic linking not supported on BGP yet

# {{{ BGP
ifeq ($(PLATFORM_SUPPORTS_PGAS_BGP),yes)

TESTS += $(patsubst test/%,test/%.pgas_bgp,$(BASE_TESTS))
LIBS += lib/libx10rt_pgas_bgp.a
PROPERTIES += etc/x10rt_pgas_bgp.properties

%.pgas_bgp: %.cc lib/libx10rt_pgas_bgp.a
	$(CXX) $(CXXFLAGS) $< $(APP_LDFLAGS_PGAS_BGP) $(APP_LDLIBS_PGAS_BGP) $(X10RT_TEST_LDFLAGS) -o $@

ifdef CUSTOM_PGAS
lib/libxlpgas_bgp.a: $(COMMON_OBJS) $(CUSTOM_PGAS)/lib/libxlpgas_bgp.a include/pgasrt.h
	$(CP) $(CUSTOM_PGAS)/lib/libxlpgas_bgp.a lib/libxlpgas_bgp.a
else
$(PGAS_BGP_TGZ).phony:
	-$(WGET) -q -N  "http://dist.codehaus.org/x10/binaryReleases/$(X10_VERSION)/$(PGAS_BGP_TGZ)"

$(PGAS_BGP_TGZ): $(PGAS_BGP_TGZ).phony

lib/libxlpgas_bgp.a: $(COMMON_OBJS) $(PGAS_BGP_TGZ)
	$(GZIP) -cd $(PGAS_BGP_TGZ) | $(TAR) -xf -
endif

lib/libx10rt_pgas_bgp.a: $(COMMON_OBJS) lib/libxlpgas_bgp.a
	$(CP) lib/libxlpgas_bgp.a lib/libx10rt_pgas_bgp.a
	$(AR) $(ARFLAGS) $@ $(COMMON_OBJS)

etc/x10rt_pgas_bgp.properties:
	@echo "X10LIB_PLATFORM=$(X10RT_PLATFORM)" > $@
	@echo "X10LIB_CXX=$(CXX)" >> $@
	@echo "X10LIB_CXXFLAGS=$(X10RT_PROPS_CXXFLAGS)" >> $@
	@echo "X10LIB_LDFLAGS=$(APP_LDFLAGS_PGAS_BGP)" >> $@
	@echo "X10LIB_LDLIBS=$(APP_LDLIBS_PGAS_BGP)" >> $@

.PRECIOUS: etc/x10rt_pgas_bgp.properties
.PHONY: $(PGAS_BGP_TGZ).phony
TGZ += $(PGAS_BGP_TGZ).phony

endif
#}}}


debug::
	@echo pgas.mk X10RT_PLATFORM = $(X10RT_PLATFORM)
	@echo pgas.mk ENABLE_X10RT_CUDA = $(ENABLE_X10RT_CUDA)
	@echo pgas.mk DISABLE_X10RT_CUDA = $(DISABLE_X10RT_CUDA)
	@echo pgas.mk ENABLE_X10RT_MPI = $(ENABLE_X10RT_MPI)
	@echo pgas.mk DISABLE_X10RT_MPI = $(DISABLE_X10RT_MPI)
	@echo pgas.mk ENABLE_X10RT_PGAS = $(ENABLE_X10RT_PGAS)
	@echo pgas.mk DISABLE_X10RT_PGAS = $(DISABLE_X10RT_PGAS)
	@echo pgas.mk PLATFORM_SUPPORTS_PGAS_BGP = $(PLATFORM_SUPPORTS_PGAS_BGP)
	@echo pgas.mk CUSTOM_PGAS = $(CUSTOM_PGAS)
	@echo pgas.mk XLPGAS_BGP_EXISTS = $(XLPGAS_BGP_EXISTS)
	@echo pgas.mk LIBS = $(LIBS)
	@echo pgas.mk PROPERTIES = $(PROPERTIES)
	@echo pgas.mk TESTS = $(TESTS)

# vim: ts=8:sw=8:noet
