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

# Test run
# Run [mpi|sockets|java] executable: make [run_mpi|run_sock|java]

# Bench mark test suite
# cd test_suite; ./ptest.pl -r

###################################################
# Source and target file
###################################################

#defalt target
target		= DenseMultBench

#List of all targets
target_list = DenseMultBench DenseBlasBench SparseMultBench 

#-------------------------------------
# GML path
gml_path    = ../../

# build path
build_path	= build

# compile options
X10_FLAG	= -d $(build_path) -report postcompile=2 -v -errors 5 -VERBOSE_CHECKS -O -NO_CHECKS

# runtime settings
runtime_list = mpi sock java
numplaces = 1
test_args = 


###################################################
# Including rules
###################################################
include $(gml_path)/scripts/build_common.mk
include $(gml_path)/scripts/build_managed.mk
include $(gml_path)/scripts/build_native.mk
include $(gml_path)/scripts/build_native_mpi.mk
include $(gml_path)/scripts/test_run.mk
