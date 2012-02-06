###################################################
###################################################
## Name:  	X10 application test
## Created by: 	Juemin Zhang
## Contact:   	zhangj@us.ibm.com
###################################################
###################################################


###################################################
# Source and target file
###################################################
target_list 	= TestDistBlock TestDupBlock TestDistMult

target		= TestDistMult
#target		= TestDistSparse
#target		= TestDist
#target		= TestDistDupMult

# build path
build_path	= build

# GML path
gml_path    = ../../

# compile options
X10_FLAG	= -d $(build_path) -report postcompile=2 -v -errors 5 -VERBOSE_CHECKS 
#-O -NO_CHECKS

# runtime settings
runtime_list = mpi sock java
numplaces = 4
test_args = 

###################################################
# Executing
###################################################
default	: java
run		: run_java

#default	: $(target).x10
#	x10c -d build -report postcompile=2 -v -errors 5 -VERBOSE_CHECKS -classpath ../../sandbox/:../../lib/managed_gml.jar -x10lib ../..//managed_gml.properties  TestDistBlock.x10 


###################################################

###################################################
# Including rules
###################################################

include $(gml_path)/scripts/build_common.mk
include $(gml_path)/scripts/build_managed.mk
include $(gml_path)/scripts/build_native.mk
include $(gml_path)/scripts/build_native_mpi.mk
include $(gml_path)/scripts/test_run.mk
