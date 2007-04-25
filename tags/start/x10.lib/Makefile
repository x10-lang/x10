#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.1.1.1 2007-04-25 09:57:46 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Toplevel Makefile for X10Lib.

MAKE = make
SUBDIRS = src doc examples test
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

test:
	(cd test; $(MAKE) all)

install:
	(cd src; $(MAKE) $@)
	(cd doc; $(MAKE) $@)

clean:
	(cd src; $(MAKE) $@)
	(cd doc; $(MAKE) $@)
	(cd examples; $(MAKE) $@)
	(cd test; $(MAKE) $@)

realclean: clean
	$(RM) -r $(INCDIR) $(LIBDIR)
