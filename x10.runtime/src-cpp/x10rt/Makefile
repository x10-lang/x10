MPI_INSTALL  	= /Users/surs/Work/mpi/mpich2-install

CXX             = $(MPI_INSTALL)/bin/mpicxx
CFLAGS          = -g -I./include/ -DENABLE_ASSERT

HEADERS = include/x10rt_api.h
OBJS 	= mpi/x10rt_mpi.o test/x10rt_basic.o

all: x10rt_basic

%.o: %.cc ${HEADERS}
	${CXX} ${CFLAGS} -c $< -o $@

x10rt_basic: ${OBJS}
	${CXX} ${CFLAGS} -o $@ ${OBJS} ${LDFLAGS}

clean:
	${RM} mpi/*.o test/*.o x10rt_basic *~ core*
