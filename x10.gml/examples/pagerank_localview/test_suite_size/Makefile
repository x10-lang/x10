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

## 
## This makefile can be used to 
## 1) launch benchmark testsuite for evaluating scalability of problem size,
## 2) parsing output log

##-----------------------------------

##-----------------------------------
SHELL=bash
MAKE =make

##-----------------------------------
run_test     = ./ptest_size.pl
result_dat   = result_size.dat

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
	@echo "====== Help: launch test for problem size scalability ===========";
	@echo "make run        : launch benchmark tests";
	@echo "make parse      : parse output log of tests and write data to $(result_dat)";
	@echo "";
