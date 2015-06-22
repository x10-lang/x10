#
#  This file is part of the X10 project (http://x10-lang.org).
#
#  This file is licensed to You under the Eclipse Public License (EPL);
#  You may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#      http://www.opensource.org/licenses/eclipse-1.0.php
#
#  (C) Copyright IBM Corporation 2014-2015.
#

###################################################
# Source and target file
###################################################
target = RunSVM

target_list = $(target)

## for source dependence check 
depend_src  = SeqSVM.x10 RunSVM.x10
#-------------------------------------
# build path
build_path	= build
gml_path    = $(X10_HOME)/x10.gml

# compile options
X10_FLAG	= -sourcepath ../.  -d $(build_path) -report postcompile=2 -v -errors 5 -VERBOSE_CHECKS -O # -NO_CHECKS

# runtime settings
runtime_list = mpi sock java
numplaces = 4
test_args = -f ionosphere.data --verify

###################################################
# Build rules
###################################################
default: 	help
run:		run_java

include $(gml_path)/scripts/build_common.mk
include $(gml_path)/scripts/build_managed.mk
include $(gml_path)/scripts/build_native.mk
include $(gml_path)/scripts/build_native_mpi.mk
include $(gml_path)/scripts/test_run.mk

###################################################

help::
	@echo "----------------- Help on building SVM app------------------"
	@echo " 1) make     --- make default targets: $(target) for managed backend"
	@echo " 2) make run --- start run the default target app $(target) built for managed backend"
	@echo "";
	@echo "Default settings"
	@echo " Default target    : $(target)"
	@echo " GML library path  : $(gml_path)"
	@echo " X10 compile option: $(X10_FLAG)"
	@echo " Build backend list: $(runtime_list)"
	@echo " Test run places   : $(numplaces)"
	@echo " Test run arguments: $(test_args)"
	@echo "";
	@echo "Troubleshooting tips";
	@echo " If runtime shows \"NoClassDefFoundError: x10/matrix/Matrix\"";
	@echo " replace \"~\" by relative directory path or absolute explicit directory path name";
	@echo "";


