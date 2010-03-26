# A top-level Makefile to compile all X10 sources
# <BEGIN> user settings #

export X10CXX 		?= x10c++
export X10CXXFLAGS 	?= -commandlineonly -report postcompile=5 #-DISABLE_GC
export X10RTTRANSPORT	?= pgas_lapi
export PGASTRANSPORT	?= lapi	   ### TEMPORARY! Added due to mismatch X10RT transport naming scheme and PGAS


# <END> user settings #

ifeq ($(shell uname -s),AIX)
   include arch/aix.mk
endif
ifeq ($(shell uname -s),Linux)
   include arch/linux.mk
endif
ifeq ($(shell uname -s),Darwin)
   include arch/darwin.mk
endif
ifeq ($(firstword $(subst _, ,$(shell uname -s))),CYGWIN)
   include arch/cygwin.mk
endif
ifeq ($(BGP_CROSS_COMPILE), 1)
   include arch/bgp.mk
endif


all: sprng all_dbg all_opt

sprng:
	$(MAKE) -C sprng2.0

all_dbg: smp-dbg\
	 dist-1d-dbg

all_opt: smp-opt\
	 dist-1d-opt


smp-opt:
	$(MAKE) -C smp smp-opt

smp-dbg:
	$(MAKE) -C smp smp-dbg

smp-clean:
	$(MAKE) -C smp clean

dist-1d: dist-1d-opt dist-1d-dbg

dist-1d-opt:
	$(MAKE) -C dist-1d dist-1d-opt

dist-1d-dbg:
	$(MAKE) -C dist-1d dist-1d-dbg

dist-1d-clean:
	$(MAKE) -C dist-1d clean

spring-clean:
	$(MAKE) -C sprng2.0 clean


clean: spring-clean smp-clean dist-1d-clean

.PHONY: smp dist-1d
.PHONY: clean
