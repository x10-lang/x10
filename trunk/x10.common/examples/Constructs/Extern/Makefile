# GNU Makefile for building X10 extern shared libraries.
# This was tested on Windows/Cygwin, Linux, and AIX (with both gcc and xlC).
# See implementation file headers for instructions on building the shared
# libraries using different compilers.

# FIXME: For some reason the shared libs are always rebuilt, even if nothing
# changed.

UNAME:=$(shell uname -s)
LIBPFX=lib
LIBEXT=so
CFLAGS="-I${JAVA_HOME}/include" $(CFLAGSEXTRA)
ifneq ($(UNAME),AIX)
ifneq ($(UNAME),Linux)
# Must be Windows/Cygwin
LIBPFX=
LIBEXT=dll
CFLAGSEXTRA="-I${JAVA_HOME}/include/win32"
endif
endif

all:	$(LIBPFX)BasicExtern1.$(LIBEXT) \
	$(LIBPFX)IntArrayExternUnsafe.$(LIBEXT) \
	$(LIBPFX)JacobiExtern.$(LIBEXT) \
	$(LIBPFX)RandomAccessLong.$(LIBEXT)

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
	cl $(CFLAGS) -LD -Fe$@ $(filter %.o,$^)
%.o:	%.c
	cl $(CFLAGS) -c $<
%.o:	%.cpp
	cl $(CFLAGS) -c $<
endif
else				# Got GCC
CC=gcc $(CCEXTRA)
CXX=g++ $(CCEXTRA)
CXXFLAGS=$(CFLAGS)

$(LIBPFX)%.$(LIBEXT):	checkjava
	$(CXX) $(CFLAGS) -shared $(LDFLAGS) -o $@ $(filter %.o,$^)

ifneq ($(UNAME),AIX)
ifneq ($(UNAME),Linux)
# Must be Cygwin
DLLWRAP=dllwrap $(CCEXTRA)
CCEXTRA=-mno-cygwin

$(LIBPFX)%.$(LIBEXT):	checkjava dll_entry.o
	@$(MAKE) $*.def __DEPS__="$^"
	$(DLLWRAP) $(CFLAGS) -shared $(LDFLAGS) -o $@ --def $*.def $(filter %.o,$^)
	$(RM) $*.def

ifneq ($(__DEPS__),)
%.din:	dll_entry.o $(__DEPS__)
	$(CXX) $(CFLAGS) -shared $(LDFLAGS) -o /dev/null -Wl,--output-def=$@ $(filter %.o,$^)

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

endif

#.PRECIOUS: %.def %.din

$(LIBPFX)BasicExtern1.$(LIBEXT):	BasicExternTest1Impl.o \
	BasicExternTest1_x10stub.c BasicExternTest1_00024C_x10stub.c

$(LIBPFX)IntArrayExternUnsafe.$(LIBEXT):	IntArrayExternUnsafeImpl.o \
	IntArrayExternUnsafe_x10stub.c

$(LIBPFX)JacobiExtern.$(LIBEXT):	JacobiExternImpl.o \
	JacobiExtern_x10stub.c

$(LIBPFX)RandomAccessLong.$(LIBEXT):	RandomAccess_longExternImpl.o \
	RandomAccess_1longExtern_x10stub.c

.PHONY:	checkjava
checkjava:
ifeq (${JAVA_HOME},)
	@echo JAVA_HOME is not set, unable to find java installation
	@false
else
	@true
endif

