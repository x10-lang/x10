VERSION=20100113
SOCKETS_TGZ = pgas-$(VERSION)-$(WPLATFORM)-sockets.tgz
LAPI_TGZ = pgas-$(VERSION)-$(WPLATFORM)-lapi.tgz
BGP_TGZ = pgas-$(VERSION)-$(WPLATFORM)-bgp.tgz

# defaults
PLATFORM_SUPPORTS_SOCKETS := no
PLATFORM_SUPPORTS_LAPI := no
PLATFORM_SUPPORTS_BGP := no

LAPI_LDFLAGS    = $(CUDA_LDFLAGS)
BGP_LDFLAGS     = $(CUDA_LDFLAGS)
SOCKETS_LDFLAGS = $(CUDA_LDFLAGS)

LAPI_LDLIBS     = -lx10rt_pgas_lapi $(CUDA_LDLIBS)
BGP_LDLIBS      = -lx10rt_pgas_bgp $(CUDA_LDLIBS)
SOCKETS_LDLIBS  = -lx10rt_pgas_sockets -lpthread $(CUDA_LDLIBS)

ifeq ($(X10RT_PLATFORM), bgp)
  WPLATFORM      := bgp_g++4
  PLATFORM_SUPPORTS_BGP        := yes
  BGP_LDFLAGS    += -L/bgsys/drivers/ppcfloor/comm/lib -L/bgsys/drivers/ppcfloor/runtime/SPI
  BGP_LDLIBS     += -ldcmf.cnk -ldcmfcoll.cnk -lSPI.cna -lpthread -lrt -lm
endif
ifeq ($(X10RT_PLATFORM), aix_xlc)
  WPLATFORM      := aix_xlc
  PLATFORM_SUPPORTS_LAPI       := yes
  #PLATFORM_SUPPORTS_SOCKETS    := yes
endif
ifeq ($(X10RT_PLATFORM), aix_gcc)
  WPLATFORM      := aix_g++4
  PLATFORM_SUPPORTS_LAPI       := yes
  LAPI_LDFLAGS   += -Wl,-binitfini:poe_remote_main -L/usr/lpp/ppe.poe/lib
  LAPI_LDLIBS    += -lmpi_r -lvtd_r -llapi_r -lpthread -lm
  #PLATFORM_SUPPORTS_SOCKETS    := yes
endif
ifeq ($(X10RT_PLATFORM), linux_ppc_64)
  WPLATFORM      := linux_ppc_64_g++4
  PLATFORM_SUPPORTS_LAPI       := yes
  LAPI_LDFLAGS   += -L/opt/ibmhpc/ppe.poe/lib
  LAPI_LDLIBS    += -lpoe -lmpi_ibm -llapi
  PLATFORM_SUPPORTS_SOCKETS    := yes
endif
ifeq ($(X10RT_PLATFORM), linux_x86_64)
  WPLATFORM      := linux_x86_64_g++4
  PLATFORM_SUPPORTS_LAPI       := yes
  LAPI_LDFLAGS   += -L/opt/ibmhpc/ppe.poe/lib
  LAPI_LDLIBS    += -lpoe -lmpi_ibm -llapi
  PLATFORM_SUPPORTS_SOCKETS    := yes
endif
ifeq ($(X10RT_PLATFORM), linux_x86_32)
  WPLATFORM      := linux_x86_g++4
  PLATFORM_SUPPORTS_LAPI       := yes
  LAPI_LDFLAGS   += -L/opt/ibmhpc/ppe.poe/lib
  LAPI_LDLIBS    += -lpoe -lmpi_ibm -llapi
  PLATFORM_SUPPORTS_SOCKETS    := yes
endif
ifeq ($(X10RT_PLATFORM), cygwin)
  WPLATFORM      := cygwin_x86_g++4
  PLATFORM_SUPPORTS_SOCKETS    := yes
endif
ifeq ($(X10RT_PLATFORM), darwin)
  WPLATFORM      := macos_x86_g++4
  PLATFORM_SUPPORTS_SOCKETS    := yes
endif
ifeq ($(X10RT_PLATFORM), darwin64)
  WPLATFORM      := macos_x86_64_g++4
  PLATFORM_SUPPORTS_SOCKETS    := yes
endif
ifeq ($(X10RT_PLATFORM), sunos)
  WPLATFORM      := sunos_sparc_g++4
  PLATFORM_SUPPORTS_SOCKETS    := yes
  SOCKETS_LDLIBS += -lresolv -lnsl -lsocket -lrt
endif

ifdef CUSTOM_PGAS
include/pgasrt.h: $(CUSTOM_PGAS)/include/pgasrt.h
	$(CP) $(CUSTOM_PGAS)/include/*.h include

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
  XLPGAS_LAPI_EXISTS := $(PLATFORM_SUPPORTS_LAPI)
  XLPGAS_SOCKETS_EXISTS := $(PLATFORM_SUPPORTS_SOCKETS)
  XLPGAS_BGP_EXISTS := $(PLATFORM_SUPPORTS_BGP)
endif

#Assume that if poe is installed then `which poe` will print its full path to
#stdout.  Since we don't know what the full path is, we can't run it because it
#will fail, and we can't trust the error messages or exit code of `which`, we
#instead test if the path is an executable file.
ifeq ($(shell test -x "`which poe 2>/dev/null`" && printf hi),hi)
  POE_EXISTS := yes
else
  POE_EXISTS := no
endif

ifeq ($(PLATFORM_SUPPORTS_SOCKETS), yes)

TESTS += $(patsubst test/%,test/%.pgas_sockets,$(BASE_TESTS))
LIBS += lib/libx10rt_pgas_sockets.a
PROPERTIES += etc/x10rt_pgas_sockets.properties
EXECUTABLES += bin/launcher bin/manager bin/daemon

%.pgas_sockets: %.cc lib/libx10rt_pgas_sockets.a
	$(CXX) $(CXXFLAGS) $< -o $@ $(LDFLAGS) $(SOCKETS_LDFLAGS) $(SOCKETS_LDLIBS)

ifdef CUSTOM_PGAS
lib/libxlpgas_sockets.a: $(COMMON_OBJS) $(CUSTOM_PGAS)/lib/libxlpgas_sockets.a include/pgasrt.h
	$(CP) $(CUSTOM_PGAS)/lib/libxlpgas_sockets.a lib/libxlpgas_sockets.a
else
$(SOCKETS_TGZ).phony:
	-$(WGET) -N  "http://dist.codehaus.org/x10/binaryReleases/svn head/$(SOCKETS_TGZ)"

$(SOCKETS_TGZ): $(SOCKETS_TGZ).phony

lib/libxlpgas_sockets.a: $(COMMON_OBJS) $(SOCKETS_TGZ)
	$(GZIP) -cd $(SOCKETS_TGZ) | $(TAR) -xvf -
endif

lib/libx10rt_pgas_sockets.a: $(COMMON_OBJS) lib/libxlpgas_sockets.a
	$(CP) lib/libxlpgas_sockets.a lib/libx10rt_pgas_sockets.a
	$(AR) $(ARFLAGS) $@ $(COMMON_OBJS)

etc/x10rt_pgas_sockets.properties:
	echo "CXX=$(CXX)" > $@
	echo "LDFLAGS=$(SOCKETS_LDFLAGS)" >> $@
	echo "LDLIBS=$(SOCKETS_LDLIBS)" >> $@

.PRECIOUS: etc/x10rt_pgas_sockets.properties
.PHONY: $(SOCKETS_TGZ).phony
TGZ += $(SOCKETS_TGZ).phony

endif


ifeq ($(PLATFORM_SUPPORTS_LAPI),yes)
ifeq ($(XLPGAS_LAPI_EXISTS),yes)
ifeq ($(POE_EXISTS),yes)
TESTS += $(patsubst test/%,test/%.pgas_lapi,$(BASE_TESTS))
else
HACK=$(shell echo "Your platform supports LAPI but we could not find the poe executable so not building LAPI tests">2)
endif

LIBS += lib/libx10rt_pgas_lapi.a
PROPERTIES += etc/x10rt_pgas_lapi.properties

%.pgas_lapi: %.cc lib/libx10rt_pgas_lapi.a
	$(CXX) $(CXXFLAGS) $< -o $@ $(LDFLAGS) $(LAPI_LDFLAGS) $(LAPI_LDLIBS)

ifdef CUSTOM_PGAS
lib/libxlpgas_lapi.a: $(COMMON_OBJS) $(CUSTOM_PGAS)/lib/libxlpgas_lapi.a include/pgasrt.h
	$(CP) $(CUSTOM_PGAS)/lib/libxlpgas_lapi.a lib/libxlpgas_lapi.a
else
$(LAPI_TGZ).phony:
	-$(WGET) -N  "http://dist.codehaus.org/x10/binaryReleases/svn head/$(LAPI_TGZ)"

$(LAPI_TGZ): $(LAPI_TGZ).phony

lib/libxlpgas_lapi.a: $(COMMON_OBJS) $(LAPI_TGZ)
	$(GZIP) -cd $(LAPI_TGZ) | $(TAR) -xvf -
endif

lib/libx10rt_pgas_lapi.a: $(COMMON_OBJS) lib/libxlpgas_lapi.a
	$(CP) lib/libxlpgas_lapi.a lib/libx10rt_pgas_lapi.a
	$(AR) $(ARFLAGS) $@ $(COMMON_OBJS)

etc/x10rt_pgas_lapi.properties:
	echo "CXX=$(CXX)" > $@
	echo "LDFLAGS=$(LAPI_LDFLAGS)" >> $@
	echo "LDLIBS=$(LAPI_LDLIBS)" >> $@

.PRECIOUS: etc/x10rt_pgas_lapi.properties
.PHONY: $(LAPI_TGZ).phony
TGZ += $(LAPI_TGZ).phony

endif #XLPGAS_LAPI_EXISTS
endif #PLATFORM_SUPPORTS_LAPI


ifeq ($(PLATFORM_SUPPORTS_BGP),yes)

TESTS += $(patsubst test/%,test/%.pgas_bgp,$(BASE_TESTS))
LIBS += lib/libx10rt_pgas_bgp.a
PROPERTIES += etc/x10rt_pgas_bgp.properties

%.pgas_bgp: %.cc lib/libx10rt_pgas_bgp.a
	$(CXX) $(CXXFLAGS) $< -o $@ $(LDFLAGS) $(BGP_LDFLAGS) $(BGP_LDLIBS)

ifdef CUSTOM_PGAS
lib/libxlpgas_bgp.a: $(COMMON_OBJS) $(CUSTOM_PGAS)/lib/libxlpgas_bgp.a include/pgasrt.h
	$(CP) $(CUSTOM_PGAS)/lib/libxlpgas_bgp.a lib/libxlpgas_bgp.a
else
$(BGP_TGZ).phony:
	-$(WGET) -N  "http://dist.codehaus.org/x10/binaryReleases/svn head/$(BGP_TGZ)"

$(BGP_TGZ): $(BGP_TGZ).phony

lib/libxlpgas_bgp.a: $(COMMON_OBJS) $(BGP_TGZ)
	$(GZIP) -cd $(BGP_TGZ) | $(TAR) -xvf -
endif

lib/libx10rt_pgas_bgp.a: $(COMMON_OBJS) lib/libxlpgas_bgp.a
	$(CP) lib/libxlpgas_bgp.a lib/libx10rt_pgas_bgp.a
	$(AR) $(ARFLAGS) $@ $(COMMON_OBJS)

etc/x10rt_pgas_bgp.properties:
	echo "CXX=$(CXX)" > $@
	echo "LDFLAGS=$(BGP_LDFLAGS)" >> $@
	echo "LDLIBS=$(BGP_LDLIBS)" >> $@

.PRECIOUS: etc/x10rt_pgas_bgp.properties
.PHONY: $(BGP_TGZ).phony
TGZ += $(BGP_TGZ).phony

endif


debug::
	@echo pgas.mk X10RT_PLATFORM = $(X10RT_PLATFORM)
	@echo pgas.mk ENABLE_X10RT_CUDA = $(ENABLE_X10RT_CUDA)
	@echo pgas.mk DISABLE_X10RT_CUDA = $(DISABLE_X10RT_CUDA)
	@echo pgas.mk ENABLE_X10RT_MPI = $(ENABLE_X10RT_MPI)
	@echo pgas.mk DISABLE_X10RT_MPI = $(DISABLE_X10RT_MPI)
	@echo pgas.mk ENABLE_X10RT_PGAS = $(ENABLE_X10RT_PGAS)
	@echo pgas.mk DISABLE_X10RT_PGAS = $(DISABLE_X10RT_PGAS)
	@echo pgas.mk PLATFORM_SUPPORTS_LAPI = $(PLATFORM_SUPPORTS_LAPI)
	@echo pgas.mk PLATFORM_SUPPORTS_SOCKETS = $(PLATFORM_SUPPORTS_SOCKETS)
	@echo pgas.mk PLATFORM_SUPPORTS_BGP = $(PLATFORM_SUPPORTS_BGP)
	@echo pgas.mk CUSTOM_PGAS = $(CUSTOM_PGAS)
	@echo pgas.mk XLPGAS_LAPI_EXISTS = $(XLPGAS_LAPI_EXISTS)
	@echo pgas.mk XLPGAS_SOCKETS_EXISTS = $(XLPGAS_SOCKETS_EXISTS)
	@echo pgas.mk XLPGAS_BGP_EXISTS = $(XLPGAS_BGP_EXISTS)
	@echo pgas.mk LIBS = $(LIBS)
	@echo pgas.mk PROPERTIES = $(PROPERTIES)
	@echo pgas.mk TESTS = $(TESTS)

# vim: ts=8:sw=8:noet
