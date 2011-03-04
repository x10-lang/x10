#!/usr/bin/ksh

#
# (c) Copyright IBM Corporation 2007
#
# $Id: build.sh,v 1.2 2007-06-04 10:51:33 srkodali Exp $
# Script for building the library.
#

CONFIGURE=${SRC_TOP}/config.sh
MAKE=`which make`

buildLib() {
	# clean the source hier
	echo "\n>> Preparing the distribution for configuration...\n"
	${MAKE} realclean
	echo "\n...done\n"

	# configure the distribution
	echo "\n>> Configuring the source...\n"
	${CONFIGURE}
	status=$?
	if [ $status -ne 0 ]
	then
		echo "\n...failed.\n"
		return 1
	else
		echo "\n...done.\n"
	fi

	# build the library
	echo "\n>> Building the library...\n"
	${MAKE} all
	status=$?
	if [ $status -ne 0 ]
	then
		echo "\n...failed.\n"
		return 1
	else
		echo "\n...done.\n"
	fi

	# do the local install
	echo "\n>> Doing the local install...\n"
	${MAKE} install
	status=$?
	if [ $status -ne 0 ]
	then
		echo "\n...failed.\n"
		return 1
	else
		echo "\n...done.\n"
	fi

	# prepare the documentation
	echo "\n>> Preparing the documentation...\n"
	${MAKE} doc
	status=$?
	if [ $status -ne 0 ]
	then
		echo "\n...failed.\n"
		return 1
	else
		echo "\n...done.\n"
	fi

	# build the example programs
	echo "\n>> Building the example programs...\n"
	${MAKE} samples
	status=$?
	if [ $status -ne 0 ]
	then
		echo "\n...failed.\n"
		return 1
	else
		echo "\n...done.\n"
	fi

	# build the benchmarks
	echo "\n>> Building the benchmarks..."
	${MAKE} bench
	status=$?
	if [ $status -ne 0 ]
	then
		echo "\n...failed.\n"
		return 1
	else
		echo "\n...done.\n"
	fi

	# build the test programs
	echo "\n>> Building the test programs..."
	${MAKE} test
	status=$?
	if [ $status -ne 0 ]
	then
		echo "\n...failed.\n"
		return 1
	else
		echo "\n...done.\n"
	fi

	return 0
}
