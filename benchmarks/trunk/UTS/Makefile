CXX=g++
CXXFLAGS=-O3 -fomit-frame-pointer 
X10RUN?=runx10
X10CXX?=x10c++ 
#X10RT_TRANSPORT?=-x10rt mpi
X10CXXFLAGS=-report postcompile=5 $(X10RT_TRANSPORT)
ARCHIVER=ar
ARCHIVERFLAGS=rcs

HEADERS=sha_endian.h \
        types.h \
        sha1.h 

SOURCES=sha1.c

OBJS=$(SOURCES:.c=.o)

TARGET=libsha1.a

all: UTS

libsha1.a: ${SOURCES} ${HEADERS}
	${CXX} -c ${CXXFLAGS} ${SOURCES}
	ar rcs $@ ${OBJS}

uts: uts.cpp sha1_rand.hpp libsha1.a
	${CXX} ${CXXFLAGS} $< -o $@ -L. -lsha1
	
UTS: UTS.x10 sha1_rand.hpp sha1.c
	${X10CXX} -O -NO_CHECKS ${X10CXXFLAGS} $< -o $@ -report postcompile=5	

run-UTS: UTS
	${X10RUN} ./UTS
	#${X10RUN} gdb ./UTS
	
test: Sha1Rand.cpp libsha1.a
	${CXX} ${CXXFLAGS} -DSHA1_TEST $< -o $@ -L. -lsha1

clean:
	rm -rf core*.* ${OBJS} ${TARGET} test
	rm -rf *.dSYM *.inc UTS UTS.h UTS.cc
