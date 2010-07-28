# A top-level Makefile to compile all X10 sources
# <BEGIN> user settings #

export X10CXX 		?= x10c++
export X10CXXFLAGS 	?= -commandlineonly -report postcompile=5 -VERBOSE_CALLS -NO_CHECKS -sourcepath . #-DISABLE_GC 
export X10RTTRANSPORT	?= pgas_lapi
export PGASTRANSPORT	?= lapi	   ### TEMPORARY! Added due to mismatch X10RT transport naming scheme and PGAS
export X10HOME		?= /vol/x10/new/x10-2.0/x10.par    ## v20 default


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
	 par-1d-dbg

all_opt: smp-opt\
	 par-1d-opt


smp-opt:
	$(MAKE) -C smp OUTDIR=opt-gen smp-opt

smp-dbg:
	$(MAKE) -C smp OUTDIR=dbg-gen smp-dbg

smp-clean:
	$(MAKE) -C smp clean

par-1d: par-1d-opt par-1d-dbg

par-1d-opt: sprng
	$(MAKE) -C par-1d OUTDIR=opt-gen par-1d-opt

par-1d-dbg: sprng
	$(MAKE) -C par-1d OUTDIR=dbg-gen par-1d-dbg

par-1d-clean: par-1d-opt-clean par-1d-dbg-clean

par-1d-dbg-clean:
	$(MAKE) -C par-1d OUTDIR=dbg-gen clean

par-1d-opt-clean:
	$(MAKE) -C par-1d OUTDIR=opt-gen clean

par-1d-hybrid-opt: sprng
	$(MAKE) -C par-1d-hybrid OUTDIR=opt-gen par-1d-opt

par-1d-hybrid-dbg: sprng
	$(MAKE) -C par-1d-hybrid OUTDIR=dbg-gen par-1d-dbg

par-1d-hybrid-clean: par-1d-hybrid-opt-clean par-1d-dbg-clean

par-1d-hybrid-dbg-clean:
	$(MAKE) -C par-1d-hybrid OUTDIR=dbg-gen clean

par-1d-hybrid-opt-clean:
	$(MAKE) -C par-1d-hybrid OUTDIR=opt-gen clean

sprng-clean:
	$(MAKE) -C sprng2.0 clean


clean: sprng-clean smp-clean par-1d-clean

.PHONY: smp par-1d
.PHONY: clean
