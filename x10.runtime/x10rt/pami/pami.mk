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

PAMI_LDFLAGS    = $(CUDA_LDFLAGS)
PANE_LDFLAGS    = $(CUDA_LDFLAGS) -btextpsize:64K -bdatapsize:64K -bstackpsize:64K
PAMI_LDLIBS     = -lx10rt_pami $(CUDA_LDLIBS)
PANE_LDLIBS     = -lx10rt_pami_pane $(CUDA_LDLIBS)

ifeq ($(X10RT_PLATFORM), aix_xlc)
  PAMI_LDFLAGS   += -Wl,-binitfini:poe_remote_main 
  PAMI_LDDEPS    := -L/usr/lpp/ppe.poe/lib -lmpi_r -lvtd_r -lpami_r -lpthread -lm
  PAMI_LDLIBS    += $(PAMI_LDDEPS)
  WPLATFORM      := aix_xlc
endif
ifeq ($(X10RT_PLATFORM), linux_x86_64)
  WPLATFORM      := linux_x86_64_g++4
  PAMI_LDLIBS    += $(PAMI_LDDEPS) -L/opt/ibmhpc/ppe.poe/lib -lpoe -lmpi_ibm -lpami
endif
ifeq ($(X10RT_PLATFORM), linux_x86_32)
  WPLATFORM      := linux_x86_g++4
  PAMI_LDLIBS    += $(PAMI_LDDEPS) -L/opt/ibmhpc/ppe.poe/lib -lpoe -lmpi_ibm -lpami
endif
ifeq ($(X10RT_PLATFORM), aix_gcc)
  WPLATFORM      := aix_g++4
  PAMI_LDFLAGS   += -Wl,-binitfini:poe_remote_main 
  PAMI_LDDEPS    := -L/usr/lpp/ppe.poe/lib -lmpi_r -lvtd_r -lpami_r -lpthread -lm
  PAMI_LDLIBS    += $(PAMI_LDDEPS)
  PANE_LDFLAGS   += -Wl,-binitfini:poe_remote_main -L/usr/lpp/ppe.poe/lib
  PANE_ARLIBS     = -lpami_r -lpthread -lm
  PANE_LDLIBS    += -lmpi_r -lvtd_r $(PANE_ARLIBS)
endif
ifeq ($(X10RT_PLATFORM), linux_ppc_64_gcc)
  WPLATFORM      := linux_ppc_64_g++4
  PAMI_LDLIBS    += $(PAMI_LDDEPS) -L/opt/ibmhpc/ppe.poe/lib -lpoe -lmpi_ibm -lpami
endif
ifeq ($(X10RT_PLATFORM), linux_ppc_64_xlc)
  WPLATFORM      := linux_ppc_64_xlc
  LAPI_LDLIBS    += $(PAMI_LDDEPS) -L/opt/ibmhpc/ppe.poe/lib -lpoe -lmpi_ibm -lpami
endif
ifeq ($(X10RT_PLATFORM), bgp)
  WPLATFORM      := bgp_g++4
  BGP_LDFLAGS    += -L/bgsys/drivers/ppcfloor/comm/lib -L/bgsys/drivers/ppcfloor/runtime/SPI
  BGP_LDLIBS     += -ldcmf.cnk -ldcmfcoll.cnk -lSPI.cna -lpthread -lrt -lm
endif

TESTS += $(patsubst test/%,test/%.pami,$(BASE_TESTS))
PAMI_DYNLIB = lib/$(LIBPREFIX)x10rt_pami$(LIBSUFFIX)
LIBS += $(PAMI_DYNLIB)
PROPERTIES += etc/x10rt_pami.properties

%.pami: %.cc $(PAMI_DYNLIB)
	$(CXX) $(CXXFLAGS) $< -o $@ $(LDFLAGS) $(PAMI_LDFLAGS) $(PAMI_LDLIBS) $(X10RT_TEST_LDFLAGS)

pami/x10rt_pami.o: pami/x10rt_pami.cc
	$(CXX) $(CXXFLAGS) $(CXXFLAGS_SHARED) -c $< -o $@

ifdef X10_STATIC_LIB
$(PAMI_DYNLIB): pami/x10rt_pami.o $(COMMON_OBJS)
	$(CP) pami/x10rt_pami.o $@
	$(AR) $(ARFLAGS) $@ $(COMMON_OBJS)
else
$(PAMI_DYNLIB): pami/x10rt_pami.o $(COMMON_OBJS)
ifeq ($(X10RT_PLATFORM),aix_xlc)
	$(SHLINK) $(CXXFLAGS) $(CXXFLAGS_SHARED) $(LDFLAGS_SHARED) $(PAMI_LDDEPS) -o $@ $^
else
	$(CXX) $(CXXFLAGS) $(CXXFLAGS_SHARED) $(LDFLAGS_SHARED) $(PAMI_LDDEPS) -o $@ $^
endif
endif

etc/x10rt_pami.properties:
	@echo "PLATFORM=$(X10RT_PLATFORM)" > $@
	@echo "CXX=$(CXX)" >> $@
	@echo "CXXFLAGS=" >> $@
	echo "LDFLAGS=$(PAMI_LDFLAGS)" >> $@
	echo "LDLIBS=$(PAMI_LDLIBS)" >> $@

.PRECIOUS: etc/x10rt_pami.properties

# vim: ts=8:sw=8:noet
