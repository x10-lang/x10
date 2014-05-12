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

package x10.matrix.block;

import x10.regionarray.Array;
import x10.util.ArrayList;

import x10.matrix.util.Debug;
import x10.matrix.Matrix;

/**
 * Block matrix multiply. Input block matrix must be partitioned in grid, which
 * allows array list index matches to its corresponding block id. 
 * Therefore, iterations of blocks do not need to search for block list.
 */
public class BlockBlockMult  {
    public static def mult(
            A:BlockMatrix, 
            B:BlockMatrix(A.N),
            C:BlockMatrix(A.M,B.N),
            plus:Boolean):BlockMatrix(C) {
        Debug.assure(Grid.match(A.grid.rowBs, C.grid.rowBs), 
            "Row partiton of first operand and result matrix mismatch in matrix multiply");
        Debug.assure(Grid.match(B.grid.colBs, C.grid.colBs),
            "Column partition of second operand and result matrix mismatch in matrix multiply");
        Debug.assure(Grid.match(A.grid.colBs, B.grid.rowBs),
            "Column partition of first and row partition of second operand mismatch in matrix multiply");

        if (A.blockMap==null) A.buildBlockMap();
        if (B.blockMap==null) B.buildBlockMap();
        if (C.blockMap==null) C.buildBlockMap();
        mult(A.blockMap, B.blockMap, C.blockMap, plus);
        return C;
    }

    public static def transMult(
            A:BlockMatrix, 
            B:BlockMatrix(A.M),
            C:BlockMatrix(A.N,B.N),
            plus:Boolean):BlockMatrix(C) {
        Debug.assure(Grid.match(A.grid.colBs, C.grid.rowBs), 
            "Column partiton of first operand and row partition of result matrix mismatch in trans-multply");
        Debug.assure(Grid.match(B.grid.colBs, C.grid.colBs), 
            "Column partition of second operand and result matrix mismatch in trans-multiply");
        Debug.assure(Grid.match(A.grid.rowBs, B.grid.rowBs), 
            "Row partition of first and second operand mismatch in trans-multiply");

        if (A.blockMap==null) A.buildBlockMap();
        if (B.blockMap==null) B.buildBlockMap();
        if (C.blockMap==null) C.buildBlockMap();
        transMult(A.blockMap, B.blockMap, C.blockMap, plus);
        return C;
        
        //return transMult(A.listBs, B.listBs, C, plus);
    }        

    public static def multTrans(
            A:BlockMatrix, 
            B:BlockMatrix{self.N==A.N},
            C:BlockMatrix(A.M,B.M),
            plus:Boolean):BlockMatrix(C) {
        Debug.assure(Grid.match(A.grid.rowBs, C.grid.rowBs), 
            "Row partiton of first operand and result matrix mismatch in multiply-trans");
        Debug.assure(Grid.match(B.grid.rowBs, C.grid.colBs), 
            "Row partition of second operand and result matrix mismatch in multiply-trans");
        Debug.assure(Grid.match(A.grid.colBs, B.grid.colBs),
            "Column partition of first and second operand mismatch in multiply-trans");
        
        if (A.blockMap==null) A.buildBlockMap();
        if (B.blockMap==null) B.buildBlockMap();
        if (C.blockMap==null) C.buildBlockMap();
        multTrans(A.blockMap, B.blockMap, C.blockMap, plus);
        return C;
    }

    public static def mult(
            A:ArrayList[MatrixBlock], 
            B:ArrayList[MatrixBlock],
            C:BlockMatrix,
            plus:Boolean):BlockMatrix(C) {
        
        val grid = C.grid;
        if (!plus) C.reset();
        for (var cb:Long=0; cb < grid.numColBlocks; cb++) {
            val blist = findColBlockList(B, cb);

            for (var rb:Long=0; rb<grid.numRowBlocks; rb++) {
                val alist = findRowBlockList(A, rb);
                val ait = alist.iterator();
                val bit = blist.iterator();
                Debug.assure(alist.size()==blist.size(), 
                        "Partition mismatch! Number of partition not same.");
                val cblk = C.findBlock(rb, cb);

                while (ait.hasNext()) {
                    val ablk = ait.next();
                    val bblk = bit.next();
                    Debug.assure(ablk.myColId==bblk.myRowId, 
                            "Block partition misaligned in block matrix multiply");
                    cblk.mult(ablk, bblk, true);                    
                }
            }
        }
        return C;    
    }
    
    
    public static def transMult(
            A:ArrayList[MatrixBlock], 
            B:ArrayList[MatrixBlock],
            C:BlockMatrix,
            plus:Boolean):BlockMatrix(C) {
        
        val grid = C.grid;
        if (!plus) C.reset();
        for (var cb:Long=0; cb < grid.numColBlocks; cb++) {
            val blist = findColBlockList(B, cb);
            for (var rb:Long=0; rb<grid.numRowBlocks; rb++) {
                val alist = findColBlockList(A, rb);
                val ait = alist.iterator();
                val bit = blist.iterator();
                Debug.assure(alist.size()==blist.size(), 
                        "Partition mismatch! Numbers of partition blocks not same");
                val cblk = C.findBlock(rb, cb);

                while (ait.hasNext()) {
                    val ablk = ait.next();
                    val bblk = bit.next();
                    Debug.assure(ablk.myRowId==bblk.myRowId,
                            "Block partition misaligned in block trans-multiply");
                    cblk.transMult(ablk, bblk, true);
                }
            }
        }
        return C;    
    }
    
    public static def multTrans(
            A:ArrayList[MatrixBlock], 
            B:ArrayList[MatrixBlock],
            C:BlockMatrix,
            plus:Boolean):BlockMatrix(C) {
        
        val grid = C.grid;
        if (!plus) C.reset();
        for (var cb:Long=0; cb < grid.numColBlocks; cb++) {
            val blist = findRowBlockList(B, cb);
            for (var rb:Long=0; rb<grid.numRowBlocks; rb++) {
                val alist = findRowBlockList(A, rb);
                val ait = alist.iterator();
                val bit = blist.iterator();
                Debug.assure(alist.size()==blist.size(), 
                        "Partition mismatch! Number of partitions not same");

                val cblk = C.findBlock(rb, cb);
                //if (!plus) cblk.reset();

                while (ait.hasNext()) {
                    val ablk = ait.next();
                    val bblk = bit.next();
                    Debug.assure(ablk.myColId==bblk.myColId,
                            "Block partition misaligned in block multiply-trans");
                    cblk.multTrans(ablk, bblk, true);
                }
            }
        }
        return C;    
    }    
    
    /**
     * This function is same as BlockSet.search(). Considering merging the two.
     * Blocks must be sorted in column-major
     */
    protected static def search(blklist:ArrayList[MatrixBlock], rowId:Long, colId:Long):Long {
        if (blklist.size() == 0L) return -1;

        var min:Long = 0; 
        var max:Long = blklist.size() - 1; 
        var blk:MatrixBlock;
        var mid:Long = min;
        do {
            mid = min + (max - min) / 2;
            blk = blklist.get(mid);
            
            if (blk.myColId < colId || ( blk.myColId == colId && blk.myRowId < rowId)) {
                min = mid + 1;
            } else {
                max = mid - 1; 
            }
            blk = blklist.get(mid);
            if (blk.myRowId == rowId && blk.myColId== colId) return mid;            
            
        } while ( min<=max );
        return -1;
    }
    
    public static def findBlock(blklist:ArrayList[MatrixBlock], rowId:Long, colId:Long):MatrixBlock {
        val idx = search(blklist, rowId, colId);
        if (idx < 0 ) {
            Debug.exit("Cannot find block ("+rowId+","+colId+") at place "+here.id());
        }
        return blklist.get(idx);
    }
    // {    //Need optimzation in searching
    //     val itr = blklist.iterator();
    //     while (itr.hasNext()) {
    //         val blk = itr.next();
    //         if (blk.myRowId == rowId && blk.myColId==colId) {
    //             return blk;
    //         }
    //     }
    //     return null;
    //             
    // }
    
    public static def findSelect(blklist:ArrayList[MatrixBlock], commonId:Long, select:(Long,Long)=>Long):ArrayList[MatrixBlock] {
        val retlst=new ArrayList[MatrixBlock]();
        val itr = blklist.iterator();
        while (itr.hasNext()) {
            val blk = itr.next();
            val tgt = select(blk.myRowId, blk.myColId);
            if (commonId == tgt) 
                retlst.add(blk);
        }
        //sort
        retlst.sort((b1:MatrixBlock,b2:MatrixBlock)=>(select(b2.myColId,b2.myRowId)-select(b1.myColId,b1.myRowId)) as Int);
        // if (here.id()==0) {
        //     val itr1 = blklist.iterator();
        //     while (itr1.hasNext()) {
        //         val b=itr1.next();
        //         val cmpid = select(b.myColId, b.myRowId);
        //         Debug.flushln("Block ("+b.myRowId+","+b.myColId+") cmp value"+cmpid);
        //     }
        // }
        // Debug.flushln("Done select+sort");
        return retlst;
    }
    
    public static def findRowBlockList(blklist:ArrayList[MatrixBlock], rid:Long) =
        findSelect(blklist, rid, (r:Long, c:Long)=>r);

    public static def findColBlockList(blklist:ArrayList[MatrixBlock], rid:Long) =
        findSelect(blklist, rid, (r:Long, c:Long)=>c);

    public static def mult(
            A:Array[MatrixBlock](2), 
            B:Array[MatrixBlock](2),
            C:Array[MatrixBlock](2),
            plus:Boolean) {

        for ([r,c] in C) {
            val cblk:MatrixBlock = C(r,c);
            val cmat = cblk.getMatrix();

            if (!plus) cmat.reset();
            for (k in A.region.min(1)..A.region.max(1)) {
                val ablk = A(r,k);
                val bblk = B(k,c);
                Debug.assure(ablk.myRowId==cblk.myRowId, 
                        "First operand block row Id "+ablk.myRowId+" not match to result row block id "+cblk.myRowId);
                Debug.assure(bblk.myColId==cblk.myColId,
                        "Second operand block column block id "+bblk.myColId+" not match to result column block id "+cblk.myColId);
                Debug.assure(ablk.myColId==bblk.myRowId, 
                        "First operand block column block Id "+ablk.myColId+" not match to second row block id "+bblk.myRowId);
                val amat = ablk.getMatrix() as Matrix(cmat.M);
                val bmat = bblk.getMatrix() as Matrix(amat.N, cmat.N);
                
                cmat.mult(amat, bmat, true);
            }
        }
    }

    public static def transMult(
            A:Array[MatrixBlock](2), 
            B:Array[MatrixBlock](2),
            C:Array[MatrixBlock](2),
            plus:Boolean) {

        for ([r,c] in C) {
            val cblk:MatrixBlock = C(r,c);
            val cmat = cblk.getMatrix();
            
            if (!plus) cmat.reset();
            for (k in A.region.min(0)..A.region.max(0)) {
                val ablk = A(k,r);
                val bblk = B(k,c);
                
                Debug.assure(ablk.myColId==cblk.myRowId, "First operand and output matrix block dismatch");
                Debug.assure(bblk.myColId==cblk.myColId, "Second operand and output matrix block dismatch" );
                Debug.assure(ablk.myRowId==bblk.myRowId, "First and second operand matrix block dismatch");
                val amat = ablk.getMatrix() as Matrix{self.N==cmat.M};
                val bmat = bblk.getMatrix() as Matrix(amat.M, cmat.N);
                
                cmat.transMult(amat, bmat, true);
            }
        }
    }
    
    public static def multTrans(
            A:Array[MatrixBlock](2), 
            B:Array[MatrixBlock](2),
            C:Array[MatrixBlock](2),
            plus:Boolean) {

        for ([r,c] in C) {
            val cblk:MatrixBlock = C(r,c);
            val cmat = cblk.getMatrix();
            
            if (!plus) cmat.reset();
            for (k in A.region.min(1)..A.region.max(1)) {
                val ablk = A(r,k);
                val bblk = B(c,k);
                
                Debug.assure(ablk.myRowId==cblk.myRowId, "First operand and output matrix block dismatch");
                Debug.assure(bblk.myRowId==cblk.myColId, "Second operand and output matrix block dismatch");
                Debug.assure(ablk.myColId==bblk.myColId, "First and second operand matrix block dismatch");
                val amat = A(r,k).getMatrix() as Matrix(cmat.M);
                val bmat = B(c,k).getMatrix() as Matrix(cmat.N, amat.N);
                
                cmat.multTrans(amat, bmat, true);
            }
        }
    }
}
    
