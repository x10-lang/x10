#
#  This file is part of the X10 project (http://x10-lang.org).
#
#  This file is licensed to You under the Eclipse Public License (EPL);
#  You may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#      http://www.opensource.org/licenses/eclipse-1.0.php
#
#  (C) Copyright IBM Corporation 2006-2011.
#

###################################################
###################################################
## Name:  	X10 global parallel matrix library - GML
## Created by: 	Juemin Zhang
## Contact:   	zhangj@us.ibm.com
###################################################
###################################################
# This is makefile for building global matrix library (GML)
# for managed, native on socket, and native on MPI
#
###################################################
# Source files and paths
###################################################

src_path	=src
lib_path	=lib
build_path	=include

#----
# Output libs, properties, and tar files
target		= native_gml

#---- short keys
default	: help 
## 

## Managed backend must be built last
all		: native_gml native_mpi_gml managed_gml

###################################################
## short-cut keys
native		: native_gml
native_mpi	: native_mpi_gml
managed		: managed_gml

###################################################
## Source files
###################################################
bwrap_path	=$(src_path)/x10/matrix/blas

base_src=\
	x10/matrix/Debug.x10 \
	x10/matrix/DenseMultXTen.x10 \
	x10/matrix/MathTool.x10 \
	x10/matrix/Matrix.x10 \
	x10/matrix/RandTool.x10 \
	x10/matrix/DenseMatrix.x10 \
	x10/matrix/DistPConsole.x10	\
	x10/matrix/MatrixMultXTen.x10  \
	x10/matrix/PConsole.x10 \
	x10/matrix/VerifyTools.x10

comm_src=\
	x10/matrix/comm/ArrayRcast.x10 \
	x10/matrix/comm/ArrayBcast.x10 \
	x10/matrix/comm/ArrayGather.x10 \
	x10/matrix/comm/ArrayScatter.x10 \
	x10/matrix/comm/ArrayReduce.x10 \
	x10/matrix/comm/ArrayRemoteCopy.x10 \
	x10/matrix/comm/DenseRemoteSourceInfo.x10 \
	x10/matrix/comm/MatrixReduce.x10 \
	x10/matrix/comm/MatrixScatter.x10 \
	x10/matrix/comm/CommHandle.x10  \
	x10/matrix/comm/MatrixBcast.x10 \
	x10/matrix/comm/MatrixRemoteCopy.x10 \
	x10/matrix/comm/SparseRemoteDestInfo.x10 \
	x10/matrix/comm/DenseRemoteDestInfo.x10 \
	x10/matrix/comm/MatrixGather.x10 \
	x10/matrix/comm/MatrixRingCast.x10 \
	x10/matrix/comm/SparseRemoteSourceInfo.x10

bwrap_src=\
	x10/matrix/blas/DenseMultBLAS.x10 \
	x10/matrix/blas/DriverBLAS.x10

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
	x10/matrix/sparse/SparseMultDenseToDense.x10

block_src=\
	x10/matrix/block/BlockMatrix.x10  \
	x10/matrix/block/DenseBlock.x10 \
	x10/matrix/block/MatrixBlock.x10 \
	x10/matrix/block/SparseBlock.x10 \
	x10/matrix/block/DenseBlockMatrix.x10 \
	x10/matrix/block/Grid.x10 \
	x10/matrix/block/SparseBlockMatrix.x10

dist_src=\
	x10/matrix/dist/DistDenseMatrix.x10 \
	x10/matrix/dist/DistMultDistToDup.x10 \
	x10/matrix/dist/DupDenseMatrix.x10  \
	x10/matrix/dist/DupSparseMatrix.x10 \
	x10/matrix/dist/DistMultDupToDist.x10 \
	x10/matrix/dist/DupMatrix.x10 \
	x10/matrix/dist/DistMatrix.x10 \
	x10/matrix/dist/DistSparseMatrix.x10  \
	x10/matrix/dist/DupMultToDup.x10

summa_src=\
	x10/matrix/dist/summa/SummaDenseMultSparse.x10 \
	x10/matrix/dist/summa/SummaDense.x10 \
	x10/matrix/dist/summa/SummaSparseMultDense.x10 \
	x10/matrix/dist/summa/SummaSparse.x10
##----
## for mpi binding
comm_mpi_src=\
	x10/matrix/comm/RequestHandleMPI.x10  \
	x10/matrix/comm/WrapMPI.x10

summa_mpi_src=\
	x10/matrix/dist/summa/mpi/SummaMPI.x10

##-----

## basic lib src, non-parallel/dist version
seq_src		= $(base_src) $(bwrap_src) $(sparse_src) $(block_src) 
## lib src parallel/dist version 
##
mpi_wrap	= $(comm_mpi_src) $(summa_mpi_src)
lib_src     = $(seq_src) $(dist_src) $(comm_src) $(summa_src)

#--------------------------------------------------
VPATH		=$(bwrap_path):$(src_path)

###################################################
##### Dependent library setting Atlas BLAS lib
###################################################
include scripts/system_setting.mk

#X10_FLAG	= -d $(build_path) -report postcompile=2 -v -errors 5 -VERBOSE_CHECKS #-O -NO_CHECKS
X10_FLAG	= -VERBOSE_CHECKS -O -NO_CHECKS

MPI_FLAG    = -define MPI_COMMU -cxx-prearg -DMPI_COMMU
POST_CPP	= -post $(CPP) \# $(POST_PATH) \# $(POST_LIBS)
POST_MPI	= -post $(MCC) \# $(POST_PATH) \# $(POST_LIBS)

###################################################
# X10 file built rules
################################################### 

###----------------------------------------------------

###----------------------------------------------------
### build dist lib, multi-place parallel execution 
## build for managed backend
jlib : $(src) chk_dir chk_jblas
	@echo "------------------ build lib for $(target) on $(server)-------------------"
	cd $(src_path) && $(XJ) $(X10_FLAG) -buildx10lib ../. $(src) -d ../include -o ../lib/$(target).jar
	@echo "------------ making properties file for $(target) ------------"
	@echo "X10LIB_SRC_JAR=lib/$(target).jar" >> $(target).properties


## build for native backend
clib : $(clib_src) chk_dir
	@echo "------------------ build lib for $(target) on $(server) -------------------"
	cd $(src_path) && $(XC) $(X10_FLAG) -buildx10lib ../. $(src) -d ../include -o $(target) $(post_opts)
	@echo "-------- Packaging source files into jar for $(target)--------"
	cd $(src_path) && $(JAR) -cf ../lib/$(target).jar $(src)
	@echo "------------ making properties file for $(target) ------------"
	@echo "X10LIB_SRC_JAR=lib/$(target).jar" >> $(target).properties

##-----------------------------------------------------------------------
managed_gml		: $(lib_src)
		@($(MAKE) jlib target=$@ src="$(lib_src)") 

native_gml		: $(lib_src)
		@($(MAKE) clib target=$@ src="$(lib_src)" post_opts='$(POST_CPP)')

native_mpi_gml	: $(lib_src) $(mpi_wrap) 
		@($(MAKE) clib target=$@ src="$(lib_src) $(mpi_wrap)" post_opts='$(MPI_FLAG) $(POST_MPI)')

managed_seq_gml	: $(seq_src)
		@($(MAKE) jlib target=$@ src="$(seq_src)")

native_seq_gml	: $(seq_src)
		@($(MAKE) clib target=$@ src="$(seq_src)" post_opts='$(POST_CPP)')

###----------------------------------------------------
### build basic non-dist lib, single place (sequential matrix lib)

#
###------------------------------------------------
### BLAS lib for java backend

chk_jblas	: chk_blas jblas
			cp $(bwrap_path)/WrapBLAS.java $(build_path)/x10/matrix/blas

chk_blas	: 
			mkdir -p $(build_path)/x10/matrix/blas

wrap_blas.o	: wrap_blas.c
			g++ -c -v -fPIC $< -o $@

jni_blas.o 	: jni_blas.c
			g++ -I $(JNI_inc) -c -v -fPIC $< -o $@

libjblas.so	: wrap_blas.o jni_blas.o $(blas_lib)
			g++ -shared -fPIC $^ $(blas_lib) -lgfortran -o $@

jblas		: libjblas.so
			cp $< $(lib_path)/.

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
	@echo "1) Build full library for multi-place execution"
	@echo "make managed    -- make library tar file for managed backend: lib/managed_gml.jar and managed_gml.properties"
	@echo "make native     -- make library and jar files for native backed: lib/libnative_gml.so, lib/native_gml.jar and native_gml.properties"
	@echo "make native_mpi -- make library and jar files for native backend running on MPI transport: lib/libnative_mpi_gml.so, lib/native_mpi_gml.jar, and native_mpi_gml.properties"
	@echo "make all   -- make all above"
	@echo ""
	@echo "2) Build subset library for single place or sequential code"  
	@echo "make native_seq_gml  -- make subset of matrix library for sequential execution in native backed: lib/libnative_seq_gml.so, lib/native_seq_gml.jar, and native_seq_gml.properties "
	@echo "make managed_seq_gml -- make subset of matrix library for sequential execution in managed backed: lib/managed_seq_gml.jar, and managed_seq_gml.properties"
	@echo ""
	@echo "3) Clean all files: make clean"
	@echo ""
	@echo "------------------ How to build apps with x10 parallel matrix libraries ---------------"
	@echo "Define following xpmat settings:"
	@echo "GML top path: gml_path"
	@echo "GML lib path: gml_lib=\$$(gml_path)/lib"
	@echo "Application build path: build_path"
	@echo ""
	@echo "1) Build app for managed backend and add following settings in x10c command line"
	@echo "   \"-classpath \$$(gml_lib)/managed_gml.jar\"" 
	@echo "   \"-x10lib \$$(gml_path)/managed_gml.properties\""
	@echo ""
	@echo "2) Build app for native backend and add following settings in x10c++ command line"
	@echo "   \"-classpath \$$(gml_lib)/native_gml.jar\""
	@echo "   \"-x10lib \$$(gml_path)/native_gml.properties\""
	@echo ""
	@echo "3) Build app for native backend for MPI transport"
	@echo "   \"-classpath \$$(gml_lib)/native_mpi_gml.jar\""
	@echo "   \"-x10lib \$$(gml_path)/native_mpi_gml.properties\""
	@echo ""
	@echo "------------------- Run application --------------------"
	@echo "1) Managed backed"
	@echo "   Add classpath:\"-classpath \$$(build_path):\$$(gml_lib)/managed_gml.jar\""
	@echo "   Add libpath: \"-libpath \$$(build_path):\$$(gml_lib)\""

################################################
.PHONY	: clean clear help clib jlib chk_dir
###########################################

