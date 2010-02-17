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
	x10.runtime \
	x10.tests
do
  echo "Standardizing file headers in $srcdir"
  cd $srcdir
  find . -name .svn -prune -o -name "*.java" -exec $top/x10.dist/releng/standardizeHeader.pl {} \;
  find . -name .svn -prune -o -name "*.x10" -exec $top/x10.dist/releng/standardizeHeader.pl {} \;
  cd $top
done
