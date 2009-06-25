#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.2 2007-08-02 13:01:03 srkodali Exp $
# This file is part of X10 Applications.
#
# Makefile for RandomAccess Benchmark (Generated Code).
#

include ../config/make.defs
include ../config/make.rules

CXXFLAGS += -g -qrtti=all

TARGET = RandomAccess_Dist 
SRCS = RandomAccess_Dist.cc 
OBJS = RandomAccess_Dist.o
HEADERS = RandomAccess_Dist.h 

all: $(TARGET)

$(TARGET): $(OBJS)
	$(RM) $@
	$(CXX) $(CXXFLGAS) $(LINKFLAGS) $(OBJS) -o $@ $(LIBDIRS) $(LIBS)

clean:
	$(RM) $(TARGET) $(OBJS)
