/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.matrix.dist;

import x10.util.Timer;

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.blas.DenseMatrixBLAS;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.sparse.DenseMultSparseToDense;
import x10.matrix.sparse.SparseMultDenseToDense;

/**
 * This class provides distributed matrix multiply with distributed sparse matrix,
 * and store result in duplicated matrices.
 *
 * <p>
 * When multiplication requires the first input matrix transposed, 
 * the distributed dense matrix and sparse matrices are partitioned in nx1 blocks, 
 * where n is number places.
 */
public class DistMultDistToDup {
    /**
     * Perform C += A<sup>T</sup> &#42 B or C = A<sup>T</sup> &#42 B. 
     * Two input matrices must have single row/column block partitioning. 
     * All copies of the duplicated matrix are updated.
     */
    public static def compTransMult(
            A:DistDenseMatrix, 
            B:DistSparseMatrix{self.M==A.M}, 
            C:DupDenseMatrix(A.N,B.N),
            plus:Boolean) : DupDenseMatrix(C){
                
        //Global.assure(A.flagTranspose == true);
        Debug.assure(C.M==A.N&&A.M==B.M&&B.N==C.N, "Matrix dimension mismatch");
        Debug.assure(A.grid.numColBlocks==1L && B.grid.numColBlocks==1L, 
                "Not single column block partitioning for input matrices");
        Debug.assure(A.dist.equals(B.dist), "Matrix partitioning mismatch");

        val rootpid = here.id();
        //C.allocTmpMs();
        /* TIMING */ var stt:Long =  Timer.milliTime();
        finish ateach([p] in A.dist) {
            val dmA = A.getMatrix(p); //get dense matrix at pid
            val smB = B.getMatrix(p) as SparseCSC{self.M==dmA.M}; 
            val dmC = C.local() as DenseMatrix(dmA.N, smB.N);

            if ((p != rootpid)|| !plus)
                DenseMultSparseToDense.compTransMult(dmA, smB, dmC, false);
            else 
                DenseMultSparseToDense.compTransMult(dmA, smB, dmC, true);
        }
        /* TIMING */ C.calcTime += Timer.milliTime() - stt;
        C.allReduceSum(); 
        return C;
    }

    /**
     * Perform C = A<sup>T</sup> &#42 B. All copies of the duplicated matrix are 
     * updated in all places.
     */
    public static def compTransMult(
            A:DistDenseMatrix, 
            B:DistSparseMatrix{self.M==A.M}, 
            C:DupDenseMatrix(A.N,B.N)) = compTransMult(A, B, C, false);

    /**
     * Return dense<sup>T</sup> &#42 sparse in duplicated dense format
     */
    public static def compTransMult(
            A:DistDenseMatrix, 
            B:DistSparseMatrix{self.M==A.M}) {
        val C = DupDenseMatrix.make(A.N, B.N);
        compTransMult(A, B, C, false);
        return C;
    }

    /**
     * A different implementation to in performing dense<sup>T</sup> &#42 sparse
     * All copies of duplicated matrix are updated .
     */
    public static def compTransMult2(
            A:DistDenseMatrix, 
            B:DistSparseMatrix{self.M==A.M}, 
            C:DupDenseMatrix(A.N,B.N),
            plus:Boolean) : DupDenseMatrix(C) {
        Debug.assure(C.M==A.N&&A.M==B.M&&B.N==C.N, "Matrix dimension mismatch");
        Debug.assure(A.grid.numColBlocks==1L && B.grid.numColBlocks==1L, 
                "Not single column block partitioning for input matrices");
        Debug.assure(A.dist.equals(B.dist), "Matrix distribution mismatch");
        
        val rootpid = here.id();
        /* TIMING */ var stt:Long =  Timer.milliTime();
        finish ateach([p] in A.dist) {
            val dmA = A.getMatrix(p); 
            val smB = B.getMatrix(p) as SparseCSC{self.M==dmA.M}; 
            val dmC = C.local() as DenseMatrix(dmA.N, smB.N);

            if ((p != rootpid) || !plus)
                DenseMultSparseToDense.compTransMult_ByDef(dmA, smB, dmC, false);
            else 
                DenseMultSparseToDense.compTransMult_ByDef(dmA, smB, dmC, true);
        }
        /* TIMING */ C.calcTime += Timer.milliTime() - stt;
        C.allReduceSum();

        return C;
    }

    /**
     * Return dense<sup>T</sup> &#42 sparse in duplicated dense format
     */
    public static def compTransMult2(
            A:DistDenseMatrix, 
            B:DistSparseMatrix{self.M==A.M}): DupDenseMatrix(A.N,B.N) {
        val C = DupDenseMatrix.make(A.N, B.N);
        compTransMult2(A, B, C, false);
        return C;
    }

    // add temp space to hold transposed A
    /**
     * Perform A<sup>T</sup> &#42 B, using tA as temporary space to store transposed A.
     * Result is updated in all places.
     */
    public static def compTransMult(
            A:DistDenseMatrix, 
            B:DistSparseMatrix{self.M==A.M}, 
            C:DupDenseMatrix(A.N,B.N),
            tA:DistDenseMatrix(A.N,A.M),
            plus:Boolean): DupDenseMatrix(C){
                
        Debug.assure(C.M==A.N&&A.M==B.M&&B.N==C.N, "Matrix dimension mismatch");
        Debug.assure(A.grid.numColBlocks==1L && B.grid.numColBlocks==1L, 
                "Not single column block partitioning for input matrices");
        Debug.assure(A.dist.equals(B.dist));

        val rootpid = here.id();
        /* TIMING */ var stt:Long =  Timer.milliTime();
        finish ateach([p] in A.dist) {
            val dmA = A.local();//getMatrix(p); //get dense matrix at pid
            val smB = B.local() as SparseCSC{self.M==dmA.M}; 
            val dmC = C.local() as DenseMatrix(dmA.N, smB.N);
            val dmA_T = tA.local() as DenseMatrix(dmA.N, dmA.M);
            
            if (p != rootpid || !plus)
                DenseMultSparseToDense.compTransMult(dmA, smB, dmC, dmA_T, false);
            else 
                DenseMultSparseToDense.compTransMult(dmA, smB, dmC, dmA_T, true);
        }
        /* TIMING */ C.calcTime += Timer.milliTime() - stt;

        C.allReduceSum(); 
        return C;
    }

    /**
     * Perform C += A<sup>T</sup> &#42 B or C = A<sup>T</sup> &#42 B. 
     * All copies of the duplicated matrix are updated.
     * Sparse matrix A is partitioned in nx1 blocks, and Dense matirx B 
     * is partitioned in nx1 blocks.
     */
    public static def compTransMult(
            A:DistSparseMatrix,
            B:DistDenseMatrix{self.M==A.M},
            C:DupDenseMatrix(A.N,B.N),
            plus:Boolean) : DupDenseMatrix(C){
                
        Debug.assure(C.M==A.N&&A.M==B.M&&B.N==C.N, "Matrix dimension mismatch");
        Debug.assure(A.grid.numColBlocks==1L && B.grid.numColBlocks==1L, 
            "Not single column block partitioning for input matrices");
        Debug.assure(A.dist.equals(B.dist));

        val rootpid = here.id();
        /* TIMING */ var stt:Long =  Timer.milliTime();
        finish ateach([p] in A.dist) {
            val dmA = A.local(); //get dense matrix at pid
            val smB = B.local() as DenseMatrix{self.M==dmA.M};
            val dmC = C.local() as DenseMatrix(dmA.N, smB.N);

            if ((p != rootpid)|| !plus)
                SparseMultDenseToDense.compTransMult(dmA, smB, dmC, false);
            else
                SparseMultDenseToDense.compTransMult(dmA, smB, dmC, true);
        }

        /* TIMING */ C.calcTime += Timer.milliTime() - stt;
        C.allReduceSum();//
        return C;
    }

    /**
     * Perform distributed dense<sup>T</sup> &#42 dense using BLAS driver.
     */
    public static def compTransMult(
            A:DistDenseMatrix, 
            B:DistDenseMatrix{self.M==A.M}, 
            C:DupDenseMatrix(A.N,B.N),
            plus:Boolean) : DupDenseMatrix(C) {
                
        Debug.assure(C.M==A.N&&A.M==B.M&&B.N==C.N, "Matrix dimension mismatch");
        Debug.assure(A.grid.numColBlocks==1L && B.grid.numColBlocks==1L, 
            "Not single column block partitioning for input matrices");
        Debug.assure(A.dist.equals(B.dist));
        
        val rootpid = here.id();
        /* TIMING */ var stt:Long =  Timer.milliTime();
        finish ateach([p] in A.dist) {
            val dmA = A.local(); 
            val dmB = B.local() as DenseMatrix{self.M==dmA.M }; 
            val dmC = C.local() as DenseMatrix(dmA.N, dmB.N);
            
            if (p != rootpid || !plus)
                DenseMatrixBLAS.compTransMult(1.0, dmA, dmB, 0.0, dmC);
            else 
                DenseMatrixBLAS.compTransMult(1.0, dmA, dmB, 1.0, dmC);
        }
        /* TIMING */ C.calcTime += Timer.milliTime() - stt;

        C.allReduceSum();
        return C;
    }

    public static def compTransMult(
            A:DistDenseMatrix, 
            B:DistDenseMatrix{self.M==A.M}, 
            C:DupDenseMatrix(A.N,B.N)) = compTransMult(A, B, C, false);

    public static def compTransMult(
            A:DistDenseMatrix, 
            B:DistDenseMatrix{self.M==A.M}): DupDenseMatrix(A.N,B.N)  {
        val C  = DupDenseMatrix.make(A.N,B.N);
        compTransMult(A, B, C, false);
        return C;
    }

    /**
     * Perform C += A &#42 B or C = A &#42 B. 
     * Matrix A has single row block partitioning (column-wise partitioning). 
     * Matrix B has single column block partitioning (row-wsie partitioning).
     *  Result are stored in duplicated dense matrix.
     */
    public static def comp(
        A:DistSparseMatrix, 
        B:DistDenseMatrix{self.M==A.M}, 
        C:DupDenseMatrix(A.M,B.N),
        plus:Boolean) : DupDenseMatrix(C){
                        
        //Global.assure(A.flagTranspose == true);
        Debug.assure(C.M==A.M&&A.N==B.M&&B.N==C.N, "Matrix dimension mismatch");
        Debug.assure(A.grid.numRowBlocks==1L && B.grid.numColBlocks==1L, 
                     "Matrices partitioning mismatch");

        val rootpid = here.id();

        /* TIMING */ var stt:Long =  Timer.milliTime();
        finish ateach([p] in A.dist) {
            val smA = A.getMatrix(p) as SparseCSC; 
            val dmB = B.getMatrix(p) as DenseMatrix{self.M==smA.N}; 
            val dmC = C.local() as DenseMatrix(smA.M, dmB.N);

           if ((p != rootpid)|| !plus)
               SparseMultDenseToDense.comp(smA, dmB, dmC, false);
           else 
               SparseMultDenseToDense.comp(smA, dmB, dmC, true);
        }
        /* TIMING */ C.calcTime += Timer.milliTime() - stt;

        //C.allReduceSum();
        C.reduceSum(); // Collect sum only at root place
        return C;
    }
}
