APGAS_DIR = $(call abspath,../apgas.cpp)

include $(APGAS_DIR)/Make.rules
include ../x10.runtime/Make.rules

###########
# Targets #
###########

all:	hello helloAll fib daxpy

hello:	hello.cc
	$(CXX) $(CXXFLAGS) -o $@ $< $(APGAS_LINK_ARGS)

helloAll:	helloAll.cc
	$(CXX) $(CXXFLAGS) -o $@ $< $(APGAS_LINK_ARGS)

fib:	fib.cc
	$(CXX) $(CXXFLAGS) -o $@ $< $(APGAS_LINK_ARGS)

daxpy:	daxpy.cc
	$(CXX) $(CXXFLAGS) -o $@ $< $(APGAS_LINK_ARGS)

clean: 
	$(RM) hello fib helloAll daxpy

.PHONY: clean 

# vim:tabstop=8:shiftwidth=8:noexpandtab
