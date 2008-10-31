#! /bin/bash

# Dave Grove

set -e
set -x

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

echo
echo cleaning $workdir
rm -rf $workdir
mkdir -p $workdir || exit 1
mkdir -p $workdir/x10-$revision

(
echo
echo getting polyglot
cd $workdir/x10-$revision
cvs -d:pserver:anonymous@gforge.cis.cornell.edu:/cvsroot/polyglot co polyglot3-dev
)

(
cd $workdir/x10-$revision

echo
echo getting distrib
for i in \
	x10.common.17 \
	x10.compiler.p3 \
	x10.constraints \
	x10.dist \
	x10.runtime.17 \
	x10.tests
do
    svn export https://x10.svn.sourceforge.net/svnroot/x10/tags/$revision/$i
done
)

(
echo
echo building polyglot
cd $workdir/x10-$revision/polyglot3-dev
ant eclipse-plugin-jar
)

(
echo
echo building x10
cd $workdir/x10-$revision/x10.dist
LOCAL_POLYGLOT_JAR=$workdir/x10-$revision/polyglot3-dev/lib/polyglot-epl.jar
export LOCAL_POLYGLOT_JAR
ant zips
)


#echo
#echo building $tarfile file
#cd $distdir
#chmod -Rf u+w *
#ls -l
#cd ..
#tar zcf $tarfile $(basename $distdir)
#
#echo building $tarfile2 file
#tar jcf $tarfile2 $(basename $distdir)
#
#echo
#date
#echo
#echo done.

echo "The distribution ($tarfile) is now in the directory $workdir"
