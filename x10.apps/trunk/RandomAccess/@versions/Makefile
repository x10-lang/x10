#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.4 2007-10-24 08:34:26 ganeshvb Exp $
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
         RandomAccess_hyper.exe\
         RandomAccess_hyper_ra.exe
 

SRCS = RandomAccess_spmd.v01.cc \
       RandomAccess_spmd.v02.cc\
       RandomAccess_hyper.cc\
       RandomAccess_hyper_ra.cc

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

