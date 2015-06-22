###################################################
# Source and target file
###################################################
target = RunGNMF

target_list = $(target)

## for source dependence check 
depend_src  = SeqGNNMF.x10 GNNMF.x10
#-------------------------------------
# build path
build_path	= build
gml_path    = $(X10_HOME)/x10.gml

# compile options
X10_FLAG	= -sourcepath ../.  -d $(build_path) -report postcompile=2 -v -errors 5 -VERBOSE_CHECKS -O -NO_CHECKS

# runtime settings for tests
runtime_list = mpi sock java
numplaces = 4
test_args = 

###################################################
# Build rules
###################################################
default : help
run		: run_java

include $(gml_path)/scripts/build_common.mk
include $(gml_path)/scripts/build_managed.mk
include $(gml_path)/scripts/build_native.mk
include $(gml_path)/scripts/build_native_mpi.mk
include $(gml_path)/scripts/test_run.mk

###################################################

help::
	@echo "----------------------- Help on building GNMF apps -----------------------"
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
	@echo "Trouble shooting tips";
	@echo " If runtime shows \"NoClassDefFoundError: x10/matrix/Matrix\"";
	@echo " replace \"~\" by relative directory path or absolute explicit directory path name";
	@echo "";
