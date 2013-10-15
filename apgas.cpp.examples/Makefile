DISTDIR = $(call abspath,../x10.dist)
X10_RUNTIME_DIR = $(call abspath,../x10.runtime)
APGAS_DIR = $(call abspath,../apgas.cpp)

#########################
# User-servicable Parts #
#########################

include $(X10_RUNTIME_DIR)/Make.rules

FIND ?= find
XARGS ?= xargs
X10RT_LIB ?= x10rt_sockets

#############
# Variables #
#############

override CXXFLAGS += -I$(APGAS_DIR)/src -I$(APGAS_DIR)/src-crx -I$(DISTDIR)/include -g

APGAS_LINK_ARGS = -L$(APGAS_DIR) -lapgas -Wl,--rpath -Wl,$(APGAS_DIR) \
	-L$(DISTDIR)/lib -l$(X10RT_LIB) -Wl,--rpath -Wl,$(DISTDIR)/lib

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
