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
## Name:  	X10 application test
## Created by: 	Juemin Zhang
## Contact:   	zhangj@us.ibm.com
###################################################
###################################################


###################################################
# Source and target file
###################################################

# build path
doc_path	= html
src_path 	= ../src

###################################################
## Compiler settings
###################################################
XDOC = x10doc

###################################################
## doc source files
###################################################

## remove dependent files
#	x10/matrix/Matrix.x10 
#   x10/matrix/sparse/SparseCSR.x10 
#   x10/matrix/sparse/SparseCSC.x10  
#	x10/matrix/block/BlockMatrix.x10  
#	x10/matrix/block/DenseBlock.x10 
#	x10/matrix/block/MatrixBlock.x10 
#	x10/matrix/block/SparseBlock.x10 
#	x10/matrix/block/DenseBlockMatrix.x10 
#	x10/matrix/block/SparseBlockMatrix.x10 
#	x10/matrix/dist/DistDenseMatrix.x10 
#	x10/matrix/dist/DupMatrix.x10 
#	x10/matrix/dist/DistMatrix.x10 
#	x10/matrix/dist/DistSparseMatrix.x10 
#	x10/matrix/dist/DupSparseMatrix.x10 
#	x10/matrix/dist/DupDenseMatrix.x10  
#	x10/matrix/comm/ArrayRemoteCopy.x10 
#	x10/matrix/comm/MatrixRemoteCopy.x10 

doc_src= \
	x10/matrix/Debug.x10 \
	x10/matrix/DenseMultXTen.x10 \
	x10/matrix/MathTool.x10 \
	x10/matrix/RandTool.x10 \
	x10/matrix/DistPConsole.x10	\
	x10/matrix/MatrixMultXTen.x10  \
	x10/matrix/PConsole.x10 \
	x10/matrix/VerifyTools.x10 \
	x10/matrix/comm/ArrayRcast.x10 \
	x10/matrix/comm/ArrayBcast.x10 \
	x10/matrix/comm/ArrayGather.x10 \
	x10/matrix/comm/ArrayScatter.x10 \
	x10/matrix/comm/ArrayReduce.x10 \
	x10/matrix/comm/MatrixReduce.x10 \
	x10/matrix/comm/MatrixScatter.x10 \
	x10/matrix/comm/CommHandle.x10  \
	x10/matrix/comm/MatrixBcast.x10 \
	x10/matrix/comm/MatrixGather.x10 \
	x10/matrix/comm/MatrixScatter.x10 \
	x10/matrix/comm/MatrixRingCast.x10 \
	x10/matrix/comm/SparseRemoteDestInfo.x10 \
	x10/matrix/comm/SparseRemoteSourceInfo.x10\
	x10/matrix/blas/DenseMatrixBLAS.x10 \
	x10/matrix/blas/DriverBLAS.x10 \
	x10/matrix/sparse/Compress1D.x10 \
    x10/matrix/sparse/Compress2D.x10 \
	x10/matrix/sparse/DenseMultSparseToDense.x10 \
	x10/matrix/sparse/SparseMultSparseToDense.x10 \
	x10/matrix/sparse/SparseAddToDense.x10 \
	x10/matrix/sparse/SparseDivToDense.x10 \
	x10/matrix/sparse/SparseSubToDense.x10 \
	x10/matrix/sparse/CompressArray.x10  \
	x10/matrix/sparse/SparseMultDenseToDense.x10 \
	x10/matrix/block/Grid.x10 \
	x10/matrix/dist/DistMultDistToDup.x10 \
	x10/matrix/dist/DistMultDupToDist.x10 \
	x10/matrix/dist/DupMultToDup.x10 \
	x10/matrix/dist/summa/SummaDenseMultSparse.x10 \
	x10/matrix/dist/summa/SummaDense.x10 \
	x10/matrix/dist/summa/SummaSparseMultDense.x10 \
	x10/matrix/dist/summa/SummaSparse.x10 \
	x10/matrix/comm/mpi/RequestHandleMPI.x10  \
	x10/matrix/comm/mpi/WrapMPI.x10 \
	x10/matrix/dist/summa/mpi/SummaMPI.x10


#srcfiles = $(subst x10/matrix, $(src_path)/x10/matrix, $(doc_src))

docfiles = $(foreach src, $(doc_src), $(src_path)/$(src) )

####################################################
## Make rules
####################################################

doc		: $(docfiles)
		$(XDOC) -d $(doc_path) -sourcepath $(src_path) $(docfiles)

html	: $(x10src)
		$(XDOC) -d $(doc_path) -sourcepath $(src_path) $(x10src)

eachdoc	: $(docfiles)
		$(foreach s, $(docfiles), $(MAKE) x10src=$(s) html;) 

#clean	:
#		rm -rf html
###################################################
# help info
###################################################
help ::
	@echo "Generatint document for classes and methods" 
###################################################
.PHONY: doc chk_doc_path alldoc html
