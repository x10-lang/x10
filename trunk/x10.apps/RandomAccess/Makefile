#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.8 2007-05-28 12:46:20 ganeshvb Exp $
# This file is part of X10 Runtime System.
#
# Makefile for RandomAccess Benchmarks (C++ Version).
#

.SUFFIXES: .cc .cpp .cxx .C .c .h .o .exe


LIBNAME = x10
LIBTARGET = lib$(LIBNAME).
CXX = mpCC_r 
CC = mpcc_r 
DEFS =  -DNDEBUG #-DWARN 
CXXFLAGS = -q64 -qarch=pwr5 -w -qtune=pwr5 -O3  -qhot -g
CCFLAGS = -q64 -qarch=pwr5 -qtune=pwr5 -O3 -qhot
INCLUDES = -I. -I/u/gbikshan/Work2/x10.lib/include -I/u/gbikshan/Work/x10.backend/x10lang
LIBDIRS = 
LIBS = -L/u/gbikshan/Work2/x10.lib/lib -l$(LIBNAME) -L/u/gbikshan/Work/x10.backend/x10lang -lx10lang
LINKFLAGS =
RM = rm -f
RMDIR = rm -r -f 

TARGET = RandomAccess_spmd.exe \
         RandomAccess_func.exe\
         gups.exe

SRCS = RandomAccess_spmd.cc \
       RandomAccess_func.cc \
       gups.cc

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

