#copyright (c) 2006 IBM

GA_BASE        = /home/osu3382/g/ga-4-0-ia64
MPI_BASE       = /usr/local/mpich
BOOST_INCLUDES = -I/home/osu3382/install/ia64/include/boost-1_33_1
BOOST_LIB      = -L/home/osu3382/install/ia64/lib/
CC             = icc
CXX            = icpc
FC             = efc

#requires ARMCI to be compiled with GPC calls enabled

      LIBRARY  = libx10system.a
  LIB_DISTRIB  = ../lib
          OBJ  = system.o

      HEADERS  = 

  LIB_TARGETS  = *.x *.o
  INCDIR       = ../include


#HEADERS = X10Runtime/X10.h 
#SRCS = X10Runtime/X10.C
#OBJS = $(SRCS:.C=.o)
#LIBRARY = libx10.a

#default: $(LIBRARY)

#$(LIBRARY): $(HEADERS) $(OBJS)
#	$(AR) rcv $(LIBRARY) $(OBJS)

include $(GA_BASE)/armci/config/makemp.h
include $(GA_BASE)/armci/config/makecoms.h

GA_INCLUDES = -I$(GA_BASE)/include
MP_INCLUDES += -I$(MPI_BASE)/include/mpi2c++

LIB_DEFINES += -DARMCI_ENABLE_GPC_CALLS -DHAVE_MPI_CPP
LIB_INCLUDES += $(COMM_INCLUDES) $(MP_INCLUDES) $(GA_INCLUDES) $(BOOST_INCLUDES) -I..
EXTRA_LIBS += $(MP_LIBS) $(COMM_LIBS)
include $(GA_BASE)/config/makefile.h
include $(GA_BASE)/config/makelib.h

CXXFLAGS = $(subst -DMPI,,$(CFLAGS))

.SUFFIXES: .o .C

.C.o:
	$(CXX) $(CXXFLAGS) -o $*.o -c $*.C

install:
	cp $(HEADERS) ../include
#	cp libx10.a ../lib

docs:
	doxygen

#clean:
#	rm -f *~
#	rm -f *.o
#	rm -f *.x
#	rm -rf ../docs/doxygen/html
#	rm -f ../include/*.h

