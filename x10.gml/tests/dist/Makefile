###################################################
# Source and target file
###################################################
target_list 	= TestDistDense TestDistSparse TestDistMatrix TestDistDupMult

target		= TestDistDense
#target		= TestDistSparse
#target		= TestDist
#target		= TestDistDupMult

# build path
build_path	= build

# GML path
gml_path    = ../../

# compile options
X10_FLAG	= -d $(build_path)  -errors 5 -VERBOSE_CHECKS 

# runtime settings
runtime_list = mpi sock java
numplaces = 4
test_args = 

###################################################
# Executing
###################################################
default	: java
run		: run_java
###################################################

###################################################
# Including rules
###################################################
include $(gml_path)/scripts/build_common.mk
include $(gml_path)/scripts/build_managed.mk
include $(gml_path)/scripts/build_native.mk
include $(gml_path)/scripts/build_native_mpi.mk
include $(gml_path)/scripts/test_run.mk
