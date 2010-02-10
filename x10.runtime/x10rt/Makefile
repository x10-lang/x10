include ../Make.rules

CP              ?= cp -f
WGET            ?= wget
TAR             ?= tar
GZIP            ?= gzip

override CXXFLAGS += -Iinclude -Icommon ${JNI_INCLUDES}

# these are used only for building tests
override LDFLAGS         += -Llib -lpthread

BASE_TESTS      = test/x10rt_basic test/x10rt_gups test/x10rt_topology

EXECUTABLES     =

X10_HOME        = ${CURDIR}/../..

COMMON_OBJS     = common/x10rt_front.o common/x10rt_logical.o common/x10rt_cuda.o
ifdef JNI_INCLUDES
  COMMON_OBJS += jni/jni_x10rt.o jni/jni_message.o
endif

ifdef ENABLE_X10RT_CUDA
	CXXFLAGS += -DENABLE_CUDA -isystem/usr/local/cuda/include
	CUDA_LDFLAGS = -L/usr/local/cuda/lib
	CUDA_LDLIBS = -lcuda
	LDFLAGS += $(CUDA_LDFLAGS) $(CUDA_LDLIBS)
endif

default: all

ifdef ENABLE_X10RT_MPI
  include mpi/mpi.mk
endif

ifndef DISABLE_X10RT_PGAS
  include pgas/pgas.mk
endif

include standalone/standalone.mk

install: $(LIBS) $(PROPERTIES)
	(test -n "$(LIBS)" && $(CP) $(LIBS) $(X10_HOME)/x10.dist/lib) || true
	$(CP) $(PROPERTIES) $(X10_HOME)/x10.dist/etc
	$(CP) include/*.h $(X10_HOME)/x10.dist/include
	(test -n "$(EXECUTABLES)" && $(CP) $(EXECUTABLES) $(X10_HOME)/x10.dist/bin) || true

tests: $(TESTS)

all: $(TESTS) $(PROPERTIES) $(LIBS) $(EXECUTABLES) $(TGZ)

debug::
	@echo X10RT_PLATFORM = $(X10RT_PLATFORM)
	@echo ENABLE_X10RT_CUDA = $(ENABLE_X10RT_CUDA)
	@echo DISABLE_X10RT_CUDA = $(DISABLE_X10RT_CUDA)
	@echo ENABLE_X10RT_MPI = $(ENABLE_X10RT_MPI)
	@echo DISABLE_X10RT_MPI = $(DISABLE_X10RT_MPI)
	@echo ENABLE_X10RT_PGAS = $(ENABLE_X10RT_PGAS)
	@echo DISABLE_X10RT_PGAS = $(DISABLE_X10RT_PGAS)
	@echo LIBS = $(LIBS)
	@echo PROPERTIES = $(PROPERTIES)
	@echo TESTS = $(TESTS)

clean:
	$(RM) -r */*.o lib/*.a bin/* etc/*.properties test/*.mpi test/*.standalone test/*.pgas_* */*~ *~ core* vgcore* include/pgasrt*.h include/xlupc*.h

%.o: %.cc
	$(CXX) $(CXXFLAGS) $(CXXFLAGS_SHARED) -c $< -o $@

# vim: ts=8:sw=8:noet
# DO NOT DELETE

common/x10rt_cuda.o: include/x10rt_types.h common/x10rt_internal.h
common/x10rt_cuda.o: include/x10rt_cuda.h
common/x10rt_front.o: include/x10rt_front.h include/x10rt_types.h
common/x10rt_front.o: include/x10rt_logical.h
common/x10rt_logical.o: include/x10rt_logical.h include/x10rt_types.h
common/x10rt_logical.o: include/x10rt_net.h include/x10rt_cuda.h
common/x10rt_logical.o: common/x10rt_internal.h
jni/jni_x10rt.o: include/x10rt_front.h include/x10rt_types.h
jni/jni_message.o: include/x10rt_front.h include/x10rt_types.h
mpi/x10rt_mpi.o: include/x10rt_net.h include/x10rt_types.h
standalone/x10rt_standalone.o: include/x10rt_net.h include/x10rt_types.h
test/x10rt_basic.o: include/x10rt_front.h include/x10rt_types.h
test/x10rt_gups.o: include/x10rt_front.h include/x10rt_types.h
test/x10rt_topology.o: include/x10rt_front.h include/x10rt_types.h
