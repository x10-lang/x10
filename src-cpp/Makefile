#########################
# User-servicable Parts #
#########################

CXX ?= g++
AR ?= ar

X10LIB ?= $(abspath ../../pgas/common/work)
CXXFLAGS += -g
INCLUDE_DIRS += -I$(X10LIB)/include -I.

ifdef ENABLE_GC
  CXXFLAGS += -DX10_USE_BDWGC
  BDWGC_INCLUDE_DIR= $(abspath ./bdwgc/install/include)
  INCLUDE_DIRS += -I$(BDWGC_INCLUDE_DIR)
endif

CXXFLAGS += $(INCLUDE_DIRS)


ifeq ($(shell uname -s),AIX)

  ifdef USE_XLC
    CXX = mpCC_r
    override CXXFLAGS -= -g
    override CXXFLAGS += -q64
    override CXXFLAGS += -O3 -qinline -qarch=pwr5 -qtune=pwr5 -qhot
    override CXXFLAGS += -qrtti=all
  else
    override CXXFLAGS += -maix64
  endif

  # FIXME
  ARFLAGS := -X64 $(ARFLAGS)

else

  override CXXFLAGS += -ansi -pedantic -Wall -Wextra -Wno-long-long -Wno-unused-parameter -fpermissive
  ifeq ($(shell uname -s),Linux)
    override CXXFLAGS += -pthread
  endif

endif

INSTDIR = $(abspath ../../x10.dist)

XRCPP_ARCHIVE = libx10rt17.a
XRX_ARCHIVE = libx10lib17.a
XRX_MANIFEST = libx10lib17.mft



###########
# Targets #
###########

all: $(X10LIB)/include/x10/x10.h $(XRCPP_ARCHIVE) 

xrx: $(XRX_ARCHIVE)

.PHONY: all xrx



################################
# XRCPP Manually-written Files #
################################

ALL_XRCPP_OBJECTS = \
	x10aux/alloc.o \
	x10aux/assert.o \
	x10aux/basic_functions.o \
	x10aux/boolean_utils.o \
	x10aux/bootstrap.o \
	x10aux/byte_utils.o \
	x10aux/char_utils.o \
	x10aux/double_utils.o \
	x10aux/int_utils.o \
	x10aux/init_dispatcher.o \
	x10aux/io/FILEPtrInputStream.o \
	x10aux/io/FILEPtrOutputStream.o \
	x10aux/io/FILEPtrStream.o \
	x10aux/float_utils.o \
	x10aux/long_utils.o \
	x10aux/math_utils.o \
	x10aux/pgas.o \
	x10aux/ref.o \
	x10aux/RTT.o \
	x10aux/rail_utils.o \
	x10aux/deserialization_dispatcher.o \
	x10aux/short_utils.o \
	x10aux/string_utils.o \
	x10aux/system_utils.o \
	x10/io/EOFException.o \
	x10/io/FileInputStream.o \
	x10/io/FileOutputStream.o \
	x10/io/FileNotFoundException.o \
	x10/io/IOException.o \
	x10/io/NativeFile.o \
	x10/io/NativeInputStream.o \
	x10/io/NativeOutputStream.o \
	x10/lang/ArrayIndexOutOfBoundsException.o \
	x10/lang/BadPlaceException.o \
	x10/lang/ClassCastException.o \
	x10/lang/Exception.o \
	x10/lang/VoidFun_0_0.o \
	x10/lang/NullPointerException.o \
	x10/lang/Object.o \
	x10/lang/Ref.o \
	x10/lang/RuntimeException.o \
	x10/lang/String.o \
	x10/lang/Throwable.o \
	x10/lang/Value.o \
	x10/runtime/InterruptedException.o \
	x10/runtime/Lock.o \
	x10/runtime/Thread.o 


$(XRCPP_ARCHIVE): $(ALL_XRCPP_OBJECTS)
	$(AR) $(ARFLAGS) $@ $^



#########################
# XRX 'Generated' Files #
#########################

# A list of all .x10 files (relative to the src-cpp dir) that we should build
# into the xrx lib.
XRXFILES  = $(shell find ../src-x10 -name '*.x10')

# Generate .cc .inc and .h files for all the X10 classes defined in the above
# .x10 files.  Rebuilding occurs if the x10 backend is updated or if any of the
# currently present x10 files are newer than the 'all-cpp-generated' file.
# Note that nothing gets rebuilt if one of the .x10 files is deleted, however
# this simply means there will be more symbols in the archive than there should
# be.  This is harmless and equivalent to the old behaviour of this Makefile,
# i.e. a make clean is needed.
gen/all-cpp-generated: $(XRXFILES) $(INSTDIR)/lib/x10cpp.jar
	@echo "Regenerating XRX cc/h/inc files"
	mkdir -p gen
	cd ../src-x10 && find * -name .svn -prune -o -name \*.x10 -print | xargs "$(INSTDIR)"/bin/x10c++ -d ../src-cpp/gen -J-ea -rtdev -sourcepath . -disable CheckNativeAnnotations -commandlineonly -c
	touch $@

# $(XRX_MANIFEST) is a list of all .cc files (relative to the ./gen dir) that
# should be included in the $(XRX_ARCHIVE).  There is a hack to ensure the
# contents of DEPENDENCIES.txt appear before everything else.
$(XRX_MANIFEST): gen/all-cpp-generated
	(cd gen && find * -name \*.cc -print) | grep -f DEPENDENCIES.txt -v > gen/MANIFEST.raw
	cat DEPENDENCIES.txt gen/MANIFEST.raw > $@

# This target will build the XRX cc files into object files
gen/all-o-generated: gen/all-cpp-generated $(XRX_MANIFEST)
	CXXFLAGS="$(CXXFLAGS) -Igen" $(MAKE) $(shell sed -e 's@^\(.*\).cc$$@gen/\1.o@' $(XRX_MANIFEST))
	touch $@

$(XRX_ARCHIVE): gen/all-o-generated $(XRX_MANIFEST)
	cd gen && $(AR) $(ARFLAGS) $@ $(shell sed -e 's@.cc$$@.o@' $(XRX_MANIFEST))
	@-cp gen/$@ .

# The dummy files are needed to avoid rebuilds so must not be removed as
# intermediate files usually are.
.PRECIOUS: gen/all-cpp-generated gen/all-o-generated



################### 
# Install Targets #
###################

# FIXME: remove the part of the rule that copies the .inc files as soon as we get rid of them..
install: $(XRCPP_ARCHIVE) $(XRX_ARCHIVE)
	mkdir -p $(INSTDIR)/include $(INSTDIR)/lib
	(cd gen && find . -name \*.h | tar -T - -cf -) | tar -C $(INSTDIR)/include -xvf -
	(cd gen && find . -name \*.inc | tar -T - -cf -) | tar -C $(INSTDIR)/include -xvf -
	find x10 -name \*.h | tar -T - -cf - | tar -C $(INSTDIR)/include -xvf -
	find x10aux -name \*.h | tar -T - -cf - | tar -C $(INSTDIR)/include -xvf -
	cp -p x10rt17.h $(INSTDIR)/include
	cp -p $(XRCPP_ARCHIVE) $(INSTDIR)/lib
	cp -p $(XRX_ARCHIVE) $(INSTDIR)/lib
	cp -p $(XRX_MANIFEST) $(INSTDIR)/lib

.PHONY: install



##################
# Standard Stuff #
##################

%.o: %.cc %.h
	$(CXX) $(CXXFLAGS) -c $< -o $@


depend:
	find . -name gen -prune -o \( -name '*.cc' -o -name '*.h' \) -print0 | xargs -r0 makedepend -I $(INCLUDE_DIRS) -f- > depend.mk 


clean:
	find . -name gen -prune -o \( -name '*.h.gch' -o -name '*.o' \) -print0 | xargs -r0 -t rm -fr
	rm -rf gen $(XRCPP_ARCHIVE) $(XRX_ARCHIVE) $(XRX_MANIFEST)


squeakyclean: clean
	find . \( -name '*~' -o -name '*.bak' \) -print0 | xargs -r0 -t rm -f


.PHONY: depend clean squeakyclean


# Optional file -- generated by makedepend (run "make depend").
-include depend.mk



# vim:tabstop=8:shiftwidth=8:noexpandtab
