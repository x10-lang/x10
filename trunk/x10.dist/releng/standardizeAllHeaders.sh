#!/bin/bash

# Dave Grove

mydir="`dirname "$0"`"
top="`cd "$mydir"/../.. && pwd`"
cdir="`pwd`"
[ "$cdir" = "/" ] && cdir="$cdir."
cd "$top"

for proj in \
	x10.common \
	x10.compiler \
	x10.constraints \
	x10.dist \
	x10.runtime \
	x10.tests
do
  echo "Standardizing file headers in $proj"
  cd $proj
  find . -name .svn -prune -o -name "*.java" -exec $top/x10.dist/releng/standardizeHeader.sh {} \;
  find . -name .svn -prune -o -name "*.x10" -exec $top/x10.dist/releng/standardizeHeader.sh {} \;
  cd $top
done
