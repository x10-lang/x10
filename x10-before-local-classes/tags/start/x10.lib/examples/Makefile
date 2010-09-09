#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.1.1.1 2007-04-25 09:57:46 srkodali Exp $
# This file is part of X10 Runtime System.
#
# Makefile for sample programs.
#

.SUFFIXES: .cc .c .o

LIBNAME = x10
LIBTARGET = lib$(LIBNAME).a
CXX = mpCC_r -q64 -qarch=pwr5
CC = mpcc_r -q64 -qarch=pwr5
DEFS = -DDEBUG
CXXFLAGS = -qtune=pwr5 -O3 -qhot
CCFLAGS = -qtune=pwr5 -O3 -qhot
INCLUDES = -I. -I../include
LIBDIRS = -L../lib
LIBS = -l$(LIBNAME)
LINKFLAGS =
RM = rm -f

TARGET1 = hello_cc
SRCS1 = $(TARGET1).cc
OBJS1 = $(TARGET1).o

TARGET2 = hello_c
SRCS2 = $(TARGET2).c
OBJS2 = $(TARGET2).o

TARGETS = $(TARGET1) $(TARGET2)

all: $(TARGETS)

$(TARGET1): $(OBJS1)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS1) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET2): $(OBJS2)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS2)  -o $@ $(LIBDIRS) $(LIBS)

install:
	@echo "Nothing to \"install\" in \"examples\" subdir."

clean:
	$(RM) $(TARGETS) $(OBJS1) $(OBJS2)

.cc.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@

.c.o:
	$(RM) $@
	$(CXX) $(CXXFLAGS) -qsourcetype=c++ $(DEFS) $(INCLUDES) -c $< -o $@
