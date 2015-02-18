# This is makefile for building SUMMA benchmarks, for
# distributed dense and sparse matrices, as well as
# SUMMA C-MPI implmentation
#

###################################################
# Source and target file
###################################################

#defalt target
target		= DistDenseBlockSUMMA

#List of all targets
target_list = DistDenseBlockSUMMA DistSparseBlockSUMMA
##SummaMPIBench

#-------------------------------------
# GML path
gml_path    = ../..

# build path
build_path	= build

# compile options
X10_FLAG	= -d $(build_path) -report postcompile=2 -v -errors 5 -VERBOSE_CHECKS -O -NO_CHECKS

# runtime settings
runtime_list = mpi sock java pami
numplaces = 2
test_args = 

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
	@echo "----------------- build and test benchmarks of SUMMA desne/sparse block matrices ----------------";
	@echo " ";
	
