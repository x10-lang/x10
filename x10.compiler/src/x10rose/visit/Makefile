
CXX = g++
NAME = X10Traversal
#SRC = $(NAME).C java_support.C jni_token.C jni_utils.C token.C
SRC = JavaSourceCodePosition.C  VisitorContext.C  X10Traversal.C  java_support.C  jni_JavaSourceCodePosition.C  jni_token.C  jni_utils.C  token.C jserver.C
X10Traversal = lib${NAME}.so

#JAVA_HOME=/etc/alternatives/java_sdk_1.7.0
ROSE_BOOST_PREINCLUDE_PATH=
top_srcdir=/opt/edg4x-rose
top_builddir=/opt/edg4x-rose/installTree

#     $(ROSE_BOOST_PREINCLUDE_PATH) \

CFLAGS = -fPIC 
ROSE_INCLUDES = \
	 -I. \
	 -I/opt/boost/boost_1_47_0/include \
	 -I/opt/boost/boost_1_47_0 \
     -I$(top_builddir)/src/roseSupport \
     -I$(top_builddir)/src/frontend/SageIII \
     -I$(top_srcdir)/src/frontend/X10_ROSE_Connection \
     -I$(top_builddir) \
     -I$(top_srcdir)/src \
     -I$(top_srcdir)/src/frontend/SageIII \
     -I$(top_srcdir)/src/frontend/SageIII/astFixup \
     -I$(top_srcdir)/src/frontend/SageIII/astPostProcessing \
     -I$(top_srcdir)/src/frontend/SageIII/astMerge \
     -I$(top_srcdir)/src/frontend/SageIII/astVisualization \
     -I$(top_srcdir)/src/frontend/SageIII/astFileIO \
     -I$(top_srcdir)/src/frontend/SageIII/sageInterface \
     -I$(top_srcdir)/src/frontend/SageIII/virtualCFG \
     -I$(top_srcdir)/src/frontend/SageIII/astTokenStream \
     -I$(top_srcdir)/src/frontend/SageIII/astHiddenTypeAndDeclarationLists \
     -I$(top_builddir)/src/frontend/SageIII/astFileIO \
     -I$(top_srcdir)/src/frontend/SageIII/astFromString \
     -I$(top_srcdir)/src/frontend/SageIII/includeDirectivesProcessing \
     -I$(top_srcdir)/src/frontend/SageIII/sage_support \
     -I$(top_srcdir)/src/frontend/OpenFortranParser_SAGE_Connection \
     -I"$(JAVA_HOME)/include" \
     -I$(top_srcdir)/src/frontend/PHPFrontend \
     -I$(top_srcdir)/src/frontend/CxxFrontend/ClangFrontend \
     -I$(top_srcdir)/src/frontend/PythonFrontend \
     -I$(top_srcdir)/src/frontend/BinaryDisassembly \
     -I$(top_srcdir)/src/frontend/BinaryFormats \
     -I$(top_srcdir)/src/frontend/BinaryLoader \
     -I$(top_srcdir)/src/frontend/Disassemblers \
     -I$(top_srcdir)/src/frontend/CxxFrontend/Clang \
     -I$(top_srcdir)/src/backend/unparser \
     -I$(top_srcdir)/src/backend/unparser/formatSupport \
     -I$(top_srcdir)/src/backend/unparser/languageIndependenceSupport \
     -I$(top_srcdir)/src/backend/unparser/CxxCodeGeneration \
     -I$(top_srcdir)/src/backend/unparser/JavaCodeGeneration \
     -I$(top_srcdir)/src/backend/unparser/FortranCodeGeneration \
     -I$(top_srcdir)/src/backend/unparser/PHPCodeGeneration \
     -I$(top_srcdir)/src/backend/unparser/PythonCodeGeneration \
     -I$(top_srcdir)/src/backend/asmUnparser \
     -I$(top_srcdir)/src/util \
     -I$(top_srcdir)/src/util/support \
     -I$(top_srcdir)/src/util/graphs  \
     -I$(top_srcdir)/src/util/stringSupport \
     -I$(top_srcdir)/src/util/commandlineProcessing \
     -I$(top_srcdir)/src/midend/abstractHandle \
     -I$(top_srcdir)/src/midend/abstractMemoryObject \
     -I$(top_srcdir)/src/midend/astDiagnostics \
     -I$(top_srcdir)/src/midend/programTransformation/astInlining \
     -I$(top_srcdir)/src/midend/programTransformation/astOutlining  \
     -I$(top_srcdir)/src/midend/astQuery \
     -I$(top_srcdir)/src/midend/astMatching \
     -I$(top_srcdir)/src/midend/astProcessing \
     -I$(top_srcdir)/src/midend/astRewriteMechanism \
     -I$(top_srcdir)/src/midend/astUtil/annotation \
     -I$(top_srcdir)/src/midend/astUtil/astInterface \
     -I$(top_srcdir)/src/midend/astUtil/astSupport \
     -I$(top_srcdir)/src/midend/astUtil/symbolicVal \
     -I$(top_srcdir)/src/midend/binaryAnalyses \
     -I$(top_srcdir)/src/midend/binaryAnalyses/dataflowanalyses \
     -I$(top_srcdir)/src/midend/binaryAnalyses/instructionSemantics \
     -I$(top_srcdir)/src/midend/programTransformation/loopProcessing/computation \
     -I$(top_srcdir)/src/midend/programTransformation/loopProcessing/depGraph \
     -I$(top_srcdir)/src/midend/programTransformation/loopProcessing/depInfo \
     -I$(top_srcdir)/src/midend/programTransformation/loopProcessing/driver \
     -I$(top_srcdir)/src/midend/programTransformation/loopProcessing/outsideInterface \
     -I$(top_srcdir)/src/midend/programTransformation/loopProcessing/prepostTransformation \
     -I$(top_srcdir)/src/midend/programTransformation/ompLowering \
     -I$(top_srcdir)/src/midend/programTransformation/extractFunctionArgumentsNormalization \
     -I$(top_srcdir)/src/midend/programTransformation/singleStatementToBlockNormalization \
     -I$(top_srcdir)/src/midend/programAnalysis \
     -I$(top_srcdir)/src/midend/programAnalysis/annotationLanguageParser \
     -I$(top_srcdir)/src/midend/programAnalysis/CFG \
     -I$(top_srcdir)/src/midend/programAnalysis/staticSingleAssignment \
     -I$(top_srcdir)/src/midend/programAnalysis/ssaUnfilteredCfg \
     -I$(top_srcdir)/src/midend/programAnalysis/systemDependenceGraph \
     -I$(top_srcdir)/src/midend/programAnalysis/systemDependenceGraph \
     -I$(top_srcdir)/src/midend/programAnalysis/CallGraphAnalysis \
     -I$(top_srcdir)/src/midend/programAnalysis/OAWrap \
     -I$(top_srcdir)/src/midend/programAnalysis/bitvectorDataflow \
     -I$(top_srcdir)/src/midend/programAnalysis/VirtualFunctionAnalysis \
     -I$(top_srcdir)/src/midend/programAnalysis/defUseAnalysis \
     -I$(top_srcdir)/src/midend/programAnalysis/distributedMemoryAnalysis \
     -I$(top_srcdir)/src/midend/programAnalysis/dominanceAnalysis \
     -I$(top_srcdir)/src/midend/programAnalysis/graphAnalysis \
     -I$(top_srcdir)/src/midend/programAnalysis/pointerAnal \
     -I$(top_srcdir)/src/midend/programAnalysis/staticInterproceduralSlicing \
     -I$(top_srcdir)/src/midend/programAnalysis/valuePropagation \
     -I$(top_srcdir)/src/midend/programAnalysis/variableRenaming \
      $(ROSE_SIDEEFFECTS_INCLUDE) \
      $(ROSE_DATABASE_INCLUDE) \
      $(ROSE_GUI_INCLUDE) \
      $(ROSE_QT_INCLUDE) \
      $(ROSE_SQLITE_DATABASE_INCLUDE) \
            $(ROSE_SIDEEFFECT_INCLUDE) \
      $(SQLITE_DATABASE_INCLUDE) \
     -I$(top_srcdir)/src/midend/programTransformation/partialRedundancyElimination \
     -I$(top_srcdir)/src/midend/programTransformation/finiteDifferencing \
     -I$(top_srcdir)/src/midend/programTransformation/functionCallNormalization \
     -I$(top_srcdir)/src/midend/programTransformation/constantFolding \
     -I$(top_srcdir)/src/midend/programTransformation/implicitCodeGeneration \
     -I$(top_srcdir)/src/roseSupport \
     -I$(top_srcdir)/src/3rdPartyLibraries/MSTL \
     -I$(top_srcdir)/src/3rdPartyLibraries/libharu-2.1.0/include \
     -I$(top_builddir)/src/3rdPartyLibraries/libharu-2.1.0/include \
     -I$(top_srcdir)/src/3rdPartyLibraries/qrose/Framework \
     -I$(top_srcdir)/src/3rdPartyLibraries/qrose/Widgets  \
     -I$(top_srcdir)/src/util/graphs \
     -I$(top_srcdir)/src/midend/astUtil/astInterface \
     -I$(top_srcdir)/libltdl \
     -I$(top_srcdir)/src/roseExtensions/failSafe \
     $(ROSE_PHP_INCLUDES) \
     $(ROSE_PYTHON_INCLUDES) \
     $(ROSE_YICES_INCLUDES) \
     $(ROSE_DWARF_INCLUDES) \
     $(ROSE_WINE_INCLUDES) \
     $(VALGRIND_CFLAGS) \
     $(SQLITE3_CFLAGS) \
     $(ROSE_BOOST_NORMAL_INCLUDE_PATH) -DBOOST_REGEX_MATCH_EXTRA \
     $(ROSE_PCH_INCLUDE) \
     $(INTEL_PIN_INCLUDE_PATHS) \
     $(ROSE_ETHER_INCLUDE)


all:
	$(CXX) $(ROSE_INCLUDES) $(CFLAGS) -shared $(SRC) -o ${X10Traversal} 
#	cp /opt/x10-2.4.0_mpi_O_NOCHECKS/trunk/x10.compiler/src/x10rose/visit/libX10Traversal.so /opt/edg4x-rose/installTree/src/frontend/ECJ_ROSE_Connection/.libs/
	cp libX10Traversal.so /opt/edg4x-rose/installTree/src/frontend/ECJ_ROSE_Connection/.libs/
	cp libX10Traversal.so /opt/edg4x-rose/installTree/src/frontend/X10_ROSE_Connection/.libs/

cp:
	cp libX10Traversal.so /opt/edg4x-rose/installTree/src/frontend/ECJ_ROSE_Connection/.libs/
	cp libX10Traversal.so /opt/edg4x-rose/installTree/src/frontend/X10_ROSE_Connection/.libs/

clean:
	rm -rf ${X10Traversal} ${NAME}.o
