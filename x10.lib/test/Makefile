#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.18 2008-06-05 11:03:24 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Toplevel Makefile for X10Lib Regression Testing.

.SUFFIXES: .cc .cxx .cpp .C .c .o .h

MAKE = gmake
CXX = mpCC_r -q64 -qarch=pwr5
CXXFLAGS = -qtune=pwr5 -O3 -qhot
CC = mpcc_r -q64 -qarch=pwr5
CCFLAGS = -qtune=pwr5 -O3 -qhot 
DEFS = -DX10_DLEVEL=0 #-DDEBUG
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

#TARGET5 = Test_async_agg_func
#SRCS5 = $(TARGET5).cc
#OBJS5 = $(TARGET5).o

TARGET6 = Test_switch
SRCS6 = $(TARGET6).cc
OBJS6 = $(TARGET6).o

TARGET7 = Test_switch_c
SRCS7 = $(TARGET7).c
OBJS7 = $(TARGET7).o

TARGET8 = Test_async_agg_c
SRCS8 = $(TARGET4).c
OBJS8 = $(TARGET4).o

TARGET9 = Test_finish
SRCS9 = $(TARGET9).cc
OBJS9 = $(TARGET9).o

TARGET10 = Test_broadcast
SRCS10 = $(TARGET10).cc
OBJS10 = $(TARGET10).o

TARGET11 = Test_transpose
SRCS11 = $(TARGET11).cc
OBJS11 = $(TARGET11).o

TARGET12 = Test_transpose_contig
SRCS12 = $(TARGET12).cc
OBJS12 = $(TARGET12).o

TARGET13 = Test_transpose_contig_overlap
SRCS13 = $(TARGET13).cc
OBJS13 = $(TARGET13).o

TARGET14 = Test_transpose_iov
SRCS14 = $(TARGET14).cc
OBJS14 = $(TARGET14).o

TARGET15= Test_async_gen
SRCS15 = $(TARGET15).cc
OBJS15 = $(TARGET15).o

TARGETS = $(TARGET1) $(TARGET2) $(TARGET3) $(TARGET4) $(TARGET5) $(TARGET6) $(TARGET7) $(TARGET9) $(TARGET10) $(TARGET11) $(TARGET12) $(TARGET13) $(TARGET14) $(TARGET15)

all: $(TARGETS)
	rm *.o
	(cd array; $(MAKE) $@)
	(cd sched; $(MAKE) $@)

$(TARGET1): $(OBJS1)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS1) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET1).o: $(TARGET1).cc
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS)   -DDEBUG -DWARN $(INCLUDES) -c $< -o $@
	
$(TARGET2): $(OBJS2)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS2) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET2).o: $(TARGET2).cc
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS)   -DDEBUG -DWARN $(INCLUDES) -c $< -o $@

$(TARGET3): $(OBJS3)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS3) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET3).o: $(TARGET3).c
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS)   -DDEBUG -DWARN $(INCLUDES)  -c $< -o $@

$(TARGET4): $(OBJS4)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS4) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET4).o: $(TARGET4).c
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS)   -DDEBUG -DWARN $(INCLUDES)  -c $< -o $@

#$(TARGET5): $(OBJS5)
#	$(RM) $@
#	$(CXX) $(LINKFLAGS) $(OBJS5) -o $@ $(LIBDIRS) $(LIBS)

#$(TARGET5).o: $(TARGET5).cc
#	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS)   -DDEBUG -DWARN $(INCLUDES) -c $< -o $@
#
$(TARGET6): $(OBJS6)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS6) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET6).o: $(TARGET6).cc
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS)   -DDEBUG -DWARN $(INCLUDES) -c $< -o $@

$(TARGET7): $(OBJS7)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS7) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET7).o: $(TARGET7).c
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS)   -DDEBUG -DWARN $(INCLUDES) -c $< -o $@

$(TARGET8): $(OBJS8)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS8) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET8).o: $(TARGET8).c
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS)   -DDEBUG -DWARN $(INCLUDES) -c $< -o $@

$(TARGET9): $(OBJS9)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS9) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET9).o: $(TARGET9).cc
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS)   -DDEBUG -DWARN $(INCLUDES) -c $< -o $@

$(TARGET10): $(OBJS10)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS10) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET10).o: $(TARGET10).cc
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS)   -DDEBUG -DWARN $(INCLUDES) -c $< -o $@

$(TARGET11): $(OBJS11)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS11) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET11).o: $(TARGET11).cc
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS)   -DDEBUG -DWARN $(INCLUDES) -c $< -o $@

$(TARGET12): $(OBJS12)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS12) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET12).o: $(TARGET12).cc
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS)   -DDEBUG -DWARN $(INCLUDES) -c $< -o $@

$(TARGET13): $(OBJS13)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS13) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET13).o: $(TARGET13).cc
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS)   -DDEBUG -DWARN $(INCLUDES) -c $< -o $@

$(TARGET14): $(OBJS14)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS14) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET14).o: $(TARGET14).cc
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS)   -DDEBUG -DWARN $(INCLUDES) -c $< -o $@

$(TARGET15): $(OBJS15)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS15) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET15).o: $(TARGET15).cc
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS)   -DDEBUG -DWARN $(INCLUDES) -c $< -o $@

install:
	@echo "Done \"install\" in \"test\" subdir."

clean:
	(cd array; $(MAKE) $@)
	(cd sched; $(MAKE) $@)
	$(RM) $(TARGETS) $(OBJS1) $(OBJS2) $(OBJS3) $(OBJS4) $(OBJS5) $(OBJS6) $(OBJS7)

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
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c $< -o $@
