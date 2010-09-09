#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.24 2007-08-23 15:20:48 sriramk Exp $
# This file is part of X10 Runtime System.
#

## Makefile for X10Lib src/sched subdir.

.SUFFIXES: .cc .cxx .cpp .C .o .h

#Define MEM_DEBUG=1 to print memory debug information. As of now, this
#prints #constructor and #destructor invocations for dynamically
#allocated objects.   

#DEFS = -DMEM_DEBUG=1

MAKE = make

ifdef GCC
  CXX = g++
  CXXFLAGS =-g -O3 -fno-inline
  LINKFLAGS = -r
else
#  CXX = mpCC_r -q64 -qarch=pwr5
  CXX = xlC_r -qarch=pwr5 
#  DEFS += -DNDEBUG
  CXXFLAGS = -g -O2 -qrtti -ma -q64 #-ma -qarch=pwr5 -qinline  #-qmaxmem=-1 #-qunwind #-ma -qhot #-Q -qinline #-g #-qtune=pwr5 -O3 -qhot
#  -pg -pthread 
  LINKFLAGS = -r -b64
#  LINKFLAGS = -r
endif

INCLUDES = -I../../include

RM = rm -f
LD = ld
AR = ar
ARFLAGS = -v -r -X64
RANLIB = ranlib -X64
LIBS = -lpthread

SCHED_A = sched.a

#SRCS = sched.cc
#OBJS = sched.o
#HEADERS = sched.h
SRCS = ActiveWorkerCount.cc Cache.cc Closure.cc Frame.cc Job.cc Lock.cc Pool.cc StealAbort.cc Worker.cc
OBJS =  ActiveWorkerCount.o Cache.o Closure.o Frame.o Job.o Lock.o Pool.o StealAbort.o Worker.o
HEADERS = ActiveWorkerCount.h Cache.h Closure.h Frame.h Job.h Lock.h Pool.h StealAbort.h Worker.h

all: $(SCHED_A)

$(SCHED_A): $(OBJS)
	$(RM) $@
	$(LD) $(LINKFLAGS) -o$@ $(OBJS)

clean:
	$(RM) $(SCHED_A) $(OBJS) *.x *.log

install:
	@echo "Nothing to install in \"sched\" subdir."

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

Fib.x: Fib.cc $(SCHED_A)
	$(CXX) $(CXXFLAGS) $(LIBS) $(DEFS) $(INCLUDES) Fib.cc $(SCHED_A) -o Fib.x

FibStack.x: FibStack.cc $(SCHED_A)
	$(CXX) $(CXXFLAGS) $(LIBS) $(DEFS) $(INCLUDES) FibStack.cc $(SCHED_A) -o FibStack.x

NQueensC.x: NQueensC.cc $(SCHED_A)
	$(CXX) $(CXXFLAGS) $(LIBS) $(DEFS) $(INCLUDES) NQueensC.cc $(SCHED_A) -o NQueensC.x

NQueensCStack.x: NQueensCStack.cc $(SCHED_A)
	$(CXX) $(CXXFLAGS) $(LIBS) $(DEFS) $(INCLUDES) NQueensCStack.cc $(SCHED_A) -o NQueensCStack.x

Na.x: NQueensCStack_a.cc $(SCHED_A)
	$(CXX) $(CXXFLAGS) $(LIBS) $(DEFS) $(INCLUDES) NQueensCStack_a.cc $(SCHED_A) -o Na.x

SpanC.x: SpanC.cc $(SCHED_A)
	$(CXX) $(CXXFLAGS) $(LIBS) $(DEFS) $(INCLUDES) SpanC.cc $(SCHED_A) -o SpanC.x

SpanCStack.x: SpanCStack.cc $(SCHED_A)
	$(CXX) $(CXXFLAGS) $(LIBS) $(DEFS) $(INCLUDES) SpanCStack.cc $(SCHED_A) -o SpanCStack.x

SpanCArray.x: SpanCArray.cc $(SCHED_A)
	$(CXX) $(CXXFLAGS) $(LIBS) $(DEFS) $(INCLUDES) SpanCArray.cc $(SCHED_A) -o SpanCArray.x

lu.x:	lu-priority.cc
	xlC_r -g -O3 lu-priority.cc -lpthread -lblas -o lu.x
