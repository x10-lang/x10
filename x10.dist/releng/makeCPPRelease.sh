#! /bin/bash

# Igor Peshansky

# Note: assumes everything is built
# FIXME: check out afresh

set -e
set -x

if (( $# == 0 )) || [ x$1 = x-h -o x$1 = x--help ] ; then
  echo usage: $0 symbolic_revision
  exit 1
fi

date

revision="$1"

tarfile="x10-$revision.tgz"

mydir="`dirname "$0"`"
top="`cd "$mydir"/.. && pwd`"
cdir="`pwd`"
[ "$cdir" = "/" ] && cdir="$cdir."
cd "$top"
tar -cvzf "$cdir/$tarfile" *-c++ bin/{runx10,setupX10,x10,x10c,x10c++} epl-v10.html include lib/{lib*,*.jar} samples/*.x10

