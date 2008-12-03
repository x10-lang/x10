#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.1 2007-12-26 12:51:27 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Makefile for sample scheduler programs.

TOP = ../..
include $(TOP)/config/Make.AIX

CXX = xlC_r -q64 -qarch=$(ARCH)
CXXFLAGS += -qrtti -ma

LIBNAME = xws
LIBS = -l$(LIBNAME)

TARGET1 = Fib
SRCS1 = $(TARGET1).cc
OBJS1 = $(TARGET1).o

TARGET2 = SpanC
SRCS2 = $(TARGET2).cc
OBJS2 = $(TARGET2).o

TARGETS = $(TARGET1) $(TARGET2)

all: $(TARGETS)

$(TARGET1): $(OBJS1)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS1) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET2): $(OBJS2)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS2) -o $@ $(LIBDIRS) $(LIBS)

clean:
	$(RM) $(TARGETS) *.o
