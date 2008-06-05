#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.57 2008-06-05 04:52:14 ipeshansky Exp $
# This file is part of X10 Runtime System.
#

## Makefile for X10Lib src subdir.

TOP = ..
include $(TOP)/config/Make.$(shell uname -s | sed 's/_..\(-[0-9]*\.[0-9]*\)\?$$//')

SUBDIR1 = array
SUBDIR2 = sched
SUBDIRS = $(SUBDIR1) $(SUBDIR2)

LIBNAME = x10
X10LIB_MAJOR = 0
X10LIB_MINOR = 6

# Build by default static library
LIBTARGET = lib$(LIBNAME).a
ARRAY_A = $(SUBDIR1)/array.a

SRCS = aggregate.cc \
        aggregate_single.cc\
	async.cc \
	async_swith.cc \
        array_copy_switch.cc \
        gen_array_copy_switch.cc \
	broadcast.cc \
	clock.cc \
	env.cc \
	err.cc \
	finish.cc \
	gas.cc \
	lock.cc \
	order.cc \
	progress.cc \
        queue.cc\
	reduce.cc \
	register.cc \
	rmc.cc \
	switch.cc \
	utility.cc \
	x10lib.cc


OBJS = aggregate.o \
       aggregate_single.o\
       async.o\
	async_switch.o \
	array_copy_switch.o \
	gen_array_copy_switch.o \
	broadcast.o \
	clock.o \
	env.o \
	err.o \
	finish.o \
	gas.o \
	lock.o \
	order.o \
	progress.o \
        queue.o\
	reduce.o \
	register.o \
	rmc.o \
	switch.o \
	utility.o \
	x10lib.o

HEADERS = aggreate.h \
        aggregate_single.h\
	async.h \
        async_closure.h\
	am.h \
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
        queue.h\
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
#SRCS += standalone/standalone.cc
#OBJS += standalone/standalone.o
#HEADERS += standalone/x10init.h
#INCLUDES += -I./standalone

# uncomment the following for hypercube based routing
#SRCS += alpha/aggregate_hc.cc
#OBJS += alpha/aggregate_hc.o
#INCLUDES += -I./alpha

all: $(LIBTARGET)

$(LIBTARGET): $(OBJS) $(ARRAY_A)
	$(RM) $@
	$(AR) $(ARFLAGS) $@ $(OBJS) $(ARRAY_A)
	$(RANLIB) $@
	$(CP) $(LIBTARGET) ../lib

$(ARRAY_A):
	(cd $(SUBDIR1); $(MAKE) all) 

clean:
	(cd $(SUBDIR1); $(MAKE) $@)
	#(cd $(SUBDIR2); $(MAKE) $@)
	$(RM) $(LIBTARGET) $(OBJS) *.log
