#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.1 2007-06-28 09:55:00 ganeshvb Exp $
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
CXXFLAGS = -g #-q64  -O3 -qinline -qarch=pwr5  -qtune=pwr5 -qhot
CCFLAGS = -q64 -qarch=pwr5 -qtune=pwr5 -O3 -qhot
INCLUDES = -I. -I../ -I/home/bikshand/x10/x10.lib/include -I/home/bikshand/x10/x10.backend/x10lang 
LIBDIRS = 
LIBS = -L/home/bikshand/x10/x10.lib/lib -l$(LIBNAME) -L/home/bikshand/x10/x10.backend/x10lang -lx10lang 
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

gups.exe: gups.cc
	$(RM) gups.exe
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) $< -o gups.exe 
 
.cc.exe: $(INCLS) $(SRCS) 
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES)  $< -o $@ $(LIBDIRS) $(LIBS)

