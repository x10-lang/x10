###################################################
###################################################
## Name:  	X10 application test
## Created by: 	Juemin Zhang
###################################################
###################################################
# This is makefile for building and running all tests
#

# set the target
sub_dirs 	= dense sparse block comm dist dup summa symtri vector distblock builder

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
