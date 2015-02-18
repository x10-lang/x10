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

# This is makefile for building SUMMA benchmarks, for
# distributed dense and sparse matrices, as well as
# SUMMA C-MPI implmentation
#

###################################################
# Source and target file
###################################################

#defalt target
target		= DistDenseBench

#List of all targets
target_list = DistDenseBench DistSparseBench 
##SummaMPIBench

#-------------------------------------
# GML path
gml_path    = ../../

# build path
build_path	= build

# compile options
X10_FLAG	= -d $(build_path) -report postcompile=2 -v -errors 5 -VERBOSE_CHECKS -O -NO_CHECKS

# runtime settings
runtime_list = mpi sock java
numplaces = 2
test_args = 

###################################################

c_mpi :
	$(MAKE) target=SummaMPIBench mpi

clean_all	::
	rm -f SummaMPIBench

###################################################
# Including rules
###################################################
include $(gml_path)/scripts/build_common.mk
include $(gml_path)/scripts/build_managed.mk
include $(gml_path)/scripts/build_native.mk
include $(gml_path)/scripts/build_native_mpi.mk
include $(gml_path)/scripts/test_run.mk


###-------------------------------
help ::	
	@echo "----------------- build and test benchmarks of SUMMA desne/sparse matrices ----------------";
	@echo " make c_mpi       : build c-mpi summa implementation";
	@echo " ";
	
