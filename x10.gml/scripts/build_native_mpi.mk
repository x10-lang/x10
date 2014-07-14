###################################################
#This make file is used for building native backend
#running on MPI transport.
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

###################################################
# Source files and paths
###################################################
x10src		= $(target).x10

cgml_prop	= $(gml_path)/cgml.so.properties
###################################################
## Compiler settings
###################################################


###################################################
##### Dependent library setting Atlas BLAS lib
###################################################

##----------------------------------

MPI_FLAG    = -define MPI_COMMU -cxx-prearg -DMPI_COMMU
MPI_GML_LIB	= -classpath $(gml_lib)/native_mpi_gml.jar -x10lib $(gml_path)/native_mpi_gml.properties

###################################################
# X10 file build rules
################################################### 
$(target)_mpi	: $(x10src) $(depend_src) $(gml_inc)
		$(X10CXX)  -x10rt mpi $(MPI_GML_LIB) $(X10_FLAG) $(MPI_FLAG) $< -o $@ \
		-post ' \# $(POST_PATH) \# $(POST_LIBS)'

###----------
mpi		: $(target)_mpi

all_mpi	:
		$(foreach src, $(target_list), $(MAKE) target=$(src) mpi; )

###---------

clean	::
		rm -f $(target)_mpi

clean_all ::
		$(foreach f, $(target_list), rm -f $(f)_mpi; )

###-----------
help	::
	@echo "----------------------------- build for native MPI transport --------------------------";
	@echo " make mpi       : build default target $(target) for native backend running on MPI transport";
	@echo " make all_mpi   : build all targets [ $(target_list) ] for native backend running on MPI transport";
	@echo " make clean     : remove default built binary $(target)_mpi";
	@echo " make clean_all : remove all built targets";
	@echo "";

