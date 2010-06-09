# A top-level Makefile to compile all X10 sources
# <BEGIN> user settings #

export X10CXX 		?= x10c++
export X10CXXFLAGS 	?= -commandlineonly -report postcompile=5 -VERBOSE_CALLS -NO_CHECKS -sourcepath . #-DISABLE_GC 
export X10RTTRANSPORT	?= pgas_lapi
export PGASTRANSPORT	?= lapi	   ### TEMPORARY! Added due to mismatch X10RT transport naming scheme and PGAS
export X10HOME		?= /vol/x10/new/x10-2.0/x10.dist    ## v20 default


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

dist-1d-opt: sprng
	$(MAKE) -C dist-1d OUTDIR=opt-gen dist-1d-opt

dist-1d-dbg: sprng
	$(MAKE) -C dist-1d OUTDIR=dbg-gen dist-1d-dbg

dist-1d-clean: dist-1d-opt-clean dist-1d-dbg-clean

dist-1d-dbg-clean:
	$(MAKE) -C dist-1d OUTDIR=dbg-gen clean

dist-1d-opt-clean:
	$(MAKE) -C dist-1d OUTDIR=opt-gen clean

sprng-clean:
	$(MAKE) -C sprng2.0 clean


clean: sprng-clean smp-clean dist-1d-clean

.PHONY: smp dist-1d
.PHONY: clean
