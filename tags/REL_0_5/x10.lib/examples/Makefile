#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.7 2007-06-27 14:48:33 srkodali Exp $
# This file is part of X10 Runtime System.
#
# Makefile for sample programs.
#

.SUFFIXES: .cc .cxx .cpp .C .h .c .o

LIBNAME = x10
LIBTARGET = lib$(LIBNAME).a
CXX = mpCC_r -q64 -qarch=pwr5
CC = mpcc_r -q64 -qarch=pwr5
DEFS = #-DDEBUG
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

TARGET3 = addr_c
SRCS3 = $(TARGET3).c
OBJS3 = $(TARGET3).o

TARGET4 = recv_c
SRCS4 = $(TARGET4).c
OBJS4 = $(TARGET4).o

TARGET5 = addr_cc
SRCS5 = $(TARGET5).cc
OBJS5 = $(TARGET5).o

TARGET6 = recv_cc
SRCS6 = $(TARGET6).cc
OBJS6 = $(TARGET6).o

TARGET7 = acc_ret_c
SRCS7 = $(TARGET7).c
OBJS7 = $(TARGET7).o

TARGET8 = acc_ret_cc
SRCS8 = $(TARGET8).cc
OBJS8 = $(TARGET8).o

TARGETS = $(TARGET1) $(TARGET2) $(TARGET3) $(TARGET4) $(TARGET5) \
			$(TARGET6) $(TARGET7) $(TARGET8)

all: $(TARGETS)

$(TARGET1): $(OBJS1)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS1) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET2): $(OBJS2)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS2)  -o $@ $(LIBDIRS) $(LIBS)

$(TARGET3): $(OBJS3)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS3)  -o $@ $(LIBDIRS) $(LIBS)

$(TARGET4): $(OBJS4)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS4)  -o $@ $(LIBDIRS) $(LIBS)

$(TARGET5): $(OBJS5)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS5)  -o $@ $(LIBDIRS) $(LIBS)

$(TARGET6): $(OBJS6)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS6)  -o $@ $(LIBDIRS) $(LIBS)

$(TARGET7): $(OBJS7)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS7)  -o $@ $(LIBDIRS) $(LIBS)

$(TARGET8): $(OBJS8)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS8)  -o $@ $(LIBDIRS) $(LIBS)

install:
	@echo "Nothing to \"install\" in \"examples\" subdir."

clean:
	$(RM) $(TARGETS) *.o

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
	$(CXX) $(CXXFLAGS)  $(DEFS) $(INCLUDES) -c $< -o $@
