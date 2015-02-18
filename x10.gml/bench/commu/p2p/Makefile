#
#  This file is part of the X10 project (http://x10-lang.org).
#
#  This file is licensed to You under the Eclipse Public License (EPL);
#  You may not use this file eX10CXXept in compliance with the License.
#  You may obtain a copy of the License at
#      http://www.opensource.org/licenses/eclipse-1.0.php
#
#  (C) Copyright IBM Corporation 2006-2014.
#

# This is makefile for building and testing P2P 
# communication


###################################################
# Source and target file
###################################################

#default target
target		= TestMatP2P

#List of all targets
target_list = $(target)

#-------------------------------------
# GML path
gml_path    = ../../../

# build path
build_path	= build

# compile options
X10_FLAG	= -d $(build_path) -report postcompile=2 -v -errors 5 -VERBOSE_CHECKS -O -NO_CHECKS

# runtime settings
runtime_list = mpi sock java
numplaces = 4
test_args = 

###################################################
TestP2P	: $(build_path)/TestP2P.class	

$(build_path)/TestP2P.class	: TestP2P.x10
			$(X10C) $(X10_FLAG)  $< 
			
TestP2P_mpi	: TestP2P.x10
	$(X10CXX)  -x10rt mpi $(X10_FLAG) $< -o $@ -post 'mpic++ # #'

TestP2P_sock: TestP2P.x10
	$(X10CXX) -x10rt sockets $(X10_FLAG) $< -o $@

clean_all::
	rm -f TestP2P_mpi TestP2P_sock

###################################################
# Including rules
###################################################
include $(gml_path)/scripts/build_common.mk
include $(gml_path)/scripts/build_managed.mk
include $(gml_path)/scripts/build_native.mk
include $(gml_path)/scripts/build_native_mpi.mk
include $(gml_path)/scripts/test_run.mk

