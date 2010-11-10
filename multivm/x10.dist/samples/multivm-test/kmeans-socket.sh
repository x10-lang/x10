# export x10LAUNCHER_NPROCS=4
export JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-1.6.0.0/jre
export X10_HOME=/home/suzumura/workspace/x10-multivm
export X10_DIST=${X10_HOME}/x10.dist
#export JNI_DEBUG="-verbose:jni"
export JNI_DEBUG=""
export APP='KMeansDist$Main'

echo "${JAVA_HOME}/bin/java  ${JNI_DEBUG} -Djava.library.path=\".:${X10_DIST}/lib/x10.jar:${X10_DIST}/lib\" -Dx10.LOAD=\"x10rt_sockets\" -ea -classpath \".:${X10_DIST}/lib/x10.jar\" '$APP'"

${JAVA_HOME}/bin/java  ${JNI_DEBUG} -Djava.library.path=".:${X10_DIST}/lib/x10.jar:${X10_DIST}/lib" -Dx10.LOAD="x10rt_sockets" -ea -classpath ".:${X10_DIST}/lib/x10.jar" "${APP}"
