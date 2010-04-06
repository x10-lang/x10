CXX=g++
CXXFLAGS=-O3 -fomit-frame-pointer 
ARCHIVER=ar
ARCHIVERFLAGS=rcs

HEADERS=endian.h \
        types.h \
        sha1.h 

SOURCES=sha1.c

OBJS=$(SOURCES:.c=.o)

TARGET=libsha1.a

all: ${TARGET}

libsha1.a: ${SOURCES} ${HEADERS}
	${CXX} -c ${CXXFLAGS} ${SOURCES}
	ar rcs $@ ${OBJS}

uts: uts.cpp sha1_rand.hpp libsha1.a
	${CXX} ${CXXFLAGS} $< -o $@ -L. -lsha1
	
test: Sha1Rand.cpp libsha1.a
	${CXX} ${CXXFLAGS} -DSHA1_TEST $< -o $@ -L. -lsha1

clean:
	rm -rf core*.* ${OBJS} ${TARGET} test
