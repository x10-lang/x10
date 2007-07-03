#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.1 2007-07-03 11:26:00 ganeshvb Exp $
# This file is part of X10 Runtime System.
#
# Makefile for RandomAccess Benchmarks (C++ Version).
#

.SUFFIXES: .cc .cpp .cxx .C .c .h .o .exe

include ../make.def

RM = rm -f
RMDIR = rm -r -f 

TARGET = RandomAccess_spmd 

SRCS = RandomAccess_spmd.cc 

INCLS = RandomAccess_spmd.h 

all: $(TARGET)

install:
	@echo "Nothing to \"install\" in this subdir."

clean:
	$(RM) $(TARGET) $(OBJS) *.log *.out
	$(RMDIR) core*

$(TARGET): $(INCLS) $(SRCS) 
	$(RM) $(TARGET)
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES)  $(SRCS) -o $(TARGET) $(LIBDIRS) $(LIBS)

