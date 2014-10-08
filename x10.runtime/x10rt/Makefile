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

include ../Make.rules

CP              ?= cp -f
WGET            ?= wget
TAR             ?= tar
GZIP            ?= gzip

override CXXFLAGS += -Iinclude -Icommon ${JNI_INCLUDES}
ifdef USE_GCC
    override CXXFLAGS += ${CXX_WARNING_FLAGS} -Werror=return-type
endif
ifdef CROSS_COMPILE_FX10
    override CXXFLAGS += ${CXX_WARNING_FLAGS}
endif

BASE_TESTS      = test/x10rt_basic test/x10rt_gups test/x10rt_topology test/x10rt_coll test/x10rt_crash test/x10rt_remote_op test/x10rt_resilient_monte_pi

X10_HOME        = ${CURDIR}/../..

COMMON_OBJS     = common/x10rt_front.o common/x10rt_logical.o common/x10rt_cuda.o common/x10rt_emu.o common/x10rt_emu_coll.o

# Include JNI bindings in x10rt if we can find a JDK unless cross-compiling
ifndef DISABLE_JAVA_RUNTIME
ifdef JNI_INCLUDES
  COMMON_OBJS += jni/jni_x10rt.o jni/jni_message.o jni/jni_team.o jni/jni_helpers.o
endif
endif

# It is worth separating the linker flags for building the shared object from
# the linker flags that should be used by an application
# The MOV_ are the params that should be part of either SO or APP depending on X10_STATIC_LIB
# Note that Make.rules gives us APP_LDFLAGS as LDFLAGS and SO_LDFLAGS as LDFLAGS_SHARED
MOV_LDLIBS := -lpthread
MOV_LDFLAGS :=
APP_LDLIBS := $(LDLIBS) 
APP_LDFLAGS := $(LDFLAGS) 
SO_LDLIBS := 
SO_LDFLAGS = $(LDFLAGS_SHARED)
X10RT_TEST_LDFLAGS += -L"$(X10_HOME)"/x10.runtime/x10rt/lib

ifdef ENABLE_X10RT_CUDA
	CUDA_PATH ?= /usr/local/cuda
	override CXXFLAGS += -DENABLE_CUDA -isystem$(CUDA_PATH)/include
	override MOV_LDFLAGS += -L$(CUDA_PATH)/lib
	override MOV_LDLIBS += -lcuda
endif

# avoid a lot of repetition in the */*.mk scripts
ifeq ($(X10RT_PLATFORM),aix_xlc)
        LINKER_PROG=$(SHLINK)
else
        LINKER_PROG=$(CXX)
endif


default: all

ifdef ENABLE_X10RT_MPI
  include mpi/mpi.mk
endif

ifdef ENABLE_X10RT_PAMI
  include pami/pami.mk
endif

ifndef CROSS_COMPILE_BLUEGENE
  include standalone/standalone.mk
  include sockets/sockets.mk
  ifdef JNI_INCLUDES
    include jni/jni.mk
  endif
endif

install: $(LIBS) $(PROPERTIES)
	(test -n "$(LIBS)" && $(CP) $(LIBS) "$(X10_HOME)"/x10.dist/lib) || true
	$(CP) $(PROPERTIES) "$(X10_HOME)"/x10.dist/etc
	$(CP) include/*.h "$(X10_HOME)"/x10.dist/include
	(test -n "$(EXECUTABLES)" && $(CP) $(EXECUTABLES) "$(X10_HOME)"/x10.dist/bin) || true

headers:
	$(CP) include/*.h "$(X10_HOME)"/x10.dist/include

tests: $(TESTS)

all: $(TESTS) $(PROPERTIES) $(LIBS) $(EXECUTABLES) $(TGZ) debug

debug::
	@echo X10RT_PLATFORM = $(X10RT_PLATFORM)
	@echo ENABLE_X10RT_CUDA = $(ENABLE_X10RT_CUDA)
	@echo DISABLE_X10RT_CUDA = $(DISABLE_X10RT_CUDA)
	@echo ENABLE_X10RT_MPI = $(ENABLE_X10RT_MPI)
	@echo DISABLE_X10RT_MPI = $(DISABLE_X10RT_MPI)
	@echo ENABLE_X10RT_PAMI = $(ENABLE_X10RT_PAMI)
	@echo DISABLE_X10RT_PAMI = $(DISABLE_X10RT_PAMI)
	@echo JNI_INCLUDES = $(JNI_INCLUDES)
	@echo LIBS = $(LIBS)
	@echo PROPERTIES = $(PROPERTIES)
	@echo TESTS = $(TESTS)

clean:
	-$(RM) -r */*.o lib/* bin/* etc/*.properties test/*.mpi test/*.standalone test/*.sockets test/*.pami */*~ *~ core* vgcore* ${EXECUTABLES}

depend:
	makedepend -I include -I common -Y */*.cc


%.o: %.cc
	$(CXX) $(CXXFLAGS) $(CXXFLAGS_SHARED) -c $< -o $@

# vim: ts=8:sw=8:noet
# DO NOT DELETE

common/x10rt_cuda.o: include/x10rt_types.h common/x10rt_internal.h
common/x10rt_cuda.o: include/x10rt_cuda.h
common/x10rt_emu.o: include/x10rt_net.h include/x10rt_types.h
common/x10rt_emu.o: common/x10rt_internal.h include/x10rt_ser.h
common/x10rt_emu_coll.o: include/x10rt_types.h common/x10rt_internal.h
common/x10rt_emu_coll.o: include/x10rt_net.h include/x10rt_ser.h
common/x10rt_emu_coll.o: include/x10rt_cpp.h
common/x10rt_front.o: include/x10rt_front.h include/x10rt_types.h
common/x10rt_front.o: include/x10rt_logical.h
common/x10rt_logical.o: include/x10rt_logical.h include/x10rt_types.h
common/x10rt_logical.o: include/x10rt_net.h include/x10rt_cuda.h
common/x10rt_logical.o: common/x10rt_internal.h include/x10rt_ser.h
jni/jni_message.o: include/x10rt_front.h
jni/jni_message.o: include/x10rt_types.h
jni/jni_team.o: include/x10_x10rt_TeamSupport.h include/x10rt_front.h
jni/jni_team.o: include/x10rt_types.h
jni/jni_x10rt.o: include/x10_x10rt_X10RT.h include/x10rt_front.h
jni/jni_x10rt.o: include/x10rt_types.h
mpi/x10rt_mpi.o: include/x10rt_net.h include/x10rt_types.h
pami/x10rt_pami.o: include/x10rt_net.h include/x10rt_types.h
sockets/Launcher.o: sockets/Launcher.h sockets/TCP.h
sockets/Launcher_Init.o: sockets/TCP.h sockets/Launcher.h
sockets/tcp.o: sockets/TCP.h
sockets/x10rt_sockets.o: include/x10rt_net.h include/x10rt_types.h
sockets/x10rt_sockets.o: sockets/Launcher.h sockets/TCP.h
standalone/x10rt_standalone.o: include/x10rt_net.h include/x10rt_types.h
test/x10rt_basic.o: include/x10rt_front.h include/x10rt_types.h
test/x10rt_coll.o: include/x10rt_front.h include/x10rt_types.h
test/x10rt_coll.o: include/x10rt_ser.h
test/x10rt_gups.o: include/x10rt_front.h include/x10rt_types.h
test/x10rt_gups.o: include/x10rt_ser.h include/x10rt_net.h
test/x10rt_gups.o: include/x10rt_logical.h
test/x10rt_topology.o: include/x10rt_front.h include/x10rt_types.h
