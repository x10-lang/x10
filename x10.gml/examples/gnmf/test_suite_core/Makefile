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

## This makefile can be used to launch scalability test with respect to 
## the number of cores or places
## 1) launch benchmark testsuite,
## 2) parsing output log

##-----------------------------------

##-----------------------------------
SHELL=bash
MAKE =make

##-----------------------------------
run_test     = ./ptest_core.pl
result_dat   = result_core.dat

##-----------------------------------
default: help

##-----------------------------------
run		:
	$(run_test) -r

parse 	:
	$(run_test) -p | tee $(result_dat)

##-------------------

help    ::
	@echo "";
	@echo "====== Help: launch benchmark for scalability on cores/places ===========";
	@echo "make run        : launch benchmark tests";
	@echo "make parse      : parse output log of tests and write data to $(result_dat)";
	@echo "";
