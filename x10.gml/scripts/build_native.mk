###################################################
#This make file is used for building executable 
#running on C++backend socket/MPI/pami transport.
###################################################

##---------------
#Input settings
##---------------
#$(gml_path)    ## gml installation path
#$(gml_inc)     ## gml include path
#$(gml_lib)     ## gml library path
#$(build_path)  ## application target build path
#$(target)      ## application target name
#$(target_list) ## list of targets
#$(X10_FLAG)    ## X10 compiling option flags
#$(X10CXX)      ## X10 compiler
#$(POST_PATH)   ## Post compiling include path
#$(POST_LIBS)   ## Post compiling include libs.
#$(GML_ELEM_TYPE) ## float or double

###################################################
# Source files and paths
###################################################


##----------------------------------
## This directory is required for building native backend
GML_NATIVE_JAR  = $(base_dir_elem)/lib/native_gml.jar
GML_NAT_OPT	= -classpath $(GML_NATIVE_JAR) -x10lib $(base_dir_elem)/native_gml.properties

###################################################
# X10 file built rules
################################################### 

# enable CPU profiling with google-perftools
PROFILE ?=
ifdef PROFILE
  X10_FLAG += -gpt
endif

#vj: used to depend on gml_inc
%_sock_$(GML_ELEM_TYPE)	: %.x10 $(depend_src) 
	        @echo "X10_HOME is |$(X10_HOME)|"
		$(X10CXX) -g -x10rt sockets $(GML_NAT_OPT) $(X10_FLAG) $< -o $@ \
		-post ' \# $(POST_PATH) \# $(POST_LIBS)'

%_mpi_$(GML_ELEM_TYPE)	: %.x10 $(depend_src) 
	        @echo "X10_HOME is |$(X10_HOME)|"
		$(X10CXX) -g -x10rt mpi $(GML_NAT_OPT) $(X10_FLAG) $< -o $@ \
		-post ' \# $(POST_PATH) \# $(POST_LIBS)'

%_pami_$(GML_ELEM_TYPE)	: %.x10 $(depend_src) 
		$(X10CXX) -g -x10rt pami $(GML_NAT_OPT) $(X10_FLAG) $< -o $@ \
		-post ' \# $(POST_PATH) \# $(POST_LIBS)'

###short-keys
#Build in native for socket transport
sock		: $(target)_sock_$(GML_ELEM_TYPE)
#Build in native for MPI transport
mpi		: $(target)_mpi_$(GML_ELEM_TYPE)
#build in native for pami transport
pami		: $(target)_pami_$(GML_ELEM_TYPE)

###
all_sock	:
			$(foreach src, $(target_list), $(MAKE) target=$(src) sock; )

all_mpi	:
			$(foreach src, $(target_list), $(MAKE) target=$(src) mpi; )

all_pami	:
			$(foreach src, $(target_list), $(MAKE) target=$(src) pami; )

##--------
clean ::
		$(foreach f, $(target_list), rm -rf $(f)_sock* $(f)_mpi* $(f)_pami*; )

###----------
help	::
	@echo "------------------- build for native sock, mpi or pami transport ------------";
	@echo " make sock       : build default target $(target) for native backend running on socket transport";
	@echo " make all_sock   : build all targets [ $(target_list) ] for native backend running on socket transport";
	@echo " make mpi        : build default target $(target) for native backend running on MPI transport";
	@echo " make all_mpi    : build all targets [ $(target_list) ] for native backend running on MPI transport";
	@echo " make pami       : build default target $(target) for native backend running on pami transport";
	@echo " make all_pami   : build all targets [ $(target_list) ] for native backend running on pami transport";
	@echo " make clean      : remove default built binary $(target)_sock $(target)_mpi $(target)_pami";
	@echo " make clean_all  : remove all builds for the list of targets";
	@echo "";
