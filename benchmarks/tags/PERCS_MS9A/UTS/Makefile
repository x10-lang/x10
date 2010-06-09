# A top-level Makefile to compile all X10 sources

# <BEGIN> user settings #

export X10CXX 		?= ~/x10-trunk/x10.dist/bin/x10c++

#leave blank by default, let x10c++ choose a sensible default
export X10RTTRANSPORT	?= 

export X10CXXFLAGS 	?= -report postcompile=5 

# <END> user settings #

ifeq ($(shell uname -s),AIX)
   include arch/aix.mk
endif
ifeq ($(shell uname -s),Linux)
   include arch/linux.mk
endif
ifeq ($(shell uname -s),Darwin)
   include arch/darwin.mk
endif
ifeq ($(firstword $(subst _, ,$(shell uname -s))),CYGWIN)
   include arch/cygwin.mk
endif
ifeq ($(BGP_CROSS_COMPILE), 1)
   include arch/bgp.mk
endif

X10RUN?=runx10
X10CXXFLAGS=-report postcompile=5 $(X10RT_TRANSPORT)

HEADERS=sha_endian.h \
        types.h \
        sha1.h 

SOURCES=sha1.c

OBJS=$(SOURCES:.c=.o)

TARGET=libsha1.a

all: UTS

libsha1.a: ${SOURCES} ${HEADERS}
	${CXX} -c ${CXXFLAGS} ${SOURCES}
	ar rcs $@ ${OBJS}

uts: uts.cpp sha1_rand.hpp libsha1.a
	${CXX} ${CXXFLAGS} $< -o $@ -L. -lsha1

UTS: UTS.x10 sha1_rand.hpp sha1.c
	${X10CXX} -v -NO_CHECKS -O  ${X10CXXFLAGS} ${X10RTTRANSPORT} $< -o $@ 

run-UTS: UTS
	mpirun -np 4 -hostfile host.list ./UTS -r 42 -b 2000 -m 8 -q 0.124975
	#${X10RUN} gdb ./UTS

test: Sha1Rand.cpp libsha1.a
	${CXX} ${CXXFLAGS} -DSHA1_TEST $< -o $@ -L. -lsha1

clean:
	rm -rf core*.* ${OBJS} ${TARGET} test
	rm -rf *.dSYM *.inc UTS UTS.h UTS.cc
