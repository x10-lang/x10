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

# Run benchmarks
# Run [bench] executable: make [run_mpi|run_sock|java]

# Bench mark test suite
# cd [placehandle|distarray|distmatrix]/test_suite_[java|sock|mpi]; ./ptest_[probsizes|nodes|cores].pl -r
##-------------------------------------
SHELL=bash
MAKE=make

###################################################
# Source and target file
###################################################
bench_dirs	=placehandle distarray distmatrix
test_list 	=probsizes nodes  

suite_dirs	= $(foreach bt, $(bench_dirs), $(bt) )

## default settings
testcase=placehandle
bench_opt = 

##========================================

default	: help

##------------------

build_all	: $(bench_dirs)

$(bench_dirs)	:
		@echo "====================================================================================================="
		@echo "================================== build benchmark in $@  ====================================="
		$(MAKE) -C $@ all

#--------------
bench_all	: $(suite_dirs)

$(suite_dirs)	:
		@echo "====================================================================================================="
		@echo "================================== run benchmark test suite in $@  ====================================="
		$(MAKE) -C $@ runall

#--------------

parse_all: 
		for dir in $(suite_dirs); do \
			$(MAKE) -C $$dir parse_all; \
		done;

##-------------
clean_all:
		for dir in $(bench_dirs); do \
			$(MAKE) -C $$dir clean; \
		done;

##-------------
clear_log :
		for dir in $(suite_dirs); do \
			$(MAKE) -C $$dir clear_log; \
		done;
##-------------
help	::
		@echo "==================== Build and test communication benchmarks ==================="
		@echo "make build_all	: build all communication benchmark tests in all sub dirs:$(bench_dirs)"
		@echo "make bench_all	: run all communication benchmark test suites in all sub dirs:$(suite_dirs)"
		@echo "make parse_all	: parse all communication benchmark test logs"
		@echo "make clean_all	: clean all communication benchmark test build"
		@echo "make clear_log	: remove all communication benchmark test suite logs"

###---------------------------------------
.PHONY	: $(bench_dirs) $(suite_dirs) build_all bench_all parse_all clean_all clear_all help
