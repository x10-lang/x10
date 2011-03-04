# A top-level Makefile to compile all X10 sources

# <BEGIN> user settings #

export X10CXX?=x10c++


#leave blank by default, X10 automatically chooses a transport for you. To
#force a particular implementation, please uncheck one of the other options.
export X10RTTRANSPORT	?= 
#export X10RTTRANSPORT	?= -x10rt mpi
#export X10RTTRANSPORT	?= -x10rt standalone
#export X10RTTRANSPORT	?= -x10rt pgas_lapi
#export X10RTTRANSPORT	?= -x10rt pgas_bgp
#export X10RTTRANSPORT	?= -x10rt pgas_sockets

export X10CXXFLAGS 	?= -report postcompile=5 -VERBOSE_CALLS

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
X10CXXFLAGS=-report \
            postcompile=5 \
            -STATIC_CALLS \
            $(X10RT_TRANSPORT) \
            -MAIN_CLASS=UTS \
            -v \
            -NO_CHECKS \
            -O  

HEADERS=sha_endian.h \
        types.h \
        sha1.h 

SOURCES=sha1.c

OBJS=$(SOURCES:.c=.o)

X10_STRUCTS=UTS__TreeNode.x10 \
            UTS__Constants.x10 \
            Counter__ValCounter.x10 \
            Counter__Stat.x10 \
            Counter__Event.x10 \
            Counter__LifeGraph.x10
X10_STRUCTS_CC=$(X10_STRUCTS:.x10=.cc)
X10_STRUCTS_H=$(X10_STRUCTS:.x10=.h)
X10_STRUCTS_STRUCTS_H=$(X10_STRUCTS:.x10=.struct_h)

X10_CLASSES=UTS.x10 \
            UTS__SeqUTS.x10 \
            ParUTS.x10 \
            Counter.x10 \
            Counter__ConstSeqAccessContainer.x10 \
            NetworkGenerator.x10 \
            TreeExpander.x10 \
            PAdicNumber.x10 \
            ParUTS__FixedSizeStack.x10 \
            Deque.x10 \
            Deque__EmptyDequeError.x10
X10_CLASSES_CC=$(X10_CLASSES:.x10=.cc)
X10_CLASSES_H=$(X10_CLASSES:.x10=.h)

TARGET=UTS

all: ${TARGET}

UTS: UTS.x10 sha1_rand.hpp sha1.c
	${X10CXX} ${X10CXXFLAGS} $< -o $@ 

run-UTS: UTS
	mpirun -np 4 -hostfile host.list ./UTS -r 42 -b 2000 -m 8 -q 0.124975

test: Sha1Rand.cpp libsha1.a
	${CXX} ${CXXFLAGS} -DSHA1_TEST $< -o $@ -L. -lsha1

clean:
	rm -rf core*.* ${OBJS} ${TARGET} *.dSYM *.inc 
	rm -rf ${X10_CLASSES_CC} ${X10_CLASSES_H}
	rm -rf ${X10_STRUCTS_CC} ${X10_STRUCTS_H} ${X10_STRUCTS_STRUCTS_H}
