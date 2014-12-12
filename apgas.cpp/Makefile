DISTDIR = $(call abspath,../x10.dist)
X10_RUNTIME_DIR = $(call abspath,../x10.runtime)
APGAS_DIR = $(call abspath,.)

#########################
# User-servicable Parts #
#########################

include $(X10_RUNTIME_DIR)/Make.rules
include $(DISTDIR)/stdlib/libx10.properties

FIND ?= find
XARGS ?= xargs

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

override CXXFLAGS += $(X10LIB_CXXFLAGS) -I$(DISTDIR)/include -Isrc -I$(DISTDIR)/stdlib/$(XRX_INCLUDE) -g 

###########
# Targets #
###########

APGAS_ARCHIVE = $(LIBPREFIX)apgas$(LIBSUFFIX)

# this builds everything
apgas: $(APGAS_ARCHIVE)

.PHONY: apgas

APGAS_SRC = src/apgas/Pool.cc src/apgas/Task.cc

APGAS_OBJS = $(APGAS_SRC:.cc=.o)

###############################
# Compilation                 #
###############################

$(APGAS_ARCHIVE): $(APGAS_OBJS)
	$(CXX) $(CXXFLAGS) $(CXXFLAGS_SHARED) $(LDFLAGS_SHARED) $(APGAS_OBJS) -o $@ 

# ##################
# # Standard Stuff #
# ##################

# disable default rules
%.o:
%.o:%.c
%.o:%.cc
%:%.o
%:%.c
%:%.cc

%.o: %.cc %.h 
	$(CXX) $(CXXFLAGS)  $(CXX_WARNING_FLAGS) $(CXXFLAGS_SHARED) -c $< -o $@


#remove only object code
cleanobj:
	$(FIND) . \( -name '*.h.gch' -o -name '*.o' -o -name '*.rpo' \) -print0 | $(XARGS) -0 -t $(RM) -r

clean: cleanobj
	rm -f $(APGAS_ARCHIVE)

.PHONY: clean cleanobj

# vim:tabstop=8:shiftwidth=8:noexpandtab
