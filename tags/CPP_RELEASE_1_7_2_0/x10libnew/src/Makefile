#
#
# (C) Copyright IBM Corporation 2007
#
# Makefile 
# Author : Ganesh 
#
#

INCLUDE = #-I$(ARMCI_INC_PATH)

CCC = g++
CCFLAGS = -c -O3 $(INCLUDE) 

LIB = libx10.a
TAR = ar
TAR_COMMAND = q
RANLIB = ranlib
	
.SUFFIXES: *.o *.cpp *.h

OBJS = 	x10-activity.o\
	x10-array.o\
	x10-clock.o\
	x10-region.o\
	x10-xfer.o
        	
.cpp.o: *.h *.cpp
	$(CCC) $(CCFLAGS) $< 

$(LIB):  $(OBJS) 
	$(TAR) $(TAR_COMMAND) $(LIB) $<

.PHONY:  clean all

all:	$(LIB)
	RANLIB $(LIB)

clean: 
	rm -f $(OBJS) $(LIB) *~ 
			
	
