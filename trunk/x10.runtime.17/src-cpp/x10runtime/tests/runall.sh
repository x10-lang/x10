#!/usr/bin/sh

# (c) Copyright IBM Corporation 2008
#
# $Id$
# This file is part of XRX/C++ native layer implementation.

## Script to run all native layer implementation tests.

LOGF=run.log

printf ">>>>>======================================================================\n\n" 2>&1| tee -a ${LOGF}
printf "$(date)\n\n" 2>&1| tee -a ${LOGF}
for test in *.x
do
	printf "==> Running test $test .....\n\n" 2>&1| tee -a ${LOGF}
	./${test} 2>&1| tee -a ${LOGF}
	printf "\n..... done.\n\n" 2>&1| tee -a ${LOGF}
done
printf "\n======================================================================<<<<<\n\n" 2>&1| tee -a ${LOGF}
