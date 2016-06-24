#!/bin/sh

cd /tmp
rm -Rf apgas-build 
mkdir apgas-build
cd apgas-build

git clone -b R4_6 https://git.eclipse.org/r/jdt/eclipse.jdt.core
git clone https://github.com/x10-lang/x10.git
git clone https://github.com/x10-lang/x10dt.git
cd eclipse.jdt.core
patch -p1 < ../x10dt/apgas.compiler/R4_6.patch
cd ../x10dt/apgas.parent
mvn -P build-individual-bundles package
cd ../..
cp x10/apgas/apgas.zip .
cp x10dt/apgas.site/target/site_assembly.zip apgas-eclipse.zip
