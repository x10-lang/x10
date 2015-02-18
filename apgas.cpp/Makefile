include ../x10.runtime/Make.rules
include Make.rules

FIND ?= find
XARGS ?= xargs

#############
# Variables #
#############

override CXXFLAGS += -g 

###########
# Targets #
###########

APGAS_ARCHIVE = $(LIBPREFIX)apgas$(LIBSUFFIX)

# this builds everything
apgas: $(APGAS_ARCHIVE)
	@echo installing $(APGAS_ARCHIVE) in $(X10_DIST)/stdlib/$(XRX_LIB)
	@cp -p $(APGAS_ARCHIVE) $(X10_DIST)/stdlib/$(XRX_LIB)
	@cd src && $(FIND) . -name \*.h | tar cf - -T - | (cd $(X10_DIST)/stdlib/$(XRX_INCLUDE) && tar xf -)

.PHONY: apgas


APGAS_SRC = src/apgas/Runtime.cc src/apgas/Task.cc
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
	$(CXX) -Isrc $(CXXFLAGS) $(CXX_WARNING_FLAGS) $(CXXFLAGS_SHARED) -c $< -o $@


#remove only object code
cleanobj:
	$(FIND) . \( -name '*.h.gch' -o -name '*.o' -o -name '*.rpo' \) -print0 | $(XARGS) -0 -t $(RM) -r

clean: cleanobj
	rm -f $(APGAS_ARCHIVE)

.PHONY: clean cleanobj

# vim:tabstop=8:shiftwidth=8:noexpandtab
