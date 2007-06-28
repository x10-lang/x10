#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.3 2007-06-28 13:42:53 ganeshvb Exp $
# This file is part of X10 Runtime System.
#
# Makefile for RandomAccess Benchmarks (C++ Version).
#

.SUFFIXES: .cc .cpp .cxx .C .c .h .o .exe

include ../make.def

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

