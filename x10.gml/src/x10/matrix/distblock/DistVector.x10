/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.matrix.distblock;

import x10.util.Timer;
import x10.compiler.Inline;
import x10.regionarray.Dist;
import x10.util.Pair;
import x10.util.StringBuilder;

import x10.matrix.Matrix;
import x10.matrix.util.Debug;
import x10.matrix.ElemType;
import x10.matrix.Vector;

import x10.matrix.util.MathTool;
import x10.matrix.block.Grid;
import x10.matrix.block.BlockMatrix;
import x10.matrix.comm.DataArrayPLH;
import x10.matrix.comm.ArrayGather;
import x10.matrix.comm.ArrayScatter;
import x10.util.resilient.DistObjectSnapshot;
import x10.util.resilient.Snapshottable;
import x10.util.resilient.VectorSnapshotInfo;

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

    public def places() = places;
    
    //oldSegSize used only for remake and restore
    private var snapshotSegSize:Rail[Long];

    public def this(m:Long, vs:PlaceLocalHandle[Vector], segsz:Rail[Long], pg:PlaceGroup) {
        property(m);
        assert (segsz.size == pg.size()) :
            "number of vector segments must be equal to number of places";
        distV  = vs;
        segSize = segsz;
        places = pg;
        distData = PlaceLocalHandle.make[Rail[ElemType]](places, ()=>vs().d);
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

    public def init(dv:ElemType) : DistVector(this) {
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

    /**
     * Initialize this vector with random 
     * values in the specified range.
     * @param min lower bound of random values
     * @param max upper bound of random values
     */ 
    public def initRandom(min:Long, max:Long):DistVector(this) {
        finish ateach(Dist.makeUnique(places)) {
            distV().initRandom(min, max);
        }
        return this;
    }
    
    public def init(f:(Long)=>ElemType):DistVector(this) {
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
        throw new UnsupportedOperationException("Error in searching index in vector");
    }
    
    protected def find(var pos:Long):Pair[Long, Long] {
        assert (pos < M) : "Vector data access out of bounds";
        return find(pos, segSize);
    }
    
    public  operator this(x:Long):ElemType {
        val loc = find(x);
        val seg = loc.first as Int;
        val off = loc.second;
        val dat = at(places(seg)) distV()(off);
        return dat;
    }

    public operator this(x:Long)=(dv:ElemType):ElemType {
        val loc = find(x);
        val seg = loc.first as Int;
        val off = loc.second;
        at(places(seg)) distV()(off)=dv;
        return dv;
    }

    /**
     * Scaling method. All copies are updated concurrently
     */
    public def scale(a:ElemType) {
        finish ateach(Dist.makeUnique(places)) {
            distV().scale(a);
        }
        return this;
    }

    /**
     * Concurrently perform cellwise addition on all copies.
     */
    public def cellAdd(that:DistVector(M))  {
        //assert (this.M==A.M&&this.N==A.N);
        finish ateach(Dist.makeUnique(places)) {
            val dst = distV();
            val src = that.distV() as Vector(dst.M);
            dst.cellAdd(src);
        }
        return this;
    }

    public def cellAdd(dv:ElemType)  {
        finish ateach(Dist.makeUnique(places)) {
            distV().cellAdd(dv);
        }
        return this;
    }

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
    public def cellSub(dv:ElemType):DistVector(this) {
        finish ateach(Dist.makeUnique(places)) {
            distV().cellSub(dv);
        }
        return this;
    }

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

    public operator - this            = clone().scale(-1.0 as ElemType) as DistVector(M);
    public operator (v:ElemType) + this = clone().cellAdd(v)  as DistVector(M);
    public operator this + (v:ElemType) = clone().cellAdd(v)  as DistVector(M);
    public operator this - (v:ElemType) = clone().cellSub(v)  as DistVector(M);
    public operator this / (v:ElemType) = clone().scale((1.0/v) as ElemType)   as DistVector(M);
    
    public operator this * (alpha:ElemType) = clone().scale(alpha) as DistVector(M);
    public operator (alpha:ElemType) * this = this * alpha;
    
    public operator this + (that:DistVector(M)) = clone().cellAdd(that)  as DistVector(M);
    public operator this - (that:DistVector(M)) = clone().cellSub(that)  as DistVector(M);
    public operator this * (that:DistVector(M)) = clone().cellMult(that) as DistVector(M);
    public operator this / (that:DistVector(M)) = clone().cellDiv(that)  as DistVector(M);

    public def dot(v:DupVector(M)):Double {
        val dot = finish(Reducible.SumReducer[Double]()) {
            var off:Long=0;
            for (p in 0..(places.size()-1)) {
                val offset = off;
                val s = segSize(p);
                at(places(p)) async {
                    val dist = distV();
                    val dup = v.local();
                    var d:Double = 0.0;
                    for (i in 0..(s-1))
                        d += dist(i) * dup(offset+i);
                    offer d;
                }
                off += s;
            }
        };
        return dot;
    }
        

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

    /** Get the sum of all elements in this vector. */
    public def sum():ElemType = reduce((a:ElemType,b:ElemType)=>{a+b}, 0.0 as ElemType);
    
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
    
    public def equals(dval:ElemType):Boolean {
        var ret:Boolean = true;
        for (var p:Long=0; p<places.size() &&ret; p++) {
            val pindx = p;
            ret &= at(places(pindx)) distV().equals(dval);
        }
        return ret;
    }

    /**
     * Apply the map function <code>op</code> to each element of this vector,
     * overwriting the element of this vector with the result.
     * @param op a unary map function to apply to each element of this vector
     * @return this vector, containing the result of the map
     */
    public final @Inline def map(op:(x:ElemType)=>ElemType):DistVector(this) {
        val stt = Timer.milliTime();
        finish ateach(Dist.makeUnique(places)) {
            val d = distV();
            d.map(op);
        }
        calcTime += Timer.milliTime() - stt;
        return this;
    }

    /**
     * Apply the map function <code>op</code> to each element of <code>a</code>,
     * storing the result in the corresponding element of this vector.
     * @param a a vector of the same distribution as this vector
     * @param op a unary map function to apply to each element of vector <code>a</code>
     * @return this vector, containing the result of the map
     */
    public final @Inline def map(a:DistVector(M), op:(x:ElemType)=>ElemType):DistVector(this) {
        assert(likeMe(a));
        val stt = Timer.milliTime();
        finish ateach(Dist.makeUnique(places)) {
            val d = distV();
            val ad = a.distV() as Vector(d.M);
            d.map(ad, op);
        }
        calcTime += Timer.milliTime() - stt;
        return this;
    }

    /**
     * Apply the map function <code>op</code> to combine each element of vector
     * <code>a</code> with the corresponding element of vector <code>b</code>,
     * overwriting the corresponding element of this vector with the result.
     * @param a first vector of the same distribution as this vector
     * @param b second vector of the same distribution as this vector
     * @param op a binary map function to apply to each element of 
     *   <code>a</code> and the corresponding element of <code>b</code>
     * @return this vector, containing the result of the map
     */
    public final @Inline def map(a:DistVector(M), b:DistVector(M), op:(x:ElemType,y:ElemType)=>ElemType):DistVector(this) {
        assert(likeMe(a));
        val stt = Timer.milliTime();
        finish ateach(Dist.makeUnique(places)) {
            val d = distV();
            val ad = a.distV() as Vector(d.M);
            val bd = b.distV() as Vector(d.M);
            d.map(ad, bd, op);
        }
        calcTime += Timer.milliTime() - stt;
        return this;
    }

    /**
     * Combine the elements of this vector using the provided reducer function.
     * @param op a binary reducer function to combine elements of this vector
     * @param unit the identity value for the reduction function
     * @return the result of the reducer function applied to all elements
     */
    public final @Inline def reduce(op:(a:ElemType,b:ElemType)=>ElemType, unit:ElemType):ElemType {
        class Reducer implements Reducible[ElemType] {
            public def zero() = unit;
            public operator this(a:ElemType, b:ElemType) = op(a,b); 
        }
        val stt = Timer.milliTime();
        val reducer = new Reducer();
        val result = finish (reducer) {
            ateach(Dist.makeUnique(places)) {
                val d = distV();
                offer d.reduce(op, unit);
            }
        };
        calcTime += Timer.milliTime() - stt;
        return result;
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

    public def allToString() {
        val output = new StringBuilder();
        output.add( "-------- Distributed vector :["+M+"] ---------\n");
        for (p in places) {
            output.add("Segment vector at place " + p.id() +"\n");
            output.add(at (p) { distV().toString()});
        }
        output.add("--------------------------------------------------\n");
        return output.toString();
    }
    
    /*
     * Snapshot mechanism
     */
    /**
     * Remake the DistVector over a new PlaceGroup
     */
    public def remake(segsz:Rail[Long], newPg:PlaceGroup){        
        assert (segsz.size == newPg.size()) :
            "number of vector segments must be equal to number of places";
        PlaceLocalHandle.destroy(places, distV, (Place)=>true);
        distV = PlaceLocalHandle.make[Vector](newPg, ()=>Vector.make(segsz(newPg.indexOf(here))));        
        segSize = segsz;
        PlaceLocalHandle.destroy(places, distData, (Place)=>true);
        distData = PlaceLocalHandle.make[Rail[ElemType]](newPg, ()=>distV().d);
        places = newPg;
    }
    
    /**
     * Remake the DistVector over a new PlaceGroup
     */
    public def remake(newPg:PlaceGroup){
        val m = M;        
        val segNum = newPg.size;
        val slst = new Rail[Long](segNum, (i:Long)=>Grid.compBlockSize(m, segNum, i as Int));
        remake (slst, newPg);
    }

    /**
     * Create a snapshot for the DistVector data 
     * @return a snapshot for the DistVector data stored in a resilient store
     */
    public def makeSnapshot():DistObjectSnapshot {
        //val startTime = Timer.milliTime();
        val snapshot = DistObjectSnapshot.make();
        finish ateach(pl in Dist.makeUnique(places)){
            val i = places.indexOf(here);
            val data = distV();
            //the segSize should only be saved only at place 0
            val distVecInfo = new VectorSnapshotInfo(i, data.d);
            snapshot.save(i, distVecInfo);
        }
        snapshotSegSize = segSize;
        //Console.OUT.println("DistVector.SnapshotTime["+(Timer.milliTime() - startTime)+"]");
        return snapshot;
    }
    
    /**
     * Restore the DistVector data using the provided snapshot object 
     * @param snapshot a snapshot from which to restore the data
     */
    public def restoreSnapshot(snapshot:DistObjectSnapshot) {
        //val startTime = Timer.milliTime();
        val currentSegSizes = segSize;

        assert (snapshotSegSize != null && currentSegSizes != null) : "Invalid segments rails";
        
        var segmentsChanged:Boolean = false;
        if (snapshotSegSize.size == currentSegSizes.size) {
            for (var i:Long = 0; i < snapshotSegSize.size; i++) {
                if (snapshotSegSize(i) != currentSegSizes(i)) {
                    segmentsChanged = true;
                    break;
                }
            }
        }
        else
            segmentsChanged = true;

        if (!segmentsChanged)
            restoreSnapshotSegmentBySegment(snapshot);
        else
            restoreSnapshotElementByElement(snapshot);
        //Console.OUT.println("DistVector.RestoreTime["+(Timer.milliTime() - startTime)+"]");
    }
    
    private def restoreSnapshotSegmentBySegment(snapshot:DistObjectSnapshot) {
        //val startTime = Timer.milliTime();
        finish ateach(Dist.makeUnique(places)) {
            //segments should be stored in places with the same order 
            //segment place index will remain the same
            val segmentPlaceIndex = places.indexOf(here);
            val storedSegment = snapshot.load(segmentPlaceIndex) as VectorSnapshotInfo;
            val srcRail = storedSegment.data;
            val dstRail = distV().d;
            Rail.copy(srcRail, 0, dstRail, 0, srcRail.size);
        }        
        //Console.OUT.println("DistVector.RestoreTimeSegmentBySegment["+(Timer.milliTime() - startTime)+"]");
    }
    
    private def restoreSnapshotElementByElement(snapshot:DistObjectSnapshot) {
        //val startTime = Timer.milliTime();
        val segmentSizes = snapshotSegSize;
        Console.OUT.println("OldSegments = " + segmentSizes.toString());
        val newSegmentsOffsets = new Rail[Long](places.size());
        newSegmentsOffsets(0) = 0;
        for (var i:Long = 1; i < places.size(); i++){
            for (var j:Long = 0; j < i; j++){
                newSegmentsOffsets(i) += segSize(j);
            }
        }
        Console.OUT.println("NewSegments = " + segSize.toString());
        Console.OUT.println("NewSegmentsOffset = " + newSegmentsOffsets.toString());
        
        val cached = PlaceLocalHandle.make[Cell[VectorSnapshotInfo]](places, ()=>new Cell[VectorSnapshotInfo](null));    
        val initFunc = (i:Long)=>{
            val myPlaceSegmentOffset = newSegmentsOffsets(places.indexOf(here));
            val loc = find(i+myPlaceSegmentOffset, segmentSizes);

            val loadPlaceIndex = loc.first;
            val offset = loc.second;
            var cashedObj:VectorSnapshotInfo = cached()();            
            if ( (cashedObj==null) || (cashedObj.placeIndex!=loadPlaceIndex))
                cashedObj = snapshot.load(loadPlaceIndex) as VectorSnapshotInfo;
            val data =cashedObj.data;
            return data(offset);
        };
        init(initFunc);
        PlaceLocalHandle.destroy(places, cached, (Place)=>true);      
        //Console.OUT.println("DistVector.RestoreTimeElementByElement["+(Timer.milliTime() - startTime)+"]");
    }
}
