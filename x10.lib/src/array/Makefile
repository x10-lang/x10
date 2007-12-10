#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.12 2007-12-10 12:12:06 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Makefile for X10Lib src/array subdir.

TOP = ../..
include $(TOP)/config/Make.AIX

LINKFLAGS += -r -b64
ARRAY_A = array.a

SRCS =  array.cc array_copy.cc array_table.cc
OBJS = array.o array_copy.o array_table.o
HEADERS = array.h region.h point.h dist.h array_table.h

all: $(ARRAY_A)

$(ARRAY_A): $(OBJS)
	$(RM) $@
	$(LD) $(LINKFLAGS) -o $@ $(OBJS)

clean:
	$(RM) $(ARRAY_A) $(OBJS) *.log

install:
	@echo "Nothing to install in \"array\" subdir."
	
.cc.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@

.C.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@

.cxx.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@

.cpp.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@
