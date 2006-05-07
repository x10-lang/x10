#! /bin/bash

# NOTE: this script assumes cx10 is installed on a network file system
# that is mounted the same way in all physical nodes involved.  Also,
# 'java' command can be found in the same directory on these nodes.
# Also keep in mind, 'ps', 'ssh' might behavoir differently on different
# platform, so you might have to customize this scirpt for your use.
# It's tested to work well on linux_x86.
# Bin Xin, xinb@purdue.edu
#


# No need to change below, if this script is used in current directory
# and x10 classes are compiled in current directory.
CLASSDIR=`pwd`
X10=../bin/x10
MAX_PLACES=4
standard_cfg=""  # maybe add this option later XXX

cfg_file=  # cx10 config file
mainvm=    # place 0 VM hostname
MAINCLASS=

testrun=0  # test the command sequences in this file

#
# Sanity checks ...
# we can find the CX10 configuration file ...
ARGS=2
if [ $# -ne "$ARGS" ]
then
    echo "cx10: Usage: `basename $0` <cx10 config file> <Main Class>"
    exit -1
fi

if [ -f "$1" ]
then
    cfg_file=$1
    MAINCLASS=$2
else
    echo "cx10: File \"$1\" does not exist."
    exit -1
fi

if [ "$testrun" -ne "0" ] ; then
    echo "cx10: Testing only ..."
fi

#
# Parse the config file
#
stripped=""
machinelist=""
vmidlist=""
places=""

stripped=`grep -v ^# $cfg_file`
places=`echo "$stripped" | cut -f3 -d,`

# More sanity check ...
# The place configured match the total number of placees ...
pls_cfged=`echo $places | wc -w`
if [  "$pls_cfged" -ne "$MAX_PLACES" ] ; then
    echo "cx10: places configured $pls_cfged doesn't match that of place.MAX_PLACES $MAX_PLACES."
    exit -1
else
    echo "" # more tests to come ... XXX
fi

# format ok
machinelist=`echo "$stripped" | cut -f2 -d,`
vmidlist=`echo "$stripped" | cut -f1 -d,`

# find place 0 vmid
mainhost=""
mainvm=`echo $stripped | grep 0 | cut -f1 -d,`
if [ -z "$mainvm" ]
then
    echo "cx10: place 0 is not configured."
    exit -1
fi


#
# Main loop to lauch all the CX10 VMs
#
#echo "$machinelist"
#echo "$vmidlist"
cnt=0
for host in $machinelist; do
    cnt=`expr 1 + $cnt`
    vmid=`echo $vmidlist | cut -f $cnt -d" "`
    if [ "$vmid" -eq "$mainvm" ] ; then
	mainhost=$host
	continue  # launch main last
    fi
    
    if [ "$testrun" -ne "0" ] ; then
	echo "ssh -f $host \"cd $CLASSDIR; $X10 -multi $vmid -Dx10.cluster.cfgfile=$cfg_file $standard_cfg $MAINCLASS\""
    else
	ssh -f $host "cd $CLASSDIR; $X10 -multi $vmid -Dx10.cluster.cfgfile=$cfg_file  $standard_cfg $MAINCLASS"
	
	# wait till the VM is started
	vm_started=`ssh $host "ps -eo comm | grep x10"`
	while [ -z "$vm_started" ] ; do
	    sleep 1
	done
    fi
    echo "cx10: Remote VM started at $host."
done

# Lauch main VM
if [ "$testrun" -ne "0" ] ; then
    echo "ssh $mainhost \"cd $CLASSDIR; $X10 -multi $mainvm -Dx10.cluster.cfgfile=$cfg_file $standard_cfg $MAINCLASS\""
else
    ssh $mainhost "cd $CLASSDIR; $X10 -multi $mainvm -Dx10.cluster.cfgfile=$cfg_file  $standard_cfg $MAINCLASS"
fi


# wait for main-VM to finish ...
#if [ "$testrun" -ne "0" ] ; then
#    echo "ssh $mainhost \"pgrep -f \"multi $mainvm\"\" | wc -w"
#else
#    main_finished=`ssh $mainhost "pgrep -f \"multi $mainvm\"" | wc -w`
#    while [ "$main_finished" -ne "2" ] ;  do
#	sleep 2
#	main_finished=`ssh $mainhost "pgrep -f \"multi $mainvm\"" | wc -w`
#    done
#fi

# shut down
#for host in $machinelist ; do
#    if [ "$testrun" -ne "0" ] ; then
#	echo "cx10: ssh $host \"pkill x10\""
#    else
#	echo "cx10: terminate $host ..."
#	ignored=`ssh $host \"pkill x10\"`
#    fi
#done

exit 0


