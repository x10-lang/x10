#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.50 2007-12-10 09:29:57 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Makefile for X10Lib src subdir.

TOP = ..
include $(TOP)/config/Make.AIX

X10LIB_ARRAY = array
X10LIB_SCHED = sched
SUBDIRS = $(X10LIB_ARRAY) $(X10LIB_SCHED)

LIBNAME = x10
X10LIB_MAJOR = 0
X10LIB_MINOR = 6

# Build by default static library
LIBTARGET = lib$(LIBNAME).a

SRCS = aggregate.cc \
	async.cc \
	async_switch.cc \
	broadcast.cc \
	clock.cc \
	env.cc \
	err.cc \
	finish.cc \
	gas.cc \
	lock.cc \
	order.cc \
	progress.cc \
	reduce.cc \
	register.cc \
	rmc.cc \
	switch.cc \
	utility.cc \
	x10lib.cc


OBJS = aggregate.o \
	async.o \
	async_switch.o \
	broadcast.o \
	clock.o \
	env.o \
	err.o \
	finish.o \
	gas.o \
	lock.o \
	order.o \
	progress.o \
	reduce.o \
	register.o \
	rmc.o \
	switch.o \
	utility.o \
	x10lib.o

HEADERS = aggreate.h \
	async.h \
	broadcast.h \
	clock.h \
	closure.h \
	env.h \
	err.h \
	finish.h \
	gas.h \
	lock.h \
	order.h \
	progress.h \
	reduce.h \
	register.h \
	rmc.h \
	switch.h \
	types.h \
	utility.h \
	x10lib.h \
	xassert.h \
	xmacros.h

# uncomment the following for standalone version
#SRCS += standalone.cc
#OBJS += standalone.o
#HEADERS += x10init.h

# uncomment the following for hypercube based routing
#SRCS += aggregate_hc.cc
#OBJS += aggregate_hc.o

all: $(LIBTARGET)

$(LIBTARGET): $(OBJS) #$(X10LIB_ARRAY)
	$(RM) $@
	$(AR) $(ARFLAGS) $@ $(OBJS)
	$(RANLIB) $@

$(X10LIB_ARRAY):
	(cd $(X10LIB_ARRAY); $(MAKE) all)

$(X10LIB_SCHED):
	(cd $(X10LIB_SCHED); $(MAKE) all)

clean:
	(cd $(X10LIB_ARRAY); $(MAKE) $@)
	(cd $(X10LIB_SCHED); $(MAKE) $@)
	$(RM) $(LIBTARGET) $(OBJS) *.log
