#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.1 2007-08-02 12:59:17 srkodali Exp $
# This file is part of X10 Applications.
#

## Makefile for LAPI GUPS.

include ../config/make.defs
include ../config/make.rules

TARGET = gups
SRCS = gups.cc timers.cc
OBJS = gups.o timers.o

all: $(TARGET)

$(TARGET): $(OBJS)
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(LINKFLAGS) $(OBJS) -o $@

clean:
	$(RM) $(TARGET) $(OBJS)
