#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.18 2008-06-05 11:03:24 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Makefile for X10Lib src/array subdir.

TOP = ../..
include $(TOP)/config/Make.$(shell uname -s | sed -e 's;_.*$$;;')

INCLUDES += -I ../
ARRAY_A = array.a

SRCS =  array.cc \
		array_copy.cc \
		array_table.cc 

OBJS = array.o \
		array_copy.o \
		array_table.o 

HEADERS = array_copy.h \
		array_table.h \
		dist.h \
		dist_array.h \
		iter.h \
		local_array.h \
		point.h \
		rectangular_region.h \
		region.h

all: $(ARRAY_A)

$(ARRAY_A): $(OBJS)
	$(RM) $@
	$(LD) $(LINKFLAGS) -o $@ $(OBJS)

clean:
	$(RM) $(ARRAY_A) $(OBJS) *.log
