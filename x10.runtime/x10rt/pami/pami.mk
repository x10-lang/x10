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


MOV_LDFLAGS_PAMI    = $(MOV_LDFLAGS)
MOV_LDLIBS_PAMI     = $(MOV_LDLIBS)
SO_LDFLAGS_PAMI    = $(SO_LDFLAGS)
SO_LDLIBS_PAMI     = $(SO_LDLIBS)
APP_LDFLAGS_PAMI    = $(APP_LDFLAGS)
APP_LDLIBS_PAMI     = $(APP_LDLIBS) -lx10rt_pami

ifdef X10_STATIC_LIB
  APP_LDFLAGS_PAMI += $(MOV_LDFLAGS_PAMI)
  APP_LDLIBS_PAMI += $(MOV_LDLIBS_PAMI)
else
  SO_LDFLAGS_PAMI += $(MOV_LDFLAGS_PAMI)
  SO_LDLIBS_PAMI += $(MOV_LDLIBS_PAMI)
endif

PAMI_MPCC=mpCC

ifeq ($(X10RT_PLATFORM), aix_xlc)
  MOV_LDFLAGS_PAMI     += -L/opt/ibmhpc/pecurrent/ppe.poe/lib64
  MOV_LDLIBS_PAMI     += -lmpi_r -lvtd_r -lpami_r -lpthread -lm
  APP_LDFLAGS_PAMI   += -Wl,-binitfini:poe_remote_main 
endif
ifeq ($(X10RT_PLATFORM), linux_x86_64)
  MOV_LDFLAGS_PAMI    += -L/opt/ibmhpc/pecurrent/ppe.pami/gnu/lib64/pami64
  MOV_LDLIBS_PAMI    += -lpoe -lmpi_ibm -lpami
  PAMI_MPCC += -pami
endif
ifeq ($(X10RT_PLATFORM), linux_x86_32)
  MOV_LDFLAGS_PAMI    += -L/opt/ibmhpc/pecurrent/ppe.pami/gnu/lib/pami
  MOV_LDLIBS_PAMI    += -lpoe -lmpi_ibm -lpami
  PAMI_MPCC += -pami
endif
ifeq ($(X10RT_PLATFORM), aix_gcc)
  MOV_LDFLAGS_PAMI     += -L/opt/ibmhpc/pecurrent/ppe.poe/lib64
  MOV_LDLIBS_PAMI     += -lmpi_r -lvtd_r -lpami_r -lpthread -lm
  APP_LDFLAGS_PAMI   += -Wl,-binitfini:poe_remote_main 
endif
ifeq ($(X10RT_PLATFORM), linux_ppc_64_gcc)
  MOV_LDFLAGS_PAMI    += -L/opt/ibmhpc/pecurrent/ppe.pami/gnu/lib64/pami64
  MOV_LDLIBS_PAMI    += -lpoe -lmpi_ibm -lpami
endif
ifeq ($(X10RT_PLATFORM), linux_ppc_64_xlc)
  MOV_LDFLAGS_PAMI    += -L/opt/ibmhpc/pecurrent/ppe.pami/gnu/lib64/pami64
  MOV_LDLIBS_PAMI    += -lpoe -lmpi_ibm -lpami
endif
ifeq ($(X10RT_PLATFORM), bgq_gcc)
  override CXXFLAGS += -I/bgsys/drivers/ppcfloor/comm/sys/include
  MOV_LDFLAGS_PAMI  += -L/bgsys/drivers/ppcfloor/comm/lib -L/bgsys/drivers/ppcfloor/comm/sys/lib -L/bgsys/drivers/ppcfloor/spi/lib
  MOV_LDLIBS_PAMI   += -lpami-gcc -lSPI_cnk -lrt -lstdc++ -lpthread -lm
  PAMI_MPCC = $(CC)
endif
ifeq ($(X10RT_PLATFORM), bgq_xlc)
  override CXXFLAGS += -I/bgsys/drivers/ppcfloor/comm/sys/include
  MOV_LDFLAGS_PAMI  += -L/bgsys/drivers/ppcfloor/comm/lib -L/bgsys/drivers/ppcfloor/comm/sys/lib -L/bgsys/drivers/ppcfloor/spi/lib
  # note, libpami is always compiled with gcc
  MOV_LDLIBS_PAMI   += -lpami-gcc -lSPI_cnk -lrt -lstdc++ -lpthread -lm
  PAMI_MPCC = $(CC)
endif


TESTS += $(patsubst test/%,test/%.pami,$(BASE_TESTS)) pami/list_collectives pami/bench_collectives
LIB_FILE_PAMI = lib/$(LIBPREFIX)x10rt_pami$(LIBSUFFIX)
LIBS += $(LIB_FILE_PAMI)
PROPERTIES += etc/x10rt_pami.properties

%.pami: %.cc $(LIB_FILE_PAMI)
	$(PAMI_MPCC) $(CXXFLAGS) $< $(APP_LDFLAGS_PAMI) $(APP_LDLIBS_PAMI) $(X10RT_TEST_LDFLAGS) -o $@

pami/x10rt_pami.o: pami/x10rt_pami.cc
	$(PAMI_MPCC) $(CXXFLAGS) $(CXXFLAGS_SHARED) $< -c -o $@

pami/list_collectives:	pami/ListCollectiveOptions.c
	$(PAMI_MPCC) $(CXXFLAGS) -std=gnu99 $< $(APP_LDFLAGS_PAMI) $(APP_LDLIBS_PAMI)  $(X10RT_TEST_LDFLAGS) -o $@

pami/bench_collectives:	pami/BenchmarkCollectiveOptions.c
	$(PAMI_MPCC) $(CXXFLAGS) -std=gnu99 $< $(APP_LDFLAGS_PAMI) $(APP_LDLIBS_PAMI)  $(X10RT_TEST_LDFLAGS) -o $@

$(LIB_FILE_PAMI): pami/x10rt_pami.o $(COMMON_OBJS)
ifdef X10_STATIC_LIB
	$(AR) $(ARFLAGS) $@ $^
else
	$(PAMI_MPCC) $(CXXFLAGS) $(CXXFLAGS_SHARED) $^ $(SO_LDFLAGS_PAMI) $(SO_LDLIBS_PAMI) -o $@
endif

etc/x10rt_pami.properties:
	@echo "X10LIB_PLATFORM=$(X10RT_PLATFORM)" > $@
	@echo "X10LIB_CXX=$(CXX)" >> $@
	@echo "X10LIB_CXXFLAGS=$(X10RT_PROPS_CXXFLAGS)" >> $@
	@echo "X10LIB_LDFLAGS=$(APP_LDFLAGS_PAMI)" >> $@
	@echo "X10LIB_LDLIBS=$(APP_LDLIBS_PAMI)" >> $@

.PRECIOUS: etc/x10rt_pami.properties

# vim: ts=8:sw=8:noet
