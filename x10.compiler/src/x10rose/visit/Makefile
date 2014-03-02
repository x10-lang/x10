X10FLAG = -O -NO_CHECKS -x10rt pami -report postcompile=1

CXX = g++
NAME = X10Traversal
#SRC = $(NAME).C java_support.C jni_token.C jni_utils.C token.C
SRC = JavaSourceCodePosition.C  VisitorContext.C  X10Traversal.C  java_support.C  jni_JavaSourceCodePosition.C  jni_token.C  jni_utils.C  token.C
X10Traversal = lib${NAME}.so

CFLAGS = -fPIC 
ROSE_INCLUDES = -I/opt/boost/boost_1_47_0/include \
-I/opt/rose.latest/src/util/ \
-I/opt/rose.latest/src/midend/astDiagnostics/ \
-I/opt/rose.latest/src/frontend/BinaryDisassembly/ \
-I/opt/rose.latest/installTree/ \
-I/opt/rose.latest/src/frontend/SageIII/ \
-I/opt/rose.latest/src/frontend/SageIII/ \
-I/opt/rose.latest/src/frontend/ECJ_ROSE_Connection/ \
-I/opt/rose.latest/src/frontend/OpenFortranParser_SAGE_Connection/ \
-I/opt/rose.latest/src/ \
-I/opt/rose.latest/src/frontend/SageIII/virtualCFG/ \
-I/opt/rose.latest/src/midend/astProcessing/ \
-I/opt/rose.latest/src/3rdPartyLibraries/MSTL/ \
-I/opt/rose.latest/src/util/stringSupport/ \
-I/opt/rose.latest/src/util/commandlineProcessing/ \
-I/opt/rose.latest/installTree/src/frontend/SageIII/ \
-I/opt/rose.latest/src/roseSupport/ \
-I/opt/rose.latest/src/frontend/Disassemblers/ \
-I/opt/rose.latest/src/frontend/BinaryFormats/ \
-I/opt/rose.latest/src/frontend/SageIII/sageInterface/ \
-I/opt/rose.latest/src/frontend/SageIII/astPostProcessing/ \
-I/opt/rose.latest/src/frontend/SageIII/astFixup/ \
-I/opt/rose.latest/src/midend/abstractHandle/ \
-I/opt/rose.latest/src/midend/abstractMemoryObject/ \
-I/opt/rose.latest/src/midend/abstractMemoryObject/ \
-I/opt/rose.latest/src/midend/programTransformation/ompLowering/ \
-I/opt/rose.latest/src/midend/astQuery/ \
-I/opt/rose.latest/src/frontend/SageIII/astMerge/ \
-I/opt/rose.latest/src/frontend/SageIII/astFromString/ \
-I/opt/rose.latest/src/midend/astRewriteMechanism/ \
-I/opt/rose.latest/src/backend/unparser/ \
-I/opt/rose.latest/src/midend/programAnalysis/defUseAnalysis/ \
-I/opt/rose.latest/src/midend/programAnalysis/CallGraphAnalysis/ 

all:
	$(CXX) $(ROSE_INCLUDES) $(CFLAGS) -shared $(SRC) -o ${X10Traversal} 
	sudo cp /opt/x10-2.4.0_mpi_O_NOCHECKS/trunk/x10.compiler/src/x10rose/visit/libX10Traversal.so /opt/rose.latest/installTree/src/frontend/ECJ_ROSE_Connection/.libs/

cp:
	sudo cp libX10Traversal.so /opt/rose.latest/installTree/src/frontend/ECJ_ROSE_Connection/.libs/

clean:
	rm -rf ${X10Traversal} ${NAME}.o
