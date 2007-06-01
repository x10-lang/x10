#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.3 2007-06-01 14:24:51 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Makefile for X10Lib Testing.

MAKE = make

all::
	(cd pass; $(MAKE) all)
	(cd fail; $(MAKE) all)

install:
	@echo "Done \"install\" in \"test\" subdir."

clean:
	(cd pass; $(MAKE) clean)
	(cd fail; $(MAKE) clean)

