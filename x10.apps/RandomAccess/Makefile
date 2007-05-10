#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.3 2007-05-10 11:54:42 ganeshvb Exp $
# This file is part of X10 Runtime System.
#
# Makefile for RandomAccess Benchmarks (C++ Version).
#

.SUFFIXES: .cc .cpp .cxx .C .c .h .o .exe

LIBNAME = x10
LIBTARGET = lib$(LIBNAME).a
CXX = mpCC_r -q64 -qarch=pwr5 -DVERIFY
CC = mpcc_r -q64 -qarch=pwr5
DEFS = -DWARN #-DNDEBUG #-DWARN
CXXFLAGS = -qtune=pwr5 -O3 -qhot
CCFLAGS = -qtune=pwr5 -O3 -qhot
INCLUDES = -I. -I/u/gbikshan/Work/x10.lib/include
LIBDIRS = -L/u/gbikshan/Work/x10.lib/lib
LIBS = -l$(LIBNAME)
LINKFLAGS =
RM = rm -f
RMDIR = rm -r -f 

TARGET = RandomAccess_spmd.exe \
         RandomAccess_forkjoin.exe 

SRCS = RandomAccess_spmd.cc \
       RandomAccess_forkjoin.cc

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

