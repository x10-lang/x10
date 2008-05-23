#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.4 2008-05-23 12:30:04 ganeshvb Exp $
# This file is part of X10 Runtime System.
#

## Makefile for X10Lib src subdir.

TOP = ..
include $(TOP)/config/Make.LINUX

LIBNAME = x10
X10LIB_MAJOR = 0
X10LIB_MINOR = 6

# Build by default static library
LIBTARGET = lib$(LIBNAME).a
ARRAY_A = $(SUBDIR1)/array.a

SRCS = array_copy.cc\
	async.cc\
       finish.cc\
       init.cc \
       queue.cc\
       refs.cc


OBJS = array_copy.o\
	async.o \
       finish.o \
       init.o \
       queue.o\
       refs.o

HEADERS = x10_acts.h \
          x10_types.h

all: $(LIBTARGET)

$(LIBTARGET): $(OBJS) 
	$(RM) $@
	$(AR) $(ARFLAGS) $@ $(OBJS) 
	$(RANLIB) $@

clean:
	$(RM) $(LIBTARGET) $(OBJS) *.log
