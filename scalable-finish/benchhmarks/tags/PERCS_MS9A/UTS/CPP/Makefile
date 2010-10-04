# <BEGIN> user settings #

X10_DIST?=${HOME}/x10-trunk/x10.dist
export X10CXX 		?= x10c++

#leave blank by default, let x10c++ choose a sensible default
#export X10RTTRANSPORT	?= -x10rt pgas_lapi

export X10CXXFLAGS 	?= -report postcompile=5 $(X10RTTRANSPORT)

# <END> user settings #

ifeq ($(shell uname -s),AIX)
   include ../arch/aix.mk
endif
ifeq ($(shell uname -s),Linux)
   include ../arch/linux.mk
endif
ifeq ($(shell uname -s),Darwin)
   include ../arch/darwin.mk
endif
ifeq ($(firstword $(subst _, ,$(shell uname -s))),CYGWIN)
   include ../arch/cygwin.mk
endif
ifeq ($(BGP_CROSS_COMPILE), 1)
   include ../arch/bgp.mk
endif

# Environment variables holding my stuff for now
X10_INCLUDE_DIR=${X10_DIST}/include
X10_LIB_DIR=${X10_DIST}/lib
X10_BIN_DIR=${X10_DIST}/bin

CXX = $(POSTCOMPILE_CXX)
CXXFLAGS = $(POSTCOMPILE_CXXFLAGS)
AR = $(POSTCOMPILE_AR)
ARFLAGS = $(POSTCOMPILE_ARFLAGS)

# The libraries that we want for the executable to be compiled
#X10RT_LIB=x10rt_pgas_sockets

ifeq ($(shell uname -s),AIX)
X10RT_LIB=x10rt_pgas_lapi
else
X10RT_LIB=x10rt_mpi
endif

# Runtime environment variables
TCSH=/bin/tcsh

# Things required to build libsha1
HEADERS=sha1_endian.h \
        sha1_types.h \
        sha1.h 

SOURCES=sha1.c

OBJS=$(SOURCES:.c=.o)

TARGETS=libsha1.a uts par_uts

# Actual rules
all: ${TARGETS}

uts: uts.cpp libsha1.a
	${CXX} ${CXXFLAGS} $< -o $@ -I${X10_INCLUDE_DIR} \
	${LDFLAGS} -L${X10_LIB_DIR} -l${X10RT_LIB} -L. -lsha1 

par_uts: par_uts.cpp libsha1.a
	${CXX} ${CXXFLAGS} $< -o $@ -I${X10_INCLUDE_DIR} -L${X10_LIB_DIR} \
  -l${X10RT_LIB} ${LDFLAGS} -L. -lsha1 

libsha1.a: ${SOURCES} ${HEADERS}
	${CXX} -c ${CXXFLAGS} ${SOURCES}
	$(AR) $(ARFLAGS) $@ ${OBJS}

ping_pong: ping_pong.cpp
	${CXX} $< -I${X10_INCLUDE_DIR} -L${X10_LIB_DIR} -l${X10RT_LIB} -o $@

clean:
	rm -rf *.o *.dSYM ping_pong ${TARGETS}
