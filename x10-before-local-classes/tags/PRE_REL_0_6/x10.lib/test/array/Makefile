#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.6 2007-10-24 10:47:43 ganeshvb Exp $
# This file is part of X10 Runtime System.
#

## Makefile for Testing X10Lib's Array Implementation.

.SUFFIXES: .cc .cxx .cpp .C .c .o .h

MAKE = make
CXX = mpCC_r -q64 -qarch=pwr5
CXXFLAGS = -qtune=pwr5 -O3 -qhot
CC = mpcc_r -q64 -qarch=pwr5
CCFLAGS = -qtune=pwr5 -O3 -qhot
DEFS = -DX10_DLEVEL=0 #-DDEBUG
INCLUDES = -I. -I../../include -I ~/root/lapi/include
LINKFLAGS =
LIBDIRS = -L../../lib
LIBS = -lx10
RM = rm -f

TARGET1 = Test_array
SRCS1 = $(TARGET1).cc
OBJS1 = $(TARGET1).o

TARGET2 = Test_array_async
SRCS2 = $(TARGET2).cc
OBJS2 = $(TARGET2).o

TARGET3 = Test_dist
SRCS3 = $(TARGET3).cc
OBJS3 = $(TARGET3).o

TARGET4 = Test_point
SRCS4 = $(TARGET4).cc
OBJS4 = $(TARGET4).o

TARGET5 = Test_region
SRCS5 = $(TARGET5).cc
OBJS5 = $(TARGET5).o

TARGET6 = Test_strided_region
SRCS6 = $(TARGET6).cc
OBJS6 = $(TARGET6).o

TARGET7 = Test_point2
SRCS7 = $(TARGET7).cc
OBJS7 = $(TARGET7).o

TARGET8 = Test_region2
SRCS8 = $(TARGET8).cc
OBJS8 = $(TARGET8).o

TARGETS = $(TARGET3) $(TARGET4) \
			$(TARGET5) $(TARGET6) $(TARGET7) $(TARGET8)

all:: $(TARGETS)
	rm *.o

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
	$(CXX) $(LINKFLAGS) $(OBJS3) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET3).o: $(TARGET3).cc
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS) -DVARARGS=1 -DFUNCTOR=1 -DDEBUG -DWARN $(INCLUDES) -c $< -o $@

$(TARGET4): $(OBJS4)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS4) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET4).o: $(TARGET4).cc
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS) -DVARARGS=1 -DFUNCTOR=1 -DDEBUG -DWARN $(INCLUDES) -c $< -o $@

$(TARGET5): $(OBJS5)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS5) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET5).o: $(TARGET5).cc
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS) -DVARARGS=1 -DFUNCTOR=1 -DDEBUG -DWARN $(INCLUDES) -c $< -o $@

$(TARGET6): $(OBJS6)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS6) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET6).o: $(TARGET6).cc
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS) -DVARARGS=1 -DFUNCTOR=1 -DDEBUG -DWARN $(INCLUDES) -c $< -o $@

$(TARGET7): $(OBJS7)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS7) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET7).o: $(TARGET7).cc
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS) -DVARARGS=1 -DFUNCTOR=1 -DDEBUG -DWARN $(INCLUDES) -c $< -o $@

$(TARGET8): $(OBJS8)
	$(RM) $@
	$(CXX) $(LINKFLAGS) $(OBJS8) -o $@ $(LIBDIRS) $(LIBS)

$(TARGET8).o: $(TARGET8).cc
	$(RM) $@
	$(CXX) $(CXXFLAGS) -w $(DEFS) -DVARARGS=1 -DFUNCTOR=1 -DDEBUG -DWARN $(INCLUDES) -c $< -o $@

install:
	@echo "Nothing to \"install\" in this subdir."

clean:
	$(RM) $(TARGETS) $(OBJS1) $(OBJS2) $(OBJS3) $(OBJS4) \
			$(OBJS5) $(OBJS6) $(OBJS7) $(OBJS8)
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
