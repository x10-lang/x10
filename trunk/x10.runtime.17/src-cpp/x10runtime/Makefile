#
# (c) Copyright IBM Corporation 2008
#
# $Id$
# This file is part of XRX/C++ native layer implementation.
#

## Makefile for XRX/C++ native layer implementation.
.SUFFIXES: .cc .h .o .a

# platform architecure
ARCH = ppc

CXX = xlC_r
CXXFLAGS = -q64 -g -O2 -qarch=$(ARCH)
INCLUDES = -I.
DEFS = #-DXRX_KERNEL_DEBUG=1

AR = ar
ARFLAGS = -r -X64
RANLIB = ranlib -X64
RM = rm -f

SUBDIR1 = tests
SUBDIRS = $(SUBDIR1)

LIBNAME = xrx

# Build by default static library
LIBTARGET = lib$(LIBNAME).a

SRCS = \
	Lock.cc \
	Thread.cc \
	Object.cc
OBJS = \
	Lock.o \
	Thread.o \
	Object.o
HEADERS = \
	Object.h \
	Types.h \
	InterruptedException.h \
	IllegalMonitorStateException.h \
	IllegalThreadStateException.h \
	Runnable.h \
	Lock.h \
	Thread.h \
	Exception.h


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
