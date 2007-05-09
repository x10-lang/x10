#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.2 2007-05-09 12:40:29 ganeshvb Exp $
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
INCLUDES = -I. -I../../../include
LIBDIRS = -L../../../lib
LIBS = -l$(LIBNAME)
LINKFLAGS =
RM = rm -f

TARGET = RandomAccess_spmd.exe \
         RandomAccess.exe \
         gups.exe

SRCS = RandomAccess_spmd.cc \
       RandomAccess.cc\
       gups.cc

all: $(TARGET)

install:
	@echo "Nothing to \"install\" in this subdir."

clean:
	$(RM) $(TARGET) $(OBJS) *.log *.out

.cc.exe: $(SRCS) 
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES)  $< -o $@ $(LIBDIRS) $(LIBS)

