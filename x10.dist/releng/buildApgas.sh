#!/bin/sh

cd /tmp
rm -Rf eclipse.jdt.core

git clone -b R4_4_2 https://git.eclipse.org/r/jdt/eclipse.jdt.core
svn co --depth=empty https://svn.code.sf.net/p/x10/code/x10dt/trunk eclipse.jdt.core
cd eclipse.jdt.core
svn update apgas.compiler apgas.runtime apgas.ui apgas.tools apgas.site apgas.parent
svn co https://svn.code.sf.net/p/x10/code/trunk/apgas
svn co https://svn.code.sf.net/p/x10/code/trunk/apgas.examples
patch -p1 < ~/jdt/eclipse.jdt.core/apgas.compiler/R4_4_2.patch
cd apgas
ant zip javadoc
cd ../apgas.parent
mvn -P build-individual-bundles package
cd /tmp
cp eclipse.jdt.core/apgas/apgas.zip .
cp eclipse.jdt.core/apgas.site/target/site_assembly.zip apgas-eclipse.zip
