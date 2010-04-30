# Environment variables holding my stuff for now
X10_HOME=${HOME}/x10-trunk/x10.dist
X10_INCLUDE_DIR=${X10_HOME}/include
X10_LIB_DIR=${X10_HOME}/lib
X10_BIN_DIR=${X10_HOME}/bin
CXX=g++
CXXFLAGS=-O3 -fomit-frame-pointer 
LDFLAGS=-Wl,-stack_size -Wl,100000000

# The libraries that we want for the executable to be compiled
X10RT_LIB=x10rt_pgas_sockets

# Runtime environment variables
TCSH=/bin/tcsh

# Things required to build libsha1
HEADERS=sha1_endian.h \
        sha1_types.h \
        sha1.h 

SOURCES=sha1.c

OBJS=$(SOURCES:.c=.o)

TARGETS=libsha1.a uts

uts: uts.cpp libsha1.a
	${CXX} ${CXXFLAGS} $< -o $@ -I${X10_INCLUDE_DIR} -L${X10_LIB_DIR} \
  -l${X10RT_LIB} ${LDFLAGS} -L. -lsha1 

par_uts: par_uts.cpp libsha1.a
	${CXX} ${CXXFLAGS} $< -o $@ -I${X10_INCLUDE_DIR} -L${X10_LIB_DIR} \
  -l${X10RT_LIB} ${LDFLAGS} -L. -lsha1 

libsha1.a: ${SOURCES} ${HEADERS}
	${CXX} -c ${CXXFLAGS} ${SOURCES}
	ar rcs $@ ${OBJS}

# Actual rules
all: ${TARGETS}

ping_pong: ping_pong.cpp
	${CXX} $< -I${X10_INCLUDE_DIR} -L${X10_LIB_DIR} -l${X10RT_LIB} -o $@

clean:
	rm -rf *.o *.dSYM ping_pong ${TARGETS}
