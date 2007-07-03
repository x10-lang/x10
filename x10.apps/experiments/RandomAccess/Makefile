#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.1 2007-07-03 11:25:54 ganeshvb Exp $
# This file is part of X10 Runtime System.
#

## Toplevel Makefile for X10Lib.

include make.def

MAKE = make
SUBDIRS = x10lib x10c hpcc
LIBDIR = lib
INCDIR = include
RM = rm -f

all:
	(cd x10lib; $(MAKE) $@)
	(cd x10c; $(MAKE) $@)
	(cd hpcc-opt; $(MAKE) arch=$(ARCH))

clean:
	(cd x10lib; $(MAKE) $@)
	(cd x10c; $(MAKE) $@)
	(cd hpcc-opt; $(MAKE) $@)

realclean: clean
	$(RM) -r $(INCDIR) $(LIBDIR)
