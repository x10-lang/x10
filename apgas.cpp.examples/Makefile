DISTDIR = $(call abspath,../x10.dist)
X10_RUNTIME_DIR = $(call abspath,../x10.runtime)
APGAS_DIR = $(call abspath,../apgas.cpp)

#########################
# User-servicable Parts #
#########################

include $(X10_RUNTIME_DIR)/Make.rules
include $(DISTDIR)/stdlib/libx10.properties

FIND ?= find
XARGS ?= xargs
X10RT_LIB ?= x10rt_sockets

#############
# Variables #
#############

ifdef DEBUG
    XRX_LIB=lib-dbg
    XRX_INCLUDE=include-dbg
else
    XRX_LIB=lib
    XRX_INCLUDE=include
endif

override CXXFLAGS += -I$(APGAS_DIR)/src -I$(DISTDIR)/include -I$(DISTDIR)/stdlib/$(XRX_INCLUDE) -g

APGAS_LINK_ARGS = -L$(APGAS_DIR) -lapgas -Wl,--rpath -Wl,$(APGAS_DIR) \
	-L$(DISTDIR)/lib -l$(X10RT_LIB) -Wl,--rpath -Wl,$(DISTDIR)/lib \
	-L$(DISTDIR)/stdlib/$(XRX_LIB) $(X10LIB_LDLIBS) -Wl,--rpath -Wl,$(DISTDIR)/stdlib/$(XRX_LIB)

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
