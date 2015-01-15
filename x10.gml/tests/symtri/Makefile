###################################################
# Source and target file
###################################################
target_list = TestTriDense TestSymDense TestTriSparse TestSymSparse

target		= TestSymSparse

# build path
build_path	= build

# GML path
gml_path    = ../../
gml_scripts = $(gml_path)/scripts

# compile options
X10_FLAG	= -sourcepath $(X10_HOME)/../x10.tests/tests/x10lib -d $(build_path)  -errors 5 -VERBOSE_CHECKS 

# runtime settings
runtime_list = mpi sock java
numplaces = 1
test_args = 

###################################################
# make rules
###################################################
## default run
default	: java

run		: run_java

###################################################
# Including rules
###################################################
include $(gml_path)/scripts/build_common.mk
include $(gml_path)/scripts/build_managed.mk
include $(gml_path)/scripts/build_native.mk
include $(gml_path)/scripts/build_native_mpi.mk
include $(gml_path)/scripts/test_run.mk

###################################################

help	::
	@echo "-------------------- build/test dense matrix ----------------";
	@echo " make          : build managed backend";
	@echo " make run      : run dense matrix tests on managed backend";
	@echo "" 
