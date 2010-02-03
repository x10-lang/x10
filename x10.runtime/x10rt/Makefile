include ../Make.rules

CP              ?= cp -f
WGET            ?= wget
TAR             ?= tar
GZIP            ?= gzip
LIBTOOL		?= libtool

override CXXFLAGS += -Iinclude -Icommon

# these are used only for building tests
override LDFLAGS         += -Llib -lpthread

BASE_TESTS      = test/x10rt_basic test/x10rt_gups test/x10rt_topology

EXECUTABLES     =

X10_HOME        = ${CURDIR}/../..

COMMON_OBJS     = common/x10rt_front.o common/x10rt_logical.o common/x10rt_cuda.o
COMMON_LT_OBJS  = $(COMMON_OBJS:.o=.lo)

ifdef ENABLE_X10RT_CUDA
	CXXFLAGS += -DENABLE_CUDA -isystem/usr/local/cuda/include
	CUDA_LDFLAGS = -L/usr/local/cuda/lib
	CUDA_LDLIBS = -lcuda
	LDFLAGS += $(CUDA_LDFLAGS) $(CUDA_LDLIBS)
endif

default: all

%.lo: %.cc
	$(LIBTOOL) --mode=compile --tag=CXX $(CXX) $(CXXFLAGS) -c $< -o $@

ifdef ENABLE_X10RT_MPI
  include mpi/mpi.mk
endif

ifndef DISABLE_X10RT_PGAS
  include pgas/pgas.mk
endif

include standalone/standalone.mk

install: $(LIBS) $(LT_LIBS) $(PROPERTIES)
	(test -n "$(LIBS)" && $(CP) $(LIBS) $(X10_HOME)/x10.dist/lib) || true
	(test -n "$(LT_LIBS)" && $(LIBTOOL) --mode=install $(CP) $(LT_LIBS) $(X10_HOME)/x10.dist/lib) || true
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
	$(LIBTOOL) --mode=clean $(RM) */*.lo lib/*.la 
	$(RM) -r */*.o lib/*.a bin/* etc/*.properties test/*.mpi test/*.standalone test/*.pgas_* */*~ *~ core* vgcore* include/pgasrt*.h include/xlupc*.h

# vim: ts=8:sw=8:noet
# DO NOT DELETE

common/x10rt_cuda.o: include/x10rt_types.h common/x10rt_internal.h
common/x10rt_cuda.lo: include/x10rt_types.h common/x10rt_internal.h
common/x10rt_cuda.o: include/x10rt_cuda.h
common/x10rt_cuda.lo: include/x10rt_cuda.h
common/x10rt_front.o: include/x10rt_front.h include/x10rt_types.h
common/x10rt_front.o: include/x10rt_logical.h
common/x10rt_front.lo: include/x10rt_front.h include/x10rt_types.h
common/x10rt_front.lo: include/x10rt_logical.h
common/x10rt_logical.o: include/x10rt_logical.h include/x10rt_types.h
common/x10rt_logical.o: include/x10rt_net.h include/x10rt_cuda.h
common/x10rt_logical.o: common/x10rt_internal.h
common/x10rt_logical.lo: include/x10rt_logical.h include/x10rt_types.h
common/x10rt_logical.lo: include/x10rt_net.h include/x10rt_cuda.h
common/x10rt_logical.lo: common/x10rt_internal.h
mpi/x10rt_mpi.o: include/x10rt_net.h include/x10rt_types.h
mpi/x10rt_mpi.lo: include/x10rt_net.h include/x10rt_types.h
standalone/x10rt_standalone.o: include/x10rt_net.h include/x10rt_types.h
standalone/x10rt_standalone.lo: include/x10rt_net.h include/x10rt_types.h
test/x10rt_basic.o: include/x10rt_front.h include/x10rt_types.h
test/x10rt_basic.lo: include/x10rt_front.h include/x10rt_types.h
test/x10rt_gups.o: include/x10rt_front.h include/x10rt_types.h
test/x10rt_gups.lo: include/x10rt_front.h include/x10rt_types.h
test/x10rt_topology.o: include/x10rt_front.h include/x10rt_types.h
test/x10rt_topology.lo: include/x10rt_front.h include/x10rt_types.h
