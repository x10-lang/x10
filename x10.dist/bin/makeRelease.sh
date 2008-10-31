#! /bin/bash

# Dave Grove

set -e

if (( $# == 0 )) || [ x$1 = x-h -o x$1 = x--help ] ; then
  echo usage: $0 symbolic_revision
  exit 1
fi

date

revision=$1

workdir=$HOME/scratch/distribution
distdir=$workdir/x10-$revision
tarfile=x10-$revision.tar.gz
tarfile2=x10-$revision.tar.bz2
sed=/bin/sed

echo
echo cleaning $workdir
mkdir -p $workdir
cd $workdir
rm -rf *

cd $workdir

echo
echo getting distrib
svn export https://x10.svn.sourceforge.net/svnroot/x10/tags/$revision x10-$revision 

#####
# x10 build goes here
#####


echo
echo building $tarfile file
cd $distdir
chmod -Rf u+w *
ls -l
cd ..
tar zcf $tarfile $(basename $distdir)

echo building $tarfile2 file
tar jcf $tarfile2 $(basename $distdir)

echo
date
echo
echo done.

echo "The distribution ($tarfile) is now in the directory $workdir"
