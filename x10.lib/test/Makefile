#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.4 2007-06-08 13:51:42 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Toplevel Makefile for X10Lib Regression Testing.

.SUFFIXES: .cc .cxx .cpp .C .c .o .h

MAKE = make
CXX = mpCC_r -q64 -qarch=pwr5
CXXFLAGS = -qtune=pwr5 -O3 -qhot
CC = mpcc_r -q64 -qarch=pwr5
CCFLAGS = -qtune=pwr5 -O3 -qhot
DEFS = #-DDEBUG
INCLUDES = -I. -I../include
LINKFLAGS =
LIBDIRS = -L../lib
LIBS = -lx10
RM = rm -f

TARGET1 = Test_async
SRCS1 = $(TARGET1).cc
OBJS1 = $(TARGET1).o

TARGET2 = Test_async_agg
SRCS2 = $(TARGET2).cc
OBJS2 = $(TARGET2).o

TARGET3 = Test_async_c
SRCS3 = $(TARGET3).c
OBJS3 = $(TARGET3).o

TARGET4 = Test_async_agg_c
SRCS4 = $(TARGET4).c
OBJS4 = $(TARGET4).o

TARGETS = $(TARGET1) $(TARGET2) $(TARGET3) $(TARGET4)

all: $(TARGETS)
	(cd array; $(MAKE) $@)
	(cd sched; $(MAKE) $@)

$(TARGET1): $(OBJS1)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS1) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET1).o: $(TARGET1).cc
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS) -DVARARGS=1 -DFUNCTOR=1 -DDEBUG -DWARN $(INCLUDES) -c $< -o $@
	
$(TARGET2): $(OBJS2)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS2) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET2).o: $(TARGET2).cc
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS) -DVARARGS=1 -DFUNCTOR=1 -DDEBUG -DWARN $(INCLUDES) -c $< -o $@

$(TARGET3): $(OBJS3)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS2) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET3).o: $(TARGET3).c
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS) -DVARARGS=1 -DFUNCTOR=1 -DDEBUG -DWARN $(INCLUDES) -qsourcetype=c++ -c $< -o $@

$(TARGET4): $(OBJS4)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS4) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET4).o: $(TARGET4).c
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS) -DVARARGS=1 -DFUNCTOR=1 -DDEBUG -DWARN $(INCLUDES) -qsourcetype=c++ -c $< -o $@

install:
	@echo "Done \"install\" in \"test\" subdir."

clean:
	(cd array; $(MAKE) $@)
	(cd sched; $(MAKE) $@)
	$(RM) $(TARGETS) $(OBJS1) $(OBJS2) $(OBJS3) $(OBJS4)

.cc.o:
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
	$(CXX) $(CXXFLAGS) -qsourcetype=c++ $(DEFS) $(INCLUDES) -c $< -o $@
