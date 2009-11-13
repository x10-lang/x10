VERSION=20091112
SOCKETS_TGZ = pgas-$(VERSION)-$(WPLATFORM)-sockets.tgz
LAPI_TGZ = pgas-$(VERSION)-$(WPLATFORM)-lapi.tgz
BGP_TGZ = pgas-$(VERSION)-$(WPLATFORM)-bgp.tgz

# defaults
SOCKETS_USE := no
LAPI_USE := no
BGP_USE := no

LAPI_LDFLAGS    = $(CUDA_LDFLAGS)
BGP_LDFLAGS     = $(CUDA_LDFLAGS)
SOCKETS_LDFLAGS = $(CUDA_LDFLAGS)

LAPI_LDLIBS     = -lx10rt_pgas_lapi $(CUDA_LDLIBS)
BGP_LDLIBS      = -lx10rt_pgas_bgp $(CUDA_LDLIBS)
SOCKETS_LDLIBS  = -lx10rt_pgas_sockets -lpthread $(CUDA_LDLIBS)

ifeq ($(X10RT_PLATFORM), bgp)
  WPLATFORM      := bgp_g++4
  BGP_USE        := yes
  BGP_LDFLAGS    += -L/bgsys/drivers/ppcfloor/comm/lib -L/bgsys/drivers/ppcfloor/runtime/SPI
  BGP_LDLIBS     += -ldcmf.cnk -ldcmfcoll.cnk -lSPI.cna -lpthread -lrt -lm
endif
ifeq ($(X10RT_PLATFORM), aix_xlc)
  WPLATFORM      := aix_xlc
  LAPI_USE       := yes
  #SOCKETS_USE    := yes
endif
ifeq ($(X10RT_PLATFORM), aix_gcc)
  WPLATFORM      := aix_g++4
  LAPI_USE       := yes
  LAPI_LDFLAGS   += -Wl,-binitfini:poe_remote_main -L/usr/lpp/ppe.poe/lib
  LAPI_LDLIBS    += -lmpi_r -lvtd_r -llapi_r -lpthread -lm
  #SOCKETS_USE    := yes
endif
ifeq ($(X10RT_PLATFORM), linux_ppc_64)
  WPLATFORM      := linux_ppc_64_g++4
  LAPI_USE       := yes
  LAPI_LDFLAGS   += -L/opt/ibmhpc/ppe.poe/lib
  LAPI_LDLIBS    += -lpoe -lmpi_ibm -llapi
  SOCKETS_USE    := yes
endif
ifeq ($(X10RT_PLATFORM), linux_x86_64)
  WPLATFORM      := linux_x86_64_g++4
  LAPI_USE       := yes
  LAPI_LDFLAGS   += -L/opt/ibmhpc/ppe.poe/lib
  LAPI_LDLIBS    += -lpoe -lmpi_ibm -llapi
  SOCKETS_USE    := yes
endif
ifeq ($(X10RT_PLATFORM), linux_x86_32)
  WPLATFORM      := linux_x86_g++4
  LAPI_USE       := yes
  LAPI_LDFLAGS   += -L/opt/ibmhpc/ppe.poe/lib
  LAPI_LDLIBS    += -lpoe -lmpi_ibm -llapi
  SOCKETS_USE    := yes
endif
ifeq ($(X10RT_PLATFORM), cygwin)
  WPLATFORM      := cygwin_x86_g++3
  SOCKETS_USE    := yes
endif
ifeq ($(X10RT_PLATFORM), darwin)
  WPLATFORM      := macos_x86_g++4
  SOCKETS_USE    := yes
endif
ifeq ($(X10RT_PLATFORM), sunos)
  WPLATFORM      := sunos_sparc_g++4
  SOCKETS_USE    := yes
  SOCKETS_LDLIBS += -lresolv -lnsl -lsocket -lrt
endif

ifdef CUSTOM_PGAS
include/pgasrt.h: $(CUSTOM_PGAS)/include/pgasrt.h
	$(CP) $(CUSTOM_PGAS)/include/*.h include
endif

ifneq ($(shell test -x `which poe` && echo -n hi), hi)
  POE_EXISTS := yes
else
  POE_EXISTS := no
endif

ifeq ($(SOCKETS_USE), yes)

TESTS += $(patsubst test/%,test/%.pgas_sockets,$(BASE_TESTS))
LIBS += lib/libx10rt_pgas_sockets.a
PROPERTIES += etc/x10rt_pgas_sockets.properties
EXECUTABLES += bin/launcher bin/manager bin/daemon

%.pgas_sockets: %.cc lib/libx10rt_pgas_sockets.a
	$(CXX) $(CXXFLAGS) $< -o $@ $(LDFLAGS) $(SOCKETS_LDFLAGS) $(SOCKETS_LDLIBS)

ifdef CUSTOM_PGAS
lib/libx10rt_pgas_sockets.a: $(COMMON_OBJS) $(CUSTOM_PGAS)/lib/libxlpgas_sockets.a include/pgasrt.h
	$(CP) $(CUSTOM_PGAS)/lib/libxlpgas_sockets.a lib/libx10rt_pgas_sockets.a
	$(CP) $(CUSTOM_PGAS)/bin/* bin
	$(AR) $(ARFLAGS) $@ $(COMMON_OBJS)
else
$(SOCKETS_TGZ):
	$(WGET) -N  "http://dist.codehaus.org/x10/binaryReleases/svn head/$(SOCKETS_TGZ)"

lib/libx10rt_pgas_sockets.a: $(COMMON_OBJS) $(SOCKETS_TGZ)
	$(GZIP) -cd $(SOCKETS_TGZ) | $(TAR) -xvf -
	$(CP) lib/libxlpgas_sockets.a lib/libx10rt_pgas_sockets.a
	$(AR) $(ARFLAGS) $@ $(COMMON_OBJS)
endif

etc/x10rt_pgas_sockets.properties:
	echo "CXX=$(CXX)" > $@
	echo "LDFLAGS=$(SOCKETS_LDFLAGS)" >> $@
	echo "LDLIBS=$(SOCKETS_LDLIBS)" >> $@

.PRECIOUS: etc/x10rt_pgas_sockets.properties
.PHONY: $(SOCKETS_TGZ)

endif


ifeq ($(LAPI_USE),yes)
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
lib/libx10rt_pgas_lapi.a: $(COMMON_OBJS) $(CUSTOM_PGAS)/lib/libxlpgas_lapi.a include/pgasrt.h
	$(CP) $(CUSTOM_PGAS)/lib/libxlpgas_lapi.a lib/libx10rt_pgas_lapi.a
	$(AR) $(ARFLAGS) $@ $(COMMON_OBJS)
else
$(LAPI_TGZ):
	$(WGET) -N  "http://dist.codehaus.org/x10/binaryReleases/svn head/$(LAPI_TGZ)"

lib/libx10rt_pgas_lapi.a: $(COMMON_OBJS) $(LAPI_TGZ)
	$(GZIP) -cd $(LAPI_TGZ) | $(TAR) -xvf -
	$(CP) lib/libxlpgas_lapi.a lib/libx10rt_pgas_lapi.a
	$(AR) $(ARFLAGS) $@ $(COMMON_OBJS)
endif

etc/x10rt_pgas_lapi.properties:
	echo "CXX=$(CXX)" > $@
	echo "LDFLAGS=$(LAPI_LDFLAGS)" >> $@
	echo "LDLIBS=$(LAPI_LDLIBS)" >> $@

.PRECIOUS: etc/x10rt_pgas_lapi.properties
.PHONY: $(LAPI_TGZ)

endif


ifeq ($(BGP_USE),yes)

TESTS += $(patsubst test/%,test/%.pgas_bgp,$(BASE_TESTS))
LIBS += lib/libx10rt_pgas_bgp.a
PROPERTIES += etc/x10rt_pgas_bgp.properties

%.pgas_bgp: %.cc lib/libx10rt_pgas_bgp.a
	$(CXX) $(CXXFLAGS) $< -o $@ $(LDFLAGS) $(BGP_LDFLAGS) $(BGP_LDLIBS)

ifdef CUSTOM_PGAS
lib/libx10rt_pgas_bgp.a: $(COMMON_OBJS) $(CUSTOM_PGAS)/lib/libxlpgas_bgp.a include/pgasrt.h
	$(CP) $(CUSTOM_PGAS)/lib/libxlpgas_bgp.a lib/libx10rt_pgas_bgp.a
	$(AR) $(ARFLAGS) $@ $(COMMON_OBJS)
else
$(BGP_TGZ):
	$(WGET) -N  "http://dist.codehaus.org/x10/binaryReleases/svn head/$(BGP_TGZ)"

lib/libx10rt_pgas_bgp.a: $(COMMON_OBJS) $(BGP_TGZ)
	$(GZIP) -cd $(BGP_TGZ) | $(TAR) -xvf -
	$(CP) lib/libxlpgas_bgp.a lib/libx10rt_pgas_bgp.a
	$(AR) $(ARFLAGS) $@ $(COMMON_OBJS)
endif

etc/x10rt_pgas_bgp.properties:
	echo "CXX=$(CXX)" > $@
	echo "LDFLAGS=$(BGP_LDFLAGS)" >> $@
	echo "LDLIBS=$(BGP_LDLIBS)" >> $@

.PRECIOUS: etc/x10rt_pgas_bgp.properties
.PHONY: $(BGP_TGZ)

endif


debug_pgas:
	echo X10RT_PLATFORM = $(X10RT_PLATFORM)
	echo ENABLE_X10RT_CUDA = $(ENABLE_X10RT_CUDA)
	echo DISABLE_X10RT_CUDA = $(DISABLE_X10RT_CUDA)
	echo ENABLE_X10RT_MPI = $(ENABLE_X10RT_MPI)
	echo DISABLE_X10RT_MPI = $(DISABLE_X10RT_MPI)
	echo ENABLE_X10RT_PGAS = $(ENABLE_X10RT_PGAS)
	echo DISABLE_X10RT_PGAS = $(DISABLE_X10RT_PGAS)
	echo LIBS = $(LIBS)
	echo PROPERTIES = $(PROPERTIES)
	echo TESTS = $(TESTS)

# vim: ts=8:sw=8:noet
