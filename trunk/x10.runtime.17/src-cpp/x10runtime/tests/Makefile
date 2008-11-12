#
# (c) Copyright IBM Corporation 2008
#
# $Id$
# This file is part of XRX/C++ native layer implementation.
#

## Build for XRX/C++ native layer implementation tests.
.SUFFIXES: .cc .h .o .x

# platform architecure
ARCH = ppc

ifeq ($(USE_GCC),1)
CXX = /vol/x10/gcc.4.2.3/bin/g++
CXXFLAGS = -maix64 -g -O2 -pthread
else
CXX = /usr/vacpp/bin/xlc++_r
CXXFLAGS = -q64 -g -O2 -qarch=$(ARCH) -qtwolink
endif

INCLUDES = -I..
LIBDIRS = -L..
LIBS = -lxrx -lpthdebug
DEFS = -D_THREAD_SAFE
LINKFLAGS = #-bnoquiet

TARGETS = ThreadConstructor1.x\
		ThreadConstructor2.x \
		ThreadConstructor3.x \
		ThreadStart1.x \
		ThreadStart2.x \
		ThreadStart3.x \
		ThreadStart4.x \
		ThreadLock1.x \
		ThreadLock2.x \
		ThreadLock3.x \
		ThreadSleep1.x \
		ThreadSleep2.x

.PHONY: all
all: $(TARGETS)

%.x: %.o
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(LINKFLAGS) -o $@ $< $(LIBDIRS) $(LIBS)
	
.PHONY: clean
clean:
	$(RM) $(TARGETS) *.o core

.cc.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@
