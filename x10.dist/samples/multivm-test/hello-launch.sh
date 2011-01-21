# export x10LAUNCHER_NPROCS=4
#export JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-1.6.0.0/jre
export X10_HOME=/home/suzumura/workspace/x10-multivm
export HOST_LIST="knoxville"
export NUM_PLACES=2
export SCRIPT=hello-socket.sh

echo "${X10_HOME}/x10.runtime/x10rt/sockets/X10Launcher -np ${NUM_PLACES} -hostlist ${HOST_LIST} sh ${SCRIPT}"
${X10_HOME}/x10.runtime/x10rt/sockets/X10Launcher -np ${NUM_PLACES} -hostlist ${HOST_LIST} sh ${SCRIPT}
