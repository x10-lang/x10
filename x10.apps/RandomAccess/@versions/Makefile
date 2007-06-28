#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.2 2007-06-28 11:25:33 ganeshvb Exp $
# This file is part of X10 Runtime System.
#
# Makefile for RandomAccess Benchmarks (C++ Version).
#

.SUFFIXES: .cc .cpp .cxx .C .c .h .o .exe


LIBNAME = x10
LIBTARGET = lib$(LIBNAME).
CXX = mpCC_r 
CC = mpcc_r 
DEFS =  -DNDEBUG #-DSTATICINIT #-DWARN 
CXXFLAGS = -q64  -O3 -qarch=pwr5  -qtune=pwr5 -qhot
CCFLAGS = -q64 -qarch=pwr5 -qtune=pwr5 -O3 -qhot
INCLUDES = -I. -I../ -I/u/gbikshan/tmp/x10.lib/include -I/u/gbikshan/tmp/x10.backend/x10lang 
LIBDIRS = 
LIBS = -L/u/gbikshan/tmp/x10.lib/lib -l$(LIBNAME) -L/u/gbikshan/tmp/x10.backend/x10lang -lx10lang 
LINKFLAGS =
RM = rm -f
RMDIR = rm -r -f 

TARGET = RandomAccess_spmd.v01.exe \
         RandomAccess_spmd.v02.exe \
         RandomAccess_spmd.v03.exe

SRCS = RandomAccess_spmd.v01.cc \
       RandomAccess_spmd.v02.cc\
       RandomAccess_spmd.v03.cc

INCLS = RandomAccess_spmd.h 

all: $(TARGET)

install:
	@echo "Nothing to \"install\" in this subdir."

clean:
	$(RM) $(TARGET) $(OBJS) *.log *.out
	$(RMDIR) core*

 
.cc.exe: $(INCLS) $(SRCS) 
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES)  $< -o $@ $(LIBDIRS) $(LIBS)

