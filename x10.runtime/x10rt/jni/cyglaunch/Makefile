#
#  This file is part of the X10 project (http://x10-lang.org).
#
#  This file is licensed to You under the Eclipse Public License (EPL);
#  You may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#      http://www.opensource.org/licenses/eclipse-1.0.php
#
#  (C) Copyright IBM Corporation 2006-2010.
#

CC=gcc $(CCEXTRA)
CXX=g++ $(CCEXTRA)
override CFLAGS+=-I"${JAVA_HOME}/include" -I"${JAVA_HOME}/include/win32" $(CFLAGSEXTRA) -g -I"$(X10_HOME)/x10.runtime/x10rt/include"
override CXXFLAGS+=-I"${JAVA_HOME}/include" -I"${JAVA_HOME}/include/win32" $(CFLAGSEXTRA) -g -I"$(X10_HOME)/x10.runtime/x10rt/include"
override LDFLAGS+="-L$(X10_HOME)/x10.runtime/x10rt/lib" -Wl,--add-stdcall-alias
override LDLIBS+=-lx10rt_sockets
#override LDLIBS+=-lx10rt_standalone

override LDFLAGS+="-L/usr/lib/w32api"
override LDLIBS+=-lkernel32

LIBPFX=
LIBSFX=.dll

.SUFFIXES: .java .class .o .c .h $(LIBSFX)

all: cyglaunch G.class H.class H.dll I.class
.PHONY: all

$(LIBPFX)%$(LIBSFX):	%.o
	$(CXX) $(CFLAGS) -shared $(LDFLAGS) -o $@ $^ $(LDLIBS)

%.h:	%.class
	"${JAVA_HOME}/bin/javah" $(subst .class,,$^)

%.class:	%.java
	"${JAVA_HOME}/bin/javac" -classpath "`cygpath -am "$(X10_HOME)/x10.runtime/classes"`" $^

.PRECIOUS: %.class %.h

%.o:	%.cc
%.o:	%.cc %.h
	$(CXX) $(CFLAGS) -c -o $@ $(filter %.cc,$^)

clean:;	$(RM) cyglaunch.exe G.class H.class H.dll H.h
.PHONY: clean
