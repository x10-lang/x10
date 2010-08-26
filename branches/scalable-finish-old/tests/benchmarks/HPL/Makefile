# GNU Makefile for building X10 extern shared libraries.
# This was tested on Windows/Cygwin, Linux, and AIX (with both gcc and xlC).
# See implementation file headers for instructions on building the shared
# libraries using different compilers.

UNAME:=$(shell uname -s)
ifneq ($(UNAME),AIX)
ifneq ($(UNAME),Linux)
CYGWIN=yes
endif
endif
LIBPFX=lib
LIBEXT=so

CFLAGS=-I${JAVA_HOME}/include $(CFLAGSEXTRA)

# Blas is in default /usr/lib
# Modify if you have it in some other location
LDFLAGS=-L/vol/x10/gcc.4.1.1/lib

LDLIBS=-lblas -lm -lf

ifneq ($(UNAME),AIX)
ifneq ($(UNAME),Linux)
# Must be Windows/Cygwin
LIBPFX=
LIBEXT=dll
CFLAGSEXTRA="-I${JAVA_HOME}/include/win32"
else
CFLAGSEXTRA="-I${JAVA_HOME}/include/linux"
endif
endif

all:	checkjava $(LIBPFX)PLUInC.$(LIBEXT)
	
ifeq ($(shell gcc --version),)	# Test for systems without GCC
ifeq ($(UNAME),AIX)	# AIX
$(LIBPFX)%.$(LIBEXT):
	xlC $(CCFLAGS) $(CFLAGS) -qmkshrobj -o $@ PLUInC.o $(LDLIBS)
%.o:	%.c
	xlC $(CCFLAGS) $(CFLAGS) -c -o $@ $<
%.o:	%.cpp
	xlC $(CCFLAGS) $(CFLAGS) -c -o $@ $<
else			# Must be Windows
$(LIBPFX)%.$(LIBEXT):
	cl $(CFLAGS) -LD -Fe$@ PLUInC.o $(LDLIBS)
%.o:	%.c
	cl $(CFLAGS) -c $<
%.o:	%.cpp
	cl $(CFLAGS) -c $<
endif
else				# Got GCC
CC=gcc $(CCEXTRA)
CXX=gcc $(CCEXTRA) #changed from g++
CXXFLAGS=$(CFLAGS)

ifeq ($(CYGWIN),)
$(LIBPFX)%.$(LIBEXT):
	$(CXX) $(CFLAGS) -shared $(LDFLAGS)  -o $@ PLUInC.o $(LDLIBS) 

else				# Must be Cygwin
DLLWRAP=dllwrap $(CCEXTRA)
CCEXTRA=-mno-cygwin

$(LIBPFX)%.$(LIBEXT):	dll_entry.o
	@$(MAKE) $*.def __DEPS__="$^"
	$(DLLWRAP) $(CFLAGS) -shared $(LDFLAGS)  -o $@ --def $*.def $(filter %.o,$^)   
	$(RM) $*.def 

ifneq ($(__DEPS__),)
%.din:	dll_entry.o $(__DEPS__)
	$(CXX) $(CFLAGS) -shared $(LDFLAGS) -o $*.tmp.dll -Wl,--output-def=$@ $(filter %.o,$^)  
	$(RM) $*.tmp.dll 

%.def:	%.din
	@#sed -e '/Java_/ {; h; s/Java_/_Java_/; G; s/ @[0-9]\+\n */ = /; s/ @[0-9]\+$$//; H; s/00024//; x; G }' < $< > $@
	sed -e '/Java_/ {; h; s|Java_|_Java_|; G; s| @[0-9]\+\n *| = |' \
           -e 's| @[0-9]\+$$||; H; s|00024||; x; G; }' < $< > $@
endif

# Empty method to circumvent Windows' linker's retarded behavior
DLLENTRY=\#include <sys/types.h>\n\
\#include <jni.h>\n\
\#ifdef __cplusplus\n\
extern "C" {\n\
\#endif\n\
\#ifdef __WIN32__\n\
JNIEXPORT void JNICALL dll_entry() { }\n\
\#endif\n\
\#ifdef __cplusplus\n\
}\n\
\#endif\n

# Empty method to circumvent Windows' linker's retarded behavior
dll_entry.o:	Makefile
	echo -e '$(DLLENTRY)' | $(CC) --language=c $(CFLAGS) -c -o $@ -
.INTERMEDIATE: dll_entry.o
endif
endif

#.PRECIOUS: %.def %.din

$(LIBPFX)PLUInC.$(LIBEXT):	lu_PLU2_1C_x10stub.o PLUInC.o \
	lu_PLU2_1C_x10stub.c PLUInC.c

.PHONY:	checkjava clean

checkjava:
ifeq (${JAVA_HOME},)
	@echo JAVA_HOME is not set, unable to find java installation
else
endif

clean:
	rm -f *.o $(LIBPFX)PLUInC.$(LIBEXT)
