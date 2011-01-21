# export x10LAUNCHER_NPROCS=4
export JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-1.6.0.0/jre
export X10_HOME=/home/suzumura/workspace/x10-multivm
export X10_DIST=${X10_HOME}/x10.dist

echo "${JAVA_HOME}/bin/java  -Djava.library.path=\".:${X10_DIST}/lib/x10.jar:${X10_DIST}/lib\" -Dx10.LOAD=\"x10rt_sockets\" -ea -classpath \".:${X10_DIST}/lib/x10.jar\" 'HelloWholeWorld$Main'"

${JAVA_HOME}/bin/java  -Djava.library.path=".:${X10_DIST}/lib/x10.jar:${X10_DIST}/lib" -Dx10.LOAD="x10rt_sockets" -ea -classpath ".:${X10_DIST}/lib/x10.jar" 'HelloWholeWorld$Main'
