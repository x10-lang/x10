export NUM_PLACES=2
#export x10LAUNCHER_NPROCS=${NUM_PLACES}
#export JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-1.6.0.0/jre
export X10_HOME=/home/suzumura/workspace/x10-multivm
#export HOST_LIST="knoxville"
export HOST_LIST="localhost"
export SCRIPT=kmeans-socket.sh

echo "${X10_HOME}/x10.runtime/x10rt/sockets/X10Launcher -np ${NUM_PLACES} -hostlist ${HOST_LIST} sh ${SCRIPT}"
${X10_HOME}/x10.runtime/x10rt/sockets/X10Launcher -np ${NUM_PLACES} -hostlist ${HOST_LIST} sh ${SCRIPT}
