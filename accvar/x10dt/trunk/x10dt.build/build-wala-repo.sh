#!/bin/bash
launcher=`find ../eclipse/plugins -name "org.eclipse.equinox.launcher_*.jar"`
java -XX:MaxPermSize=256M -Xmx512M -jar ${launcher} -application org.eclipse.ant.core.antRunner -buildfile wala-build.xml -DbuildLabel=Wala -Dbuild.loc=`pwd` $*
[ $? -eq 0 ] && rm -fr ../IWala
