#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.48 2007-11-13 05:28:49 ganeshvb Exp $
# This file is part of X10 Runtime System.
#

## Makefile for X10Lib src subdir.

.SUFFIXES: .cc .cpp .cxx .C .c .o .h

MAKE = make
SUBDIR1 = array
SUBDIR2 = sched
SUBDIRS = $(SUBDIR1) $(SUBDIR2)
LIBDIR = ../lib
CXX = mpCC_r -q64 -qarch=pwr5 
DEFS =  -DNDEBUG 
CXXFLAGS = -O3 -qinline -qtune=pwr5  -qhot  -qpath=ILbc:/vol/x10/patches/xlC/070919/exe
INCLUDES = -I. -I../include
LINKFLAGS =
RM = rm -f
AR = ar
ARFLAGS = -r -X64
RANLIB = ranlib -X64
CP = cp
MKDIR = mkdir

LIBNAME = x10
MAJOR_VERSION = 0
MINOR_VERSION = 1

LIBTARGET = lib$(LIBNAME).a
SCHED_A = $(SUBDIR2)/sched.a
ARRAY_A = $(SUBDIR1)/array.a

#SRCS = x10lib.cc gas.cc aggregate.cc async.cc switch.cc finish.cc 
#OBJS = x10lib.o  gas.o  aggregate.o  async.o switch.o finish.o
#HEADERS = types.h x10lib.h gas.h alloc.h  async.h aggregate.h finish.h standalone.h

SRCS = aggregate.cc aggregate_hc.cc async.cc env.cc err.cc finish.cc gas.cc lock.cc \
		order.cc progress.cc aggregate_ra.cc \
		register.cc rmc.cc switch.cc x10lib.cc async_switch.cc \
		utility.cc misc.cc clock.cc array_copy_switch.cc standalone.cc reduce.cc\
                gen_array_copy_switch.cc
OBJS = aggregate.o async.o env.o err.o finish.o gas.o lock.o \
		order.o progress.o aggregate_ra.o standalone.o\
		register.o rmc.o switch.o x10lib.o async_switch.o \
		utility.o misc.o clock.o aggregate_hc.o array_copy_switch.o reduce.o \
                gen_array_copy_switch.o
HEADERS = env.h err.h gas.h lock.h order.h progress.h \
		register.h rmc.h switch.h types.h x10lib.h xassert.h \
		xmacros.h aggregate.h async.h finish.h utility.h misc.h \
                clock.h  
all: $(LIBTARGET) 

#$(LIBTARGET): $(OBJS) #$(SCHED_A)
$(LIBTARGET): $(OBJS) $(ARRAY_A)
	$(RM) $@
	$(AR) $(ARFLAGS) $@ $(OBJS) $(ARRAY_A)
	$(RANLIB) $@

myput: myput.o
	$(CXX) $(LINKFLAGS) myput.o -o $@ -L. -lx10

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
#	(cd $(SUBDIR2); $(MAKE) $@)
	$(RM) $(LIBTARGET) $(OBJS) *.log core*
	
.cc.o: $(SRCS) $(HEADERS)
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@

.cxx.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@

.cpp.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@

.C.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@

.c.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@
