#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.11 2007-06-25 16:31:38 ganeshvb Exp $
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
CXXFLAGS = -O3 -qinline -q64 -qarch=pwr5 -w -qtune=pwr5 -qhot 
CCFLAGS = -q64 -qarch=pwr5 -qtune=pwr5 -O3 -qhot
INCLUDES = -I. -I/u/gbikshan/Work4/x10.lib/include -I/u/gbikshan/Work4/x10.backend/x10lang
LIBDIRS = 
LIBS = -L/u/gbikshan/Work4/x10.lib/lib -l$(LIBNAME) -L/u/gbikshan/Work4/x10.backend/x10lang -lx10lang
LINKFLAGS =
RM = rm -f
RMDIR = rm -r -f 

TARGET = RandomAccess_spmd.exe \
         gups.exe \
         RandomAccess_spmd.2.exe \
#         RandomAccess_func.exe\

SRCS = RandomAccess_spmd.cc \
        gups.cc \
       RandomAccess_spmd.2.cc \
#       RandomAccess_func.cc \

INCLS = RandomAccess_spmd.h \
        RandomAccess_func.h 

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

