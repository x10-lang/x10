CXX             ?= g++
MPICXX          ?= mpicxx
CXXFLAGS        ?= -g -O3 -ansi -pedantic -Wall -Wextra -Wno-long-long -Wno-unused
AR              ?= ar
CP              ?= cp
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

include pgas/pgas.mk

include standalone/standalone.mk

install: $(LIBS) $(PROPERTIES)
	cp $(LIBS) $(X10_HOME)/x10.dist/lib
	cp $(PROPERTIES) $(X10_HOME)/x10.dist/etc
	cp include/*.h $(X10_HOME)/x10.dist/include
	-cp $(EXECUTABLES) $(X10_HOME)/x10.dist/bin

tests: $(TESTS)

all: $(TESTS) $(PROPERTIES) $(LIBS)

clean:
	$(RM) */*.o *.tgz lib/*.a bin/* etc/*.properties test/*.mpi test/*.standalone test/*.pgas_* */*~ *~ core* vgcore* include/pgasrt*.h include/xlupc*.h

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
