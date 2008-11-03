#
# (c) Copyright IBM Corporation 2008
#
# $Id$
# This file is part of XRX Kernel implementation in C++.
#

## Makefile for XRX Kernel library.
.SUFFIXES: .cc .o .a

# platform architecure
ARCH = ppc

CXX = xlC_r
CXXFLAGS = -q64 -g -O2 -qarch=$(ARCH)
INCLUDES = -I.

AR = ar
ARFLAGS = -r -X64
RANLIB = ranlib -X64
RM = rm -f

SUBDIR1 = tests
SUBDIRS = $(SUBDIR1)

LIBNAME = xrx_kernel

# Build by default static library
LIBTARGET = lib$(LIBNAME).a

SRCS = Lock.cc Thread.cc
HEADERS = Types.h InterruptedException.h Runnable.h Lock.h Thread.h
OBJS = Lock.o Thread.o


all: $(LIBTARGET)

$(LIBTARGET): $(OBJS) $(HEADERS)
	$(RM) $@
	$(AR) $(ARFLAGS) $@ $(OBJS)
	$(RANLIB) $@

clean:
	#(cd $(SUBDIR1); $(MAKE) $@)
	$(RM) $(LIBTARGET) $(OBJS)

.cc.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@
