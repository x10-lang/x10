# A top-level Makefile to compile all X10 sources

# <BEGIN> user settings #

ifeq ($(shell uname -s),Darwin)
  export X10CXX?=/Users/pkambadu/Software/src/x10/x10-trunk/x10.dist/bin/x10c++
else
  export X10CXX?=/gsa/yktgsa-h1/08/pkambadu/Software/src/x10/x10-trunk/x10.dist/bin/x10c++
endif

#leave blank by default, X10 automatically chooses a transport for you. To
#force a particular implementation, please uncheck one of the other options.
export X10RTTRANSPORT	?= 
#export X10RTTRANSPORT	?= -x10rt mpi
#export X10RTTRANSPORT ?= -x10rt standalone
#export X10RTTRANSPORT ?= -x10rt pgas_lapi
#export X10RTTRANSPORT ?= -x10rt pgas_bgp
#export X10RTTRANSPORT ?= -x10rt pgas_sockets

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
            -v \
            -NO_CHECKS \
            -O  

X10_STRUCTS=Rmat.x10 \
            NetReader.x10 
X10_STRUCTS_CC=$(X10_STRUCTS:.x10=.cc)
X10_STRUCTS_H=$(X10_STRUCTS:.x10=.h)
X10_STRUCTS_STRUCTS_H=$(X10_STRUCTS:.x10=.struct_h)

X10_CLASSES=Brandes.x10 \
            WeightedGraph.x10 \
            UnweightedGraph.x10 \
            FixedBinaryHeap.x10 \
            FixedRailStack.x10 \
            FixedRailQueue.x10 \
            AtomicDouble.x10 \
            LockedDouble.x10 \
            AbstractCSRGraph.x10 \
            AbstractWeightedCSRGraph.x10 \
            AbstractUnweightedCSRGraph.x10 \
            TestRandom.x10 
X10_CLASSES_CC=$(X10_CLASSES:.x10=.cc)
X10_CLASSES_H=$(X10_CLASSES:.x10=.h)

OTHER_CLASSES=
OTHER_CLASSES_CC=$(OTHER_CLASSES:.x10=.cc)
OTHER_CLASSES_H=$(OTHER_CLASSES:.x10=.h)

TARGET=brandes

all: ${TARGET}

brandes: ${X10_CLASSES} ${X10_STRUCTS}
	${X10CXX} ${X10CXXFLAGS} ${X10RTTRANSPORT} -MAIN_CLASS=Brandes $< -o $@ 

run-brandes: brandes
	mpirun -np 1 -hostfile host.list ./brandes

testrng: TestRandom.x10
	${X10CXX} ${X10CXXFLAGS} $< -o $@ 

run-rng: testrng
	mpirun -np 1 -hostfile host.list ./testrng

test-pq: FixedBinaryHeap.x10
	${X10CXX} ${X10CXXFLAGS} ${X10RTTRANSPORT} $< -o $@ 

test-st: FixedRailStack.x10
	${X10CXX} ${X10CXXFLAGS} ${X10RTTRANSPORT} $< -o $@ 

test-q: FixedRailQueue.x10
	${X10CXX} ${X10CXXFLAGS} ${X10RTTRANSPORT} $< -o $@ 

clean:
	rm -rf core*.* *.dSYM *.inc 
	rm -rf ${X10_CLASSES_CC} ${X10_CLASSES_H} ${TARGET}
	rm -rf ${X10_STRUCTS_CC} ${X10_STRUCTS_H} ${X10_STRUCTS_STRUCTS_H}
	rm -rf ${OTHER_STRUCTS_CC} ${OTHER_STRUCTS_H} ${OTHER_STRUCTS_STRUCTS_H}
	rm -rf ${OTHER_CLASSES_CC} ${OTHER_CLASSES_H} ${TARGET} *.out *.log
	rm -rf test-pq test-st
