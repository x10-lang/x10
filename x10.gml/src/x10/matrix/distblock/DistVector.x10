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

import x10.regionarray.Dist;
import x10.util.Pair;
import x10.util.StringBuilder;

import x10.matrix.Matrix;
import x10.matrix.util.Debug;
import x10.matrix.util.MathTool;
import x10.matrix.Vector;
import x10.matrix.block.Grid;
import x10.matrix.block.BlockMatrix;
import x10.matrix.comm.DataArrayPLH;
import x10.matrix.comm.ArrayGather;
import x10.matrix.comm.ArrayScatter;
import x10.util.resilient.DistObjectSnapshot;
import x10.util.resilient.Snapshottable;

public type DistVector(m:Long)=DistVector{self.M==m};
public type DistVector(v:DistVector)=DistVector{self==v};

public class DistVector(M:Long) implements Snapshottable {
    public var distV:PlaceLocalHandle[Vector];
    //Repack dist vector to dist array
    public var distData:DataArrayPLH;
    public transient var segSize:Rail[Long]; //this should be the same size as the place group
    /*
     * Time profiling
     */
    transient var commTime:Long = 0;
    transient var calcTime:Long = 0;
    
    /*The place group used for distribution*/
    private var places:PlaceGroup;
    
    public def this(m:Long, vs:PlaceLocalHandle[Vector], segsz:Rail[Long], pg:PlaceGroup) {
        property(m);
        Debug.assure(segsz.size == pg.size(),
            "number of vector segments must be equal to number of places");
        distV  = vs;
        segSize = segsz;
        places = pg;
        distData = PlaceLocalHandle.make[Rail[Double]](places, ()=>vs().d);
    }

    public static def make(m:Long, segNum:Long, pg:PlaceGroup):DistVector(m) {
        val hdv = PlaceLocalHandle.make[Vector](pg,
                            ()=>Vector.make(Grid.compBlockSize(m, segNum, pg.indexOf(here))));
        val slst = new Rail[Long](segNum, (i:Long)=>Grid.compBlockSize(m, segNum, i as Int));
        return new DistVector(m, hdv, slst, pg) as DistVector(m);
    }
    
    public static def make(m:Long, segNum:Long):DistVector(m) = make (m, segNum, Place.places()); 
    
    public static def make(m:Long, pg:PlaceGroup) = make (m, pg.size(), pg);
    
    public static def make(m:Long) = make (m, Place.places());

    public static def make(m:Long, segsz:Rail[Long], pg:PlaceGroup):DistVector(m) {
        val hdv = PlaceLocalHandle.make[Vector](pg, ()=>Vector.make(segsz(pg.indexOf(here))));
        return new DistVector(m, hdv, segsz, pg) as DistVector(m);
    }
    
    public static def make(m:Long, segsz:Rail[Long]):DistVector(m) = make (m, segsz, Place.places());

    public def alloc(m:Long, pg:PlaceGroup):DistVector(m) = make(m, pg);
    public def alloc(pg:PlaceGroup) = alloc(M, pg);
    
    public def alloc(m:Long):DistVector(m) = make(m, Place.places());
    public def alloc() = alloc(M, Place.places());
    
    
    public def clone():DistVector(M) {
        val dv = PlaceLocalHandle.make[Vector](places, 
                ()=>distV().clone());    
        return new DistVector(M, dv, segSize, places) as DistVector(M);
    }
    
    public def reset() {
        finish ateach(Dist.makeUnique(places)) {
            distV().reset();
        }
    }

    public def init(dv:Double) : DistVector(this) {
        finish ateach(Dist.makeUnique(places)) {
            distV().init(dv);
        }
        return this;
    }
    
    public def initRandom() : DistVector(this) {
        finish ateach(Dist.makeUnique(places)) {
            distV().initRandom();
        }
        return this;
    }
    
    public def initRandom(lo:Int, up:Int) : DistVector(this) {
        finish ateach(Dist.makeUnique(places)) {
            distV().initRandom(lo, up);
        }
        return this;
    }
    
    public def init(f:(Long)=>Double) : DistVector(this) {
        finish ateach(Dist.makeUnique(places)) {
            distV().init(f);
        }
        return this;
    }
    
    public def copyTo(dst:DistVector(M)):void {
        finish ateach(Dist.makeUnique(places)) {
            distV().copyTo(dst.distV());
        }
    }

    public def copyTo(vec:Vector(M)):void {
        ArrayGather.gather(distData, vec.d, segSize, places);
    }

    public def copyFrom(vec:Vector(M)): void {
        ArrayScatter.scatter(vec.d, distData, segSize, places);
    }

    /**
     * For debug and verification use only. 
     * This method is not efficient for actual computation.
     * Use copyTo(...) method instead.
     */
    public def toVector():Vector(M) {
        val nv = Vector.make(M);
        for (var i:Long=0; i<M; i++) nv(i) = this(i);
        return nv;
    }

    private def find(var pos:Long, segments:Rail[Long]):Pair[Long, Long] {        
        for (var i:Long=0; i<segments.size; i++) {
            if (pos < segments(i))
                return new Pair[Long,Long](i, pos);
            pos -= segments(i);
        }
        Debug.exit("Error in searching index in vector");
        return new Pair[Long,Long](-1, -1);
    }
    
    protected def find(var pos:Long):Pair[Long, Long] {
        Debug.assure(pos<M, "Vector data access out of bound");
        return find(pos, segSize);
    }
    
    public  operator this(x:Long):Double {
        val loc = find(x);
        val seg = loc.first as Int;
        val off = loc.second;
        val dat = at(places(seg)) distV()(off);
        return dat;
    }

    public operator this(x:Long)=(dv:Double):Double {
        val loc = find(x);
        val seg = loc.first as Int;
        val off = loc.second;
        at(places(seg)) distV()(off)=dv;
        return dv;
    }

    /**
     * Scaling method. All copies are updated concurrently
     */
    public def scale(a:Double) {
        finish ateach(Dist.makeUnique(places)) {
            distV().scale(a);
        }
        return this;
    }

    /**
     * Concurrently perform cellwise addition on all copies.
     */
    public def cellAdd(that:DistVector(M))  {
        //Debug.assure(this.M==A.M&&this.N==A.N);
        finish ateach(Dist.makeUnique(places)) {
            val dst = distV();
            val src = that.distV() as Vector(dst.M);
            dst.cellAdd(src);
        }
        return this;
    }

    public def cellAdd(dv:Double)  {
        finish ateach(Dist.makeUnique(places)) {
            distV().cellAdd(dv);
        }
        return this;
    }

    // Cellwise subtraction


    /**
     * Concurrently perform cellwise subtraction on all copies
     */
    public def cellSub(A:DistVector(M)) {
        finish ateach(Dist.makeUnique(places)) {
            val dst = distV();
            val src = A.distV() as Vector(dst.M);
            dst.cellSub(src);
        }
        return this;
    }

    
    /**
     * Perform cell-wise subtraction  this = this - dv.
     */
    public def cellSub(dv:Double):DistVector(this) {
        finish ateach(Dist.makeUnique(places)) {
            distV().cellSub(dv);
        }
        return this;
    }
    
    /**
     * this = dv - this
     */
    protected def cellSubFrom(dv:Double):DistVector(this) {
        finish ateach(Dist.makeUnique(places)) {
            distV().cellSubFrom(dv);
        }
        return this;
    }


    // Cellwise multiplication

    
    /**
     * Cellwise multiplication. All copies are modified with
     * the corresponding vector copies.
     */
    public def cellMult(A:DistVector(M)) {
        finish ateach(Dist.makeUnique(places)) {
            val dst = this.distV();
            val src = A.distV() as Vector(dst.M);
            dst.cellMult(src);
        }
        return this;
    }


    // Cellwise division

    /**
     * Cellwise division. All copies are modified with
     * the corresponding vector copies.
     */    
    public def cellDiv(A:DistVector(M)) {
        finish ateach(Dist.makeUnique(places)) {
            val dst = this.distV();
            val src = A.distV() as Vector(dst.M);
            dst.cellDiv(src);
        }
        return this;
    }
    

    // Operator overloading cellwise operations

    public operator - this            = clone().scale(-1.0) as DistVector(M);
    public operator (v:Double) + this = clone().cellAdd(v)  as DistVector(M);
    public operator this + (v:Double) = clone().cellAdd(v)  as DistVector(M);
    public operator this - (v:Double) = clone().cellSub(v)  as DistVector(M);
    public operator (v:Double) - this = clone().cellSubFrom(v) as DistVector(M);
    public operator this / (v:Double) = clone().scale(1.0/v)   as DistVector(M);
    
    public operator this * (alpha:Double) = clone().scale(alpha) as DistVector(M);
    public operator (alpha:Double) * this = this * alpha;
    
    public operator this + (that:DistVector(M)) = clone().cellAdd(that)  as DistVector(M);
    public operator this - (that:DistVector(M)) = clone().cellSub(that)  as DistVector(M);
    public operator this * (that:DistVector(M)) = clone().cellMult(that) as DistVector(M);
    public operator this / (that:DistVector(M)) = clone().cellDiv(that)  as DistVector(M);
        

    // Multiplication operations 

    public def mult(mA:DistBlockMatrix(M), vB:DupVector(mA.N), plus:Boolean):DistVector(this) =
        DistDupVectorMult.comp(mA, vB, this, plus);
    
    public def mult(vB:DupVector, mA:DistBlockMatrix(vB.M, this.M), plus:Boolean):DistVector(this) =
        DistDupVectorMult.comp(vB, mA, this, plus);

    public def mult(mA:DistBlockMatrix(M), vB:DupVector(mA.N))      = DistDupVectorMult.comp(mA, vB, this, false);
    public def mult(vB:DupVector, mA:DistBlockMatrix(vB.M, this.M)) = DistDupVectorMult.comp(vB, mA, this, false);

    //FIXME: review the correctness of using places here
    public operator this % (that:DistBlockMatrix(this.M)) = 
         DistDupVectorMult.comp(this, that, DupVector.make(that.N, places), false);
    //FIXME: review the correctness of using places here
    public operator (that:DistBlockMatrix{self.N==this.M}) % this = 
        DistDupVectorMult.comp(that, this, DupVector.make(that.M, places), false);
    
    public def likeMe(that:DistVector): Boolean  {
        if (this.M!=that.M) return false;
        for (var i:Long=0; i<segSize.size; i++)
            if (this.segSize(i) != that.segSize(i)) return false;
        return true;
    }
        
    public def equals(dv:DistVector(this.M)):Boolean {
        var ret:Boolean = true;
        if (dv.getPlaces().equals(places)){
            for (var p:Long=0; p<places.size() &&ret; p++) {
                val pindx = p;
                ret &= at(places(pindx)) {
                    val srcv = distV();
                    val tgtv = dv.distV() as Vector(srcv.M);
                    srcv.equals(tgtv)
                };
            }
        }
        else
            ret = false;
        return ret;
    }
    public def equals(that:Vector(this.M)):Boolean {
        var ret:Boolean = true;
        var i:Long=0;
        for (; i<M&&ret; i++) ret &= MathTool.isZero(this(i)-that(i));
        if (!ret) {
            Debug.flushln("Diff found at index:"+i+" value: "+this(i)+" <> "+that(i));
        }
        return ret;
    }
    
    public def equals(dval:Double):Boolean {
        var ret:Boolean = true;
        for (var p:Long=0; p<places.size() &&ret; p++) {
            val pindx = p;
            ret &= at(places(pindx)) distV().equals(dval);
        }
        return ret;
    }

    public def getCalcTime() = calcTime;
    public def getCommTime() = commTime;
    public def getPlaces() = places;
    
    public def toString() :String {
        val output=new StringBuilder();
        output.add("---Distributed Vector:["+M+"], ---\n[ ");
        for (var i:Long=0; i<M-1; i++) output.add(this(i).toString()+",");
        
        output.add(this(M-1).toString()+" ]\n--------------------------------------------------\n");
        return output.toString();
    }

    public def printAllCopies() {
        val output = new StringBuilder();
        output.add( "-------- Distributed vector :["+M+"] ---------\n");
        for (p in places) {
            output.add("Segment vector at place " + p.id() +"\n");
            output.add(at (p) { distV().toString()});
        }
        output.add("--------------------------------------------------\n");
        Console.OUT.print(output.toString());
        Console.OUT.flush();
    }
    
    /*
     * Snapshot mechanism
     */
    /**
     * Remake the DistBlockMatrix over a new PlaceGroup
     */
    public def remake(segsz:Rail[Long], newPg:PlaceGroup){        
        Debug.assure(segsz.size == newPg.size(), "number of vector segments must be equal to number of places");
        PlaceLocalHandle.destroy(places, distV, (Place)=>true);
        distV = PlaceLocalHandle.make[Vector](newPg, ()=>Vector.make(segsz(newPg.indexOf(here))));
        segSize = segsz;        
        PlaceLocalHandle.destroy(places, distData, (Place)=>true);
        distData = PlaceLocalHandle.make[Rail[Double]](newPg, ()=>distV().d);
        places = newPg;
    }
    
    public def remake(newPg:PlaceGroup){
        val m = M;
        val segNum = newPg.size;
        val slst = new Rail[Long](segNum, (i:Long)=>Grid.compBlockSize(m, segNum, i as Int));
        remake (slst, newPg);
    }
    
    static class DistVectorSnapshotInfo (placeIndex:Long,v:Vector, segsz:Rail[Long]) {}
    public def makeSnapshot():DistObjectSnapshot[Any,Any]{        
        val snapshot:DistObjectSnapshot[Any, Any] = DistObjectSnapshot.make[Any,Any]();              
        val segments = segSize;
        finish for (pl in places) {
            at (pl) async {
                val i = places.indexOf(here);
                val data = distV();
                //the segSize should only be saved only at place 0
                val distVecInfo:DistVectorSnapshotInfo;
                if (i == 0)
                    distVecInfo = new DistVectorSnapshotInfo(i, data, segments);
                else
                    distVecInfo = new DistVectorSnapshotInfo(i, data, null);
        
                snapshot.save(i, distVecInfo);
            }
        }        
        return snapshot;
    }
    
    public def restoreSnapshot(snapshot:DistObjectSnapshot[Any,Any]) {        
        val savedP0Info = snapshot.load(0) as DistVectorSnapshotInfo; //loading the snapshot at place 0
        val segmentSizes = savedP0Info.segsz;        
        val cached = PlaceLocalHandle.make[Cell[DistVectorSnapshotInfo]](places, ()=>new Cell[DistVectorSnapshotInfo](null));    
        val initFunc = (i:Long)=>{            
            val loc = find(i, segmentSizes);    
            val loadPlaceIndex = loc.first;
            val offset = loc.second;           
        
            var cashedObj:DistVectorSnapshotInfo = cached()();            
            if ( (cashedObj==null) || (cashedObj.placeIndex!=loadPlaceIndex))
                cashedObj = snapshot.load(loadPlaceIndex) as DistVectorSnapshotInfo;
        
            val vec =cashedObj.v;
        
            return vec(offset);            
        };  
        
        init(initFunc);
        PlaceLocalHandle.destroy(places, cached, (Place)=>true);        
    }
}