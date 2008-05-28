#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.5 2008-05-28 13:56:48 ganeshvb Exp $
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
	place.cc\
       queue.cc\
       refs.cc\
	remote_ref.cc


OBJS = array_copy.o\
	async.o \
       finish.o \
	place.o\
       init.o \
       queue.o\
       refs.o\
	remote_ref.o

HEADERS = x10_acts.h \
          x10_types.h

all: $(LIBTARGET)

$(LIBTARGET): $(OBJS) 
	$(RM) $@
	$(AR) $(ARFLAGS) $@ $(OBJS) 
	$(RANLIB) $@

clean:
	$(RM) $(LIBTARGET) $(OBJS) *.log
