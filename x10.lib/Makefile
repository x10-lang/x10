#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.5 2008-06-05 11:03:22 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Toplevel Makefile for X10Lib.

MAKE = gmake
SUBDIRS = src doc examples test benchmarks
LIBDIR = lib
INCDIR = include
RM = rm -f

all:
	(cd src; $(MAKE) $@)
	(cd doc; $(MAKE) $@)

doc:
	(cd doc; $(MAKE) all)

samples:
	(cd examples; $(MAKE) all)

bench:
	@echo "@@Benchmarks building is disabled in the current release.@@"
#(cd benchmarks; $(MAKE) all)

test::
	(cd test; $(MAKE) all)

install:
	(cd src; $(MAKE) $@)
	(cd doc; $(MAKE) $@)

clean:
	(cd src; $(MAKE) $@)
	(cd doc; $(MAKE) $@)
	(cd examples; $(MAKE) $@)
	#(cd benchmarks; $(MAKE) $@)
	(cd test; $(MAKE) $@)

realclean: clean
	$(RM) -r $(INCDIR) $(LIBDIR)
