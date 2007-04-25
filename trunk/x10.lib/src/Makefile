#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.1.1.1 2007-04-25 09:57:46 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Makefile for X10Lib src subdir.

.SUFFIXES: .cc .o .h

MAKE = make
SUBDIR1 = array
SUBDIR2 = sched
SUBDIRS = $(SUBDIR1) $(SUBDIR2)
LIBDIR = ../lib
CXX = mpCC_r -q64 -qarch=pwr5
DEFS = -DDEBUG
CXXFLAGS = -qtune=pwr5 -O3 -qhot
INCLUDES = -I. -I../include
LINKFLAGS =
RM = rm -f
AR = ar
ARFLAGS = -v -r -X64
RANLIB = ranlib -X64
CP = cp
MKDIR = mkdir

LIBNAME = x10
MAJOR_VERSION = 0
MINOR_VERSION = 1

LIBTARGET = lib$(LIBNAME).a
SCHED_A = $(SUBDIR2)/sched.a
ARRAY_A = $(SUBDIR1)/array.a

SRCS = x10lib.cc
OBJS = x10lib.o
HEADERS = types.h x10lib.h

all: $(LIBTARGET)

$(LIBTARGET): $(OBJS) $(ARRAY_A) $(SCHED_A)
	$(RM) $@
	$(AR) $(ARFLAGS) $@ $(OBJS) $(ARRAY_A) $(SCHED_A)
	$(RANLIB) $@

$(SCHED_A):
	(cd $(SUBDIR2); $(MAKE) all)

$(ARRAY_A):
	(cd $(SUBDIR1); $(MAKE) all)

install: $(LIBTARGET)
	@if [ ! -d $(LIBDIR) ]; then \
		$(MKDIR) $(LIBDIR); \
	fi
	$(CP) -p $(LIBTARGET) ../lib/$(LIBTARGET)

clean:
	(cd $(SUBDIR1); $(MAKE) $@)
	(cd $(SUBDIR2); $(MAKE) $@)
	$(RM) $(LIBTARGET) $(OBJS) *.log
	
.cc.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@
