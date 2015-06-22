#########################
# User-servicable Parts #
#########################

include ../Make.rules

FIND ?= find
XARGS ?= xargs

#############
# Variables #
#############

DISTDIR = $(call abspath,../../x10.dist)
INSTDIR = $(DISTDIR)/stdlib

ifdef DEBUG
    GEN_DIR=gen-dbg
    INST_LIB=lib-dbg
    INST_INCLUDE=include-dbg
else
    GEN_DIR=gen
    INST_LIB=lib
    INST_INCLUDE=include
endif

INCLUDE_DIRS += -I. -I$(DISTDIR)/include

override XRX_PROPS_LDLIBS += -lx10

ifdef ENABLE_GC
  override CXXFLAGS += -DX10_USE_BDWGC
  override XRX_PROPS_CXXFLAGS += -DX10_USE_BDWGC
  override XRX_PROPS_LDLIBS += -lgc
endif

override XRX_PROPS_LDLIBS += -lm -lpthread

# -lrt
ifeq ($(shell uname -s),Linux)
  override XRX_PROPS_LDLIBS += -lrt
endif
ifeq ($(shell uname -s),FreeBSD)
  override XRX_PROPS_LDLIBS += -lrt
endif

# -ldl
ifndef X10_STATIC_LIB
  ifneq ($(shell uname -s),FreeBSD)
    override XRX_PROPS_LDLIBS += -ldl
  endif
endif

override CXXFLAGS += $(INCLUDE_DIRS) -I$(GEN_DIR)

###########
# Targets #
###########

XRX_ARCHIVE = $(LIBPREFIX)x10$(LIBSUFFIX)
ifeq ($(X10RT_PLATFORM),cygwin)
  XRX_DEP_LIBS = -L"$(DISTDIR)"/lib -L"$(INSTDIR)"/$(INST_LIB) -lx10rt_$(DEFAULT_X10RT_LIB)
  ifdef ENABLE_GC
    override XRX_DEP_LIBS += -lgc
  endif
endif
ifeq ($(subst 64,,$(X10RT_PLATFORM)),darwin)
  XRX_DEP_LIBS = -L"$(DISTDIR)"/lib
  ifdef ENABLE_GC
    override XRX_DEP_LIBS += -lgc
  endif
endif
XRX_PROPERTIES = libx10.properties
SHLIB_PROPERTIES = sharedlib.properties

# this builds everything
xrx: $(XRX_ARCHIVE)

.PHONY: xrx 

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
#
# By convention, we also ignore all packages called 'managed' when compiling Native X10.
#
XRX_REL_SRCS  = $(shell $(FIND) ../src-x10 -name .svn -prune -o -name managed -prune -o -name '*.x10' -print | sort )
XRX_SRCS = $(subst ../src-x10/,, $(XRX_REL_SRCS))

#enable asserts, do not look in jar for x10 files
X10CPPFLAGS += -STATIC_CHECKS -J-Xmx512M -J-ea -rtdev -commandlineonly

# Generate .cc and .h files for all the X10 classes defined in the above
# .x10 files.  Rebuilding occurs if the x10 compiler is updated or if any of the
# currently present x10 files are newer than the 'all-cpp-generated' file.
# Note that nothing gets rebuilt if one of the .x10 files is deleted, however
# this simply means there will be more symbols in the archive than there should
# be.  This is harmless and equivalent to the old behaviour of this Makefile,
# i.e. a make clean is needed.
SPACE = $(EMPTY) $(EMPTY)
$(GEN_DIR)/all-cpp-generated: $(XRX_REL_SRCS) $(subst $(SPACE),\$(SPACE),$(DISTDIR))/lib/x10c.jar
	@echo "Regenerating XRX cc/h files"
	mkdir -p $(GEN_DIR)
	cd ../src-x10 && "$(DISTDIR)"/bin/x10c++ -c $(X10CPPFLAGS) -d ../src-cpp/$(GEN_DIR) $(XRX_SRCS)
	find $(GEN_DIR) -name "*.cc" -print > $@

$(XRX_PROPERTIES): $(XRX_REL_SRCS)
	@echo "X10LIB_PLATFORM=$(X10RT_PLATFORM)" > $@
	@echo "X10LIB_TIMESTAMP=$(shell date)" >> $@
	@echo "X10LIB_CXX=$(CXX)" >> $@
	@echo "X10LIB_CXXFLAGS=$(XRX_PROPS_CXXFLAGS)" >> $@
	@echo "X10LIB_LDFLAGS=$(XRX_PROPS_LDFLAGS)" >> $@
	@echo "X10LIB_LDLIBS=$(XRX_PROPS_LDLIBS)" >> $@
	@echo "X10LIB_SRC_JAR=x10.jar" >> $@

$(SHLIB_PROPERTIES): $(XRX_REL_SRCS)
	@echo "X10LIB_CXXFLAGS_SHARED=$(CXXFLAGS_SHARED)" > $@
	@echo "X10LIB_LDFLAGS_SHARED=$(LDFLAGS_SHARED)" >> $@
	@echo "X10LIB_LIBPREFIX=$(LIBPREFIX)" >> $@
	@echo "X10LIB_LIBSUFFIX=$(LIBSUFFIX)" >> $@
	@echo "X10LIB_X10_STATIC_LIB=$(X10_STATIC_LIB)" >> $@
	@echo "X10LIB_AR=$(AR)" >> $@
	@echo "X10LIB_ARFLAGS=$(ARFLAGS)" >> $@


##########################
# Manually-written Files #
##########################

ALL_XRCPP_OBJECTS = \
	x10aux/alloc.o \
	x10aux/assert.o \
	x10aux/atomic_ops.o \
	x10aux/basic_functions.o \
	x10aux/bootstrap.o \
	x10aux/class_cast.o \
	x10aux/config.o \
	x10aux/static_init.o \
	x10aux/itables.o \
	x10aux/lock.o \
	x10aux/network.o \
	x10aux/network_dispatcher.o \
	x10aux/pcond.o \
	x10aux/place_local.o \
	x10aux/reference_logger.o \
	x10aux/RTT.o \
	x10aux/deserialization_dispatcher.o \
	x10aux/serialization.o \
	x10aux/throw.o \
	x10aux/debug.o \
	x10/io/FileReader__FileInputStream.o \
	x10/io/FileWriter__FileOutputStream.o \
	x10/io/File__NativeFile.o \
	x10/io/InputStreamReader__InputStream.o \
	x10/io/OutputStreamWriter__OutputStream.o \
	x10/lang/Any.o \
	x10/lang/Arithmetic.o \
	x10/lang/Bitwise.o \
	x10/lang/Boolean.o \
	x10/lang/Byte.o \
	x10/lang/Char.o \
	x10/lang/CheckedThrowable.o \
	x10/lang/Closure.o \
	x10/lang/Comparable.o \
	x10/lang/Complex.o \
	x10/lang/Condition.o \
	x10/lang/Double.o \
	x10/lang/Fun.o \
	x10/lang/GlobalRef.o \
	x10/lang/IBox.o \
	x10/lang/Int.o \
	x10/lang/Float.o \
	x10/lang/Long.o \
	x10/lang/Rail.o \
	x10/lang/Reference.o \
	x10/lang/RuntimeNatives.o \
	x10/lang/Short.o \
	x10/lang/String.o \
	x10/lang/Lock__ReentrantLock.o \
	x10/lang/PlaceLocalHandle_Impl.o \
	x10/lang/UByte.o \
	x10/lang/UInt.o \
	x10/lang/ULong.o \
	x10/lang/UShort.o \
	x10/lang/X10Class.o \
	x10/util/Ordered.o \
	x10/util/concurrent/AtomicBooleanNatives.o \
	x10/util/concurrent/AtomicIntegerNatives.o \
	x10/util/concurrent/AtomicLongNatives.o \
	x10/util/concurrent/AtomicReference.o \
	x10/xrx/Deque.o \
	x10/xrx/Thread.o

ALL_MANUAL_OBJECTS=$(ALL_XRCPP_OBJECTS)

# This target will build the XRX cc files into object files
$(GEN_DIR)/all-o-generated: $(GEN_DIR)/all-cpp-generated
	$(MAKE) CXX_NO_WARNINGS=1 $(shell sed -e's@^\(.*\)\.cc$$@\1.o@' $(GEN_DIR)/all-cpp-generated)
	touch $@

ifdef X10_STATIC_LIB
$(XRX_ARCHIVE): $(GEN_DIR)/all-o-generated $(ALL_MANUAL_OBJECTS)
	$(AR) $(ARFLAGS) $@ $(shell sed -e's@^\(.*\)\.cc$$@\1.o@' $(GEN_DIR)/all-cpp-generated) $(ALL_MANUAL_OBJECTS)
else
$(XRX_ARCHIVE): $(GEN_DIR)/all-o-generated $(ALL_MANUAL_OBJECTS)
	$(CXX) $(CXXFLAGS) $(CXXFLAGS_SHARED) $(LDFLAGS_SHARED) -o $@ \
	$(shell sed -e's@^\(.*\)\.cc$$@\1.o@' $(GEN_DIR)/all-cpp-generated) \
	$(ALL_MANUAL_OBJECTS) \
	$(XRX_DEP_LIBS)
endif

# The dummy files are needed to avoid rebuilds so must not be removed as
# intermediate files usually are.
.PRECIOUS: gen/all-cpp-generated gen/all-o-generated gen-dbg/all-cpp-generated gen-dbg/all-o-generated


################## 
# Install Targets #
###################

ifeq ($(findstring Usage,$(shell tar --version 2>&1)),Usage)  # non-GNU tar
  INC_FLAG=-I /dev/stdin
else
  INC_FLAG=-T -
endif

install_props: $(XRX_PROPERTIES) $(SHLIB_PROPERTIES)
	@cp -p $(XRX_PROPERTIES) "$(INSTDIR)"/$(XRX_PROPERTIES)
	@cp -p $(SHLIB_PROPERTIES) "$(DISTDIR)"/etc/$(SHLIB_PROPERTIES)

install: $(XRX_ARCHIVE) install_props
	@echo "Installing into $(INSTDIR)"
	@mkdir -p "$(INSTDIR)"/$(INST_INCLUDE) "$(INSTDIR)"/$(INST_LIB)
	@(cd $(GEN_DIR) && $(FIND) . -name \*.h | tar cf - $(INC_FLAG)) | (cd "$(INSTDIR)"/$(INST_INCLUDE) && tar xf -)
	@$(FIND) x10 -name \*.h | tar cf - $(INC_FLAG) | (cd "$(INSTDIR)"/$(INST_INCLUDE) && tar xf -)
	@$(FIND) x10aux -name \*.h | tar cf - $(INC_FLAG) | (cd "$(INSTDIR)"/$(INST_INCLUDE) && tar xf -)
	@$(FIND) x10aux -name \*.cuh | tar cf - $(INC_FLAG) | (cd "$(INSTDIR)"/$(INST_INCLUDE) && tar xf -)
	@cp -p x10rt.h "$(INSTDIR)"/$(INST_INCLUDE)
	@cp -p $(XRX_ARCHIVE) "$(INSTDIR)"/$(INST_LIB)

.PHONY: install install_props


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

# depend on all-cpp-generated because we need to make sure the x10 code 
# has been compiled to cc,h since the manually written code may include generated headers
%.o: %.cc $(GEN_DIR)/all-cpp-generated 
	$(CXX) $(CXXFLAGS) $(CXX_WARNING_FLAGS) $(CXXFLAGS_SHARED) -c $< -o $@
%.o: %.cc %.h $(GEN_DIR)/all-cpp-generated 
	$(CXX) $(CXXFLAGS)  $(CXX_WARNING_FLAGS) $(CXXFLAGS_SHARED) -c $< -o $@


depend:
	touch depend.mk
	$(FIND) . -name $(GEN_DIR) -prune -o \( -name '*.cc' -o -name '*.h' \) -print0 | $(XARGS) -r0 makedepend -I $(INCLUDE_DIRS) -fdepend.mk 


#nuke only object code
cleanobj:
	$(FIND) . \( -name '*.h.gch' -o -name '*.o' -o -name '*.rpo' \) -print0 | $(XARGS) -0 -t $(RM) -r
	$(RM) $(GEN_DIR)/all-o-generated

#nuke everything
cleangen:
	-$(RM) -r gen gen-dbg $(XRX_ARCHIVE) $(XRX_PROPERTIES) $(SHLIB_PROPERTIES)

clean: cleanobj cleangen

squeakyclean: clean
	$(FIND) . \( -name '*~' -o -name '*.bak' \) -print0 | $(XARGS) -r0 -t $(RM)


.PHONY: depend clean cleanobj cleangen squeakyclean


# Optional file -- generated by makedepend (run "make depend").
-include depend.mk



# vim:tabstop=8:shiftwidth=8:noexpandtab
