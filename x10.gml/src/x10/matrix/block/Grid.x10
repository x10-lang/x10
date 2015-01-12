/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 *  (C) Copyright Australian National University 2013.
 */
package x10.matrix.block;

import x10.regionarray.Dist;
import x10.regionarray.DistArray;
import x10.compiler.Inline;
import x10.util.StringBuilder;

import x10.matrix.util.Debug;

public type Grid(bM:Long,bN:Long)=Grid{self.numRowBlocks==bM, self.numColBlocks==bN};
public type Grid(m:Long,n:Long,bM:Long,bN:Long)=Grid{self.M==m,self.N==n,self.numRowBlocks==bM,self.numColBlocks==bN};

/**
 * This class represents a grid-based decomposition of a block matrix,
 * with the rows divided into equal-sized blocks, 
 * and the columns divided into equal-sized blocks. 
 * If the number of rows (or columns) in the matrix cannot be equally divided
 * the lower-numbered row blocks (or column blocks) are assigned with one more row (or column).
 * <p>
 * In grid partitioning, blocks in the same row of partition grid (row blocks) share the same number of rows.
 * Blocks in the same column of partition grid (column blocks) share the same number of columns.
 * <p> Users can specify their own partitioning by constructing an instance
 * with specified "rowBs" and "colBs".
 */
public class Grid(M:Long, N:Long, numRowBlocks:Long, numColBlocks:Long) {
    /**
     * Number of blocks in partitioning
     */
    public val size:Long;

    /**
     * List of numbers of rows in blocks. Blocks in the same partitioning row blocks 
     * have the same number of rows.
     */
    public val rowBs:Rail[Long]; //list of block row sizes  

    /**
     * List of numbers of columns in blocks. Blocks in the same partitioning column
     * blocks have the same number of columns.
     */
    public val colBs:Rail[Long]; //list of block column sizes

    /**
     * Construct a partition for an m x n matrix with the rows divided by the
     * specified number of row blocks (rb) and columns divided by the 
     * specified number of column blocks (cb). 
     * In the partitioning, blocks in the same partition row have the same number of
     * rows, and blocks in the same partition column have the same number of columns,
     * making a grid partitioning.
     *
     * <p> In partitioned blocks, number of rows and columns of the matrix 
     * are evenly divided by the number of row blocks and the number of column blocks.
     * If the numbers cannot be evenly divided, 
     * the first m%rb partition row blocks are assigned with one extra
     * row, and n%cb partition column blocks are assigned with one extra
     * column.
     *
     * @param  m         number of rows in matrix
     * @param  n         number of columns in matrix
     * @param numRowBs   number of partition row blocks
     * @param numColBs   number of partition column blocks
     */
    public def this(m:Long, n:Long, numRowBs:Long, numColBs:Long) {
        property(m,n, numRowBs, numColBs);
        size = numRowBs*numColBs;
        //Guard for divided by 0 condition!
        Debug.assure(numRowBs!=0L && numColBs!=0L);
        rowBs = partition(M, numRowBs);
        colBs = partition(N, numColBs);
    }

    /**
     * Construct a partition for an n x n matrix with the rows and columns
     * divided by the specified number of blocks (nblks). 
     * In the partitioning, blocks in the same partition row have the same
     * number of rows, and blocks in the same partition column have the same
     * number of columns, making a grid partitioning.
     *
     * <p> In partitioned blocks, number of rows and columns of the matrix 
     * are evenly divided by the number of row blocks and the number of column
     * blocks. If the numbers cannot be evenly divided,  the first n%rb 
     * partition row/col blocks are assigned with one extra element.
     *
     * @param  n      number of rows/cols in matrix
     * @param nblks   number of partition row/col blocks
     */
    public def this(n:Long, nblks:Long) {
        property(n, n, nblks, nblks);
        size = nblks*nblks;
        //Guard for divided by 0 condition!
        Debug.assure(nblks!=0L);
        rowBs = partition(M, nblks);
        colBs = partition(N, nblks);
    }

    /**
     * Create a partition for an m x n matrix in specified sizes for rows and columns
     * of blocks
     *
     * @param  m      number of rows in matrix
     * @param  n      number of columns in matrix
     * @param rBs     list of rows for blocks
     * @param cBs     list of columns for blocks
     */
    public def this(m:Long, n:Long, rBs:Rail[Long], cBs:Rail[Long]) {
        property(m, n, rBs.size, cBs.size);
        size = (rBs.size * cBs.size);
        rowBs = rBs;
        colBs = cBs;
    }

    /**
     * Create a partition for an n x n matrix in specified sizes for rows and columns
     * of blocks
     *
     * @param  n     number of rows/columns in matrix
     * @param Bs     list of rows for blocks
     */
    public def this(n:Long, Bs:Rail[Long]) {
        val numBlocks = Bs.size;
        property(n, n, numBlocks, numBlocks);
        size = (numBlocks * numBlocks);
        rowBs = Bs;
        colBs = Bs;
    }

    /**
     * Construct a grid partitioning with specified numbers of row and 
     * specified numbers of column for partitioning blocks. 
     * Blocks in the same partition row share the same number of rows, and
     * blocks in the same partition column share the same number of columns.
     *
     * @param  rbs    rows of blocks rowwise
     * @param  cbs    columns of blocks columnwise
     */
    public def this(rbs:Rail[Long], cbs:Rail[Long]){
        property(
             ( ()=> {
                 var t:Long=0L; 
                 val rs = rbs.size;
                 for (rb in rbs)
                     t += rb;
                 t } )(), 
             ( () => {
                 var t:Long=0L; 
                 val cs = cbs.size;
                 for (cb in cbs)
                     t += cb; 
                 t } )(), 
             rbs.size, 
             cbs.size 
            );
        rowBs = rbs;
        colBs = cbs;
        size  = (rbs.size * cbs.size);
    }

    /**
     * Make a grid partitioning based on specified matrix dimension and
     * maximize the number of row blocks allowed.
     *
     * @param  m          number of rows in matrix
     * @param  n          number of columns in matrix
     * @param maxRowBs    the largest number of blocks in partition row
     * @param totalBs     total number of blocks
     * 
     */
    public static def makeMaxRow(m:Long, n:Long, var maxRowBs:Long, totalBs:Long) {
        if (maxRowBs > m) maxRowBs = m;
        while (totalBs % maxRowBs != 0L) { maxRowBs--; }
        if (maxRowBs == 0L) maxRowBs = 1;
        val cb = totalBs/maxRowBs;
        return new Grid(m, n, maxRowBs, cb);
    }
    
    public static def makeMaxCol(m:Long, n:Long, var maxColBs:Long, totalBs:Long) {
        if (maxColBs > n) maxColBs = n;
        while (totalBs % maxColBs != 0L) { maxColBs--; }
        if (maxColBs == 0L) maxColBs = 1;
        val rb = totalBs/maxColBs;
        return new Grid(m, n, rb, maxColBs);
    }    
    /**
     * Make a grid partitioning based on specified matrix dimension and total
     * number of blocks, where the difference between numbers of row blocks
     * and column blocks is minimized. This method makes a squared partitioning 
     * or close-squared partitioning.
     *
     * @param  m         number of rows in matrix
     * @param  n         number of columns in matrix
     * @param totalBs    total number of partition blocks
     */
    public static def make(m:Long, n:Long, totalBs:Long) =
        makeMaxRow(m, n, Math.sqrt(totalBs as Double) as Long, totalBs); 
    /*{
        var rb:Long = Math.sqRoot(s) as Long;
        if (rb == 0) rb = 1;
        while (s % rb != 0) { rb--; }
        val cb = s / rb;
        return new Grid(m, n, rb, cb);         
    }*/


    /**
     * Make a grid partitioning based on specified matrix dimension targeting
     * the total number of places in current parallel execution.
     * This method creates a squared or close to squared partitioning.
     */
    public static def make(m:Long, n:Long) =
        make(m, n, Place.places()); 

    /**
     * Make a grid partitioning based on specified matrix dimension targeting
     * the number of places in a given PlaceGroup.
     * This method creates a squared or close to squared partitioning.
     */
    public static def make(m:Long, n:Long, places:PlaceGroup) =
        makeMaxRow(m, n, Math.sqrt(places.size()) as Long, places.size()); 

    public static def make(rbs:Long, cbs:Long, 
            rowPartFunc:(Long)=>Long, colPartFunc:(Long)=>Long) {
        val rBzList = new Rail[Long](rbs, (i:Long)=>rowPartFunc(i));
        val cBzList = new Rail[Long](cbs, (i:Long)=>colPartFunc(i));
        var m:Long=0, n:Long=0;
        for (var r:Long=0; r<rbs; r++) m+=rBzList(r);
        for (var c:Long=0; c<cbs; c++) n+=cBzList(c);
        
        return new Grid(m, n, rBzList, cBzList);
    }

    /** 
     * Compute the size of segment in partitioning.
       *
     * @param  n     the number of rows or columns to be partitioned
     * @param  b     the number of blocks in partitioning
     */
    public static def partition(n:Long, b:Long):Rail[Long] {
        val bdim = new Rail[Long](b);
        val bs:Long = n / b;
        var k:Long  = n % b;

        Debug.assure(bs>0, "Partition has 0 size");
        for (var i:Long=0; i< b; i++) {
            //bdim(i) = compBlockSize(n, b, i);
            bdim(i) = bs;
            if (k>0) {
                bdim(i)++; 
                k--;
            } 
        }
        return bdim;
    }

    @Inline
    public static def compBlockSize(nTotal:Long, blkNum:Long, blkId:Long):Long {
        var sz:Long = (blkId < nTotal % blkNum)?1:0;
        sz += nTotal / blkNum;
        return sz;
    }

    // Mapping 1D->2D

    /**
     * For a given place/block id, return its row block id 
     */
    public def getRowBlockId(id:Long):Long = (id%numRowBlocks);

    /**
     * For a given place/block id, return the column block id
     */        
    public def getColBlockId(id:Long):Long = (id/numRowBlocks);

    /**
     * Given a block's row and column id, return the place id
     */    
    public def getBlockId(r:Long, c:Long):Long = (c*numRowBlocks+r);
    

    /**
     * Given a block id, return the number of rows in the corresponding block
     */    
    public def getRowSize(id:Long):Long = rowBs(getRowBlockId(id));
    /**
     * Given a block id, return the number of columns in the corresponding block
     */    
    public def getColSize(id:Long):Long = colBs(getColBlockId(id));

    /**
     * Given block id, return the number of elements in the block
     */    
    public def getBlockSize(id:Long):Long = getRowSize(id) * getColSize(id);
    /**
     * Given the block's row and column id, return the block's size.
     */    
    public def getBlockSize(rid:Long, cid:Long):Long = rowBs(rid) * colBs(cid);
    
    // The last row block has the smallest size.
    public def getMinRowSize() = rowBs(numRowBlocks-1); 
    // The last col block has the smallest size.
    public def getMinColSize() = colBs(numColBlocks-1); 

    /**
     * Given a block id, return its south block id
     */    
    public def getSouthId(pid:Long):Long = ((pid % numRowBlocks + 1)==numRowBlocks) ? (pid - numRowBlocks + 1):(pid + 1);
    /**
     * Given a block id, return its east block id
     */        
    public def getEastId(pid:Long):Long  = ((pid / numRowBlocks + 1)==numColBlocks) ? (pid % numRowBlocks):(pid + numRowBlocks);
    /**
     * Given a block id, return its north block id
     */    
    public def getNorthId(pid:Long):Long = (pid % numRowBlocks==0L) ? (pid + numRowBlocks - 1):(pid - 1);
    /**
     * Given a block id, return its west block id
     */
    public def getWestId(pid:Long):Long = (pid / numRowBlocks==0L) ? (pid + numRowBlocks*(numColBlocks - 1)):(pid - numRowBlocks); 

    /**
     * Non-cyclic neighboring block.
     */
    public def getNorthId(rid:Long, cid:Long):Long = rid>0L?              getBlockId(rid-1,cid):-1L;
    public def getSouthId(rid:Long, cid:Long):Long = rid<numRowBlocks-1 ? getBlockId(rid+1,cid):-1L;
    public def getWestId(rid:Long, cid:Long):Long  = cid>0L?              getBlockId(rid,cid-1):-1L;
    public def getEastId(rid:Long, cid:Long):Long  = cid<numColBlocks-1 ? getBlockId(rid,cid+1):-1L;

    // Locating block matrix

    /**
     * Given row and column position of element find its partition blocks and
     * row and column index within the block
     *
     * @param x -- row position in matrix
     * @param y -- column position in matrix
     * @param [row block index, column block index, row index, column index]
     */
       public def find(var x:Long, var y:Long):Rail[Long]{
        var br:Long;
        var bc:Long;
        Debug.assure( (x >=0) && (x<this.M) && (y>=0) && (y<this.N));
     
        for (br=0; x >= rowBs(br) && br<numRowBlocks; br++) {
                x -= rowBs(br); 
        }
        for (bc=0; y >= colBs(bc) && bc<numColBlocks; bc++) {
                y -= colBs(bc); 
        }
        
        return [br, bc, x, y];
    }
            
       /**
        * Given row and column index, return block ID in the column-wise
        * indexing.
        */
       public def findBlock(var x:Long, var y:Long):Long {
           var br:Long;
           var bc:Long;
           var bid:Long=0;
           Debug.assure( (x >=0) && (x<this.M) && (y>=0) && (y<this.N));

           for (bc=0; y >= colBs(bc) && bc<numColBlocks; bc++) {
               y -= colBs(bc);
               bid += numRowBlocks;
           }
           
           for (br=0; x >= rowBs(br) && br<numRowBlocks; br++) {
               x -= rowBs(br);
               bid++;
           }
           return bid;           
       }
       
       /**
        * Compute the starting row for a given row block id
        */
       public def startRow(rid:Long):Long {
           var sttrow:Long=0;
           for (var i:Long=0; i<rid; i++)
               sttrow += rowBs(i);
           return sttrow;
       }
       
       /**
        * Compute the starting column for a given column block id;
        */
       public def startCol(cid:Long):Long {
           var sttcol:Long=0;
           for (var i:Long=0; i<cid; i++)
               sttcol += colBs(i);
           return sttcol;
       }      
       
       public def startColumn(cid:Long) = startCol(cid);

    /**
     * Return a grid partition for the transposed matrix.
     */
    public def newT():Grid = new Grid(this.colBs, this.rowBs);

    /**
     * Test two partitions are same or not. This method only
     * check matrix dimension and numbers row blocks and column blocks.
     *
     * @param that   the target partitioning
     */
    public def likeMe(that:Grid):Boolean {
        return M == that.M && N == that.N 
            && numRowBlocks == that.numRowBlocks
            && numColBlocks == that.numColBlocks;
    }

    /**
     * Check two partitions are exactly same or not. This method only
     * check all row block sizes and column block sizes.
     *
     * @param that   the target partitioning
     */
    public def equals(that:Grid):Boolean {
        if (this == that) return true;
        if (!likeMe(that)) return false;

        return (match(this.rowBs, that.rowBs) && match(this.colBs, that.colBs));
    }
    
    public static def match(alist:Rail[Long], blist:Rail[Long]):Boolean {
        var ret:Boolean = true;
        for (var i:Long=0L; i<alist.size&&i<blist.size&&ret; i++)
            ret &= (alist(i) == blist(i));
        return ret;
    }

    // Assuming one-to-one unique distribution    
    /**
     * Return a distributed array, each entry contains an array of place ids
     * which locate in the same row as the entry's index
     */
    public def getRowBsPsMap():DistArray[Rail[Long]](1) {
        //FIXME: allow arbitrary place group
        val map = DistArray.make[Rail[Long]](Dist.makeUnique());            
        finish ateach(p in map.dist){
            val pid = here.id();
            map(here.id()) = new Rail[Long](numColBlocks,
                    (c:Long)=>(getBlockId(getRowBlockId(pid), c)));
        }
        return map;
    }
    
    public def getColBsPsMap():DistArray[Rail[Long]](1) {
        //FIXME: allow arbitrary place group
        val map = DistArray.make[Rail[Long]](Dist.makeUnique());
        finish ateach(p in map.dist) {
            val pid = here.id();
            map(here.id())= new Rail[Long](numRowBlocks, 
                    (r:Long)=>getBlockId(r, getColBlockId(pid))); 
        }
        return map;
    }    

    public def toString() : String {
        val sb = new StringBuilder();
        sb.add("Grid:\nPartition "+M+" rows into "+numRowBlocks+" blocks [");
        for (var i:Long=0; i<numRowBlocks; i++) {
            if (i > 0) sb.add(",");
            sb.add(rowBs(i));
        }
        sb.add("]\nPartition "+N+" cols into "+numColBlocks+" blocks [");
        for (var i:Long=0; i<numColBlocks; i++) {
            if (i > 0) sb.add(",");
            sb.add(colBs(i));
        }
        sb.add("]");
        return sb.toString();
    }
}

