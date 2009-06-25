# GNU Makefile for building X10 extern shared libraries.
# This was tested on Windows/Cygwin, Linux, and AIX (with both gcc and xlC).
# See implementation file headers for instructions on building the shared
# libraries using different compilers.
# FIXME: For some reason the shared libs are always rebuilt, even if nothing
# changed.

UNAME:=$(shell uname -s)
ifneq ($(UNAME),AIX)
ifneq ($(UNAME),Linux)
CYGWIN=yes
endif
endif
LIBPFX=lib
LIBEXT=so

#Specify the location of fftw3 library in the following, and if the thread-safe option 
#is chosen, pthreads library is needed.
#----example for AIX----
#CFLAGS="-I${JAVA_HOME}/include" $(CFLAGSEXTRA) "-I${HOME}/fftw3/include"
#LDFLAGS="-L${HOME}/fftw3/lib"
#LDLIBS= -lpthreads -l fftw3 -lm
#----example for cygwin----
CFLAGS="-I${JAVA_HOME}/include" $(CFLAGSEXTRA) -I/cygdrive/c/FFTW/fftw-3.1.2-dll 
LDFLAGS= -L/cygdrive/c/FFTW/fftw-3.1.2-dll
LDLIBS=-l fftw3-3  -lm

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

all:	$(LIBPFX)ft.$(LIBEXT) \
	$(LIBPFX)ftStatic.$(LIBEXT) 
#	$(LIBPFX)test.$(LIBEXT)
#	$(LIBPFX)ftTest.$(LIBEXT)
	
ifeq ($(shell gcc --version),)	# Test for systems without GCC
ifeq ($(UNAME),AIX)	# AIX
$(LIBPFX)%.$(LIBEXT):	checkjava
	xlC $(CCFLAGS) $(CFLAGS) -qmkshrobj -o $@ $(filter %.o,$^)
%.o:	%.c
	xlC $(CCFLAGS) $(CFLAGS) -c -o $@ $<
%.o:	%.cpp
	xlC $(CCFLAGS) $(CFLAGS) -c -o $@ $<
else			# Must be Windows
$(LIBPFX)%.$(LIBEXT):	checkjava
	cl $(CFLAGS) -LD -Fe$@ $(filter %.o,$^) $(LDLIBS)
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
$(LIBPFX)%.$(LIBEXT):	checkjava
	$(CXX) $(CFLAGS) -shared $(LDFLAGS)  -o $@ $(filter %.o,$^) $(LDLIBS) 

else				# Must be Cygwin
DLLWRAP=dllwrap $(CCEXTRA)
CCEXTRA=-mno-cygwin

$(LIBPFX)%.$(LIBEXT):	checkjava dll_entry.o
	@$(MAKE) $*.def __DEPS__="$^"
	$(DLLWRAP) $(CFLAGS) -shared $(LDFLAGS)  -o $@ --def $*.def $(filter %.o,$^)  $(LDLIBS)   
	$(RM) $*.def 

ifneq ($(__DEPS__),)
%.din:	dll_entry.o $(__DEPS__)
	$(CXX) $(CFLAGS) -shared $(LDFLAGS) -o $*.tmp.dll -Wl,--output-def=$@ $(filter %.o,$^)  $(LDLIBS)  
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



$(LIBPFX)ft.$(LIBEXT):	 c_randdp.o fft_fftw3.o Ft.o  \
	Ft_x10stub.c

$(LIBPFX)ftStatic.$(LIBEXT):	 c_randdp.o fft_fftw3.o FtStatic.o  \
	FtStatic_x10stub.c

#$(LIBPFX)ftTest.$(LIBEXT):	xsupport1d.o \
#	ftTest_x10stub.c

#$(LIBPFX)test.$(LIBEXT):	Test.o \
#	Test_x10stub.c

.PHONY:	checkjava
checkjava:
ifeq (${JAVA_HOME},)
	@echo JAVA_HOME is not set, unable to find java installation
	@false
else
	@true
endif

