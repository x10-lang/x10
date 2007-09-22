#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.1 2007-09-22 14:00:24 ganeshvb Exp $
# This file is part of X10 Runtime System.
#
# Makefile for RandomAccess Benchmarks (C++ Version).
#

.SUFFIXES: .cc .cpp .cxx .C .c .h .o .exe

include make.def

RM = rm -f
RMDIR = rm -r -f 

TARGET = FtStatic.exe

SRCS =  FtStatic.cc \
        fft_fftw3.cc\
        FtStatic_externs.cc \
        c_randdp.cc \
        FtStatic_x10stub.cc

OBJS =  fft_fftw3.o FtStatic_externs.o c_randdp.o FtStatic.o FtStatic_x10stub.o 

INCLS = FtStatic.h 

all: $(TARGET)

install:
	@echo "Nothing to \"install\" in this subdir."

clean:
	$(RM) $(TARGET) $(OBJS) *.log *.out
	$(RMDIR) core*

.cc.o:  $(SRCS) $(INCLS)
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES) -c  $< 

$(TARGET): $(OBJS) 
	$(RM) $@
	$(CXX) $(CXXFLAGS) $(DEFS) $(INCLUDES)  $(OBJS) -o $@ $(LIBDIRS) $(LIBS)

