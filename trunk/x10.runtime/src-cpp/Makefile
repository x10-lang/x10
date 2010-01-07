#########################
# User-servicable Parts #
#########################

CXX ?= g++
AR ?= ar

__m_have_abspath:=$(abspath /)
ifndef __m_have_abspath
  abspath=$(shell cd $1 && /bin/pwd)
else
  abspath=$(abspath $1)
endif

CXXFLAGS += -g
INCLUDE_DIRS += -I. -Ix10rt/include

ifdef ENABLE_GC
  CXXFLAGS += -DX10_USE_BDWGC
  BDWGC_INCLUDE_DIR= ./bdwgc/install/include
  INCLUDE_DIRS += -I$(BDWGC_INCLUDE_DIR)
endif

#avoid specifying -NDEBUG twice
ifdef OPTIMIZE
  USE_NDEBUG := -DNDEBUG
  X10CPPFLAGS += -optimize
endif
ifdef NO_CHECKS
  USE_NDEBUG := -DNDEBUG
  CXXFLAGS += -DNO_CHECKS
  X10CPPFLAGS += -NO_CHECKS
endif
CXXFLAGS += $(USE_NDEBUG)

ifdef USE_BFD
  CXXFLAGS += -DUSE_BFD
endif

CXXFLAGS += $(INCLUDE_DIRS) -Igen

ifndef NO_VAR_OVERRIDE

ifdef ENABLE_CROSS_COMPILE_BGP
  override MPICXX = /bgsys/drivers/ppcfloor/comm/bin/mpicxx
  #override CXX = /bgsys/ibm_compilers/gcc-4.3.2/gnu-linux/bin/powerpc-bgp-linux-g++
  #override CC =/bgsys/ibm_compilers/gcc-4.3.2/gnu-linux/bin/powerpc-bgp-linux-gcc
  override CXX = /bgsys/drivers/ppcfloor/gnu-linux/bin/powerpc-bgp-linux-g++
  override CC =/bgsys/drivers/ppcfloor/gnu-linux/bin/powerpc-bgp-linux-gcc
  export X10RT_PLATFORM="bgp"
else

  ifeq ($(shell uname -s),AIX)


    ifdef USE_GCC
      export X10RT_PLATFORM="aix_gcc"
      ifdef OPTIMIZE
        override CXXFLAGS += -O2 -finline-functions
      endif
      override CXXFLAGS += -maix64
      MPICXX ?= mpicxx
    else	
      export X10RT_PLATFORM="aix_xlc"
      CXX = mpCC_r
      MPICXX ?= mpCC_r
      override CXXFLAGS -= -g
      ifndef USE_32BIT
        override CXXFLAGS += -q64
      else
        override CXXFLAGS += -q32
      endif
      ifdef OPTIMIZE
        override CXXFLAGS += -O3 -qinline -qarch=pwr5 -qtune=pwr5 -qhot
      endif
      override CXXFLAGS += -qrtti=all
      override CXXFLAGS += -qsuppress=1540-0809:1500-029:1540-1101
      override CXXFLAGS += $(XLC_EXTRA_FLAGS)
      ifeq ($(AR),ar) # use AIX default ar
        AR = /usr/bin/ar
      endif
    endif

    # FIXME
    ifndef USE_32BIT
      ARFLAGS := -X64 $(ARFLAGS)
    else
      ARFLAGS := -X32 $(ARFLAGS)
    endif

  else

    MPICXX ?= mpicxx

    ifdef OPTIMIZE
      override CXXFLAGS += -O2 -finline-functions
    endif

    override CXXFLAGS += -ansi -pedantic -Wall -Wextra -Wno-long-long -Wno-unused-parameter
    ifeq ($(shell uname -s),Linux)
      override CXXFLAGS += -pthread
      ifeq ($(shell uname -m),x86_64)
        export X10RT_PLATFORM="linux_x86_64"
      else
        ifeq ($(shell uname -m),ppc64)
          export X10RT_PLATFORM="linux_ppc_64"
        else
          export X10RT_PLATFORM="linux_x86_32"
        endif
      endif
    else
      ifeq ($(firstword $(subst _, ,$(shell uname -s))),CYGWIN)
        export X10RT_PLATFORM="cygwin"
      else
        ifeq ($(shell uname -s),Darwin)
          export X10RT_PLATFORM=$(shell echo | gcc -E -dM - | grep -q x86_64 && echo darwin64 || echo darwin)
        else
          ifeq ($(shell uname -s),SunOS)
            export X10RT_PLATFORM="sunos"
          endif
        endif
      endif
    endif
  endif
endif

endif

INSTDIR = $(call abspath,../../x10.dist)




###########
# Targets #
###########

XRX_ARCHIVE = libx10.a
XRX_MANIFEST = libx10.mft

# this builds everything
xrx: x10rt $(XRX_ARCHIVE)

.PHONY: xrx
.PHONY: x10rt

export CXX
export MPICXX
export AR
export CXXFLAGS
export ARFLAGS

# why must X10RT_PLATFORM be specified explicitly?
x10rt:
	cd x10rt && $(MAKE) -j1 X10RT_PLATFORM=$(X10RT_PLATFORM) debug default

###################
# Generated Files #
###################

# First thing to do: compile x10 files to cc,h,inc


# A list of all .x10 files (relative to the src-cpp dir) that we should build
# into the xrx lib.
# prune .svn dirs (they probably don't contain *.x10 files so this is just an optimisation)
#
# The sort is a workaround for XTENLANG-676.  The order in which files are listed on the x10c++ commandline
# appears to affect the results of typechecking.  By sorting, we force a deterministic order of files.
XRXFILES  = $(shell find ../src-x10 -name .svn -prune -o -name '*.x10' -print | sort )

#enable asserts, do not look in jar for x10 files
X10CPPFLAGS += -J-ea -rtdev -disable CheckNativeAnnotations -commandlineonly

# Generate .cc .inc and .h files for all the X10 classes defined in the above
# .x10 files.  Rebuilding occurs if the x10 backend is updated or if any of the
# currently present x10 files are newer than the 'all-cpp-generated' file.
# Note that nothing gets rebuilt if one of the .x10 files is deleted, however
# this simply means there will be more symbols in the archive than there should
# be.  This is harmless and equivalent to the old behaviour of this Makefile,
# i.e. a make clean is needed.
gen/all-cpp-generated: $(XRXFILES) $(INSTDIR)/lib/x10c.jar
	@echo "Regenerating XRX cc/h/inc files"
	mkdir -p gen
	"$(INSTDIR)"/bin/x10c++ -c $(X10CPPFLAGS) -sourcepath ../src-x10 -d ../src-cpp/gen \
	    $(if $(filter %.jar,$?),$(XRXFILES),$?);
	touch $@

# $(XRX_MANIFEST) is a list of all .cc files (relative to the ./gen dir) that
# should be included in the $(XRX_ARCHIVE).
$(XRX_MANIFEST): gen/all-cpp-generated
	(cd gen && find * -name \*.cc -print) > $@
	@#prune .svn dirs (they probably don't contain *.x10 files so this is just an optimisation)
	(cd ../src-x10 && find * -name .svn -prune -o -name '*.x10' -print) >> $@
	echo x10.jar >> $@ # FIXME: hard-coded JAR name




##########################
# Manually-written Files #
##########################

ALL_XRCPP_OBJECTS = \
	x10aux/alloc.o \
	x10aux/assert.o \
	x10aux/atomic_ops.o \
	x10aux/basic_functions.o \
	x10aux/boolean_utils.o \
	x10aux/bootstrap.o \
	x10aux/byte_utils.o \
	x10aux/char_utils.o \
	x10aux/class_cast.o \
	x10aux/config.o \
	x10aux/double_utils.o \
	x10aux/int_utils.o \
	x10aux/init_dispatcher.o \
	x10aux/static_init.o \
	x10aux/itables.o \
	x10aux/io/FILEPtrInputStream.o \
	x10aux/io/FILEPtrOutputStream.o \
	x10aux/io/FILEPtrStream.o \
	x10aux/float_utils.o \
	x10aux/long_utils.o \
	x10aux/network.o \
	x10aux/place_local.o \
	x10aux/ref.o \
	x10aux/reference_logger.o \
	x10aux/RTT.o \
	x10aux/rail_utils.o \
	x10aux/deserialization_dispatcher.o \
	x10aux/serialization.o \
	x10aux/short_utils.o \
	x10aux/string_utils.o \
	x10aux/system_utils.o \
	x10/io/FileReader__FileInputStream.o \
	x10/io/FileWriter__FileOutputStream.o \
	x10/io/File__NativeFile.o \
	x10/io/InputStreamReader__InputStream.o \
	x10/io/OutputStreamWriter__OutputStream.o \
	x10/lang/Any.o \
	x10/lang/Closure.o \
	x10/lang/Fun.o \
	x10/lang/Object.o \
	x10/lang/Rail.o \
	x10/lang/Ref.o \
	x10/lang/Reference.o \
	x10/lang/String.o \
	x10/lang/Struct.o \
	x10/lang/Throwable.o \
	x10/lang/ValRail.o \
	x10/runtime/Deque.o \
	x10/runtime/Lock__ReentrantLock.o \
	x10/lang/PlaceLocalHandle.o \
	x10/lang/Thread.o \
	x10/util/GrowableRail.o \
	x10/util/concurrent/atomic/AtomicBoolean.o \
	x10/util/concurrent/atomic/AtomicInteger.o \
	x10/util/concurrent/atomic/AtomicLong.o \
	x10/util/concurrent/atomic/AtomicReference.o 

ALL_MANUAL_OBJECTS=$(ALL_XRCPP_OBJECTS)




# This target will build the XRX cc files into object files
gen/all-o-generated: gen/all-cpp-generated $(XRX_MANIFEST)
	$(MAKE) $(shell sed -e 's@^\(.*\)\.cc$$@gen/\1.o@' -e '/\.x10$$/d' -e '/\.jar$$/d' $(XRX_MANIFEST))
	touch $@

$(XRX_ARCHIVE): gen/all-o-generated $(XRX_MANIFEST)
	$(MAKE) $(ALL_MANUAL_OBJECTS)
	$(AR) $(ARFLAGS) $@ \
	    $(shell sed -e 's@^\(.*\)\.cc$$@gen/\1.o@' -e '/\.x10$$/d' -e '/\.jar$$/d' $(XRX_MANIFEST)) \
            $(ALL_MANUAL_OBJECTS)

# The dummy files are needed to avoid rebuilds so must not be removed as
# intermediate files usually are.
.PRECIOUS: gen/all-cpp-generated gen/all-o-generated



################## 
# Install Targets #
###################

ifeq ($(findstring Usage,$(shell tar --version 2>&1)),Usage)  # non-GNU tar
  INC_FLAG=-I /dev/stdin
else
  INC_FLAG=-T -
endif

# FIXME: remove the part of the rule that copies the .inc files as soon as we get rid of them..
install: $(XRCPP_ARCHIVE) $(XRX_ARCHIVE)
	mkdir -p $(INSTDIR)/include $(INSTDIR)/lib
	(cd gen && find . -name \*.h | tar cf - $(INC_FLAG)) | (cd $(INSTDIR)/include && tar xvf -)
	(cd gen && find . -name \*.struct_h | tar cf - $(INC_FLAG)) | (cd $(INSTDIR)/include && tar xvf -)
	(cd gen && find . -name \*.inc | tar cf - $(INC_FLAG)) | (cd $(INSTDIR)/include && tar xvf -)
	find x10 -name \*.h | tar cf - $(INC_FLAG) | (cd $(INSTDIR)/include && tar xvf -)
	find x10 -name \*.struct_h | tar cf - $(INC_FLAG) | (cd $(INSTDIR)/include && tar xvf -)
	find x10aux -name \*.h | tar cf - $(INC_FLAG) | (cd $(INSTDIR)/include && tar xvf -)
	cp -p x10rt.h $(INSTDIR)/include
	cp -p $(XRX_ARCHIVE) $(INSTDIR)/lib
	cp -p $(XRX_MANIFEST) $(INSTDIR)/lib
	cd x10rt && $(MAKE) -j1 debug all install X10RT_PLATFORM=$(X10RT_PLATFORM)

.PHONY: install



##################
# Standard Stuff #
##################

# disable default rules
%:%.o
%:%.c
%:%.cc

# want to make sure the x10 code has been compiled to cc,h,inc since the
# manually written code may #include some of the generated headers

%.o: %.cc %.h gen/all-cpp-generated 
	$(CXX) $(CXXFLAGS) -c $< -o $@


depend:
	find . -name gen -prune -o \( -name '*.cc' -o -name '*.h' \) -print0 | xargs -r0 makedepend -I $(INCLUDE_DIRS) -fdepend.mk 


#nuke only object code
cleanobj:
	find . \( -name '*.h.gch' -o -name '*.o' -o -name '*.rpo' \) -print0 | xargs -0 -t rm -fr

#nuke everything
cleangen:
	rm -rf gen $(XRX_ARCHIVE) $(XRX_MANIFEST)

clean: cleanobj cleangen
	cd x10rt && $(MAKE) -j1 debug clean X10RT_PLATFORM=$(X10RT_PLATFORM)

squeakyclean: clean
	find . \( -name '*~' -o -name '*.bak' \) -print0 | xargs -r0 -t rm -f


.PHONY: depend clean cleanobj cleangen squeakyclean


# Optional file -- generated by makedepend (run "make depend").
-include depend.mk



# vim:tabstop=8:shiftwidth=8:noexpandtab
