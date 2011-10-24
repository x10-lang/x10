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
#This make file is used for building executable 
#running on C++backend socket transport.
###################################################
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
#$(XC)          ## X10 compiler
#$(CPP)         ## Post compiler
#$(POST_PATH)   ## Post compiling include path
#$(POST_LIBS)   ## Post compiling include libs.


###################################################
x10src		= $(target).x10

###################################################
# Source files and paths
###################################################


##----------------------------------
## This directory is required for building mative backend
GML_NAT_OPT= -classpath $(gml_lib)/native_gml.jar -x10lib $(gml_path)/native_gml.properties

###################################################
# X10 file built rules
################################################### 

$(target)_sock	: $(x10src) $(depend_src) $(gml_inc)
		$(XC) -x10rt sockets $(GML_NAT_OPT) $(X10_FLAG) $< -o $@ \
		-post '$(CPP) # $(POST_PATH) # $(POST_LIBS)'

$(target)_lapi	: $(x10src) $(depend_src) $(gml_inc)
		$(XC) -x10rt pgas_lapi $(GML_NAT_OPT) $(X10_FLAG) $< -o $@ \
		-post '$(CPP) # $(POST_PATH) # $(POST_LIBS)'

###
sock		: $(target)_sock
lapi		: $(target)_lapi

###
all_sock	:
			$(foreach src, $(target_list), $(MAKE) target=$(src) sock; )

all_lapi	:
			$(foreach src, $(target_list), $(MAKE) target=$(src) lapi; )

##--------
## clean
clean	::
		rm -f $(target)_sock $(target)_lapi

clean_all ::
		$(foreach f, $(target_list), rm -f $(f)_sock $(f)_lapi; )
###----------
help	::
	@echo "------------------- build for native sock or lapi transport ------------";
	@echo " make sock       : build default target $(target) for native backend running on socket transport";
	@echo " make all_mpi    : build all targets [ $(target_list) ] for native backend running on socket transport";
	@echo " make lapi       : build default target $(target) for native backend running on socket transport";
	@echo " make all_lapi   : build all targets [ $(target_list) ] for native backend running on socket transport";
	@echo " make clean      : remove default built binary $(target)_sock $(target)_lapi";
	@echo " make clean_all  : remove all builds for the list of tragets";
	@echo "";

		