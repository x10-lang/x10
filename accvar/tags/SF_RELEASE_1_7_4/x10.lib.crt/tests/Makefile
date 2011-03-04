#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.5 2008-06-20 13:53:27 ganeshvb Exp $
# This file is part of X10 Runtime System.
#

## Makefile for X10Lib src subdir.
ARCH = linux
X10_DEVEL_HOME =
CC = mpcc_r -I ../src -g -q64
CFLAGS = -O3
CXX = mpCC_r -q64 -I../src -g
LIBS = -L ../src -lx10 -L /home/bikshand/upc/src/rts/src/hybrid/lapi -lupcrts_lapi -lpthread  -L/home/bikshand/libs/rsct/lapi/linux/lapi32/ -llapi standalone.o

AR = ar
ARFLAGS = -r
RANLIB = ranlib

.SUFFIXES: .cc .cxx .cpp .C .c .o .h .x10

.c.o:
	$(RM) $@
	$(CC) $(CCFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@


LIBNAME = x10
X10LIB_MAJOR = 0
X10LIB_MINOR = 6

# Build by default static library
LIBTARGET = test

SRCS = test_async.c\
       test_finish1.c\
       test_finish2.c\
       test_finish3.c\
       test_hello.c\
       test_queue.c\
       test_refs.c

OBJS = test_async.o\
       test_async_noargs.o\
       test_finish1.o\
       test_finish2.o\
       test_finish3.o\
       test_hello.o\
       test_refs.o


HEADERS = x10_acts.h \
          x10_types.h
all:
	$(c_test_command)
	$(cxx_test_command)

c_build = $(shell $(CC) -c $(test).c; $(CXX) -o $(test) $(test).o $(LIBS)) 
cxx_build = $(shell $(CXX) -c $(test).cc; $(CXX) -o $(test) $(test).o $(LIBS)) 

CTESTS := test_async_noargs test_async test_finish1 test_finish2 test_hello test_refs test_array_put test_array_get test_async_array_put test_finish3 

CXXTESTS := test_queue

c_test_command := $(foreach test,$(CTESTS),$(c_build))
cxx_test_command := $(foreach test,$(CXXTESTS),$(cxx_build))

clean:
	$(RM) $(LIBTARGET) $(OBJS) *.log $(TESTS)
