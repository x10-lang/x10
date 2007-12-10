#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.14 2007-12-10 16:44:39 ganeshvb Exp $
# This file is part of X10 Runtime System.
#

## Makefile for X10Lib src/array subdir.

TOP = ../..
include $(TOP)/config/Make.AIX

LINKFLAGS += -r -b64
INCLUDES += -I ../
ARRAY_A = array.a

SRCS =  array.cc \
		array_copy.cc \
		array_table.cc \
		array_copy_switch.cc \
		gen_array_copy_switch.cc

OBJS = array.o \
		array_copy.o \
		array_table.o \
		array_copy_switch.o \
		gen_array_copy_switch.o

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
