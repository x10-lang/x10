DISTDIR = $(call abspath,../x10.dist)
X10_RUNTIME_DIR = $(call abspath,../x10.runtime)
APGAS_DIR = $(call abspath,.)

#########################
# User-servicable Parts #
#########################

include $(X10_RUNTIME_DIR)/Make.rules

FIND ?= find
XARGS ?= xargs

#############
# Variables #
#############

ifdef DEBUG
    GEN_DIR=gen-dbg
    INST_LIB=lib-dbg
    INST_INCLUDE=include-dbg
else
    GEN_DIR=gen
    INST_LIB=lib
    INST_INCLUDE=include
endif

override CXXFLAGS += -I$(DISTDIR)/include -Isrc-crx -Isrc -I$(GEN_DIR) -g 

###########
# Targets #
###########

APGAS_ARCHIVE = $(LIBPREFIX)apgas$(LIBSUFFIX)

# this builds everything
apgas: $(APGAS_ARCHIVE)

.PHONY: apgas

###############################
# Copy Files from x10.runtime #
###############################

ALL_LANG = $(shell cd $(X10_RUNTIME_DIR)/src-x10 && $(FIND) x10/lang -name .svn -prune -o -name '*.x10' -print)

ALL_ANN = $(shell cd $(X10_RUNTIME_DIR)/src-x10 && $(FIND) x10/lang/annotations -name .svn -prune -o -name '*.x10' -print)

ALL_CONC = $(shell cd $(X10_RUNTIME_DIR)/src-x10 && $(FIND) x10/util/concurrent -name .svn -prune -o -name '*.x10' -print)

SELECTED_COMPILER = Native.x10 NativeRep.x10 NoThisAccess.x10 NonEscaping.x10 Embed.x10 Global.x10 Pinned.x10 \
		NoInferType.x10 StackAllocate.x10 StackAllocateUninitialized.x10 \
		RemoteInvocation.x10 Volatile.x10 NativeCPPInclude.x10 \
		SuppressTransientError.x10 NoInline.x10 Inline.x10 InlineOnly.x10 NoReturn.x10 NativeClass.x10 \
		Pragma.x10 CompilerFlags.x10 CompileTimeConstant.x10 Profile.x10 Header.x10 \
		Finalization.x10 Abort.x10 NativeDef.x10 FinishAsync.x10 AsyncClosure.x10 Endpoint.x10 CUDA.x10 \
		Opaque.x10 TransientInitExpr.x10 Synthetic.x10

SELECTED_UTIL = HashMap.x10 Map.x10 Box.x10 Ordered.x10 Pair.x10 Triple.x10 Set.x10 Random.x10 \
		AbstractCollection.x10 AbstractContainer.x10  Collection.x10 GrowableRail.x10 \
		Container.x10 NoSuchElementException.x10 Timer.x10 Indexed.x10 \
		CollectionIterator.x10 MapIterator.x10 StringBuilder.x10 Builder.x10 \
		HashSet.x10 MapSet.x10

SELECTED_IO = 	CustomSerialization.x10 Serializer.x10 Deserializer.x10 \
		Unserializable.x10 NotSerializableException.x10 \
		SerializationException.x10 \
		Printer.x10 Reader.x10 Writer.x10 Marshal.x10 \
		IOException.x10 ReaderIterator.x10 EOFException.x10 Console.x10 OutputStreamWriter.x10 \
		InputStreamReader.x10 FilterWriter.x10 FileNotFoundException.x10 NotSerializableException.x10 

ALL_X10_SRC = $(ALL_LANG) $(ALL_ANN) $(ALL_CONC) $(patsubst %,x10/compiler/%,$(SELECTED_COMPILER)) \
	$(patsubst %,x10/util/%,$(SELECTED_UTIL)) $(patsubst %,x10/io/%,$(SELECTED_IO)) 

X10_COPY_SRC = $(patsubst %,src-xrx/%,$(ALL_X10_SRC))

src-xrx/%.x10: $(X10_RUNTIME_DIR)/src-x10/%.x10
	@mkdir -p src-xrx/x10/compiler src-xrx/x10/lang src-xrx/x10/lang/annotations src-xrx/x10/util/concurrent src-xrx/x10/io
	@cp -pu $< $@

NATIVE_CC_FILES = $(shell cd $(X10_RUNTIME_DIR)/src-cpp && $(FIND) x10aux -name .svn -prune -o -name '*.cc' -print) \
	$(shell cd $(X10_RUNTIME_DIR)/src-cpp && $(FIND) x10/lang -name .svn -prune -o -name '*.cc' -print) \
	$(shell cd $(X10_RUNTIME_DIR)/src-cpp && $(FIND) x10/util -name .svn -prune -o -name '*.cc' -print) \
	x10/io/FileReader__FileInputStream.cc x10/io/FileWriter__FileOutputStream.cc \
	x10/io/InputStreamReader__InputStream.cc x10/io/OutputStreamWriter__OutputStream.cc

NATIVE_H_FILES = $(shell cd $(X10_RUNTIME_DIR)/src-cpp && $(FIND) x10aux -name .svn -prune -o -name '*.h' -print) \
	$(shell cd $(X10_RUNTIME_DIR)/src-cpp && $(FIND) x10/lang -name .svn -prune -o -name '*.h' -print) \
	$(shell cd $(X10_RUNTIME_DIR)/src-cpp && $(FIND) x10/util -name .svn -prune -o -name '*.h' -print) \
	x10/io/FileReader__FileInputStream.h x10/io/FileWriter__FileOutputStream.h \
	x10/io/InputStreamReader__InputStream.h x10/io/OutputStreamWriter__OutputStream.h \
	x10rt.h

NATIVE_OBJS = $(patsubst %,src-crx/%,$(NATIVE_CC_FILES:.cc=.o))

ALL_NATIVE_SRC = $(NATIVE_H_FILES) $(NATIVE_CC_FILES)

NATIVE_COPY_SRC = $(patsubst %,src-crx/%,$(ALL_NATIVE_SRC))

APGAS_SRC = src/apgas/Pool.cc src/apgas/Task.cc

APGAS_OBJS = $(APGAS_SRC:.cc=.o)

src-crx/%.cc: $(X10_RUNTIME_DIR)/src-cpp/%.cc
	@mkdir -p src-crx/x10aux src-crx/x10/lang src-crx/x10/util/concurrent src-crx/x10/io
	@cp -pu $< $@

src-crx/%.h: $(X10_RUNTIME_DIR)/src-cpp/%.h
	@mkdir -p src-crx/x10aux src-crx/x10/lang src-crx/x10/util/concurrent src-crx/x10/io
	@cp -pu $< $@

testme: $(X10_COPY_SRC) $(NATIVE_COPY_SRC)

###############################
# Compilation                 #
###############################

X10CPPFLAGS += -APGAS_LIB_MODE -STATIC_CHECKS -J-Xmx512M -rtdev -commandlineonly
ifdef OPTIMIZE
	X10CPPFLAGS += -O
endif
ifdef NO_CHECKS
	x10CPPFLAGS += -NO_CHECKS
endif

$(GEN_DIR)/all-cpp-generated: $(X10_COPY_SRC)
	@echo "Compiling X10 source files to C++"
	@mkdir -p $(GEN_DIR)
	cd src-xrx && "$(DISTDIR)"/bin/x10c++ $(X10CPPFLAGS) -c $(X10CPPFLAGS) -d ../$(GEN_DIR) $(ALL_X10_SRC)
	@ $(FIND) $(GEN_DIR) -name "*.cc" -print > $@


# This target will build the XRX cc files into object files
$(GEN_DIR)/all-o-generated: $(GEN_DIR)/all-cpp-generated $(NATIVE_COPY_SRC)
	$(MAKE) CXX_NO_WARNINGS=1 $(shell sed -e's@^\(.*\)\.cc$$@\1.o@' $(GEN_DIR)/all-cpp-generated)
	touch $@

$(APGAS_ARCHIVE): $(GEN_DIR)/all-o-generated $(NATIVE_OBJS) $(APGAS_OBJS)
	$(CXX) $(CXXFLAGS) $(CXXFLAGS_SHARED) $(LDFLAGS_SHARED) -o $@ \
	    $(shell sed -e's@^\(.*\)\.cc$$@\1.o@' $(GEN_DIR)/all-cpp-generated) $(NATIVE_OBJS) $(APGAS_OBJS)


# The dummy files are needed to avoid rebuilds so must not be removed as
# intermediate files usually are.
.PRECIOUS: gen/all-cpp-generated gen/all-o-generated gen-dbg/all-cpp-generated gen-dbg/all-o-generated


# ################## 
# # Install Targets #
# ###################

# ifeq ($(findstring Usage,$(shell tar --version 2>&1)),Usage)  # non-GNU tar
#   INC_FLAG=-I /dev/stdin
# else
#   INC_FLAG=-T -
# endif

# install_props: $(XRX_PROPERTIES) $(SHLIB_PROPERTIES)
# 	@cp -p $(XRX_PROPERTIES) "$(INSTDIR)"/$(XRX_PROPERTIES)
# 	@cp -p $(SHLIB_PROPERTIES) "$(DISTDIR)"/etc/$(SHLIB_PROPERTIES)

# install: $(XRX_ARCHIVE) install_props
# 	@echo "Installing into $(INSTDIR)"
# 	@mkdir -p "$(INSTDIR)"/$(INST_INCLUDE) "$(INSTDIR)"/$(INST_LIB)
# 	@(cd $(GEN_DIR) && $(FIND) . -name \*.h | tar cf - $(INC_FLAG)) | (cd "$(INSTDIR)"/$(INST_INCLUDE) && tar xf -)
# 	@$(FIND) x10 -name \*.h | tar cf - $(INC_FLAG) | (cd "$(INSTDIR)"/$(INST_INCLUDE) && tar xf -)
# 	@$(FIND) x10aux -name \*.h | tar cf - $(INC_FLAG) | (cd "$(INSTDIR)"/$(INST_INCLUDE) && tar xf -)
# 	@$(FIND) x10aux -name \*.cuh | tar cf - $(INC_FLAG) | (cd "$(INSTDIR)"/$(INST_INCLUDE) && tar xf -)
# 	@cp -p x10rt.h "$(INSTDIR)"/$(INST_INCLUDE)
# 	@cp -p $(XRX_ARCHIVE) "$(INSTDIR)"/$(INST_LIB)

# .PHONY: install install_props


# ##################
# # Standard Stuff #
# ##################

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

#remove only object code
cleanobj:
	$(FIND) . \( -name '*.h.gch' -o -name '*.o' -o -name '*.rpo' \) -print0 | $(XARGS) -0 -t $(RM) -r
	$(RM) $(GEN_DIR)/all-o-generated

#remove all generated and copied files
cleangen:
	-$(RM) -r gen gen-dbg $(APGAS_ARCHIVE) 
	rm -rf $(GEN_DIR)
	rm -rf src-xrx
	rm -rf src-crx

clean: cleanobj cleangen

.PHONY: clean cleanobj cleangen

# vim:tabstop=8:shiftwidth=8:noexpandtab
