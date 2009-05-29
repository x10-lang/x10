#! /bin/ksh

# This script can be run by hand or it will be invoked by
# the ant LAPISupportantTask

# Some horrible things...
#   - javah produces header files in the classes directory. (should probably
#     find a real place for these.)
#   - result (.so file) goes in examples/lib/$X10_PLATFORM (since that
#     directory is already on the LD_LIBRARY_PATH)

if [[ "$1" == "x" || "$1" == "xx" ]]
then
    # used to NOT run javap which is PAINFULLY SLOW
    one=$1
    shift
fi
if [[ -x $JAVA_HOME/../bin/javah ]]
then
    JAVAH=$JAVA_HOME/../bin/javah    # suitable for use with J9
else
    JAVAH=$JAVA_HOME/bin/javah
fi
TOP=${TOP:-${X10}}
lapisuppdir=${1:-${PWD}}
classesdir=${2:-${PWD}}
compout=${lapisuppdir}/compiler.out

rc=0
if [[ "$one" != "xx" ]]
then
    cd ${classesdir};
    for f in VMInfo \
	     JAxeqb
    do
        ${JAVAH} ${f} >${compout} 2>&1 
	rc=$?
	if [[ $rc -ne 0 ]]
	then
	    break
	fi
    done
fi

if [[ $rc -eq 0 ]]
then
    cd ${lapisuppdir};
    slibclean
    xlC_r -g -qnolm -qnoeh -qnotempinc -bM:SHR -bnoentry -bexpall -I${classesdir} -I$JAVA_HOME/bin/include -I$JAVA_HOME/include -llapi_r -o libLAPISupport.a LAPISupport.cpp >${compout} 2>&1
    rc=$?
    if [[ $rc -eq 0 ]]
    then
	rm -f ${compout}
    else
	cat ${compout}
    fi
fi

