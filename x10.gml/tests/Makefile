#
#  This file is part of the X10 project (http://x10-lang.org).
#
#  This file is licensed to You under the Eclipse Public License (EPL);
#  You may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#      http://www.opensource.org/licenses/eclipse-1.0.php
#
#  (C) Copyright IBM Corporation 2006-2011.
#

###################################################
###################################################
## Name:  	X10 application test
## Created by: 	Juemin Zhang
## Contact:   	zhangj@us.ibm.com
###################################################
###################################################
# This is makefile for building and running all tests
#

# set the target
sub_dirs 	= dense sparse block comm dist dup summa

.PHONY		: test_all clean $(sub_dirs) help

default		: help
###-----------------------------------------------
test_all	: $(sub_dirs)

$(sub_dirs)	:
			$(MAKE) -C $@ runall

clean:
			for dir in $(sub_dirs); do \
				$(MAKE) -C $$dir clean_all; \
			done
help::
	@echo "--------------------------------------------------";
	@echo " make test_all    : test all sub dirs:$(sub_dirs)";
	@echo " make clean       : clean up all sub dirs";
	@echo "";