###################################################
###################################################
## Name:  	X10 application test
## Created by: 	Juemin Zhang
## Contact:   	zhangj@us.ibm.com
###################################################
# This make file is used for building managed backend.
###################################################

##---------------
#Input settings
##---------------
#$(gml_path)
#$(gml_inc)
#$(gml_lib)
#$(build_path)
#$(target)
#$(target_list)
#$(X10_FLAG)
#$(XJ)

###################################################
# Source files and paths
###################################################
x10src		= $(target).x10

##----------------------------------
GML_LIB_JAVA= -x10lib $(gml_path)/managed_gml.properties -cp $(gml_lib)/managed_gml.jar

###################################################
# X10 file built rules
################################################### 

$(build_path)/$(target).class	: $(x10src) $(depend_src)
			$(XJ) $(X10_FLAG) $(GML_LIB_JAVA) $< 

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
