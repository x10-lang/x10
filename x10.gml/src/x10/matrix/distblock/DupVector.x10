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

import x10.compiler.Inline;
import x10.regionarray.Dist;
import x10.util.Timer;
import x10.util.StringBuilder;

import x10.matrix.Vector;
import x10.matrix.ElemType;

import x10.matrix.util.Debug;

import x10.matrix.comm.ArrayBcast;
import x10.matrix.comm.ArrayReduce;

import x10.util.resilient.DistObjectSnapshot;
import x10.util.resilient.Snapshottable;
import x10.util.resilient.VectorSnapshotInfo;

public type DupVector(m:Long)=DupVector{self.M==m};
public type DupVector(v:DupVector)=DupVector{self==v};

public class DupVector(M:Long) implements Snapshottable {
    public var dupV:PlaceLocalHandle[Vector];
    private var dupData:PlaceLocalHandle[Rail[ElemType]];//Repackage vector.d
    public  var tmpData:PlaceLocalHandle[Rail[ElemType]];
    private transient var tmpReady:Boolean;
    /*
     * Time profiling
     */
    transient var commTime:Long = 0;
    transient var calcTime:Long = 0;
    
    /*The place group used for distribution*/
    private var places:PlaceGroup;

    public def this(vs:PlaceLocalHandle[Vector], pg:PlaceGroup) {
        val m = vs().M;
        property(m);
        dupV  = vs;
        tmpReady = false;
        places = pg;
        dupData = PlaceLocalHandle.make[Rail[ElemType]](places, ()=>vs().d);
    }

    public static def make(v:Vector, pg:PlaceGroup):DupVector(v.M){
        val m = v.M;
        val hdl = PlaceLocalHandle.make[Vector](pg, () => v);
        val newDV = new DupVector(hdl, pg);
        newDV.sync();
        return newDV as DupVector(m);
    }
    
    public static def make(v:Vector):DupVector(v.M) = make (v, Place.places());
    
    public static def make(m:Long, pg:PlaceGroup) {
        val hdl = PlaceLocalHandle.make[Vector](pg, ()=>Vector.make(m));
        return new DupVector(hdl, pg) as DupVector(m);
    }
    
    public static def make(m:Long) = make(m, Place.places());
    
    public def alloc(m:Long, pg:PlaceGroup):DupVector(m) = make(m, pg);
    public def alloc(pg:PlaceGroup) = alloc(M, pg);
    public def alloc(m:Long):DupVector(m) = make(m, Place.places());
    public def alloc() = alloc(M, Place.places());
    
    
    public def clone():DupVector(M) {
        val bs = PlaceLocalHandle.make[Vector](places, 
                ()=>dupV().clone());    
        return new DupVector(bs, places) as DupVector(M);
    }
    
    public def reset() {
        finish ateach(Dist.makeUnique(places)) {
            dupV().reset();
        }
    }

    public def allocTmp() : void {
        if (tmpReady) return;
        tmpReady = true;
        tmpData = PlaceLocalHandle.make[Rail[ElemType]](places, ()=>new Rail[ElemType](dupV().M));
    }

    public def init(dv:ElemType) : DupVector(this) {
        
        finish ateach(Dist.makeUnique(places)) {
            dupV().init(dv);
        }
        return this;
    }
    
    public def initRandom() : DupVector(this) {
        dupV().initRandom();
        sync();
        return this;
    }
    
    public def initRandom(lo:Int, up:Int) : DupVector(this) {
        dupV().initRandom(lo, up);
        sync();
        return this;
    }
    
    public def init(f:(Long)=>ElemType) : DupVector(this) {
        dupV().init(f);
        sync();
        return this;
    }

    public def copyTo(dst:DupVector(M)):void {
        finish ateach(Dist.makeUnique(places)) {
            dupV().copyTo(dst.dupV());
        }
    }

    public def copyFrom(vec:Vector(M)):void {
        vec.copyTo(local());
        sync();
    }
    
    public  operator this(x:Long):ElemType = dupV()(x);

    public operator this(x:Long)=(dv:ElemType):ElemType {
        finish ateach(Dist.makeUnique(places)) {
            //Remote capture: x, y, d
            dupV()(x) = dv;    
        }
        return dv;
    }
    
    public def local() = dupV() as Vector(M);

    /**
     * Scale all elements: this *= alpha
     * All copies are updated concurrently.
     */
    public def scale(alpha:ElemType)
        = map((x:ElemType)=>{alpha * x});

    /**
     * this = alpha * V
     */
    public def scale(alpha:Double, V:DupVector(M))
        = map(V, (v:Double)=> {alpha * v});
    
    /** 
     * Cellwise addition: this = this + V
     */
    public def cellAdd(V:Vector(M)) {
        val v = local();
        v.cellAdd(V as Vector(v.M));
        sync();
        return this;
    }

    /**
     * Cellwise addition: this = this + V
     * All copies are updated concurrently.
     */
    public def cellAdd(V:DupVector(M))
        = map(this, V, (x:Double, v:Double)=> {x + v});

    /**
     * Cellwise addition: this = this + d
     * All copies are updated concurrently.
     */
    public def cellAdd(d:ElemType)
        = map((x:ElemType)=> {x + d});

    /** 
     * Cellwise subtraction: this = this - V
     */
    public def cellSub(V:Vector(M)) {
        val v = local() as Vector(M);
        v.cellSub(V);
        sync();
        return this;
    }

    /**
     * Cellwise subtraction: this = this - V
     * All copies are updated concurrently.
     */
    public def cellSub(V:DupVector(M))
        = map(this, V, (x:Double, v:Double)=> {x - v});
   
    /**
     * Cellwise subtraction:  this = this - d
     * All copies are updated concurrently.
     */
    public def cellSub(d:ElemType)
        = map((x:ElemType)=> {x - d});

    /**
     * Cellwise multiplication. 
     */
    public def cellMult(V:Vector(M)) {
        val dstv = local();
        dstv.cellMult(V);
        sync();
        return this;
    }
    
    /**
     * Cellwise multiplication
     * All copies are updated concurrently.
     */
    public def cellMult(V:DupVector(M))
        = map(this, V, (x:Double, v:Double)=> {x * v});

    /**
     * Cellwise division: this = this / V
     */
    public def cellDiv(V:Vector(M)) {
        local().cellDiv(V);
        sync();
        return this;
    }

    /**
     * Cellwise division
     * All copies are updated concurrently.
     */
    public def cellDiv(V:DupVector(M))
        = map(this, V, (x:Double, v:Double)=> {x / v});
    

    // Operator overloading cellwise operations

    public operator - this            = clone().scale(-1.0 as ElemType) as DupVector(M);
    public operator (v:ElemType) + this = clone().cellAdd(v)  as DupVector(M);
    public operator this + (v:ElemType) = clone().cellAdd(v)  as DupVector(M);
    public operator this - (v:ElemType) = clone().cellSub(v)  as DupVector(M);
    public operator this / (v:ElemType) = clone().scale((1.0/v) as ElemType)   as DupVector(M);
    
    public operator this * (alpha:ElemType) = clone().scale(alpha) as DupVector(M);
    public operator (alpha:ElemType) * this = this * alpha;
    
    public operator this + (that:DupVector(M)) = clone().cellAdd(that)  as DupVector(M);
    public operator this - (that:DupVector(M)) = clone().cellSub(that)  as DupVector(M);
    public operator this * (that:DupVector(M)) = clone().cellMult(that) as DupVector(M);
    public operator this / (that:DupVector(M)) = clone().cellDiv(that)  as DupVector(M);

    /**
     * Dot (scalar) product of this vector with another DupVector
     */
    public def dot(v:DupVector(M)):Double {
        return local().dot(v.local());
    }

    /**
     * L2-norm (Euclidean norm) of this vector, i.e. the square root of the
     * sum of squares of all elements
     */
    public def norm():Double {
        return local().norm();
    }
         
    // Multiplication operations 

    public def mult(mA:DistBlockMatrix(this.M), vB:DistVector(mA.N), plus:Boolean):DupVector(this) =
        DistDupVectorMult.comp(mA, vB, this, plus);
    
    public def mult(vB:DistVector, mA:DistBlockMatrix(vB.M, this.M), plus:Boolean):DupVector(this) =
        DistDupVectorMult.comp(vB, mA, this, plus);
    
    public def mult(mA:DistBlockMatrix(this.M), vB:DupVector(mA.N), plus:Boolean):DupVector(this) =
        DistDupVectorMult.comp(mA, vB, this, plus);
    
    public def mult(vB:DupVector, mA:DistBlockMatrix(vB.M, this.M), plus:Boolean):DupVector(this) =
        DistDupVectorMult.comp(vB, mA, this, plus);

    public def mult(mA:DistBlockMatrix(this.M), vB:DistVector(mA.N)) = DistDupVectorMult.comp(mA, vB, this, false);
    public def mult(vB:DistVector, mA:DistBlockMatrix(vB.M, this.M)) = DistDupVectorMult.comp(vB, mA, this, false);
    public def mult(mA:DistBlockMatrix(this.M), vB:DupVector(mA.N))  = DistDupVectorMult.comp(mA, vB, this, false);
    public def mult(vB:DupVector, mA:DistBlockMatrix(vB.M, this.M))  = DistDupVectorMult.comp(vB, mA, this, false);
    
    //FIXME: review the correctness of using places here
    public operator this % (that:DistBlockMatrix(M)) = 
        DistDupVectorMult.comp(this, that, DistVector.make(that.N, that.getAggColBs(), places), true);
    //FIXME: review the correctness of using places here
    public operator (that:DistBlockMatrix{self.N==this.M}) % this = 
        DistDupVectorMult.comp(that , this, DistVector.make(that.M, that.getAggRowBs(), places), true);


    public def sync():void {
        /* Timing */ val st = Timer.milliTime();        
        ArrayBcast.bcast(dupData, places);
        /* Timing */ commTime += Timer.milliTime() - st;
    }

    public def reduce(opFunc:(Rail[ElemType],Rail[ElemType],Long)=>Int): void {
        allocTmp();
        /* Timing */ val st = Timer.milliTime();
        ArrayReduce.reduce(dupData, tmpData, this.M, places, opFunc);
        /* Timing */ commTime += Timer.milliTime() - st;
    }
    
    public def reduceSum(): void {
        allocTmp();
        /* Timing */ val st = Timer.milliTime();        
        ArrayReduce.reduceSum(dupData, tmpData, this.M, places);        
        /* Timing */ commTime += Timer.milliTime() - st;
    }
    
    public def allReduceSum(): void {
        allocTmp();
        /* Timing */ val st = Timer.milliTime();
        ArrayReduce.allReduceSum(dupData, tmpData, this.M, places);
        /* Timing */ commTime += Timer.milliTime() - st;
    }    

    public def likeMe(that:DupVector): Boolean = (this.M==that.M);
        
    public def checkSync():Boolean {
        val rootvec  = dupV();
        var retval:Boolean = true;
        for (var p:Long=0; p < places.size() && retval; p++) {
            if (p == here.id()) continue;
            retval &= at(places(p)) {
                    val vec = dupV();
                    rootvec.equals(vec as Vector(rootvec.M))
            };
        }
        return retval;
    }
    
    public def equals(dv:DupVector(this.M)):Boolean {
        var ret:Boolean = true;
        if (dv.getPlaces().equals(places)){
            for (var p:Long=0; p<places.size() &&ret; p++) {
                ret &= at(places(p)) this.local().equals(dv.local());
            }
        }
        else
            ret = false;
        return ret;
    }
    
    public def equals(dval:ElemType):Boolean {
        var ret:Boolean = true;
        for (var p:Long=0; p<places.size() &&ret; p++) {
            ret &= at(places(p)) this.local().equals(dval);
        }
        return ret;
    }

    /**
     * Apply the map function <code>op</code> to each element of this vector,
     * overwriting the element of this vector with the result.
     * @param op a unary map function to apply to each element of this vector
     * @return this vector, containing the result of the map
     */
    public final @Inline def map(op:(x:Double)=>Double):DupVector(this) {
        val stt = Timer.milliTime();
        finish ateach(Dist.makeUnique(places)) {
            val d = local();
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
    public final @Inline def map(a:DupVector(M), op:(x:Double)=>Double):DupVector(this) {
        assert(likeMe(a));
        val stt = Timer.milliTime();
        finish ateach(Dist.makeUnique(places)) {
            val d = local();
            val ad = a.local() as Vector(d.M);
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
    public final @Inline def map(a:DupVector(M), b:DupVector(M), op:(x:Double,y:Double)=>Double):DupVector(this) {
        assert(likeMe(a));
        val stt = Timer.milliTime();
        finish ateach(Dist.makeUnique(places)) {
            val d = local();
            val ad = a.local() as Vector(d.M);
            val bd = b.local() as Vector(d.M);
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
    public final @Inline def reduce(op:(a:Double,b:Double)=>Double, unit:Double) 
        = local().reduce(op, unit);

    public def getCalcTime() = calcTime;
    public def getCommTime() = commTime;
    public def getPlaces() = places;

    public def toString() :String {
        val output = new StringBuilder();
        output.add("---Duplicated Vector:["+M+"], local copy---\n");
        output.add(dupV().toString());
        return output.toString();
    }

    public def allToString() {
        val output = new StringBuilder();
        output.add("-------- Duplicate vector :["+M+"] ---------\n");
        for (p in places) {
            output.add("Copy at place " + p.id() +"\n");
            output.add(at (p) { dupV().toString()});
        }
        return output.toString();
    }
    
    /**
     * Remake the DupVector over a new PlaceGroup
     */
    public def remake(newPg:PlaceGroup){
        PlaceLocalHandle.destroy(places, dupV, (Place)=>true);    
        dupV = PlaceLocalHandle.make[Vector](newPg, ()=>Vector.make(M));    
        if (tmpReady){
            tmpReady = false;
            PlaceLocalHandle.destroy(places, tmpData, (Place)=>true);
        }
        PlaceLocalHandle.destroy(places, dupData, (Place)=>true);
        dupData = PlaceLocalHandle.make[Rail[ElemType]](newPg, ()=>dupV().d); 
        places = newPg;
    }
    
    /*
     * Snapshot mechanism
     */
    private transient val DUMMY_KEY:Long = 8888L;
    /**
     * Create a snapshot for the DupVector by storing the current place's vector 
     * @return a snapshot for the DupVector data stored in a resilient store
     */
    public def makeSnapshot():DistObjectSnapshot {
        val snapshot = DistObjectSnapshot.make();        
        val data = dupV();
        val placeIndex  = 0;
        snapshot.save(DUMMY_KEY, new VectorSnapshotInfo(placeIndex, data.d));
        return snapshot;
    }

    /**
     * Restore the DupVector data using the provided snapshot object 
     * @param snapshot  a snapshot to restore the data from
     */
    public def restoreSnapshot(snapshot:DistObjectSnapshot) {
        val dupSnapshotInfo:VectorSnapshotInfo = snapshot.load(DUMMY_KEY) as VectorSnapshotInfo;
        new Vector(dupSnapshotInfo.data).copyTo(dupV());
        sync();
    }
}
