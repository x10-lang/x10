#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.3 2007-09-22 14:00:22 ganeshvb Exp $
# This file is part of X10 Runtime System.
#
# Makefile for RandomAccess Benchmarks (C++ Version).
#

.SUFFIXES: .cc .cpp .cxx .C .c .h .o .exe

include make.def

RM = rm -f
RMDIR = rm -r -f 

TARGET = Ft.exe

SRCS =  Ft.cc \
        fft_fftw3.cc\
        Ft_externs.cc \
        c_randdp.cc \
        Ft_x10stub.cc

OBJS =  fft_fftw3.o Ft_externs.o c_randdp.o Ft.o Ft_x10stub.o 

INCLS = Ft.h 

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

