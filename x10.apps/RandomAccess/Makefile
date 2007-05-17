#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.5 2007-05-17 09:38:35 ganeshvb Exp $
# This file is part of X10 Runtime System.
#
# Makefile for RandomAccess Benchmarks (C++ Version).
#

.SUFFIXES: .cc .cpp .cxx .C .c .h .o .exe

LIBNAME = x10
LIBTARGET = lib$(LIBNAME).
CXX = mpCC_r 
CC = mpcc_r 
DEFS = -DVERIFY -DNDEBUG -DFUNCTOR=${FUNCTOR}  -DVARARGS=${VARARGS}  #-DWARN 
CXXFLAGS = -q64 -qarch=pwr5 -w -qinline -qtune=pwr5 -O3 -qhot
CCFLAGS = -q64 -qarch=pwr5 -qtune=pwr5 -O3 -qhot
INCLUDES = -I. -I/u/gbikshan/Work/x10.lib/include -I/u/gbikshan/Work/x10.backend/x10lang
LIBDIRS = 
LIBS = -L/u/gbikshan/Work/x10.lib/lib -l$(LIBNAME) -L/u/gbikshan/Work/x10.backend/x10lang -lx10lang
LINKFLAGS =
RM = rm -f
RMDIR = rm -r -f 

TARGET = RandomAccess_spmd.exe \
         gups.exe

SRCS = RandomAccess_spmd.cc \
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

