#
# (c) Copyright IBM Corporation 2007
#
# $Id: Makefile,v 1.2 2007-04-28 09:28:45 ganeshvb Exp $
# This file is part of X10 Runtime System.
#

## Makefile for X10Lib Testing.

MAKE = make

all:
	(cd pass; $(MAKE) all)
	(cd fail; $(MAKE) all)

install:
	@echo "Done \"install\" in \"test\" subdir."

clean:
	(cd pass; $(MAKE) clean)
	(cd fail; $(MAKE) clean)

