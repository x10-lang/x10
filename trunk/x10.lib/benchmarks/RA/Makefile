#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.1 2007-12-10 07:58:00 ganeshvb Exp $
# This file is part of X10 Runtime System.
#
# Makefile for RandomAccess Benchmarks (C++ Version).
#

.SUFFIXES: .cc .cpp .cxx .C .c .h .o .exe

include make.def

RM = rm -f

RMDIR = rm -r -f 

TARGET = RandomAccess

SRCS = RandomAccess.cc 

INCLS = RandomAccess.h 

all: $(TARGET)

install:
	@echo "Nothing to \"install\" in this subdir."

clean:
	$(RM) $(TARGET) $(OBJS) *.log *.out
	$(RMDIR) core*

 
$(TARGET): $(INCLS) $(SRCS) 
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES)  $(SRCS) -o $@ $(LIBDIRS) $(LIBS)

