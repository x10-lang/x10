#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.3 2007-08-02 13:19:21 srkodali Exp $
# This file is part of X10 Applications.
#

## Toplevel Makefile for RandomAccess experiments.

include config/make.defs

SUBDIRS = hpcc hpcc_opt lapi x10c x10lib

all:
	(cd hpcc; $(MAKE) arch=$(ARCH))
	$(CP) hpcc/hpcc run/hpcc
	$(CP) hpcc/_hpccinf.txt run/_hpccinf.txt
	(cd hpcc_opt; $(MAKE) arch=$(ARCH))
	$(CP) hpcc_opt/hpcc run/hpcc_opt
	(cd lapi; $(MAKE) $@)
	$(CP) lapi/gups run/gups
	(cd x10c; $(MAKE) $@)
	$(CP) x10c/RandomAccess_Dist run/RandomAccess_Dist
	(cd x10lib; $(MAKE) $@)
	$(CP) x10lib/RandomAccess_spmd run/RandomAccess_spmd

clean:
	(cd hpcc; $(MAKE) arch=$(ARCH) $@)
	(cd hpcc-opt; $(MAKE) arch=$(ARCH) $@)
	(cd lapi; $(MAKE) $@)
	(cd x10c; $(MAKE) $@)
	(cd x10lib; $(MAKE) $@)
	(cd run; $(RM) hpcc _hpccinf.txt hpcc_opt gups \
		RandomAccess_Dist RandomAccess_spmd)
