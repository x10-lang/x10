#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.3 2007-05-10 05:42:38 srkodali Exp $
# This file is part of X10 Runtime System.
#
# Makefile for RandomAccess Benchmarks (C++ Version).
#

.SUFFIXES: .cc .cpp .cxx .C .c .h .o

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

TARGET = gups
SRCS = gups.cc
OBJS = gups.o

all: $(TARGET)

$(TARGET): $(OBJS)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS) -o $@ $(LIBDIRS) $(LIBS)

install:
	@echo "Nothing to \"install\" in this subdir."

clean:
	$(RM) $(TARGET) $(OBJS) *.log *.out

.cc.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES)  -c $< -o $@

.cxx.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES)  -c $< -o $@

.cpp.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES)  -c $< -o $@

.C.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES)  -c $< -o $@

.c.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) -qsourcetype=c++ $(DEFS) $(INCLUDES) -c $< -o $@
