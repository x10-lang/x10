CXX             ?= g++
MPICXX          ?= mpicxx
CXXFLAGS        ?= -g -O3 -ansi -pedantic -Wall -Wextra -Wno-long-long -Wno-unused
AR              ?= ar
CP              ?= cp -f
WGET            ?= wget
TAR             ?= tar
GZIP            ?= gzip

# these are used only for building tests
CXXFLAGS        += -Iinclude -Icommon
LDFLAGS         += -Llib -lpthread

BASE_TESTS      = test/x10rt_basic test/x10rt_gups test/x10rt_topology

EXECUTABLES     =

X10_HOME        = ../../..

COMMON_OBJS     = common/x10rt_front.o common/x10rt_logical.o common/x10rt_cuda.o

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
	$(CP) $(LIBS) $(X10_HOME)/x10.dist/lib
	$(CP) $(PROPERTIES) $(X10_HOME)/x10.dist/etc
	$(CP) include/*.h $(X10_HOME)/x10.dist/include
	(test -n "$(EXECUTABLES)" && $(CP) $(EXECUTABLES) $(X10_HOME)/x10.dist/bin) || true

tests: $(TESTS)

all: $(TESTS) $(PROPERTIES) $(LIBS) $(TGZ)

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
	$(RM) -r */*.o *.tgz lib/*.a bin/* etc/*.properties test/*.mpi test/*.standalone test/*.pgas_* */*~ *~ core* vgcore* include/pgasrt*.h include/xlupc*.h

# vim: ts=8:sw=8:noet
# DO NOT DELETE

common/x10rt_cuda.o: include/x10rt_types.h common/x10rt_internal.h
common/x10rt_cuda.o: include/x10rt_cuda.h
common/x10rt_front.o: include/x10rt_front.h include/x10rt_types.h
common/x10rt_front.o: include/x10rt_logical.h
common/x10rt_logical.o: include/x10rt_logical.h include/x10rt_types.h
common/x10rt_logical.o: include/x10rt_net.h include/x10rt_cuda.h
common/x10rt_logical.o: common/x10rt_internal.h
mpi/x10rt_mpi.o: include/x10rt_net.h include/x10rt_types.h
standalone/x10rt_standalone.o: include/x10rt_net.h include/x10rt_types.h
test/x10rt_basic.o: include/x10rt_front.h include/x10rt_types.h
test/x10rt_gups.o: include/x10rt_front.h include/x10rt_types.h
test/x10rt_topology.o: include/x10rt_front.h include/x10rt_types.h
