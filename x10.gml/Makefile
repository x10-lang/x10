# Makefile for X10 global matrix library - GML

include scripts/system_setting.mk

src_path	=src
lib_path	=lib
build_path	=include

# Output libs, properties, and tar files
target		= native_gml

default	: help 

## Managed backend must be built last
all		: native_gml native_mpi_gml managed_gml

## short-cut keys
native		: native_gml
native_mpi	: native_mpi_gml
managed		: managed_gml

## Source files
bwrap_path =$(src_path)/x10/matrix/blas
lwrap_path =$(src_path)/x10/matrix/lapack

base_src=\
	x10/matrix/DenseMultXTen.x10 \
	x10/matrix/SymDense.x10 \
	x10/matrix/TriDense.x10 \
	x10/matrix/Vector.x10 \
	x10/matrix/Matrix.x10 \
	x10/matrix/DenseMatrix.x10 \
	x10/matrix/VectorMult.x10 \
	x10/matrix/MatrixMultXTen.x10  \
	x10/matrix/util/Debug.x10 \
	x10/matrix/util/DistPConsole.x10	\
	x10/matrix/util/MathTool.x10 \
	x10/matrix/util/PConsole.x10 \
	x10/matrix/util/RandTool.x10 \
	x10/matrix/util/VerifyTool.x10 \
	x10/matrix/util/PlaceGroupBuilder.x10 \
	x10/util/resilient/DistObjectSnapshot.x10 \
	x10/util/resilient/ResilientExecutor.x10 \
	x10/util/resilient/ResilientIterativeApp.x10 \
	x10/util/resilient/ResilientStoreForApp.x10 \
	x10/util/resilient/Snapshottable.x10

comm_src=\
	x10/matrix/comm/ArrayBcast.x10 \
	x10/matrix/comm/ArrayGather.x10 \
	x10/matrix/comm/ArrayScatter.x10 \
	x10/matrix/comm/ArrayReduce.x10 \
	x10/matrix/comm/ArrayRemoteCopy.x10 \
	x10/matrix/comm/DenseRemoteSourceInfo.x10 \
	x10/matrix/comm/MatrixReduce.x10 \
	x10/matrix/comm/MatrixScatter.x10 \
	x10/matrix/comm/MatrixBcast.x10 \
	x10/matrix/comm/MatrixRemoteCopy.x10 \
	x10/matrix/comm/SparseRemoteDestInfo.x10 \
	x10/matrix/comm/DenseRemoteDestInfo.x10 \
	x10/matrix/comm/MatrixGather.x10 \
	x10/matrix/comm/MatrixRingCast.x10 \
    x10/matrix/comm/RemotePair.x10 \
	x10/matrix/comm/SparseRemoteSourceInfo.x10\
	x10/matrix/comm/BlockRemoteCopy.x10 \
	x10/matrix/comm/BlockBcast.x10 \
	x10/matrix/comm/BlockGather.x10 \
	x10/matrix/comm/BlockReduce.x10 \
	x10/matrix/comm/BlockScatter.x10 \
	x10/matrix/comm/BlockSetBcast.x10 \
	x10/matrix/comm/BlockSetReduce.x10 \
	x10/matrix/comm/BlockSetRemoteCopy.x10 \
    x10/matrix/comm/CompArrayPLH.x10 \
    x10/matrix/comm/DataArrayPLH.x10 \
    x10/matrix/comm/DistCompArray.x10 \
    x10/matrix/comm/DistDataArray.x10

bwrap_src=\
	x10/matrix/blas/DenseMatrixBLAS.x10 \
	x10/matrix/blas/BLAS.x10 \
	x10/matrix/blas/DriverBLAS.x10

lapack_src=\
	x10/matrix/lapack/DenseMatrixLAPACK.x10 \
	x10/matrix/lapack/LAPACK.x10 \
	x10/matrix/lapack/DriverLAPACK.x10

sparse_src=\
	x10/matrix/sparse/Compress1D.x10 \
	x10/matrix/sparse/Compress2D.x10 \
	x10/matrix/sparse/DenseMultSparseToDense.x10 \
	x10/matrix/sparse/SparseCSR.x10 \
	x10/matrix/sparse/SparseMultSparseToDense.x10 \
	x10/matrix/sparse/SparseAddToDense.x10 \
	x10/matrix/sparse/SparseDivToDense.x10 \
	x10/matrix/sparse/SparseSubToDense.x10 \
	x10/matrix/sparse/CompressArray.x10  \
	x10/matrix/sparse/SparseCSC.x10  \
	x10/matrix/sparse/SparseMultDenseToDense.x10 \
	x10/matrix/sparse/SymSparseCSC.x10

block_src=\
	x10/matrix/block/BlockMatrix.x10  \
	x10/matrix/block/DenseBlock.x10 \
	x10/matrix/block/MatrixBlock.x10 \
	x10/matrix/block/SparseBlock.x10 \
	x10/matrix/block/DenseBlockMatrix.x10 \
	x10/matrix/block/Grid.x10 \
	x10/matrix/block/SparseBlockMatrix.x10 \
	x10/matrix/block/BlockBlockMult.x10 \
	x10/matrix/block/SymGrid.x10

dist_src=\
	x10/matrix/dist/DistDenseMatrix.x10 \
	x10/matrix/dist/DistMultDistToDup.x10 \
	x10/matrix/dist/DupDenseMatrix.x10  \
	x10/matrix/dist/DupSparseMatrix.x10 \
	x10/matrix/dist/DistMultDupToDist.x10 \
	x10/matrix/dist/DistMatrix.x10 \
	x10/matrix/dist/DistSparseMatrix.x10  \
	x10/matrix/dist/DupMultToDup.x10 \
	x10/matrix/distblock/DistMap.x10 \
	x10/matrix/distblock/DistGrid.x10 \
	x10/matrix/distblock/BlockSet.x10 \
	x10/matrix/distblock/DistBlockMatrix.x10 \
	x10/matrix/distblock/DupBlockMatrix.x10  \
	x10/matrix/distblock/DistDistMult.x10 \
	x10/matrix/distblock/CastPlaceMap.x10 \
	x10/matrix/distblock/DistDupMult.x10 \
	x10/matrix/distblock/DistVector.x10 \
	x10/matrix/distblock/DupVector.x10 \
	x10/matrix/distblock/BlockVectorMult.x10 \
	x10/matrix/distblock/DistDupVectorMult.x10 \
	x10/matrix/distblock/summa/SummaMult.x10 \
	x10/matrix/distblock/summa/AllGridCast.x10 \
	x10/matrix/distblock/summa/BlockGridCast.x10 \
	x10/matrix/distblock/summa/BinaryTreeCast.x10 \
	x10/matrix/distblock/summa/RingCast.x10 \
	x10/matrix/distblock/summa/AllGridReduce.x10 \
	x10/matrix/distblock/summa/BlockGridReduce.x10 \
	x10/matrix/distblock/summa/BinaryTreeReduce.x10 \
	x10/matrix/distblock/summa/RingReduce.x10 \
	x10/matrix/distblock/summa/SummaMultTrans.x10

builder_src=\
	x10/matrix/builder/MatrixBuilder.x10 \
	x10/matrix/builder/DenseBuilder.x10 \
	x10/matrix/builder/SymDenseBuilder.x10 \
	x10/matrix/builder/TriDenseBuilder.x10 \
	x10/matrix/builder/SparseCSCBuilder.x10 \
	x10/matrix/builder/SymSparseBuilder.x10 \
	x10/matrix/builder/TriSparseBuilder.x10 \
	x10/matrix/builder/distblock/DistMatrixBuilder.x10 \
	x10/matrix/builder/distblock/DistSymMatrixBuilder.x10

summa_src=\
	x10/matrix/dist/summa/SummaDenseMultSparse.x10 \
	x10/matrix/dist/summa/SummaDense.x10 \
	x10/matrix/dist/summa/SummaSparseMultDense.x10 \
	x10/matrix/dist/summa/SummaSparse.x10
##----
## for mpi binding
comm_mpi_src=\
	x10/matrix/comm/mpi/RequestHandleMPI.x10  \
	x10/matrix/comm/mpi/WrapMPI.x10

summa_mpi_src=\
	x10/matrix/dist/summa/mpi/SummaMPI.x10

##-----

## basic lib src, non-parallel/dist version
seq_src		= $(base_src) $(bwrap_src) $(lapack_src) $(sparse_src) $(block_src) 
## lib src parallel/dist version 
##
mpi_wrap	= $(comm_mpi_src) $(summa_mpi_src)
lib_src     = $(seq_src) $(dist_src) $(builder_src) $(comm_src) $(summa_src)

#--------------------------------------------------
VPATH		=$(bwrap_path):$(lwrap_path):$(src_path)


X10FLAGS	+= -VERBOSE_CHECKS -O -NO_CHECKS -g
ifdef COVERAGE
    X10CXXFLAGS += --coverage
endif

#-define ENABLE_RING_COMM

MPI_X10CXXFLAGS = -x10rt mpi -define MPI_COMMU -cxx-prearg -DMPI_COMMU 

###################################################
# X10 file built rules
################################################### 

### build dist lib, multi-place parallel execution 
## build for managed backend
jlib : $(src) chk_dir $(add_jblas) $(add_jlapack)
	@echo "------------------ build lib for $(target) on $(HOSTNAME)-------------------"
	cd $(src_path) && $(X10C) $(X10FLAGS) -buildx10lib ../. $(src) -d ../include -o ../lib/$(target).jar
	@echo "------------ making properties file for $(target) ------------"
	@echo "X10LIB_SRC_JAR=lib/$(target).jar" >> $(target).properties


## build for native backend
clib : $(clib_src) chk_dir
	@echo "------------------ build lib for $(target) on $(HOSTNAME) -------------------"
	cd $(src_path) && $(X10CXX) $(X10FLAGS) -report postcompile=1 $(X10CXX_PREARGS) $(X10CXX_POSTARGS) $(extra_opts) -buildx10lib ../. $(src) -d ../include -o $(target)
	@echo "-------- Packaging source files into jar for $(target)--------"
	cd $(src_path) && $(JAR) -cf ../lib/$(target).jar $(src)
	@echo "------------ making properties file for $(target) ------------"
	@echo "X10LIB_SRC_JAR=lib/$(target).jar" >> $(target).properties

##-----------------------------------------------------------------------
managed_gml		: $(lib_src)
		@($(MAKE) jlib target=$@ src="$(lib_src)") 
## native on socket
native_gml		: $(lib_src)
		@($(MAKE) clib target=$@ src="$(lib_src)")

native_mpi_gml	: $(lib_src) $(mpi_wrap) 
		@($(MAKE) clib target=$@ src="$(lib_src) $(mpi_wrap)" extra_opts='$(MPI_X10CXXFLAGS)')

### build basic non-dist lib, single place (sequential matrix lib)
managed_seq_gml	: $(seq_src)
		@($(MAKE) jlib target=$@ src="$(seq_src)")

native_seq_gml	: $(seq_src)
		@($(MAKE) clib target=$@ src="$(seq_src)")


###------------------------------------------------
### BLAS lib for java backend

chk_jblas	: chk_blas jblas 
			cp $(bwrap_path)/WrapBLAS.java $(build_path)/x10/matrix/blas

chk_blas	: 
			mkdir -p $(build_path)/x10/matrix/blas 

wrap_blas.o	: wrap_blas.cc
			$(CXX) -c -fPIC -DENABLE_BLAS $< -o $@

jni_blas.o 	: jni_blas.cc
			$(CXX) $(JNI_INCLUDES) -c -fPIC $< -o $@

libjblas.so	: wrap_blas.o jni_blas.o
			$(CXX) $(JNI_INCLUDES) -shared -fPIC $^ $(X10CXX_POSTARGS:-cxx-postarg=) -o $@ 

jblas		: libjblas.so
			cp $< $(lib_path)/.

###------------------------------------------------
chk_jlapack	: chk_lapack jlapack
			cp $(lwrap_path)/WrapLAPACK.java $(build_path)/x10/matrix/lapack

chk_lapack	:
			mkdir -p $(build_path)/x10/matrix/lapack

wrap_lapack.o: wrap_lapack.cc
			g++ -c -fPIC -DENABLE_LAPACK $< -o $@

jni_lapack.o: jni_lapack.cc
			g++ $(JNI_INCLUDES) -c -fPIC $< -o $@

libjlapack.so: wrap_lapack.o jni_lapack.o
			g++ -shared -fPIC $^ -o $@ $(X10CXX_POSTARGS:-cxx-postarg=)

jlapack		: libjlapack.so
			cp $< $(lib_path)/.

###------------------------------------------------
X10_VERSION ?= HEAD
srctar		: $(lib_src)
			tar -cjvf x10-gml-$(X10_VERSION)-src.tar.bz2 --exclude=*.svn* --exclude=*~ src tests examples Makefile scripts doc/Makefile 

###------------------------------------------------
chk_dir:
		mkdir -p lib
		mkdir -p include/x10/matrix/comm/mpi

###------------------------------------------------
clean	::
		rm -rf build *.o *.so lib include *.properties
clear	::
		rm -rf build *.o *.so
###------------------------------------------------

###------------------------------------------------
help	::
	@echo "--------------- Make global matrix libraries ---------------"
	@echo ""
	@echo "Configure system settings"
	@echo "Dependent library: BLAS"
	@echo "Configuring script/system_setting.mk"
	@echo ""
	@echo "1) Build full library for multi-place execution"
	@echo "make managed    -- make library tar file for managed backend: lib/managed_gml.jar and managed_gml.properties"
	@echo "make native     -- make library and jar files for native backend: lib/libnative_gml.so, lib/native_gml.jar and native_gml.properties"
	@echo "make native_mpi -- make library and jar files for native backend running on MPI transport: lib/libnative_mpi_gml.so, lib/native_mpi_gml.jar, and native_mpi_gml.properties"
	@echo "make all   -- make all above"
	@echo ""
	@echo "2) Build subset library for single place or sequential code"  
	@echo "make native_seq_gml  -- make subset of matrix library for sequential execution in native backend: lib/libnative_seq_gml.so, lib/native_seq_gml.jar, and native_seq_gml.properties "
	@echo "make managed_seq_gml -- make subset of matrix library for sequential execution in managed backend: lib/managed_seq_gml.jar, and managed_seq_gml.properties"
	@echo ""
	@echo "3) Clean all files: make clean"
	@echo ""
	@echo "------------------ How to build apps with x10 parallel matrix libraries ---------------"
	@echo "Define following xpmat settings:"
	@echo "GML top path: gml_path"
	@echo "GML lib path: gml_lib=\$$(gml_path)/lib"
	@echo "Application build path: build_path"
	@echo ""
	@echo "1) Build app for managed backend, add following settings in x10c command line"
	@echo "   \"-x10lib \$$(gml_path)/managed_gml.properties\""
	@echo ""
	@echo "2) Build app for native backend, add following settings in x10c++ command line"
	@echo "   \"-classpath \$$(gml_lib)/native_gml.jar\""
	@echo "   \"-x10lib \$$(gml_path)/native_gml.properties\""
	@echo ""
	@echo "3) Build app for native backend for MPI transport, add following settings in x10c++ command line"
	@echo "   \"-classpath \$$(gml_lib)/native_mpi_gml.jar\""
	@echo "   \"-x10lib \$$(gml_path)/native_mpi_gml.properties\""
	@echo ""
	@echo "------------------- Run application --------------------"
	@echo "1) Managed backend"
	@echo "   Add classpath:\"-classpath \$$(build_path):\$$(gml_lib)/managed_gml.jar\""
	@echo "   Add libpath: \"-libpath \$$(build_path):\$$(gml_lib)\""

coverage:	
	lcov --directory include --ignore-errors gcov --capture -o lcov.info; genhtml lcov.info --output-directory coverage

################################################
.PHONY	: help clean clear help clib jlib chk_dir
###########################################

