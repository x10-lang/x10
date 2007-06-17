#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.8 2007-06-17 20:14:15 ganeshvb Exp $
# This file is part of X10 Runtime System.
#

## Makefile for X10Lib src subdir.

.SUFFIXES: .cc .cc .cxx .C .o .h .exe

MAKE = make
CXX = mpCC_r -O3 -qhot -w 
CC = mpcc_r 
DEFS = -DDEBUG -DWARN
CXXFLAGS = -q64 -qarch=pwr5 -qtune=pwr5 
INCLUDES = -I. -I../../include -I ~root/lapi/include
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

CSRCS = Test_async_c.c \
        Test_async_agg_c.c \

EXE = Test_array.exe\
      Test_array_async.exe\
      Test_async.exe\
      Test_async_agg.exe\
      Test_dist.exe\
      Test_point.exe\
      Test_region.exe\
      Test_tiled_region.exe\
      Test_async_c.exe\
      Test_async_agg_c.exe
OBJS = 
HEADERS = 

all: $(EXE)

clean:
	$(RM) $(EXE) *.log core*
	$(RMDIR) core*
	
.cc.exe: $(SRCS)
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) $< -o $@ $(LINKFLAGS)

.c.exe: $(CSRCS)
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) $< -o $@ $(LINKFLAGS)
