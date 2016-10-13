/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

package x10.matrix.distblock;

import x10.util.ArrayList;
import x10.util.Pair;
import x10.util.StringBuilder;
import x10.compiler.Inline;

import x10.matrix.util.RandTool;

/**
 * This class represents a mapping from matrix blocks to indices in a PlaceGroup.
 * <p>
 * There are several mappings provided in this class, including cyclic, 
 * constant, unique and random distribution of blocks to places.
 */
public class DistMap(numBlock:Long, numPlace:Long)  {
    val blockmap:Rail[Long];            //mapping block ID to its place index in a Place Group
    
    def this(blkmap:Rail[Long], numPlc:Long) {
        property(blkmap.size as Long, numPlc);
        blockmap = blkmap;
    }

    public static def make(numBlk:Long, mapfunc:(Long)=>Long) = make(numBlk, mapfunc, Place.numPlaces());
    
    public static def make(numBlk:Long, mapfunc:(Long)=>Long, numPlc:Long) {
        return new DistMap(new Rail[Long](numBlk, mapfunc), numPlc);
    }
    
    public static def makeCyclic(numBlk:Long) = make(numBlk, (i:Long)=>i%Place.numPlaces());
    public static def makeCyclic(numBlk:Long, numPlc:Long) = make(numBlk, (i:Long)=>i%numPlc);
    public static def makeUnique() = make(Place.numPlaces(), (i:Long)=>i);
    public static def makeUnique(places:PlaceGroup) = make(places.size(), (i:Long)=>i);
    public static def makeUnique(numBlk:Long) = make(numBlk, (i:Long)=>i);
    
    public static def makeConstant(numBlk:Long) = make(numBlk, (i:Long)=>0L);
    public static def makeConstant(numBlk:Long, p:Long) = make(numBlk, (i:Long)=>p); 

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
    }

    /**
     * Add block ID and place Index in mapping
     */
     public def set(blkID:Long, plcIndex:Long) {
         blockmap(blkID)=plcIndex;
     }
     
     /**
      * Find place ID for a given block ID
      */
     @Inline
     public def findPlace(blkID:Long):Long = Place.places()(this.blockmap(blkID)).id;

     @Inline
     public def findPlace(blkID:Long, places:PlaceGroup):Long = places(this.blockmap(blkID)).id;

     public def findPlaceIndex(blkID:Long):Long = this.blockmap(blkID);
    
     /**
      * Get block ID set mapped to the same place
      */
     public def buildBlockListAtPlace(plcIndex:Long):ArrayList[Long] {
         val blst = new ArrayList[Long]();
         for (var b:Long=0; b<blockmap.size; b++)
             if (blockmap(b) == plcIndex)
                 blst.add(b);
         blst.sort((a:Long,b:Long)=>(a-b) as Int);
         return blst;
     }
     
     /**
      * Return iterator on blocks within the specified place
      * Used by BlockSet
      */
     public def buildBlockIteratorAtPlace(plcIndex:Long) : Iterator[Long] {
         val blst = buildBlockListAtPlace(plcIndex);
         return blst.iterator();
     }
     
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
         mapstr.add(numBlock.toString()+" blocks at "+ numPlace.toString()+" places\n[");
         for (var i:Long=0; i<blockmap.size; i++)
             mapstr.add (" b"+i+":"+blockmap(i)+",");
         mapstr.add("]");
         return mapstr.toString();
     }
}
