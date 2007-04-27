#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.3 2007-04-27 12:54:54 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Makefile for X10Lib src/array subdir.

.SUFFIXES: .cc .cxx .cpp .C .o .h

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

ARRAY_A = array.a

SRCS = array.cc
OBJS = array.o
HEADERS = array.h array.tcc

all: $(ARRAY_A)

$(ARRAY_A): $(OBJS)
	$(RM) $@
	$(LD) $(LINKFLAGS) -o$@ $(OBJS)

clean:
	$(RM) $(ARRAY_A) $(OBJS) *.log

install:
	@echo "Nothing to install in \"array\" subdir."
	
.cc.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@

.C.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@

.cxx.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@

.cpp.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@
