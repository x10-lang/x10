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
###################################################

# Inputs 
# target      --- default source x10 file
# target_list --- list of targets, which are built independently
# build_path  --- the directory to store the compiling object files
# gml_path    --- GML library path


# runtime settings
# numplaces = 1
# test_args = 


###################################################
# Source and target file
###################################################

# test source
x10src		= $(target).x10

###################################################
# settings
###################################################

# lib source
gml_inc		= $(gml_path)/include
gml_lib		= $(gml_path)/lib
gml_scripts	= $(gml_path)/scripts
###################################################
# Build rules
###################################################
include $(gml_scripts)/system_setting.mk

#Build defautl target for all runtime backend and runtime transports
all	:
	$(foreach rt, $(runtime_list), $(MAKE) all_$(rt); )

###
clean	::
		rm -rf $(build_path)  $(target)_mpi $(target)_sock $(target)_lapi
		
clean_all :: 
		rm -rf $(build_path)
		$(foreach f, $(target_list), rm -f $(f)_mpi $(f)_sock $(f)_lapi; )

###-----------------------------------------
##  build library
lib	:
	cd $(gml_path) && make all -f Makefile all

check_gml_inc 	: $(gml_path)/include $(gml_path)/lib
# If fails, rebuild x10.gml library

check_gml_mpi	: $(gml_path)/native_mpi_gml.properties  $(gml_path)/lib/native_mpi_gml.jar  $(gml_path)/lib/native_mpi_gml.so
# If fails, rebuild x10.gml native MPI flaor by running make native_mpi at $(gml_path)

check_gml_c	: check_gml_inc $(gml_path)/native_gml.properties $(gml_path)/lib/native_gml.jar $(gml_path)/lib/native_gml.so
# If fails, rebuild x10.gml native backend, by running make native at $(gml_path)

check_gml_java	: check_gml_inc $(gml_path)/managed_gml.properties $(gml_path)/lib/managed_gml.jar
# If fails, rebuild x10.gml managed backend by running make managed at $(gml_path)

####----------------------------------------------
.PHONY		: lib all runall sock mpi java clean cleanall all_mpi all_sock all_lapi all_java chk_gml_inc help check_gml_inc check_gml_mpi check_gml_c check_gml_java help
####----------------------------------------------

help ::
	@echo "---------------------- build/test on all tests --------------------";
	@echo " make all       : build default test on all backends and runtime transports:$(runtime_list)";
	@echo " make clean     : clean up the build for default target - $(target)";
	@echo " make lib       : build gml library";
	@echo " make clean     : clean up default build";
	@echo " make clean_all : clean up alltests build";
	@echo "";

