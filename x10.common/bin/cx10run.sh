#! /bin/bash

# NOTE: this script assumes cx10 is installed on a network file system
# that is mounted the same way in all physical nodes involved.  Also,
# 'java' command can be found in the same directory on these nodes.
# Also keep in mind, 'ps', 'ssh' might behavior differently on different
# platform, so you might have to customize this script for your use.
# It's tested to work well on linux_x86.
# Bin Xin, xinb@purdue.edu
#


# No need to change below, if this script is used in current directory
# and x10 classes are compiled in current directory.
CLASSDIR=`pwd`
X10=../bin/x10

testrun=0  # test the command sequences in this file
printhelp=0
quitvms=0
args=""
numargs=0
cfg_file=""
standard_cfg=""

#
# Command line processing ...
#
while true; do
    case "$1" in
	"") break;;
	-h) printhelp="1"; shift;;
	-t) testrun="1"; shift;;
	-q) quitvms="1"; shift;; 
	-f) shift; cfg_file="$1"; shift;;
	-s) shift; standard_cfg="$1"; shift;;
	*) args="$args '$1'"; numargs=$((${numargs}+1)); shift;;
    esac
done

if [ "$printhelp" -eq "1" -o "$numargs" -eq "0" ] ; then
    echo "NAME"
    echo "    cx10run.sh  script to launch an X10 program on a cluster."
    echo ""
    echo "SYNOPSIS"
    echo "    cx10run.sh [-h -t -q][-f 'cluster.cfg'][-s 'standard.cfg'] 'Main'"
    echo ""
    echo "DESCRIPTION"
    echo "    This script runs an X10 program on a cluster by ssh'ing to machines found in 'cluster.cfg' file, starting all X10 VMs in the right order and run the 'Main' program.  After the program finishes, all VMs will exit automatically."
    echo ""
    echo "    Standard configuration, including maximum number of places, can be specified in 'standard.cfg' , if necessary.  The default max number of places is 4."
    echo ""
    echo "    To do a test run without actually launching VMs, use -t option.  If something goes wrong and VMs didn't quit automatically, use -q option to force quite (test on Linux, might not work on all platforms). The -q option require the -f option, too, in order to find the machines. To print this manual page, use -h option."
    echo ""
    echo "OPTIONS"
    echo "    -f cluster.cfg  Specify the place-to-machines configuration file.  Required."
    echo ""
    echo "    -s standard.cfg Specify the standard configurations, like max number of places. Optional."
    echo ""
    echo "    -t  Test run."
    echo ""
    echo "    -q  Force quit all VMs launched previously in case of failure. Require -f option."
    echo ""
    echo "    -h  Print this message."
    echo ""
    echo "COMMENTS"
    echo "    Send comments to xinb@cs.purdue.edu."
    echo ""
    exit -1
fi


#
# Sanity checks ...
# we can find the CX10 configuration file ...
#
if [ -n "$cfg_file" ] ; then
    if [ ! -f "$cfg_file" ] ; then
	echo "cx10: File \"$cfg_file\" does not exist."
	exit -1
    fi
else
    echo "cx10: cluster configuration file not specified."
    exit -1;
fi
MAX_PLACES=4  
standard_opt=""
if [ -n "$standard_cfg" ] ; then
    if [ ! -f "$standard_cfg" ] ; then
	echo "cx10: File \"$standard_cfg\" does not exist."
	exit -1
    else
	numplaces=`grep NUMBER_OF_LOCAL_PLACES $standard_cfg`;
	if [ -n "$numplaces" ] ; then
	    MAX_PLACES=`echo "$numplaces" | cut -f2 -d=`
	fi
	if [ "$MAX_PLACES" -lt "1" ] ; then
	    echo "cx10: incorrect number of places specified $MAX_PLACES"
	    exit -1;
	fi
	standard_opt="-Dx10.configuration=$standard_cfg";
    fi
fi

if [ "$testrun" -ne "0" ] ; then
    echo "cx10: Testing only ..."
fi

mainvm=    # place 0 VM hostname
MAINCLASS="$args"

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


# Force quite in case of failure. Need a more platform independent way.
force_quit() {
    for host in $machinelist ; do
	if [ "$testrun" -ne "0" ] ; then
	    echo "cx10: ssh $host \"killall -g x10\""
	else
	    echo "cx10: terminate $host ...";
	    ssh $host \"killall -g x10\";
	fi
    done
}

if [ "$quitvms" -eq "1" ] ; then
    force_quit;
    exit -1;
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
	echo "ssh -f $host \"cd $CLASSDIR; $X10 -multi $vmid $standard_opt -Dx10.cluster.cfgfile=$cfg_file $MAINCLASS\""
    else
	ssh -f $host "cd $CLASSDIR; $X10 -multi $vmid $standard_opt -Dx10.cluster.cfgfile=$cfg_file $MAINCLASS"
	
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
    echo "ssh $mainhost \"cd $CLASSDIR; $X10 -multi $mainvm $standard_opt -Dx10.cluster.cfgfile=$cfg_file $MAINCLASS\""
else
    ssh $mainhost "cd $CLASSDIR; $X10 -multi $mainvm $standard_opt -Dx10.cluster.cfgfile=$cfg_file $MAINCLASS"
fi


exit 0


