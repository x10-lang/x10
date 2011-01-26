#########################
# User-servicable Parts #
#########################

include ../Make.rules

FIND ?= find
XARGS ?= xargs

INCLUDE_DIRS += -I. -I../x10rt/include

ifdef ENABLE_GC
  override CXXFLAGS += -DX10_USE_BDWGC
  BDWGC_INCLUDE_DIR= ./bdwgc/install/include
  INCLUDE_DIRS += -I"$(BDWGC_INCLUDE_DIR)"
  XRX_PROPS_CXXFLAGS += -DX10_USE_BDWGC
  XRX_PROPS_LDLIBS += -lgc
endif

override CXXFLAGS += $(INCLUDE_DIRS) -Igen


INSTDIR = $(call abspath,../../x10.dist)

###########
# Targets #
###########

XRX_ARCHIVE = $(LIBPREFIX)x10$(LIBSUFFIX)
ifeq ($(X10RT_PLATFORM),cygwin)
  XRX_DEP_LIBS = -L"$(INSTDIR)"/lib -lx10rt_$(DEFAULT_X10RT_LIB)
  ifdef ENABLE_GC
    override XRX_DEP_LIBS += -lgc
  endif
endif
XRX_MANIFEST = libx10.mft
XRX_PROPERTIES = libx10.properties

# this builds everything
xrx: $(XRX_ARCHIVE)

.PHONY: xrx $(XRX_PROPERTIES)

export CXX
export MPICXX
export AR
export CXXFLAGS
export ARFLAGS

###################
# Generated Files #
###################

# First thing to do: compile x10 files to cc,h


# A list of all .x10 files (relative to the src-cpp dir) that we should build
# into the xrx lib.
# prune .svn dirs (they probably don't contain *.x10 files so this is just an optimisation)
#
# The sort is a workaround for XTENLANG-676.  The order in which files are listed on the x10c++ commandline
# appears to affect the results of typechecking.  By sorting, we force a deterministic order of files.
XRXFILES  = $(shell $(FIND) ../src-x10 -name .svn -prune -o -name '*.x10' -print | sort )

#enable asserts, do not look in jar for x10 files
X10CPPFLAGS += -J-Xmx1024M -J-ea -rtdev -commandlineonly

# Generate .cc and .h files for all the X10 classes defined in the above
# .x10 files.  Rebuilding occurs if the x10 backend is updated or if any of the
# currently present x10 files are newer than the 'all-cpp-generated' file.
# Note that nothing gets rebuilt if one of the .x10 files is deleted, however
# this simply means there will be more symbols in the archive than there should
# be.  This is harmless and equivalent to the old behaviour of this Makefile,
# i.e. a make clean is needed.
SPACE = $(EMPTY) $(EMPTY)
gen/all-cpp-generated: $(XRXFILES) $(subst $(SPACE),\$(SPACE),$(INSTDIR))/lib/x10c.jar
	@echo "Regenerating XRX cc/h files"
	mkdir -p gen
	"$(INSTDIR)"/bin/x10c++ -c $(X10CPPFLAGS) -sourcepath ../src-x10 -d ../src-cpp/gen \
	    $(if $(filter %.jar,$?),$(XRXFILES),$?);
	touch $@

# $(XRX_MANIFEST) is a list of all .cc files (relative to the ./gen dir) that
# should be included in the $(XRX_ARCHIVE).
$(XRX_MANIFEST): gen/all-cpp-generated
	(cd gen && $(FIND) * -name \*.cc -print) > $@
	@#prune .svn dirs (they probably don't contain *.x10 files so this is just an optimisation)
	(cd ../src-x10 && $(FIND) * -name .svn -prune -o -name '*.x10' -print) >> $@
	echo x10.jar >> $@ # FIXME: hard-coded JAR name




##########################
# Manually-written Files #
##########################

ALL_XRCPP_OBJECTS = \
	x10aux/alloc.o \
	x10aux/assert.o \
	x10aux/atomic_ops.o \
	x10aux/atomic_boolean_funs.o \
	x10aux/atomic_int_funs.o \
	x10aux/atomic_long_funs.o \
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
	x10aux/lock.o \
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
	x10aux/throw.o \
	x10aux/debug.o \
	x10/io/FileReader__FileInputStream.o \
	x10/io/FileWriter__FileOutputStream.o \
	x10/io/File__NativeFile.o \
	x10/io/InputStreamReader__InputStream.o \
	x10/io/OutputStreamWriter__OutputStream.o \
	x10/lang/Any.o \
	x10/lang/Closure.o \
	x10/lang/Comparable.o \
	x10/lang/Fun.o \
	x10/lang/GlobalRef.o \
	x10/lang/IBox.o \
	x10/lang/Object.o \
	x10/lang/Rail.o \
	x10/lang/Reference.o \
	x10/lang/String.o \
	x10/lang/Struct.o \
	x10/lang/Throwable.o \
	x10/lang/Deque.o \
	x10/lang/Lock__ReentrantLock.o \
	x10/lang/PlaceLocalHandle_Impl.o \
	x10/lang/Thread.o \
	x10/util/GrowableRail.o \
	x10/util/IndexedMemoryChunk.o \
	x10/util/RemoteIndexedMemoryChunk.o \
	x10/util/concurrent/AtomicReference.o 

ALL_MANUAL_OBJECTS=$(ALL_XRCPP_OBJECTS)

# This target will build the XRX cc files into object files
gen/all-o-generated: gen/all-cpp-generated $(XRX_MANIFEST) 
	$(MAKE) CXX_NO_WARNINGS=1 $(shell sed -e 's@^\(.*\)\.cc$$@gen/\1.o@' -e '/\.x10$$/d' -e '/\.jar$$/d' $(XRX_MANIFEST)) 
	touch $@

ifdef X10_STATIC_LIB
$(XRX_ARCHIVE): gen/all-o-generated $(XRX_MANIFEST) $(ALL_MANUAL_OBJECTS)
	$(AR) $(ARFLAGS) $@ \
	    $(shell sed -e 's@^\(.*\)\.cc$$@gen/\1.o@' -e '/\.x10$$/d' -e '/\.jar$$/d' $(XRX_MANIFEST)) \
            $(ALL_MANUAL_OBJECTS)
else
$(XRX_ARCHIVE): gen/all-o-generated $(XRX_MANIFEST) $(ALL_MANUAL_OBJECTS)
ifeq ($(X10RT_PLATFORM),aix_xlc)
	$(SHLINK) $(CXXFLAGS) $(CXXFLAGS_SHARED) $(LDFLAGS_SHARED) -o $@ \
	    $(shell sed -e 's@^\(.*\)\.cc$$@gen/\1.o@' -e '/\.x10$$/d' -e '/\.jar$$/d' $(XRX_MANIFEST)) \
            $(ALL_MANUAL_OBJECTS) \
	    $(XRX_DEP_LIBS)
else
	$(CXX) $(CXXFLAGS) $(CXXFLAGS_SHARED) $(LDFLAGS_SHARED) -o $@ \
	    $(shell sed -e 's@^\(.*\)\.cc$$@gen/\1.o@' -e '/\.x10$$/d' -e '/\.jar$$/d' $(XRX_MANIFEST)) \
            $(ALL_MANUAL_OBJECTS) \
	    $(XRX_DEP_LIBS)
endif
endif


$(XRX_PROPERTIES):
	@echo "PLATFORM=$(X10RT_PLATFORM)" > $@
	@echo "CXXFLAGS=$(XRX_PROPS_CXXFLAGS)" >> $@
	@echo "LDFLAGS=$(XRX_PROPS_LDFLAGS)" >> $@
	@echo "LDLIBS=$(XRX_PROPS_LDLIBS)" >> $@


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

install: $(XRCPP_ARCHIVE) $(XRX_ARCHIVE) $(XRX_PROPERTIES)
	@echo "Installing into $(INSTDIR)"
	@mkdir -p "$(INSTDIR)"/include "$(INSTDIR)"/lib
	@(cd gen && $(FIND) . -name \*.h | tar cf - $(INC_FLAG)) | (cd "$(INSTDIR)"/include && tar xf -)
	@(cd gen && $(FIND) . -name \*.struct_h | tar cf - $(INC_FLAG)) | (cd "$(INSTDIR)"/include && tar xf -)
	@$(FIND) x10 -name \*.h | tar cf - $(INC_FLAG) | (cd "$(INSTDIR)"/include && tar xf -)
	@$(FIND) x10 -name \*.struct_h | tar cf - $(INC_FLAG) | (cd "$(INSTDIR)"/include && tar xf -)
	@$(FIND) x10aux -name \*.h | tar cf - $(INC_FLAG) | (cd "$(INSTDIR)"/include && tar xf -)
	@$(FIND) x10aux -name \*.cuh | tar cf - $(INC_FLAG) | (cd "$(INSTDIR)"/include && tar xf -)
	@cp -p x10rt.h "$(INSTDIR)"/include
	@cp -p $(XRX_ARCHIVE) "$(INSTDIR)"/lib
	@cp -p $(XRX_MANIFEST) "$(INSTDIR)"/lib
	@cp -p $(XRX_PROPERTIES) "$(INSTDIR)"/etc

.PHONY: install



##################
# Standard Stuff #
##################

# disable default rules
%.o:
%.o:%.c
%.o:%.cc
%:%.o
%:%.c
%:%.cc

# want to make sure the x10 code has been compiled to cc,h since the
# manually written code may #include some of the generated headers

%.o: %.cc gen/all-cpp-generated 
	$(CXX) $(CXXFLAGS) $(CXX_WARNING_FLAGS) $(CXXFLAGS_SHARED) -c $< -o $@
%.o: %.cc %.h gen/all-cpp-generated 
	$(CXX) $(CXXFLAGS)  $(CXX_WARNING_FLAGS) $(CXXFLAGS_SHARED) -c $< -o $@


depend:
	touch depend.mk
	$(FIND) . -name gen -prune -o \( -name '*.cc' -o -name '*.h' \) -print0 | $(XARGS) -r0 makedepend -I $(INCLUDE_DIRS) -fdepend.mk 


#nuke only object code
cleanobj:
	$(FIND) . \( -name '*.h.gch' -o -name '*.o' -o -name '*.rpo' \) -print0 | $(XARGS) -0 -t $(RM) -r

#nuke everything
cleangen:
	-$(RM) -r gen $(XRX_ARCHIVE) $(XRX_MANIFEST) $(XRX_PROPERTIES)

clean: cleanobj cleangen

squeakyclean: clean
	$(FIND) . \( -name '*~' -o -name '*.bak' \) -print0 | $(XARGS) -r0 -t $(RM)


.PHONY: depend clean cleanobj cleangen squeakyclean


# Optional file -- generated by makedepend (run "make depend").
-include depend.mk



# vim:tabstop=8:shiftwidth=8:noexpandtab
