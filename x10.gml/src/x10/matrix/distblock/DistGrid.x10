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

package x10.matrix.distblock;

import x10.matrix.util.Debug;
import x10.matrix.block.Grid;

/**
 * A DistGrid instance specifies how blocks are distributed among places in a grid.
 * DistGrid uses integer array to map block IDs to place IDs.
 * DistGrid is a special case of distribution map, in which matrix blocks in a partition
 * grid is distributed in places of grid. Blocks neighoring in partitioning grid are
 * either in the same place or neighering places.
 */
public class DistGrid(numRowPlaces:Long, numColPlaces:Long) {
    public val dmap:DistMap;
    public val placeGrid:Grid;
    
    public def this(plzGrid:Grid,dp:DistMap) {
        property(plzGrid.numRowBlocks, plzGrid.numColBlocks);
        placeGrid = plzGrid;
        dmap = dp;
    }
    
    public def this(matgrid:Grid, nRowPs:Long, nColPs:Long) {
        property(nRowPs, nColPs);
        dmap = new DistMap(matgrid.size, nRowPs*nColPs);
        Debug.assure(
                nRowPs <= matgrid.numRowBlocks && nColPs <= matgrid.numColBlocks, 
                "Cannot distribute ("+matgrid.numRowBlocks+" x "+matgrid.numColBlocks+") blocks"+
                " over ("+nRowPs+" x "+nColPs+") places");       
        placeGrid = new Grid(matgrid.numRowBlocks, matgrid.numColBlocks, nRowPs, nColPs);                
        //This is not an efficient method, 
        for (var cb:Long=0; cb<matgrid.numColBlocks; cb++) { 
            for (var rb:Long=0; rb<matgrid.numRowBlocks; rb++) {
                val plcIndex = placeGrid.findBlock(rb, cb); 
                val bid = matgrid.getBlockId(rb, cb);
                dmap.set(bid, plcIndex);                
            }
        }
    }
    
    public static def compPartition(num:Long, blks:Long, plcs:Long):Rail[Long] {
        Debug.assure(num>=blks&&blks>=plcs, 
                "Unsatisfied partitioning - Total:"+num+" >= Blocks:"+blks+" >= Places:"+plcs);
        val szlist = new Rail[Long](blks);
        var cnt:Long =0;
        for (var p:Long=0; p<plcs; p++) {
            val tt = Grid.compBlockSize(num, plcs, p);
            val nb = Grid.compBlockSize(blks, plcs, p);
            for (var b:Long=0; b<nb; b++, cnt++){
                szlist(cnt) = Grid.compBlockSize(tt, nb, b);
            }
        }
        //Debug.flushln("Partition "+num+" to "+blks+" segs : " + szlist.toString());
        return szlist;
    }
    
    /**
     * Create grid partitioning with balanced number of rows and columns among places.
     */
    public static def makeGrid(M:Long, N:Long, blkM:Long, blkN:Long, plcM:Long, plcN:Long):Grid {
        val rowBs = compPartition(M, blkM, plcM);
        val colBs = compPartition(N, blkN, plcN);
        
        return new Grid(M, N, rowBs, colBs);
    }
    
    /**
     * Partitioning all blocks among all places. All matrix blocks are specified 
     * by matrix partitioning of g.  The blocks are partitioned among all places
     * in the same way as matrix is partitioned.
     * 
     * @param  g     the partitioning blocks
     * @return       the map of block IDs to place IDs.
     */
    public static def make(g:Grid) = make(g, Place.numPlaces());

    public static def make(g:Grid, numPlaces:Long) = makeMaxRow(g, Math.sqrt(numPlaces) as Long, numPlaces);
    
    
    /**
     * Partitioning all blocks to clusters, while maximizing number of groups (cluster) in row.
     * 
     * @param matgrid          the partitioning matrix in blocks specified by Grid
     * @param maxRowClusters   the maximum number clusters in a row
     * @param totalClusters    the total clusters used in partitioning blocks
     * @return                 the map of block IDs to place IDs.
     */    
    public static def makeMaxRow(matgrid:Grid, maxRowPs:Long, totalPs:Long) {
        val nRowBs    = matgrid.numRowBlocks;
        var rowPs:Long = nRowBs < maxRowPs ? nRowBs : maxRowPs;
        while (totalPs % rowPs != 0L) { rowPs--; }
        if (rowPs == 0L) rowPs = 1;
        val colPs = totalPs/rowPs;
        return new DistGrid(matgrid, rowPs, colPs);
    }
    
    public static def makeMaxCol(matgrid:Grid, maxColPs:Long, totalPs:Long) {
        val nColBs    = matgrid.numColBlocks;
        var colPs:Long = nColBs < maxColPs ? nColBs : maxColPs;
        while (totalPs % colPs != 0L) { colPs--; }
        if (colPs == 0L) colPs = 1;
        val rowPs = totalPs/colPs;
        return new DistGrid(matgrid, rowPs, colPs);
    }
    
    public static def makeHorizontal(g:Grid) = makeHorizontal(g, Place.numPlaces());

    public static def makeHorizontal(g:Grid, numPlaces:Long) = makeMaxRow(g, 1, numPlaces);

    public static def makeVertical(g:Grid) = makeVertical(g, Place.numPlaces());
    
    public static def makeVertical(g:Grid, numPlaces:Long) = makeMaxRow(g, numPlaces, numPlaces);

    public static def isHorizontal(g:Grid, dmap:DistMap):Boolean {
        for (var c:Long=0; c<g.numColBlocks; c++) {
            val bid0 = g.getBlockId(0, c);
            val pIndex0 = dmap.findPlaceIndex(bid0);
            for (var r:Long=1; r<g.numRowBlocks; r++) {
                val bid = g.getBlockId(r,c);
                val pIndex = dmap.findPlaceIndex(bid);
                if (pIndex != pIndex0) return false;
            }
        }
        return true;
    }

    /**
     * Check vertical distribution of blocks. 
     */
    public static def isVertical(g:Grid, dmap:DistMap):Boolean {
        for (var r:Long=0; r<g.numRowBlocks; r++) {
            val bid0 = g.getBlockId(r, 0);
            val pIndex0 = dmap.findPlaceIndex(bid0);
            for (var c:Long=1; c<g.numColBlocks; c++) {
                val bid = g.getBlockId(r,c);
                val pIndex = dmap.findPlaceIndex(bid);
                if (pIndex != pIndex0) return false;
            }
        }
        return true;
    }

    /**
     * Aggregate all segments in the same place to one segment,
     * when computing the number of rows or columns within a place
     */
    private def compLocalRows(matGrid:Grid, rowPlaceIndex:Long):Long {
        val sttRowBlk = placeGrid.startRow(rowPlaceIndex);
        var rowtt:Long = 0L;
        for (var rowbid:Long=0; rowbid<placeGrid.rowBs(rowPlaceIndex); rowbid++) {
            rowtt += matGrid.rowBs(rowbid+sttRowBlk);
        }
        return rowtt;
    }

    private def compLocalCols(matGrid:Grid, colPlaceIndex:Long):Long {
        val sttColBlk = placeGrid.startCol(colPlaceIndex);
        var coltt:Long = 0L;
        for (var colbid:Long=0; colbid<placeGrid.colBs(colPlaceIndex); colbid++) {
            coltt += matGrid.colBs(colbid+sttColBlk);
        }
        return coltt;
    }
    
    public def getAggRowBs(grid:Grid):Rail[Long]{
        val rbs = new Rail[Long](numRowPlaces, (i:Long)=>compLocalRows(grid, i as Long));
        return rbs;
    }
    
    public def getAggColBs(grid:Grid):Rail[Long]{
        val cbs = new Rail[Long](numColPlaces, (i:Long)=>compLocalCols(grid, i as Long));
        return cbs;
    }
}
