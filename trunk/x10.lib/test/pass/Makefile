#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.4 2007-05-23 11:35:56 ganeshvb Exp $
# This file is part of X10 Runtime System.
#

## Makefile for X10Lib src subdir.

.SUFFIXES: .cc .cc .cxx .C .o .h .exe

MAKE = make
CXX = mpCC_r -q64 -qarch=pwr5 -DVARARGS=1 -DFUNCTOR=1
DEFS = -DDEBUG -DWARN
CXXFLAGS = -qtune=pwr5 -O3 -qhot -w 
INCLUDES = -I. -I../../include
LINKFLAGS = -L../../src -lx10
RM = rm  -f
RMDIR = rm -r -f
AR = ar
ARFLAGS = -v -r -X64
RANLIB = ranlib -X64
CP = cp
MKDIR = mkdir

LIBNAME = x10
MAJOR_VERSION = 0
MINOR_VERSION = 1

SRCS = Test_array.cc \
       Test_array_async.cc\
       Test_async.cc \
       Test_async_agg.cc\
       Test_dist.cc  \
       Test_point.cc \
       Test_region.cc \
       Test_tiled_region.cc

EXE = Test_array.exe\
      Test_array_async.exe\
      Test_async.exe\
      Test_async_agg.exe\
      Test_dist.exe\
      Test_point.exe\
      Test_region.exe\
      Test_tiled_region.exe
OBJS = 
HEADERS = 

all: $(EXE)

clean:
	$(RM) $(EXE) *.log core*
	$(RMDIR) core*
	
.cc.exe: $(SRCS)
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) $< -o $@ $(LINKFLAGS)


