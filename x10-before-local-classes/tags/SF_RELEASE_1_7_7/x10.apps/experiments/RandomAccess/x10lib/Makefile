#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.2 2007-08-02 13:02:18 srkodali Exp $
# This file is part of X10 Applications.
#
# Makefile for RandomAccess Benchmark (X10Lib Version).
#

include ../config/make.defs
include ../config/make.rules

TARGET = RandomAccess_spmd 
SRCS = RandomAccess_spmd.cc timers.cc 
OBJS = RandomAccess_spmd.o timers.o
HEADERS = RandomAccess_spmd.h 

all: $(TARGET)

$(TARGET): $(OBJS)
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(LINKFLAGS) $(OBJS) -o $@ $(LIBDIRS) $(LIBS)

clean:
	$(RM) $(TARGET) $(OBJS)
