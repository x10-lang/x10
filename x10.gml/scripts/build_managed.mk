#
#  This file is part of the X10 project (http://x10-lang.org).
#
#  This file is licensed to You under the Eclipse Public License (EPL);
#  You may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#      http://www.opensource.org/licenses/eclipse-1.0.php
#
#  (C) Copyright IBM Corporation 2006-2011.
#

###################################################
###################################################
## Name:  	X10 application test
## Created by: 	Juemin Zhang
## Contact:   	zhangj@us.ibm.com
###################################################
# This make file is used for building managed ackend.
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
GML_LIB_JAVA= -x10lib $(gml_path)/managed_gml.properties  -cp $(gml_lib)/managed_gml.jar

###################################################
# X10 file built rules
################################################### 

$(build_path)/$(target).class	: $(x10src) $(depend_src) check_gml_java
			$(XJ) $(X10_FLAG) $(GML_LIB_JAVA) $< 

###----------------
java		: $(build_path)/$(target).class

all_java	:
			$(foreach src, $(target_list), $(MAKE) target=$(src) java; )

####----------
clean		::
			rm -rf $(build_path)

clean_all	::
			rm -rf $(build_path)

####----------

help	::
	@echo "------------------- build benchmark test for managed backend ------------";
	@echo " make java     : build default target $(target) for managed backend";
	@echo " make all_java : build all [ $(target_list) ] for managed backend";
	@echo " make clean    : remove build dir";	
	@echo " make clean_all: remove build dir for all targets. Same as clean";
	@echo ""
	
