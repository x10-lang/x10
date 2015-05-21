#!/bin/bash

# Dave Grove

mydir="`dirname "$0"`"
top="`cd "$mydir"/../.. && pwd`"
cdir="`pwd`"
[ "$cdir" = "/" ] && cdir="$cdir."
cd "$top"

# NOTE: x10.common/src to avoid including x10.common/contrib

for srcdir in \
	x10.common/src \
	x10.compiler \
	x10.constraints \
	x10.dist \
	x10.network \
	x10.runtime \
	x10.tests
do
  echo "Standardizing file headers in $srcdir"
  cd $srcdir
  JAVA_FILES=`find . -name .svn -prune -o -name "*.java" -print`
  X10_FILES=`find . -name .svn -prune -o -name "*.x10" -print`
  for file in $JAVA_FILES $X10_FILES
  do
      hasHeader=yes
      `grep -q "This file is part of the X10 project (http://x10-lang.org)." $file` || hasHeader=no
      `grep -q "This file is licensed to You under the Eclipse Public License (EPL)" $file` || hasHeader=no
      `grep -q "(C) Copyright" $file` || hasHeader=no
      if [[ "X$hasHeader" == "Xno" ]]; then
	  echo "Invoking standardizeHeader.pl on $file"
	  $top/x10.dist/releng/standardizeHeader.pl $file
      fi
  done
  cd $top
done
