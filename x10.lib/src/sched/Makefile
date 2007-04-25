#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.1.1.1 2007-04-25 09:57:46 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Makefile for X10Lib src/sched subdir.

.SUFFIXES: .cc .o .h

MAKE = make
CXX = mpCC_r -q64 -qarch=pwr5
DEFS = -DDEBUG
CXXFLAGS = -qtune=pwr5 -O3 -qhot
INCLUDES = -I. -I.. -I../../include
LINKFLAGS = -r -b64
RM = rm -f
LD = ld
AR = ar
ARFLAGS = -v -r -X64
RANLIB = ranlib -X64

SCHED_A = sched.a

SRCS = sched.cc
OBJS = sched.o
HEADERS = sched.h

all: $(SCHED_A)

$(SCHED_A): $(OBJS)
	$(RM) $@
	$(LD) $(LINKFLAGS) -o$@ $(OBJS)

clean:
	$(RM) $(SCHED_A) $(OBJS) *.log

install:
	@echo "Nothing to install in \"sched\" subdir."
	
.cc.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@
