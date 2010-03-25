include ../arch/aix.mk

TARGET = SSCA2

CC=${POSTCOMPILE_CXX}
CFLAGS=${POSTCOMPILE_NATIVE_LIB_FLAGS} -qsmp=omp
INC=-I../sprng2.0/include
LIB=-L../sprng2.0/lib -lsprng

OBJS = SSCA2.o init.o utils.o genScalData.o gen2DTorus.o \
    computeGraph.o getStartLists.o findSubGraphs.o  \
    betweennessCentrality.o

.c.o: defs.h Makefile
	$(CC) $(INC) $(CFLAGS) -c $<


all: $(OBJS) defs.h Makefile
	$(CC) $(INC) $(CFLAGS) -o $(TARGET) $(OBJS) $(LIB)

sprng: 
	(cd sprng2.0; $(MAKE); cd ..)


clean: 
	rm -f *.o *~ $(TARGET) core*
