###################################################
#This make file is used for building executable 
#running on C++backend socket/pami transport.
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
x10src		= $(target).x10

###################################################
# Source files and paths
###################################################


##----------------------------------
## This directory is required for building native backend
GML_NAT_OPT	= -classpath $(gml_lib)/native_gml.jar -x10lib $(gml_path)/native_gml.properties

###################################################
# X10 file built rules
################################################### 

$(target)_sock	: $(x10src) $(depend_src) $(gml_inc)
		$(X10CXX) -g -x10rt sockets $(GML_NAT_OPT) $(X10_FLAG) $< -o $@ \
		-post ' \# $(POST_PATH) \# $(POST_LIBS)'

$(target)_pami	: $(x10src) $(depend_src) $(gml_inc)
		$(X10CXX) -g -x10rt pami $(GML_NAT_OPT) $(X10_FLAG) $< -o $@ \
		-post ' \# $(POST_PATH) \# $(POST_LIBS)'

###short-keys
#Build in native for socket transport
sock		: $(target)_sock
#build in native for pami transport
pami		: $(target)_pami

###
all_sock	:
			$(foreach src, $(target_list), $(MAKE) target=$(src) sock; )

all_pami	:
			$(foreach src, $(target_list), $(MAKE) target=$(src) pami; )

##--------
## clean
clean	::
		rm -f $(target)_sock $(target)_pami

clean_all ::
		$(foreach f, $(target_list), rm -f $(f)_sock $(f)_pami; )

###----------
help	::
	@echo "------------------- build for native sock or pami transport ------------";
	@echo " make sock       : build default target $(target) for native backend running on socket transport";
	@echo " make all_sock   : build all targets [ $(target_list) ] for native backend running on socket transport";
	@echo " make pami       : build default target $(target) for native backend running on pami transport";
	@echo " make all_pami   : build all targets [ $(target_list) ] for native backend running on pami transport";
	@echo " make clean      : remove default built binary $(target)_sock $(target)_pami";
	@echo " make clean_all  : remove all builds for the list of targets";
	@echo "";
