#!/bin/bash
# Author: Dave Grove
# Author: Igor Peshansky
#
# Simple script that can be run across a piece
# of the X10 repository and ensure that 
# svn properties are correctly set for a set of standard
# file types based on their extension.
#
# Arguments: directory names to search; defaults to "." if no arguments.
#

# Source code files should have the following properties set:
#   svn:eol-style : native
#   svn:mime-type : text/plain
# Source code files should not be executable (svn:executable should not be set).
#

# Default to current directory if no arguments
[ "$#" -eq "0" ] && set -- .

# The array of extensions to normalize properties for
declare -a EXTENSIONS=( .java .c .h .cc .x10 )

# Exit on interrupt
trap "exit 2" INT

# Compute 'find' arguments to match any of the EXTENSIONS
declare -a MATCH
for extension in "${EXTENSIONS[@]}"; do
    MATCH=("${MATCH[@]}" ${MATCH[*]:+-o} -name "*$extension")
done

for prop_cmd in "propset -q svn:eol-style native" \
                "propset -q svn:mime-type text/plain" \
                "propdel -q svn:executable"
do
    echo "Executing 'svn $prop_cmd' on files in $@"
    find "$@" -name .svn -prune -o -type f \( "${MATCH[@]}" \) -print | \
        xargs svn $prop_cmd
done
