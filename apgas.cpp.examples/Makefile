APGAS_DIR = $(call abspath,../apgas.cpp)

include $(APGAS_DIR)/Make.rules
include ../x10.runtime/Make.rules

###########
# Targets #
###########

all:	hello fib

hello:	hello.cc
	$(CXX) $(CXXFLAGS) -o hello hello.cc $(APGAS_LINK_ARGS)

fib:	fib.cc
	$(CXX) $(CXXFLAGS) -o fib fib.cc $(APGAS_LINK_ARGS)

clean: 
	$(RM) hello fib

.PHONY: clean 

# vim:tabstop=8:shiftwidth=8:noexpandtab
