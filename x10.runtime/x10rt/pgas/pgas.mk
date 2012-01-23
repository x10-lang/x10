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

#X10_VERSION=svn head
X10_VERSION=2.2.2
VERSION=20110513

#WPLATFORM is the string used to identify the platform in the web tarballs
PGAS_SOCKETS_TGZ = pgas-$(VERSION)-$(WPLATFORM)-sockets.tgz
PGAS_LAPI_TGZ = pgas-$(VERSION)-$(WPLATFORM)-lapi.tgz
PGAS_BGP_TGZ = pgas-$(VERSION)-$(WPLATFORM)-bgp.tgz

# defaults
PLATFORM_SUPPORTS_PGAS_SOCKETS := no
PLATFORM_SUPPORTS_PGAS_PANE := no
PLATFORM_SUPPORTS_PGAS_LAPI := no
PLATFORM_SUPPORTS_PGAS_BGP := no

# MOV: flags that are either used at app or so compile time depending on X10_STATIC_LIB
# SO: flags that are always used at so compile time
# APP: flags that are always used at app compile time

# configuration common to all platforms
MOV_LDFLAGS_PGAS_LAPI    = $(MOV_LDFLAGS)
MOV_LDLIBS_PGAS_LAPI     = $(MOV_LDLIBS)
SO_LDFLAGS_PGAS_LAPI    = $(SO_LDFLAGS)
SO_LDLIBS_PGAS_LAPI     = $(SO_LDLIBS) 
APP_LDFLAGS_PGAS_LAPI    = $(APP_LDFLAGS)
APP_LDLIBS_PGAS_LAPI     = $(APP_LDLIBS) -lx10rt_pgas_lapi
ifdef X10_STATIC_LIB
  APP_LDFLAGS_PGAS_LAPI += $(MOV_LDFLAGS_PGAS_LAPI)
  APP_LDLIBS_PGAS_LAPI += $(MOV_LDLIBS_PGAS_LAPI)
else
  SO_LDFLAGS_PGAS_LAPI += $(MOV_LDFLAGS_PGAS_LAPI)
  SO_LDLIBS_PGAS_LAPI += $(MOV_LDLIBS_PGAS_LAPI)
endif

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

MOV_LDFLAGS_PGAS_SOCKETS    = $(MOV_LDFLAGS)
MOV_LDLIBS_PGAS_SOCKETS     = $(MOV_LDLIBS) -lpthread
SO_LDFLAGS_PGAS_SOCKETS = $(SO_LDFLAGS)
SO_LDLIBS_PGAS_SOCKETS  = $(SO_LDLIBS)
APP_LDFLAGS_PGAS_SOCKETS = $(APP_LDFLAGS)
APP_LDLIBS_PGAS_SOCKETS  = $(APP_LDLIBS) -lx10rt_pgas_sockets
ifdef X10_STATIC_LIB
  APP_LDFLAGS_PGAS_SOCKETS += $(MOV_LDFLAGS_PGAS_SOCKETS)
  APP_LDLIBS_PGAS_SOCKETS += $(MOV_LDLIBS_PGAS_SOCKETS)
else
  SO_LDFLAGS_PGAS_SOCKETS += $(MOV_LDFLAGS_PGAS_SOCKETS)
  SO_LDLIBS_PGAS_SOCKETS += $(MOV_LDLIBS_PGAS_SOCKETS)
endif

MOV_LDFLAGS_PGAS_PANE    = $(MOV_LDFLAGS)
MOV_LDLIBS_PGAS_PANE     = $(MOV_LDLIBS)
SO_LDFLAGS_PGAS_PANE    = $(SO_LDFLAGS)
SO_LDLIBS_PGAS_PANE     = $(SO_LDLIBS) 
APP_LDFLAGS_PGAS_PANE    = $(APP_LDFLAGS) -btextpsize:64K -bdatapsize:64K -bstackpsize:64K
APP_LDLIBS_PGAS_PANE     = $(APP_LDLIBS) -lx10rt_pgas_pane
ifdef X10_STATIC_LIB
  APP_LDFLAGS_PGAS_PANE += $(MOV_LDFLAGS_PGAS_PANE)
  APP_LDLIBS_PGAS_PANE += $(MOV_LDLIBS_PGAS_PANE)
else
  SO_LDFLAGS_PGAS_PANE += $(MOV_LDFLAGS_PGAS_PANE)
  SO_LDLIBS_PGAS_PANE += $(MOV_LDLIBS_PGAS_PANE)
endif


#configuration specific to each platform
ifeq ($(X10RT_PLATFORM), bgp)
  WPLATFORM      := bgp_g++4
  PLATFORM_SUPPORTS_PGAS_BGP        := yes
  MOV_LDFLAGS_PGAS_BGP    += -L/bgsys/drivers/ppcfloor/comm/lib -L/bgsys/drivers/ppcfloor/runtime/SPI
  MOV_LDLIBS_PGAS_BGP     += -ldcmf.cnk -ldcmfcoll.cnk -lSPI.cna -lpthread -lrt -lm
endif
ifeq ($(X10RT_PLATFORM), aix_xlc)
  WPLATFORM      := aix_xlc
  PLATFORM_SUPPORTS_PGAS_LAPI       := yes
  PLATFORM_SUPPORTS_PGAS_PANE       := yes
  # PLATFORM_SUPPORTS_PGAS_SOCKETS    := yes
  APP_LDFLAGS_PGAS_LAPI   += -Wl,-binitfini:poe_remote_main  -L/usr/lpp/ppe.poe/lib
  APP_LDFLAGS_PGAS_PANE   += -Wl,-binitfini:poe_remote_main -L/usr/lpp/ppe.poe/lib
  APP_LDLIBS_PGAS_LAPI    += -lmpi_r -lvtd_r
  APP_LDLIBS_PGAS_PANE    += -lmpi_r -lvtd_r
  MOV_LDFLAGS_PGAS_LAPI   += -L/usr/lpp/ppe.poe/lib
  MOV_LDFLAGS_PGAS_PANE   += -L/usr/lpp/ppe.poe/lib
  MOV_LDLIBS_PGAS_LAPI    += -llapi_r -lpthread -lm
  MOV_LDLIBS_PGAS_PANE    += -llapi_r -lpthread -lm
endif
ifeq ($(X10RT_PLATFORM), aix_gcc)
  WPLATFORM      := aix_g++4
  PLATFORM_SUPPORTS_PGAS_LAPI       := yes
  PLATFORM_SUPPORTS_PGAS_PANE       := yes
  #PLATFORM_SUPPORTS_PGAS_SOCKETS    := yes
  APP_LDFLAGS_PGAS_LAPI   += -Wl,-binitfini:poe_remote_main  -L/usr/lpp/ppe.poe/lib
  APP_LDFLAGS_PGAS_PANE   += -Wl,-binitfini:poe_remote_main -L/usr/lpp/ppe.poe/lib
  APP_LDLIBS_PGAS_LAPI    += -lmpi_r -lvtd_r
  APP_LDLIBS_PGAS_PANE    += -lmpi_r -lvtd_r
  MOV_LDFLAGS_PGAS_LAPI   += -L/usr/lpp/ppe.poe/lib
  MOV_LDFLAGS_PGAS_PANE   += -L/usr/lpp/ppe.poe/lib
  MOV_LDLIBS_PGAS_LAPI    += -llapi_r -lpthread -lm
  MOV_LDLIBS_PGAS_PANE    += -llapi_r -lpthread -lm
endif
ifeq ($(X10RT_PLATFORM), linux_ppc_64_gcc)
  WPLATFORM      := linux_ppc_64_g++4
  PLATFORM_SUPPORTS_PGAS_LAPI       := yes
  MOV_LDFLAGS_PGAS_LAPI   += -L/opt/ibmhpc/ppe.poe/lib 
  MOV_LDLIBS_PGAS_LAPI    += -lpoe -lmpi_ibm -llapi
  PLATFORM_SUPPORTS_PGAS_SOCKETS    := yes
endif
ifeq ($(X10RT_PLATFORM), linux_ppc_64_xlc)
  WPLATFORM      := linux_ppc_64_xlc
  PLATFORM_SUPPORTS_PGAS_LAPI       := yes
  MOV_LDFLAGS_PGAS_LAPI    += -L/opt/ibmhpc/ppe.poe/lib 
  MOV_LDLIBS_PGAS_LAPI     += -lpoe -lmpi_ibm -llapi
  PLATFORM_SUPPORTS_PGAS_SOCKETS    := yes
endif
ifeq ($(X10RT_PLATFORM), linux_x86_64)
  WPLATFORM      := linux_x86_64_g++4
  PLATFORM_SUPPORTS_PGAS_LAPI       := yes
  MOV_LDFLAGS_PGAS_LAPI   += -L/opt/ibmhpc/ppe.poe/lib 
  MOV_LDLIBS_PGAS_LAPI    += -lpoe -lmpi_ibm -llapi
  PLATFORM_SUPPORTS_PGAS_SOCKETS    := yes
endif
ifeq ($(X10RT_PLATFORM), linux_x86_32)
  WPLATFORM      := linux_x86_g++4
# TODO: re-enable when we build the 32 bit lapi version of pgas and post it.
#  PLATFORM_SUPPORTS_PGAS_LAPI       := yes
  MOV_LDFLAGS_PGAS_LAPI   += -L/opt/ibmhpc/ppe.poe/lib 
  MOV_LDLIBS_PGAS_LAPI    += -lpoe -lmpi_ibm -llapi
  PLATFORM_SUPPORTS_PGAS_SOCKETS    := yes
endif
ifeq ($(X10RT_PLATFORM), cygwin)
  WPLATFORM      := cygwin_x86_g++4
  PLATFORM_SUPPORTS_PGAS_SOCKETS    := yes
endif
ifeq ($(X10RT_PLATFORM), darwin)
  WPLATFORM      := macos_x86_g++4
  PLATFORM_SUPPORTS_PGAS_SOCKETS    := yes
endif
ifeq ($(X10RT_PLATFORM), darwin64)
  WPLATFORM      := macos_x86_g++4
  PLATFORM_SUPPORTS_PGAS_SOCKETS    := yes
endif
ifeq ($(X10RT_PLATFORM), sunos)
  WPLATFORM      := sunos_sparc_g++4
  PLATFORM_SUPPORTS_PGAS_SOCKETS    := yes
  MOV_LDLIBS_PGAS_SOCKETS += -lresolv -lnsl -lsocket -lrt
endif

#disable building all PGAS transports except PGAS_BGP
PLATFORM_SUPPORTS_PGAS_SOCKETS := no
PLATFORM_SUPPORTS_PGAS_PANE := no
PLATFORM_SUPPORTS_PGAS_LAPI := no

#{{{ Deciding what underlying PGAS builds are available
ifdef CUSTOM_PGAS
include/pgasrt.h: $(CUSTOM_PGAS)/include/pgasrt.h
	$(CP) $(CUSTOM_PGAS)/include/*.h include

  ifeq ($(shell test -r $(CUSTOM_PGAS)/lib/libxlpgas_pane.a && printf hi),hi)
    XLPGAS_PANE_EXISTS := yes
  else
    XLPGAS_PANE_EXISTS := no
  endif
  ifeq ($(shell test -r $(CUSTOM_PGAS)/lib/libxlpgas_lapi.a && printf hi),hi)
    XLPGAS_LAPI_EXISTS := yes
  else
    XLPGAS_LAPI_EXISTS := no
  endif
  ifeq ($(shell test -r $(CUSTOM_PGAS)/lib/libxlpgas_sockets.a && printf hi),hi)
    XLPGAS_SOCKETS_EXISTS := yes
  else
    XLPGAS_SOCKETS_EXISTS := no
  endif
  ifeq ($(shell test -r $(CUSTOM_PGAS)/lib/libxlpgas_bgp.a && printf hi),hi)
    XLPGAS_BGP_EXISTS := yes
  else
    XLPGAS_BGP_EXISTS := no
  endif
else
  # if the platform supports it, it can be found in the website tarball for that platform
  XLPGAS_PANE_EXISTS := no
  XLPGAS_LAPI_EXISTS := $(PLATFORM_SUPPORTS_PGAS_LAPI)
  XLPGAS_SOCKETS_EXISTS := $(PLATFORM_SUPPORTS_PGAS_SOCKETS)
  XLPGAS_BGP_EXISTS := $(PLATFORM_SUPPORTS_PGAS_BGP)
endif
#}}}

LIB_FILE_PGAS_SOCKETS = lib/$(LIBPREFIX)x10rt_pgas_sockets$(LIBSUFFIX)
LIB_FILE_PGAS_PANE = lib/$(LIBPREFIX)x10rt_pgas_pane$(LIBSUFFIX)
LIB_FILE_PGAS_LAPI = lib/$(LIBPREFIX)x10rt_pgas_lapi$(LIBSUFFIX)
#LIB_FILE_PGAS_BGP = lib/$(LIBPREFIX)x10rt_pgas_bgp$(LIBSUFFIX) # dynamic linking not supported on BGP yet


#{{{ Sockets
ifeq ($(PLATFORM_SUPPORTS_PGAS_SOCKETS), yes)

TESTS += $(patsubst test/%,test/%.pgas_sockets,$(BASE_TESTS))
LIBS += $(LIB_FILE_PGAS_SOCKETS)
PROPERTIES += etc/x10rt_pgas_sockets.properties
PGAS_EXECUTABLES = bin/launcher bin/manager bin/daemon
EXECUTABLES += $(PGAS_EXECUTABLES)

%.pgas_sockets: %.cc $(LIB_FILE_PGAS_SOCKETS)
	$(CXX) $(CXXFLAGS) $< -o $@ $(APP_LDFLAGS_PGAS_SOCKETS) $(APP_LDLIBS_PGAS_SOCKETS) $(X10RT_TEST_LDFLAGS)

ifdef CUSTOM_PGAS
lib/libxlpgas_sockets.a: $(COMMON_OBJS) $(CUSTOM_PGAS)/lib/libxlpgas_sockets.a include/pgasrt.h
	$(CP) $(CUSTOM_PGAS)/lib/libxlpgas_sockets.a lib/libxlpgas_sockets.a

$(PGAS_EXECUTABLES): $(PGAS_EXECUTABLES:%=$(CUSTOM_PGAS)/%)
	-$(CP) $^ bin/
else
$(PGAS_SOCKETS_TGZ).phony:
	-$(WGET) -q -N  "http://dist.codehaus.org/x10/binaryReleases/$(X10_VERSION)/$(PGAS_SOCKETS_TGZ)"

$(PGAS_SOCKETS_TGZ): $(PGAS_SOCKETS_TGZ).phony

lib/libxlpgas_sockets.a: $(COMMON_OBJS) $(PGAS_SOCKETS_TGZ)
	$(GZIP) -cd $(PGAS_SOCKETS_TGZ) | $(TAR) -xf -
endif

ifdef X10_STATIC_LIB
# On the Mac, AR=libtool, and the target library is overwritten, so the initial $(CP) is harmless.
# However, we do need to link in the original archive.
ifeq ($(subst 64,,$(X10RT_PLATFORM)),darwin)
DARWIN_EXTRA_LIB:=lib/libxlpgas_sockets.a
endif
$(LIB_FILE_PGAS_SOCKETS): $(COMMON_OBJS) lib/libxlpgas_sockets.a
	$(CP) lib/libxlpgas_sockets.a $@
	$(AR) $(ARFLAGS) $@ $(DARWIN_EXTRA_LIB) $(COMMON_OBJS)
else
$(LIB_FILE_PGAS_SOCKETS): $(COMMON_OBJS) lib/libxlpgas_sockets.a
	$(LINKER_PROG) $(CXXFLAGS) $(CXXFLAGS_SHARED) $(SO_LDFLAGS_PGAS_SOCKETS) $(SO_LDLIBS_PGAS_SOCKETS) -o $@ $^
endif


etc/x10rt_pgas_sockets.properties:
	@echo "X10LIB_PLATFORM=$(X10RT_PLATFORM)" > $@
	@echo "X10LIB_CXX=$(CXX)" >> $@
	@echo "X10LIB_LDFLAGS=$(APP_LDFLAGS_PGAS_SOCKETS)" >> $@
	@echo "X10LIB_LDLIBS=$(APP_LDLIBS_PGAS_SOCKETS)" >> $@

.PRECIOUS: etc/x10rt_pgas_sockets.properties
.PHONY: $(PGAS_SOCKETS_TGZ).phony
TGZ += $(PGAS_SOCKETS_TGZ).phony

endif
#}}}


# {{{ PANE
ifeq ($(PLATFORM_SUPPORTS_PGAS_PANE),yes)
ifeq ($(XLPGAS_PANE_EXISTS),yes)
TESTS += $(patsubst test/%,test/%.pgas_pane,$(BASE_TESTS))

LIBS += $(LIB_FILE_PGAS_PANE)
PROPERTIES += etc/x10rt_pgas_pane.properties

%.pgas_pane: %.cc $(LIB_FILE_PGAS_PANE)
	$(CXX) $(CXXFLAGS) $< -o $@ -DX10RT_PANE_HACK $(APP_LDFLAGS_PGAS_PANE) $(APP_LDLIBS_PGAS_PANE) $(X10RT_TEST_LDFLAGS)

ifdef CUSTOM_PGAS
lib/libxlpgas_pane.a: $(COMMON_OBJS) $(CUSTOM_PGAS)/lib/libxlpgas_pane.a include/pgasrt.h
	$(CP) $(CUSTOM_PGAS)/lib/libxlpgas_pane.a lib/libxlpgas_pane.a
else
HACK=$(shell echo "Your platform has no prebuilt PGAS available.  You must export CUSTOM_PGAS=pgas2/common/work">2)
endif

ifdef X10_STATIC_LIB
$(LIB_FILE_PGAS_PANE): $(COMMON_OBJS) lib/libxlpgas_pane.a
	$(CP) lib/libxlpgas_pane.a $@
	$(AR) $(ARFLAGS) $@ $(COMMON_OBJS)
else
$(LIB_FILE_PGAS_PANE): $(COMMON_OBJS) lib/libxlpgas_pane.a
	$(LINKER_PROG) $(CXXFLAGS) $(CXXFLAGS_SHARED) $(SO_LDFLAGS_PGAS_PANE) $(SO_LDLIBS_PGAS_PANE) -o $@ $(COMMON_OBJS) -Wl,-bexpfull lib/libxlpgas_pane.a 
endif

etc/x10rt_pgas_pane.properties:
	@echo "X10LIB_PLATFORM=$(X10RT_PLATFORM)" > $@
	@echo "X10LIB_CXX=$(CXX)" >> $@
	@echo "X10LIB_LDFLAGS=$(APP_LDFLAGS_PGAS_PANE)" >> $@
	@echo "X10LIB_LDLIBS=$(APP_LDLIBS_PGAS_PANE)" >> $@

.PRECIOUS: etc/x10rt_pgas_pane.properties
.PHONY: $(PGAS_PANE_TGZ).phony
TGZ += $(PGAS_PANE_TGZ).phony

endif #XLPGAS_PANE_EXISTS
endif #PLATFORM_SUPPORTS_PGAS_PANE
#}}}


# {{{ LAPI
ifeq ($(PLATFORM_SUPPORTS_PGAS_LAPI),yes)
ifeq ($(XLPGAS_LAPI_EXISTS),yes)
#If there is no poe then do not add any pgas_lapi targets. 
#Assume that if poe is installed then `which poe` will print its full path to
#stdout.  Since we don't know what the full path is, we can't run it because it
#will fail, and we can't trust the error messages or exit code of `which`, we
#instead test if the path is an executable file.
ifeq ($(shell test -x "`which poe 2>/dev/null`" && printf hi),hi)
TESTS += $(patsubst test/%,test/%.pgas_lapi,$(BASE_TESTS))
LIBS += $(LIB_FILE_PGAS_LAPI)
PROPERTIES += etc/x10rt_pgas_lapi.properties
else
HACK=$(shell echo "Your platform supports LAPI but we could not find the poe executable so not building LAPI tests">2)
endif

%.pgas_lapi: %.cc $(LIB_FILE_PGAS_LAPI)
	$(CXX) $(CXXFLAGS) $< -o $@ $(APP_LDFLAGS_PGAS_LAPI) $(APP_LDLIBS_PGAS_LAPI) $(X10RT_TEST_LDFLAGS)

ifdef CUSTOM_PGAS
lib/libxlpgas_lapi.a: $(COMMON_OBJS) $(CUSTOM_PGAS)/lib/libxlpgas_lapi.a include/pgasrt.h
	$(CP) $(CUSTOM_PGAS)/lib/libxlpgas_lapi.a lib/libxlpgas_lapi.a
else
$(PGAS_LAPI_TGZ).phony:
	-$(WGET) -q -N  "http://dist.codehaus.org/x10/binaryReleases/$(X10_VERSION)/$(PGAS_LAPI_TGZ)"

$(PGAS_LAPI_TGZ): $(PGAS_LAPI_TGZ).phony

lib/libxlpgas_lapi.a: $(COMMON_OBJS) $(PGAS_LAPI_TGZ)
	$(GZIP) -cd $(PGAS_LAPI_TGZ) | $(TAR) -xf -
endif

ifdef X10_STATIC_LIB
$(LIB_FILE_PGAS_LAPI): $(COMMON_OBJS) lib/libxlpgas_lapi.a
	$(CP) lib/libxlpgas_lapi.a $@
	$(AR) $(ARFLAGS) $@ $(COMMON_OBJS)
else
$(LIB_FILE_PGAS_LAPI): $(COMMON_OBJS) lib/libxlpgas_lapi.a
	$(LINKER_PROG) $(CXXFLAGS) $(CXXFLAGS_SHARED) $(SO_LDFLAGS_PGAS_LAPI) $(SO_LDLIBS_PGAS_LAPI) -o $@ $^
endif

etc/x10rt_pgas_lapi.properties:
	@echo "X10LIB_PLATFORM=$(X10RT_PLATFORM)" > $@
	@echo "X10LIB_CXX=$(CXX)" >> $@
	@echo "X10LIB_LDFLAGS=$(APP_LDFLAGS_PGAS_LAPI)" >> $@
	@echo "X10LIB_LDLIBS=$(APP_LDLIBS_PGAS_LAPI)" >> $@

.PRECIOUS: etc/x10rt_pgas_lapi.properties
.PHONY: $(PGAS_LAPI_TGZ).phony
TGZ += $(PGAS_LAPI_TGZ).phony

endif #XLPGAS_LAPI_EXISTS
endif #PLATFORM_SUPPORTS_PGAS_LAPI
#}}}


# {{{ BGP
ifeq ($(PLATFORM_SUPPORTS_PGAS_BGP),yes)

TESTS += $(patsubst test/%,test/%.pgas_bgp,$(BASE_TESTS))
LIBS += lib/libx10rt_pgas_bgp.a
PROPERTIES += etc/x10rt_pgas_bgp.properties

%.pgas_bgp: %.cc lib/libx10rt_pgas_bgp.a
	$(CXX) $(CXXFLAGS) $< -o $@ $(APP_LDFLAGS_PGAS_BGP) $(APP_LDLIBS_PGAS_BGP) $(X10RT_TEST_LDFLAGS)

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
	@echo pgas.mk PLATFORM_SUPPORTS_PGAS_LAPI = $(PLATFORM_SUPPORTS_PGAS_LAPI)
	@echo pgas.mk PLATFORM_SUPPORTS_PGAS_SOCKETS = $(PLATFORM_SUPPORTS_PGAS_SOCKETS)
	@echo pgas.mk PLATFORM_SUPPORTS_PGAS_BGP = $(PLATFORM_SUPPORTS_PGAS_BGP)
	@echo pgas.mk CUSTOM_PGAS = $(CUSTOM_PGAS)
	@echo pgas.mk XLPGAS_LAPI_EXISTS = $(XLPGAS_LAPI_EXISTS)
	@echo pgas.mk XLPGAS_SOCKETS_EXISTS = $(XLPGAS_SOCKETS_EXISTS)
	@echo pgas.mk XLPGAS_BGP_EXISTS = $(XLPGAS_BGP_EXISTS)
	@echo pgas.mk LIBS = $(LIBS)
	@echo pgas.mk PROPERTIES = $(PROPERTIES)
	@echo pgas.mk TESTS = $(TESTS)

# vim: ts=8:sw=8:noet
