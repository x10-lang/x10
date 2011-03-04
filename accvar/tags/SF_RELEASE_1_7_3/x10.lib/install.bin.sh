#!/usr/bin/ksh

#
# (c) Copyright IBM Corporation 2007
#
# $Id: install.bin.sh,v 1.3 2007-12-10 18:06:02 srkodali Exp $
# This file is part of X10 Runtime System.
#

## Script for installing files in the binary distribution.

# set install prefix
PREFIX=${PREFIX}
if [ -z "${PREFIX}" ]
then
	PREFIX=${HOME}
fi

# setup install paths
DEST_BIN=${PREFIX}/bin
DEST_INC=${PREFIX}/include/x10
DEST_LIB=${PREFIX}/lib
DEST_SHARE=${PREFIX}/share/X10Lib
DEST_DOC=${DEST_SHARE}/doc
DEST_EX=${DEST_SHARE}/examples
DEST_HOST=${DEST_SHARE}/hostfiles

# src paths
WORK=`pwd`
SRC_BIN=${WORK}/bin
SRC_INC=${WORK}/include
SRC_LIB=${WORK}/lib
SRC_DOC=${WORK}/doc
SRC_EX=${WORK}/examples
SRC_HOST=${WORK}/hostfiles

# setup x10lib vars for customization
X10LIB_HOME=${PREFIX}
X10LIB_INCLUDE=${DEST_INC}
X10LIB_LIBPATH=${DEST_LIB}

# IDs and modes for installation
UID=`id -u -n`
GID=`id -g -n`
BINMODE=755
DATAMODE=644

# files to customize before install
FRONT_ENDS="x10libcxx x10libcc"
EX_MAKE="Makefile"

# other files to install
TOPLFILES="RELEASE.NOTES RELEASE.NOTES_05 INSTALL epl-v10.html"
DOCFILES="api.txt usage.txt x10lib-design.pdf"
EXFILES="README.examples *.c *.cc"
EXSCRIPTS="*.sh"
HOSTFILES="*.list"
LIBFILE=libx10.a
INCFILES="*.h"

# install log file
LOGFILE=/tmp/install.$$.log

# statrt logging
echo "$0 started on `date`....." 2>&1| tee ${LOGFILE}

# create dest dirs if not already available
echo "\nCreating the necessary directories.....\n" 2>&1| tee -a ${LOGFILE}
for dir in ${DEST_BIN} ${DEST_INC} ${DEST_LIB} ${DEST_SHARE} \
			${DEST_DOC} ${DEST_EX} ${DEST_HOST}
do
	if [ ! -d ${dir} ]
	then
		echo "mkdir -p ${dir}" 2>&1| tee -a ${LOGFILE}
		mkdir -p ${dir}
	fi
done
echo "\n.....done\n" 2>&1| tee -a ${LOGFILE}

# install toplevel files
echo "\nInstalling toplevel files in ${DEST_SHARE}.....\n" 2>&1| tee -a ${LOGFILE}
for file in ${TOPLFILES}
do
	echo "install -f ${DEST_SHARE} -G ${GID} -M ${DATAMODE} -o -O ${UID} ${file}" \
			2>&1| tee -a ${LOGFILE}
	install -f ${DEST_SHARE} -G ${GID} -M ${DATAMODE} -o -O ${UID} ${file}
done
echo "\n.....done\n" 2>&1| tee -a ${LOGFILE}

# install documentation
echo "\nInstalling documentation in ${DEST_DOC}.....\n" 2>&1| tee -a ${LOGFILE}
(cd ${SRC_DOC}; \
for file in ${DOCFILES}; \
do \
	echo "install -f ${DEST_DOC} -G ${GID} -M ${DATAMODE} -o -O ${UID} ${file}" \
			2>&1| tee -a ${LOGFILE} ; \
	install -f ${DEST_DOC} -G ${GID} -M ${DATAMODE} -o -O ${UID} ${file} ; \
done; \
)
echo "\n.....done\n" 2>&1| tee -a ${LOGFILE}

# install example sources and scripts
echo "\nInstalling example programs in ${DEST_EX}.....\n" 2>&1| tee -a ${LOGFILE}
(cd ${SRC_EX}; \
for file in ${EXFILES}; \
do \
	echo "install -f ${DEST_EX} -G ${GID} -M ${DATAMODE} -o -O ${UID} ${file}" \
			2>&1| tee -a ${LOGFILE} ; \
	install -f ${DEST_EX} -G ${GID} -M ${DATAMODE} -o -O ${UID} ${file} ; \
done; \
for file in ${EXSCRIPTS}; \
do \
	echo "install -f ${DEST_EX} -G ${GID} -M ${BINMODE} -o -O ${UID} ${file}" \
			2>&1| tee -a ${LOGFILE} ; \
	install -f ${DEST_EX} -G ${GID} -M ${BINMODE} -o -O ${UID} ${file} ; \
done; \
)
echo "\n.....done\n" 2>&1| tee -a ${LOGFILE}


# install hostfiles
echo "\nInstalling hostfiles in ${DEST_HOST}.....\n" 2>&1| tee -a ${LOGFILE}
(cd ${SRC_HOST}; \
for file in ${HOSTFILES}; \
do \
	echo "install -f ${DEST_HOST} -G ${GID} -M ${DATAMODE} -o -O ${UID} ${file}" \
			2>&1| tee -a ${LOGFILE} ; \
	install -f ${DEST_HOST} -G ${GID} -M ${DATAMODE} -o -O ${UID} ${file} ; \
done; \
)
echo "\n.....done\n" 2>&1| tee -a ${LOGFILE}

# install headers
echo "\nInstalling headers in ${DEST_INC}.....\n" 2>&1| tee -a ${LOGFILE}
(cd ${SRC_INC}; \
for file in ${INCFILES}; \
do \
	echo "install -f ${DEST_INC} -G ${GID} -M ${DATAMODE} -o -O ${UID} ${file}" \
			2>&1| tee -a ${LOGFILE} ; \
	install -f ${DEST_INC} -G ${GID} -M ${DATAMODE} -o -O ${UID} ${file} ; \
done; \
)
echo "\n.....done\n" 2>&1| tee -a ${LOGFILE}

# install library
echo "\nInstalling library in ${DEST_LIB}.....\n" 2>&1| tee -a ${LOGFILE}
(cd ${SRC_LIB}; \
echo "install -f ${DEST_LIB} -G ${GID} -M ${DATAMODE} -o -O ${UID} ${LIBFILE}" \
	2>&1| tee -a ${LOGFILE} ; \
install -f ${DEST_LIB} -G ${GID} -M ${DATAMODE} -o -O ${UID} ${LIBFILE} ; \
)
echo "\n.....done\n" 2>&1| tee -a ${LOGFILE}
	
# customize scripts before installation
(cd ${SRC_BIN}; \
for script in ${FRONT_ENDS}; \
do \
	echo "sed -e 's;@@X10LIB_HOME@@;'${X10LIB_HOME}';' ${script} > __${script}" \
		2>&1| tee -a ${LOGFILE} ; \
	sed -e 's;@@X10LIB_HOME@@;'${X10LIB_HOME}';' ${script} > __${script} ; \
	echo "install -f ${DEST_BIN} -G ${GID} -M ${BINMODE} -O ${UID} __${script}" \
		2>&1| tee -a ${LOGFILE} ; \
	install -f ${DEST_BIN} -G ${GID} -M ${BINMODE} -O ${UID} __${script} ; \
	echo "mv -f ${DEST_BIN}/__${script} ${DEST_BIN}/${script}" 2>&1| tee -a ${LOGFILE} ; \
	mv -f ${DEST_BIN}/__${script} ${DEST_BIN}/${script} ; \
	echo "rm -f __${script}" 2>&1| tee -a ${LOGFILE} ; \
	rm -f __${script} ; \
done; \
)

# customize examples Makefile before installation
(cd ${SRC_EX};
echo "sed -e 's;@@X10LIB_HOME@@;'${X10LIB_HOME}';' ${EX_MAKE} > __${EX_MAKE}" \
	2>&1| tee -a ${LOGFILE} ; \
sed -e 's;@@X10LIB_HOME@@;'${X10LIB_HOME}';' ${EX_MAKE} > __${EX_MAKE} ; \
echo "install -f ${DEST_EX} -G ${GID} -M ${DATAMODE} -O ${UID} __${EX_MAKE}" \
	2>&1| tee -a ${LOGFILE} ; \
install -f ${DEST_EX} -G ${GID} -M ${DATAMODE} -O ${UID} __${EX_MAKE} ; \
echo "mv -f ${DEST_EX}/__${EX_MAKE} ${DEST_EX}/${EX_MAKE}" 2>&1| tee -a ${LOGFILE} ; \
mv -f ${DEST_EX}/__${EX_MAKE} ${DEST_EX}/${EX_MAKE} ; \
echo "rm -f __${EX_MAKE}" 2>&1| tee -a ${LOGFILE} ; \
rm -f __${EX_MAKE} ;
)

# stop logging
echo ".....done install." 2>&1| tee -a ${LOGFILE}

# move log file to shared dir
mv -f ${LOGFILE} ${DEST_SHARE}/install.log

exit 0
