CC=gcc $(CCEXTRA)
CXX=g++ $(CCEXTRA)
override CFLAGS+=-I"${JAVA_HOME}/include" -I"${JAVA_HOME}/include/win32" $(CFLAGSEXTRA) -g -I"$(X10HOME)/x10.dist/include"
override CXXFLAGS+=-I"${JAVA_HOME}/include" -I"${JAVA_HOME}/include/win32" $(CFLAGSEXTRA) -g -I"$(X10HOME)/x10.dist/include"
CFLAGSEXTRA=-D__int64=__int64_t
X10HOME?=/home/igor/x10
override LDFLAGS+="-L$(X10HOME)/x10.dist/lib" -Wl,--add-stdcall-alias
#override LDLIBS+=-lx10rt_sockets
override LDLIBS+=-lx10rt_standalone

override LDFLAGS+="-L/usr/lib/w32api"
override LDLIBS+=-lkernel32

LIBPFX=
LIBSFX=.dll

.SUFFIXES: .java .class .o .c .h $(LIBSFX)

all: cyglaunch G.class H.class H.dll
.PHONY: all

$(LIBPFX)%$(LIBSFX):	%.o
	$(CXX) $(CFLAGS) -shared $(LDFLAGS) -o $@ $^ $(LDLIBS)

%.h:	%.class
	"${JAVA_HOME}/bin/javah" $(subst .class,,$^)

%.class:	%.java
	"${JAVA_HOME}/bin/javac" $^

.PRECIOUS: %.class %.h

%.o:	%.cc
%.o:	%.cc %.h
	$(CXX) $(CFLAGS) -c -o $@ $(filter %.cc,$^)

clean:;	$(RM) cyglaunch.exe G.class H.class H.dll H.h
.PHONY: clean
