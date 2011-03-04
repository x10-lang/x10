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
            -VERBOSE_CALLS \
            -O \
            -NO_CHECKS=true \
            $(X10RT_TRANSPORT) \
            -v \
            -sourcepath .

HEADERS=

SOURCES=examples/UTS/sha1.c

OBJS=$(SOURCES:.c=.o)

GLB_CLASSES=global/lb/GlobalRunner.x10 \
            global/lb/Counter.x10 \
            global/lb/SeqRunner.x10 \
            global/lb/Runner.x10 \
            global/lb/NetworkGenerator.x10 \
            global/lb/PAdicNumber.x10 \
            global/lb/Event.x10 \
            global/lb/TaskFrame.x10 
GLB_CLASSES_CC=$(GLB_CLASSES:.x10=.cc)
GLB_CLASSES_H=$(GLB_CLASSES:.x10=.h)

GLB_STRUCTS=global/lb/Counter__Stat.x10
GLB_STRUCTS_CC=$(GLB_STRUCTS:.x10=.cc)
GLB_STRUCTS_H=$(GLB_STRUCTS:.x10=.h)
GLB_STRUCTS_STRUCTS_H=$(GLB_STRUCTS:.x10=.struct_h)

OTHER_CLASSES=global/lb/GlobalRunner__FixedSizeStack.x10\
	       global/lb/GlobalRunner__LocalRunner.x10
OTHER_CLASSES_CC=$(OTHER_CLASSES:.x10=.cc)
OTHER_CLASSES_H=$(OTHER_CLASSES:.x10=.h)

UTS_CLASSES=examples/UTS/UTS.x10\
	    examples/UTS/Geometric.x10\
	    examples/UTS/Binomial.x10\
	    examples/UTS/TreeNode.x10 

HEADERS=examples/UTS/sha_endian.h \
        examples/UTS/types.h \
        examples/UTS/sha1.h 

SOURCES=examples/UTS/sha1.c

OBJS=$(SOURCES:.c=.o)

UTS_STRUCTS= examples/UTS/UTS__Constants.x10 \
            examples/UTS/TreeNode.x10\
            examples/UTS/UTS__Anonymous__0.x10 \

UTS_STRUCTS_CC=$(UTS_STRUCTS:.x10=.cc)
UTS_STRUCTS_H=$(UTS_STRUCTS:.x10=.h)
UTS_STRUCTS_STRUCTS_H=$(UTS_STRUCTS:.x10=.struct_h)
UTS_CLASSES_CC=$(UTS_CLASSES:.x10=.cc)
UTS_CLASSES_H=$(UTS_CLASSES:.x10=.h)

TARGET= UTS

all: ${TARGET}


UTS: ${UTS_CLASSES} ${GLB_CLASSES}
	${X10CXX} -v  ${X10CXXFLAGS} -MAIN_CLASS=examples.UTS.UTS ${X10RTTRANSPORT} $< -o $@ 

Fib: examples/Fib.x10 ${GLB_CLASSES}
	${X10CXX} -v  ${X10CXXFLAGS} -MAIN_CLASS=examples.Fib ${X10RTTRANSPORT} $< -o $@ 

Ones: examples/Ones.x10 ${GLB_CLASSES}
	${X10CXX} -v  ${X10CXXFLAGS} -MAIN_CLASS=examples.Ones ${X10RTTRANSPORT} $< -o $@ 

Counts: examples/Counts.x10 ${GLB_CLASSES}
	${X10CXX} -v  ${X10CXXFLAGS} -MAIN_CLASS=examples.Counts ${X10RTTRANSPORT} $< -o $@ 

test: examples/UTS/Sha1Rand.cc examples/UTS/libsha1.a
	${CXX} ${CXXFLAGS} -DSHA1_TEST $< -o $@ -L. -lsha1

clean:
	rm -rf core*.* ${OBJS} ${TARGET} *.dSYM *.inc 
	rm -rf ${UTS_CLASSES_CC} ${UTS_CLASSES_H}
	rm -rf ${UTS_STRUCTS_CC} ${UTS_STRUCTS_H} ${UTS_STRUCTS_STRUCTS_H}
	rm -rf ${GLB_CLASSES_CC} ${GLB_CLASSES_H}
	rm -rf ${GLB_STRUCTS_CC} ${GLB_STRUCTS_H} ${GLB_STRUCTS_STRUCTS_H}
	rm -rf ${OTHER_CLASSES_CC} ${OTHER_CLASSES_H}
