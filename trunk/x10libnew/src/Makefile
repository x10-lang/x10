#INCLUDE = -I$(ARMCI_INC_PATH)
CCC = g++
CCFLAGS = -c -O3 $(INCLUDE) 
LIB = libx10.a
TAR = ar
TAR_COMMAND = q
	
.SUFFIXES: *.o *.cpp *.h

OBJS = 	x10-array.o\
	x10-clock.o\
	x10-region.o
	
.cpp.o: *.h *.cpp
	$(CCC) $(CCFLAGS) $< 

$(LIB):  $(OBJS) 
	$(TAR) $(TAR_COMMAND) $(LIB) $<

all:	$(LIB)
	ranlib $(LIB)

clean: *.o *.a
	rm -f *.o *.a *~ 
			
	
