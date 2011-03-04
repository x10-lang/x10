#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.1 2007-06-08 13:51:45 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Makefile for Testing X10Lib's Scheduler Implementation.

.SUFFIXES: .cc .cxx .cpp .C .c .o .h

MAKE = make
CXX = mpCC_r -q64 -qarch=pwr5
CXXFLAGS = -qtune=pwr5 -O3 -qhot
CC = mpcc_r -q64 -qarch=pwr5
CCFLAGS = -qtune=pwr5 -O3 -qhot
DEFS = #-DDEBUG
INCLUDES = -I. -I../../include
LINKFLAGS =
LIBDIRS = -L../../lib
LIBS = -lx10
RM = rm -f

#TARGET1 = Sched_test
#SRCS1 = $(TARGET1).cc
#OBJS1 = $(TARGET1).o

TARGET = $(TARGET1)

all:: $(TARGETS)

$(TARGET1): $(OBJS1)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS1) -o $@ $(LIBDIRS) $(LIBS)

install:
	@echo "Nothing to \"install\" in this subdir."

clean:
	$(RM) $(TARGETS)

.cc.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@

.cxx.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@

.cpp.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@

.C.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@

.c.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) -qsourcetype=c++ $(DEFS) $(INCLUDES) -c $< -o $@
