#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.25 2007-12-14 13:39:35 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Makefile for X10Lib src/sched subdir.

TOP = ../..
include $(TOP)/config/Make.AIX

CXX = xlC_r -q64 -qarch=$(ARCH)
CXXFLAGS += -qrtti -ma
#LINKFLAGS += -r -b64
#INCLUDES += -I ../
SCHED_A = libsched.a #sched.a

# Define MEM_DEBUG=1 to print memory debug information.
# As of now, this prints constructor and destructor invocations
# for dynamically allocated objects.

#DEFS  += -DMEM_DEBUG=1

SRCS = ActiveWorkerCount.cc \
	Cache.cc \
	Closure.cc \
	Frame.cc \
	Job.cc \
	Lock.cc \
	Pool.cc \
	StealAbort.cc \
	Worker.cc

OBJS = ActiveWorkerCount.o \
	Cache.o \
	Closure.o \
	Frame.o \
	Job.o \
	Lock.o \
	Pool.o \
	StealAbort.o \
	Worker.o

HEADERS = ActiveWorkerCount.h \
	Cache.h \
	Closure.h \
	Frame.h \
	Job.h \
	Lock.h \
	Pool.h \
	StealAbort.h \
	Worker.h

all: $(SCHED_A)

$(SCHED_A): $(OBJS)
	$(RM) $@
	$(AR) $(ARFLAGS) $@ $(OBJS) $(SCHED_A)
	$(RANLIB) $@

clean:
	$(RM) $(SCHED_A) $(OBJS) *.log
