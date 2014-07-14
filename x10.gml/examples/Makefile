#
#  This file is part of the X10 project (http://x10-lang.org).
#
#  This file is licensed to You under the Eclipse Public License (EPL);
#  You may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#      http://www.opensource.org/licenses/eclipse-1.0.php
#
#  (C) Copyright IBM Corporation 2006-2014.
#

###################################################
###################################################
## Name:  	X10 application test
## Created by: 	Juemin Zhang
###################################################
###################################################


###################################################
# Source and target files
###################################################
target_list = DenseExample SparseExample 

target		= DenseExample

# test source
x10src		= $(target).x10

# lib source
gml_path    = ./..


# build path
build_path	= build

# compiling options
X10_FLAG= -d $(build_path) -report postcompile=2 -v -errors 5 -VERBOSE_CHECKS #-O -NO_CHECKS

# runtime settings
runtime_list = mpi sock java
numplaces = 4
test_args = 

###################################################
# Executing
###################################################


##--------------------------
default : help
run		: run_java

###################################################
# Build rules
###################################################
include $(gml_path)/scripts/build_common.mk
include $(gml_path)/scripts/build_managed.mk
include $(gml_path)/scripts/build_native.mk
include $(gml_path)/scripts/build_native_mpi.mk
include $(gml_path)/scripts/test_run.mk

###################################################

help ::
	@echo "------------------------ Help on building/running page rank app ------------------";
	@echo " 1) make     --- make default targets: $(target) for managed backend"
	@echo " 2) make run --- start run the default target app $(target) built for managed backend"
	@echo "";
	@echo " To build apps with GML, you need to build GML library first by";
	@echo " running \"make all\" at the GML root directory.  This will create all depending libraries, jar files and";
	@echo " and properties, which are used in building apps for managed backend, native";
	@echo " backend, and native C++ on MPI transport";
	@echo " NOTE: after GML library is built, the \"include\" in the matrix top directory";
	@echo " must be preserved for compiling apps.";
	@echo "";
	@echo "Trouble shooting tips";
	@echo " If runtime shows \"NoClassDefFoundError: x10/matrix/Matrix\"";
	@echo " replace \"~\" by relative directory path or absolute explicit directory path name";
	@echo "";
	@echo "Default settings"
	@echo " Default target    : $(target)"
	@echo " GML library path  : $(gml_path)"
	@echo " X10 compile option: $(X10_FLAG)"
	@echo " Build backend list: $(runtime_list)"
	@echo " Test run places   : $(numplaces)"
	@echo " Test run arguments: $(test_args)"
	@echo "";
