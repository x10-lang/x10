#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.3 2007-05-09 12:40:31 ganeshvb Exp $
# This file is part of X10 Runtime System.
#

## Makefile for X10Lib src subdir.

.SUFFIXES: .cc cc .cxx .C .o .h .exe

MAKE = make
CXX = mpCC_r -q64 -qarch=pwr5
DEFS = -DWARN
CXXFLAGS = -qtune=pwr5 -O3 -qhot -w 
INCLUDES = -I. -I../../include
LINKFLAGS = -L../../src -lx10
RM = rm -f
RMDIR = rm -r -f
AR = ar
ARFLAGS = -v -r -X64
RANLIB = ranlib -X64
CP = cp
MKDIR = mkdir

LIBNAME = x10
MAJOR_VERSION = 0
MINOR_VERSION = 1

SRCS = Test_point.cc  \
       Test_region.cc 

EXE = Test_point.exe\
      Test_region.exe

OBJS = 
HEADERS = 

all: $(EXE)

clean:
	$(RMDIR) core*
	$(RM) $(EXE) *.log 
	
.cc.exe: $(SRCS)
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) $< -o $@ $(LINKFLAGS)


