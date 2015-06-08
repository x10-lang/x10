#!/bin/sh

cd /tmp
rm -Rf apgas-build 
mkdir apgas-build

git clone -b R4_4_2 https://git.eclipse.org/r/jdt/eclipse.jdt.core apgas-build
svn co --depth=empty https://svn.code.sf.net/p/x10/code/x10dt/trunk apgas-build
cd apgas-build
svn update apgas.compiler apgas.runtime apgas.ui apgas.tools apgas.site apgas.parent
svn co https://svn.code.sf.net/p/x10/code/trunk/apgas
svn co https://svn.code.sf.net/p/x10/code/trunk/apgas.examples
patch -p1 < apgas.compiler/R4_4_2.patch
cd apgas.parent
mvn -P build-individual-bundles package
cd ..
cp apgas/apgas.zip .
cp apgas.site/target/site_assembly.zip apgas-eclipse.zip
