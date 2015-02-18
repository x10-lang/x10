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

#default target
target		= SnapshotRestoreBench

#List of all targets
target_list = SnapshotRestoreBench

## for source dependence check
depend_src  = SnapshottableExecutor.x10


#-------------------------------------
# GML path
gml_path    = ../../

# build path
build_path	= build

# compile options
X10_FLAG	= -d $(build_path) -report postcompile=2 -v -errors 5 -VERBOSE_CHECKS -O -NO_CHECKS

# runtime settings
runtime_list = mpi sock java

############################
#Configure these parameters#
numplaces = 8
iterations = 5
test_class = SparseDistBlockMatrix #Possible Values: DistVector,DupVector,SparseDistBlockMatrix,DenseDistBlockMatrix
size_KB = 10
row_blocks = 8
col_blocks = 1
non_zero_density = 0.09
hosts = bigbird1,bigbird2
############################

params = $(size_KB) $(size_KB) $(row_blocks) $(col_blocks) $(non_zero_density)
numplaces_withspare = $(shell echo ${numplaces}+1 | bc)
first_place = 1
middle_place = $(shell echo ${numplaces}/2 | bc)
last_place = $(shell echo ${numplaces}-1 | bc)

.PHONY: p0_sock dist_sock 

p0_sock : sock
#Srink Tests
        X10_HOSTLIST=$(hosts) X10_RESILIENT_MODE=1 X10_PLACE_GROUP_RESTORE_MODE=0 X10_NPLACES=$(numplaces) ./$(target)_sock $(test_class) $(iterations) 0 $(first_place) $(params)
        X10_HOSTLIST=$(hosts) X10_RESILIENT_MODE=1 X10_PLACE_GROUP_RESTORE_MODE=0 X10_NPLACES=$(numplaces) ./$(target)_sock $(test_class) $(iterations) 0 $(middle_place) $(params)
        X10_HOSTLIST=$(hosts) X10_RESILIENT_MODE=1 X10_PLACE_GROUP_RESTORE_MODE=0 X10_NPLACES=$(numplaces) ./$(target)_sock $(test_class) $(iterations) 0 $(last_place) $(params)
#Redundant Tests
        X10_HOSTLIST=$(hosts) X10_RESILIENT_MODE=1 X10_PLACE_GROUP_RESTORE_MODE=1 X10_NPLACES=$(numplaces_withspare) ./$(target)_sock $(test_class) $(iterations) 1 $(first_place) $(params)
        X10_HOSTLIST=$(hosts) X10_RESILIENT_MODE=1 X10_PLACE_GROUP_RESTORE_MODE=1 X10_NPLACES=$(numplaces_withspare) ./$(target)_sock $(test_class) $(iterations) 1 $(middle_place) $(params)
        X10_HOSTLIST=$(hosts) X10_RESILIENT_MODE=1 X10_PLACE_GROUP_RESTORE_MODE=1 X10_NPLACES=$(numplaces_withspare) ./$(target)_sock $(test_class) $(iterations) 1 $(last_place) $(params)
#Shrink Rebalance for DistBlockMatrix >> X10_GML_REBALANCE=1
ifneq (,$(findstring DistBlockMatrix,$(test_class)))
        X10_HOSTLIST=$(hosts) X10_RESILIENT_MODE=1 X10_PLACE_GROUP_RESTORE_MODE=0 X10_NPLACES=$(numplaces) X10_GML_REBALANCE=1 ./$(target)_sock $(test_class) $(iterations) 0 $(first_place) $(params)
        X10_HOSTLIST=$(hosts) X10_RESILIENT_MODE=1 X10_PLACE_GROUP_RESTORE_MODE=0 X10_NPLACES=$(numplaces) X10_GML_REBALANCE=1 ./$(target)_sock $(test_class) $(iterations) 0 $(middle_place) $(params)
        X10_HOSTLIST=$(hosts) X10_RESILIENT_MODE=1 X10_PLACE_GROUP_RESTORE_MODE=0 X10_NPLACES=$(numplaces) X10_GML_REBALANCE=1 ./$(target)_sock $(test_class) $(iterations) 0 $(last_place) $(params)
endif

dist_sock       : sock
#Srink Tests
        X10_HOSTLIST=$(hosts) X10_RESILIENT_MODE=1 X10_PLACE_GROUP_RESTORE_MODE=0 X10_NPLACES=$(numplaces) X10_RESILIENT_STORE_MODE=1 ./$(target)_sock $(test_class) $(iterations) 0 $(first_place) $(params)
        X10_HOSTLIST=$(hosts) X10_RESILIENT_MODE=1 X10_PLACE_GROUP_RESTORE_MODE=0 X10_NPLACES=$(numplaces) X10_RESILIENT_STORE_MODE=1 ./$(target)_sock $(test_class) $(iterations) 0 $(middle_place) $(params)
        X10_HOSTLIST=$(hosts) X10_RESILIENT_MODE=1 X10_PLACE_GROUP_RESTORE_MODE=0 X10_NPLACES=$(numplaces) X10_RESILIENT_STORE_MODE=1 ./$(target)_sock $(test_class) $(iterations) 0 $(last_place) $(params)
#Redundant Tests
        X10_HOSTLIST=$(hosts) X10_RESILIENT_MODE=1 X10_PLACE_GROUP_RESTORE_MODE=1 X10_NPLACES=$(numplaces_withspare) X10_RESILIENT_STORE_MODE=1 ./$(target)_sock $(test_class) $(iterations) 1 $(first_place) $(params)
        X10_HOSTLIST=$(hosts) X10_RESILIENT_MODE=1 X10_PLACE_GROUP_RESTORE_MODE=1 X10_NPLACES=$(numplaces_withspare) X10_RESILIENT_STORE_MODE=1 ./$(target)_sock $(test_class) $(iterations) 1 $(middle_place) $(params)
        X10_HOSTLIST=$(hosts) X10_RESILIENT_MODE=1 X10_PLACE_GROUP_RESTORE_MODE=1 X10_NPLACES=$(numplaces_withspare) X10_RESILIENT_STORE_MODE=1 ./$(target)_sock $(test_class) $(iterations) 1 $(last_place) $(params)
#Shrink Rebalance for DistBlockMatrix >> X10_GML_REBALANCE=1 
ifneq (,$(findstring DistBlockMatrix,$(test_class)))
        X10_HOSTLIST=$(hosts) X10_RESILIENT_MODE=1 X10_PLACE_GROUP_RESTORE_MODE=0 X10_NPLACES=$(numplaces) X10_RESILIENT_STORE_MODE=1 X10_GML_REBALANCE=1 ./$(target)_sock $(test_class) $(iterations) 0 $(first_place) $(params)
        X10_HOSTLIST=$(hosts) X10_RESILIENT_MODE=1 X10_PLACE_GROUP_RESTORE_MODE=0 X10_NPLACES=$(numplaces) X10_RESILIENT_STORE_MODE=1 X10_GML_REBALANCE=1 ./$(target)_sock $(test_class) $(iterations) 0 $(middle_place) $(params)
        X10_HOSTLIST=$(hosts) X10_RESILIENT_MODE=1 X10_PLACE_GROUP_RESTORE_MODE=0 X10_NPLACES=$(numplaces) X10_RESILIENT_STORE_MODE=1 X10_GML_REBALANCE=1 ./$(target)_sock $(test_class) $(iterations) 0 $(last_place) $(params)
endif

###################################################
# Including rules
###################################################
include $(gml_path)/scripts/build_common.mk
include $(gml_path)/scripts/build_managed.mk
include $(gml_path)/scripts/build_native.mk
include $(gml_path)/scripts/build_native_mpi.mk
include $(gml_path)/scripts/test_run.mk

