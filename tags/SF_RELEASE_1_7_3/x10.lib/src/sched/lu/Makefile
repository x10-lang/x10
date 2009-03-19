#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.1 2007-08-05 02:04:22 sriramk Exp $
# This file is part of X10 Runtime System.
#

## Makefile for X10Lib src/sched subdir.

.SUFFIXES: .cc .cxx .cpp .C .o .h

#Define MEM_DEBUG=1 to print memory debug information. As of now, this
#prints #constructor and #destructor invocations for dynamically
#allocated objects.   

#DEFS = -DMEM_DEBUG=1

MAKE = make
#CXX = mpCC_r -q64 -qarch=pwr5
CXX = xlC_r
#DEFS += -DNDEBUG
CXXFLAGS = -g -O3 -qrtti -ma -q64 #-ma -qarch=pwr5 -qinline  #-qmaxmem=-1 #-qunwind #-ma -qhot #-Q -qinline #-g #-qtune=pwr5 -O3 -qhot
#-pg -pthread 

INCLUDES = -I../../include -I.. -I.
LINKFLAGS = -r -b64
RM = rm -f
LD = ld
AR = ar
ARFLAGS = -v -r -X64
RANLIB = ranlib -X64
LIBS = -lpthread

SCHED_A = ../sched.a

#SRCS = sched.cc
#OBJS = sched.o
#HEADERS = sched.h
SRCS = ../ActiveWorkerCount.cc ../Cache.cc ../Closure.cc ../Frame.cc ../Job.cc ../Lock.cc ../Pool.cc ../StealAbort.cc ../Worker.cc
OBJS =  $(SRCS:.cc=.o)
#HEADERS = ActiveWorkerCount.h Cache.h Closure.h Frame.h Job.h Lock.h Pool.h StealAbort.h Worker.h

LU_SRCS = Schur.cc LowerSolve.cc AuxLowerSolve.cc UpperSolve.cc AuxUpperSolve.cc base.cc Lu.cc main.cc

LU_OBJS = $(LU_SRCS:.cc=.o)


default: lu.x

$(SCHED_A): $(SRCS)
	cd ..; make 

clean:
	$(RM) *.o *.x *.log

install:
	@echo "Nothing to install in \"sched/lu\" subdir."

.cc.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@

.cxx.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@

.cpp.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@

.C.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@


%.x: %.cc $(SCHED_A)
	$(CXX) $(CXXFLAGS) $(LIBS) $(DEFS) $(INCLUDES) $^ -o $@

lu.x:  $(LU_OBJS) $(SCHED_A)
	$(CXX) $(CXXFLAGS) $(LIBS) $(DEFS) $(INCLUDES)  $^  -o $@

