###################################################
# This make file is used for building managed backend.
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
x10src		= $(target).x10

##----------------------------------
GML_LIB_JAVA= -x10lib $(gml_path)/managed_gml_$(GML_ELEM_TYPE).properties

###################################################
# X10 file built rules
################################################### 

$(build_path)/$(target).class	: $(x10src) $(depend_src)
			$(X10C) $(X10_FLAG) $(GML_LIB_JAVA) $< -post ' \# $(POST_PATH) \# $(POST_LIBS)'

###----------------
java		: $(build_path)/$(target).class

all_java	:
			$(foreach src, $(target_list), $(MAKE) target=$(src) java; )

####----------
clean		::
			rm -rf $(build_path)

####----------

help	::
	@echo "------------------- build benchmark test for managed backend ------------";
	@echo " make java     : build default target $(target) for managed backend";
	@echo " make all_java : build all targets [ $(target_list) ] for managed backend";
	@echo " make clean    : remove build dir";	
	@echo ""
