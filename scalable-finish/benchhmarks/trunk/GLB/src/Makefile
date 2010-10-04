# A top-level Makefile to compile all X10 sources

# <BEGIN> user settings #

export X10CXX 		?= ~/x10-opt/x10.dist/bin/x10c++

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
X10CXXFLAGS=-report postcompile=5 -VERBOSE_CALLS $(X10RT_TRANSPORT)

HEADERS=
        
SOURCES=sha1.c

OBJS=$(SOURCES:.c=.o)

X10_STRUCTS= Counter__ValCounter.x10 \
            Counter__Stat.x10 
X10_STRUCTS_CC=$(X10_STRUCTS:.x10=.cc)
X10_STRUCTS_H=$(X10_STRUCTS:.x10=.h)
X10_STRUCTS_STRUCTS_H=$(X10_STRUCTS:.x10=.struct_h)

X10_CLASSES=GlobalRunner.x10 \
            GlobalRunner__FixedSizeStack.x10 \
            GlobalRunner__LocalRunner.x10 \
            Counter.x10 \
            SeqRunner.x10 \
            NetworkGenerator.x10 \
            PAdicNumber.x10 \
            Event.x10 \
            TaskFrame.x10 
X10_CLASSES_CC=$(X10_CLASSES:.x10=.cc)
X10_CLASSES_H=$(X10_CLASSES:.x10=.h)

TARGET= GlobalRunner

all: ${TARGET}

GlobalRunner: GlobalRunner.x10 TaskFrame.x10  Counter.x10 SeqRunner.x10 NetworkGenerator.x10 PAdicNumber.x10 Event.x10 
	${X10CXX}  -v -NO_CHECKS -O  ${X10CXXFLAGS} ${X10RTTRANSPORT} $< -o $@ 


test: Sha1Rand.cpp libsha1.a
	${CXX} ${CXXFLAGS} -DSHA1_TEST $< -o $@ -L. -lsha1

clean:
	rm -rf core*.* ${OBJS} ${TARGET} *.dSYM *.inc 
	rm -rf ${X10_CLASSES_CC} ${X10_CLASSES_H}
	rm -rf ${X10_STRUCTS_CC} ${X10_STRUCTS_H} ${X10_STRUCTS_STRUCTS_H}
