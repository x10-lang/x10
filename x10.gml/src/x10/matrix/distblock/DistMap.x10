/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

package x10.matrix.distblock;

import x10.util.ArrayList;
import x10.util.Pair;
import x10.util.StringBuilder;
import x10.compiler.Inline;

import x10.matrix.Debug;
import x10.matrix.RandTool;
import x10.matrix.block.Grid;

/**
 * This class represents how matrix blocks in a partition grid are distributed
 * to all places.
 * <p>
 * In a grid partitioning, all blocks are assigned with block numbers in a column-wise
 * fashion.  DistGrid provides a mechanism (mapping functions) between a block number
 * and a place number.
 * 
 * <p> There are several mapping functions provided in this class, including cylic, 
 * constant and unique distrition of blocks to number of places.
 */
public class DistMap(numBlock:Long, numPlace:Long)  {
    public val blockmap:Rail[Long];            //mapping block ID to its place ID
    //public val placemap:Rail[ArrayList[Long]]; //mapping place ID to list of block IDs
    
    public def this(numBlk:Long, numPlc:Long) {
        property(numBlk, numPlc);
        blockmap = new Rail[Long](numBlk, -1);
        //placemap = new Rail[ArrayList[Long]](numPlc, (i:Long)=>(new ArrayList[Long]()));
    }
    
    public def this(blkmap:Rail[Long], numPlc:Long) {
        property(blkmap.size as Long, numPlc);
        blockmap = blkmap;
        //placemap = plcmap;
    }

    public static def make(numBlk:Long):DistMap {
        return new DistMap(numBlk, Place.MAX_PLACES);
    }
    
    public static def make(numBlk:Long, mapfunc:(Long)=>Long) {
        val dmap = make(numBlk);
        for (var b:Long=0; b<numBlk; b++) 
            dmap.set(b, mapfunc(b));
        return dmap;
    }
    
    public static def makeCylic(numBlk:Long) = make(numBlk, (i:Long)=>i%Place.MAX_PLACES);
    public static def makeCylic(numBlk:Long, numPlc:Long) = make(numBlk, (i:Long)=>i%numPlc);
    public static def makeUnique() = make(Place.MAX_PLACES, (i:Long)=>i);
    public static def makeUnique(numBlk:Long) = make(numBlk, (i:Long)=>i);
    
    public static def makeConstant(numBlk:Long) = make(numBlk, (i:Long)=>0L);
    public static def makeConstant(numBlk:Long, p:Long) = make(numBlk, (i:Long)=>p); 

    //This method could leave emply block for some place
    //public static def makeRandom(numBlk:Long, numPlc:Long) = make(numBlk, (i:Long)=>RandTool.nextLong(numPlc));
    /**
     * Make random block distribution. Note, every place must have at least one block.
     */
    public static def makeRandom(numBlk:Long, numPlc:Long) {
        val bmap = new Rail[Long](numBlk);
        val plst = new ArrayList[Pair[Long,Long]](numPlc);
        var i:Long = 0;
        for (; i<numPlc; i++) 
            plst(i) = Pair(i,RandTool.nextLong(numPlc * 10));
        plst.sort((x:Pair[Long,Long], y:Pair[Long,Long])=>(x.second-y.second) as Int);
        
        i=0;
        for (; i<numPlc; i++) bmap(i) = plst(i).first;
        for (; i<numBlk; i++) bmap(i) = RandTool.nextLong(numPlc);
        
        return new DistMap(bmap, numPlc);
        //make(numBlk, (i:Long)=>RandTool.nextLong(numPlc));
    }

    /**
     * Add block ID and place ID in mapping
     */
     public def set(blkID:Long, plcID:Long) {
         blockmap(blkID)=plcID;
     }
     
     /**
      * Find place ID for a given block ID
      */
     @Inline
     public def findPlace(blkID:Long):Long = this.blockmap(blkID);
    
     /**
      * Get block ID set mapped to the same place
      */
     public def buildBlockListAtPlace(plcID:Long):ArrayList[Long] {
         val blst = new ArrayList[Long]();
         for (var b:Long=0; b<blockmap.size; b++)
             if (blockmap(b) == plcID)
                 blst.add(b);
         blst.sort((a:Long,b:Long)=>(a-b) as Int);
         return blst;
     }
     
     /**
      * Return iterator on blocks within the specified place
      */
     public def buildBlockIteratorAtPlace(plcID:Long) : Iterator[Long] {
         val blst = buildBlockListAtPlace(plcID);
         return blst.iterator();
     }
     
     // public def transEquals(g:Grid, tmap:DistMap):Boolean {
     //     var retval:Boolean = true;
     //     val tg = g.newT();
     //     for (var rb:Long=0; rb<g.numRowBlocks&&retval; rb++) {
     //         for (var cb:Long=0; cb<g.numColBlocks&&retval; cb++) {
     //             val bid = g.getBlockId(rb, cb);
     //             val tbid = tg.getBlockId(cb,rb);
     //             retval &= (blockmap(bid)==tmap.blockmap(tbid));
     //         }
     //     }
     //     return retval;
     // }
     
     public def equals(that:DistMap) : Boolean {
         var retval:Boolean = true;
         
         if (this==that) return true;
         if (this.numBlock!=that.numBlock) return false;

         for (var i:Long=0; i<blockmap.size && retval; i++) {
             retval &= this.blockmap(i)==that.blockmap(i);
         }
         return retval;
     }
     
     public def toString():String {
         var mapstr:StringBuilder=new StringBuilder();
         mapstr.add(numBlock.toString()+" blocks"+ numPlace.toString()+"\n[");
         for (var i:Long=0; i<blockmap.size; i++)
             mapstr.add (" b"+i+":"+blockmap(i)+",");
         mapstr.add("]");
         return mapstr.toString();
     }
}
