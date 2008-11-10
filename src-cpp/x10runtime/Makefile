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

ifeq ($(USE_GCC),1)
CXX = /vol/x10/gcc.4.2.3/bin/g++
CXXFLAGS = -maix64 -g -O2 -pthread
else
CXX = /usr/vacpp/bin/xlc++_r
CXXFLAGS = -q64 -g -O2 -qarch=$(ARCH) -qtwolink
endif

INCLUDES = -I.
DEFS = -D_THREAD_SAFE #-DXRX_DEBUG=1

AR = ar
ARFLAGS = -r -X64
RANLIB = ranlib -X64
RM = rm -f
MAKE = gmake

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
	Object.o \
	Xrx.o
HEADERS = \
	Object.h \
	Types.h \
	InterruptedException.h \
	IllegalMonitorStateException.h \
	IllegalThreadStateException.h \
	Runnable.h \
	Lock.h \
	Thread.h \
	Exception.h \
	Debug.h \
	Xrx.h


all: $(LIBTARGET)
	USE_GCC=$(USE_GCC) $(MAKE) -C $(SUBDIR1)

$(LIBTARGET): $(OBJS) $(HEADERS)
	$(RM) $@
	$(AR) $(ARFLAGS) $@ $(OBJS)
	$(RANLIB) $@

clean:
	@(cd $(SUBDIR1); $(MAKE) $@)
	$(RM) $(LIBTARGET) $(OBJS)

.cc.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@
