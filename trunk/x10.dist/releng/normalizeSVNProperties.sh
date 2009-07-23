#!/bin/bash
# Author: Dave Grove
#
# Simple script that can be run across a piece
# of the X10 repository and ensure that 
# svn properties are correctly set for a set of standard
# file types based on their extension.
#

# Source code files should have the following properties set:
#   svn:eol-style : native
#   svn:mime-type : text/plain
# Source code files should not be executable (svn:executable should not be set).
#
for extension in .java .c .h .cc .x10; do
    find . -name .svn -prune -o -type f -name "*$extension" -exec  svn propset svn:eol-style native {} \;
    find . -name .svn -prune -o -type f -name "*$extension" -exec svn propset svn:mime-type text/plain {} \;
    find . -name .svn -prune -o -type f -name "*$extension" -exec svn propdel svn:executable {} \;
done




