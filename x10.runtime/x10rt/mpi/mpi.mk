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

TESTS += $(patsubst test/%,test/%.mpi,$(BASE_TESTS))

LIB_FILE_MPI = lib/$(LIBPREFIX)x10rt_mpi$(LIBSUFFIX)
LIBS += $(LIB_FILE_MPI)

PROPERTIES += etc/x10rt_mpi.properties

ifndef DISABLE_JAVA_RUNTIME
  EXECUTABLES += mpi/X10MPIJava
endif

MOV_LDFLAGS_MPI = $(MOV_LDFLAGS)
MOV_LDLIBS_MPI = $(MOV_LDLIBS)
SO_LDFLAGS_MPI = $(SO_LDFLAGS)
SO_LDLIBS_MPI = $(SO_LDLIBS)
APP_LDFLAGS_MPI = $(APP_LDFLAGS)
APP_LDLIBS_MPI = $(APP_LDLIBS) -lx10rt_mpi
APP_LDLIBS_MPI_NO_X10RT = $(APP_LDLIBS)

ifdef X10_STATIC_LIB
  APP_LDFLAGS_MPI += $(MOV_LDFLAGS_MPI)
  APP_LDLIBS_MPI += $(MOV_LDLIBS_MPI)
  APP_LDLIBS_MPI_NO_X10RT += $(MOV_LDLIBS_MPI)
else
  SO_LDFLAGS_MPI += $(MOV_LDFLAGS_MPI)
  SO_LDLIBS_MPI += $(MOV_LDLIBS_MPI)
endif

%.mpi: %.cc $(LIB_FILE_MPI)
	$(MPICXX) $(CXXFLAGS) $< $(APP_LDFLAGS_MPI) $(APP_LDLIBS_MPI) $(X10RT_TEST_LDFLAGS) -o $@

mpi/x10rt_mpi.o: mpi/x10rt_mpi.cc
	$(MPICXX) $(CXXFLAGS) $(CXXFLAGS_SHARED) $< -c -o $@

$(LIB_FILE_MPI): mpi/x10rt_mpi.o $(COMMON_OBJS)
ifdef X10_STATIC_LIB
	$(AR) $(ARFLAGS) $@ $^
else
	$(MPICXX) $(CXXFLAGS) $(CXXFLAGS_SHARED) $^ $(SO_LDFLAGS_MPI) $(SO_LDLIBS_MPI) -o $@
endif

# -ljsig is needed for Oracle VM
# -ldl is needed for MPICH
mpi/X10MPIJava: mpi/Java.cc
	$(MPICXX) $(CXXFLAGS) $(APP_LDFLAGS_MPI) $(APP_LDLIBS_MPI_NO_X10RT) mpi/Java.cc -o mpi/X10MPIJava -ljvm -ljsig -ldl $(JNI_LIBS)

etc/x10rt_mpi.properties:
	@echo "X10LIB_PLATFORM=$(X10RT_PLATFORM)" > $@
	@echo "X10LIB_CXX=$(MPICXX)" >> $@
	@echo "X10LIB_CXXFLAGS=$(X10RT_PROPS_CXXFLAGS)" >> $@
	@echo "X10LIB_LDFLAGS=$(APP_LDFLAGS_MPI)" >> $@
	@echo "X10LIB_LDLIBS=$(APP_LDLIBS_MPI)" >> $@

.PRECIOUS: etc/x10rt_mpi.properties

# vim: ts=8:sw=8:noet
