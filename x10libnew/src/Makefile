#INCLUDE = -I$(ARMCI_INC_PATH)
CCC = g++
CCFLAGS = -c -O3 $(INCLUDE) 
LIB = libx10.a
TAR = ar
TAR_COMMAND = q
	
.SUFFIXES: *.o *.cpp *.h

.cpp.o: *.h
	$(CCC) $(CCFLAGS) $< 

$(LIB):   *.o
	$(TAR) $(TAR_COMMAND) $(LIB) $<

all:	$(LIB)
	ranlib $(LIB)

clean: *.o *.a
	rm -f *.o *.a *~
			
	