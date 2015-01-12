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

import x10.regionarray.Dist;
import x10.util.Timer;
import x10.util.StringBuilder;

import x10.matrix.Matrix;
import x10.matrix.util.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.comm.ArrayBcast;
import x10.matrix.comm.ArrayReduce;

import x10.util.resilient.DistObjectSnapshot;
import x10.util.resilient.Snapshottable;
import x10.util.resilient.VectorSnapshotInfo;

public type DupVector(m:Long)=DupVector{self.M==m};
public type DupVector(v:DupVector)=DupVector{self==v};

public class DupVector(M:Long) implements Snapshottable {
    public var dupV:PlaceLocalHandle[Vector];
    private var dupData:PlaceLocalHandle[Rail[Double]];//Repackage vector.d
    public  var tmpData:PlaceLocalHandle[Rail[Double]];
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
        dupData = PlaceLocalHandle.make[Rail[Double]](places, ()=>vs().d);
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
        tmpData = PlaceLocalHandle.make[Rail[Double]](places, ()=>new Rail[Double](dupV().M));
    }

    public def init(dv:Double) : DupVector(this) {
        
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
    
    public def init(f:(Long)=>Double) : DupVector(this) {
        dupV().init(f);
        sync();
        return this;
    }
    
    public def copyTo(den:DenseMatrix):void {
        dupV().copyTo(den);
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
    
    public  operator this(x:Long):Double = dupV()(x);

    public operator this(x:Long)=(dv:Double):Double {
        finish ateach(Dist.makeUnique(places)) {
            //Remote capture: x, y, d
            dupV()(x) = dv;    
        }
        return dv;
    }
    
    public def local() = dupV() as Vector(M);

    /**
     * Scaling method. All copies are updated concurrently
     */
    public def scale(a:Double) {
        finish ateach(Dist.makeUnique(places)) {
            local().scale(a);
        }
        return this;
    }

    
    /**
     * Cellwise multiplication. 
     */
    public def cellAdd(A:Vector(M)) {
        val v = local();
        v.cellAdd(A as Vector(v.M));
        sync();
        return this;
    }

    /**
     * Concurrently perform cellwise addition on all copies.
     */
    public def cellAdd(that:DupVector(M))  {
        //Debug.assure(this.M==A.M&&this.N==A.N);
        finish ateach(Dist.makeUnique(places)) {
            val dstv = local() as Vector(M);
            val srcv = that.local() as Vector(M);
            dstv.cellAdd(srcv);
        }
        return this;
    }

    public def cellAdd(dv:Double)  {
        finish ateach(Dist.makeUnique(places)) {
            local().cellAdd(dv);
        }
        return this;
    }


    // Cellwise subtraction

    public def cellSub(A:Vector(M)) {
        val v = local() as Vector(M);
        v.cellSub(A);
        sync();
        return this;
    }
    /**
     * Concurrently perform cellwise subtraction on all copies
     */
    public def cellSub(A:DupVector(M)) {
        finish ateach(Dist.makeUnique(places)) {
            val dstv = local() as Vector(M);
            val srcv = A.local() as Vector(M);
            dstv.cellSub(srcv);
        }
        return this;
    }

    
    /**
     * Perform cell-wise subtraction  this = this - dv.
     */
    public def cellSub(dv:Double):DupVector(this) {
        finish ateach(Dist.makeUnique(places)) {
            local().cellSub(dv);
        }
        return this;
    }

    // Cellwise multiplication

    /**
     * Cellwise multiplication. 
     */
    public def cellMult(A:Vector(M)) {
        val dstv = local();
        dstv.cellMult(A);
        sync();
        return this;
    }
    
    /**
     * Cellwise multiplication. All copies are modified with
     * the corresponding vector copies.
     */
    public def cellMult(A:DupVector(M)) {
        finish ateach(Dist.makeUnique(places)) {
            local().cellMult(A.local());
        }
        return this;
    }


    // Cellwise division

    /**
     * this = this / A
     */
    public def cellDiv(A:Vector(M)) {
        local().cellDiv(A);
        sync();
        return this;
    }
    

    /**
     * Cellwise division. All copies are modified with
     * the corresponding vector copies.
     */    
    public def cellDiv(A:DupVector(M)) {
        finish ateach(Dist.makeUnique(places)) {
            local().cellDiv(A.local());
        }
        return this;
    }
    

    // Operator overloading cellwise operations


    public operator - this            = clone().scale(-1.0) as DupVector(M);
    public operator (v:Double) + this = clone().cellAdd(v)  as DupVector(M);
    public operator this + (v:Double) = clone().cellAdd(v)  as DupVector(M);
    public operator this - (v:Double) = clone().cellSub(v)  as DupVector(M);
    public operator this / (v:Double) = clone().scale(1.0/v)   as DupVector(M);
    
    public operator this * (alpha:Double) = clone().scale(alpha) as DupVector(M);
    public operator (alpha:Double) * this = this * alpha;
    
    public operator this + (that:DupVector(M)) = clone().cellAdd(that)  as DupVector(M);
    public operator this - (that:DupVector(M)) = clone().cellSub(that)  as DupVector(M);
    public operator this * (that:DupVector(M)) = clone().cellMult(that) as DupVector(M);
    public operator this / (that:DupVector(M)) = clone().cellDiv(that)  as DupVector(M);
         

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

    
    public def reduce(opFunc:(Rail[Double],Rail[Double],Long)=>Int): void {
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
    
    public def equals(dval:Double):Boolean {
        var ret:Boolean = true;
        for (var p:Long=0; p<places.size() &&ret; p++) {
            ret &= at(places(p)) this.local().equals(dval);
        }
        return ret;
    }

    public def getCalcTime() = calcTime;
    public def getCommTime() = commTime;
    public def getPlaces() = places;

    public def toString() :String {
        val output = new StringBuilder();
        output.add("---Duplicated Vector:["+M+"], local copy---\n");
        output.add(dupV().toString());
        output.add("--------------------------------------------------\n");
        return output.toString();
    }

    public def allToString() {
        val output = new StringBuilder();
        output.add("-------- Duplicate vector :["+M+"] ---------\n");
        for (p in places) {
            output.add("Copy at place " + p.id() +"\n");
            output.add(at (p) { dupV().toString()});
        }
        output.add("--------------------------------------------------\n");
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
        dupData = PlaceLocalHandle.make[Rail[Double]](newPg, ()=>dupV().d); 
        places = newPg;
    }
    
    /*
     * Snapshot mechanism
     */
    private transient val DUMMY_KEY:Long = 8888L;

    public def makeSnapshot():DistObjectSnapshot {
        val snapshot = DistObjectSnapshot.make();        
        val data = dupV();
        val placeIndex  = 0;
        snapshot.save(DUMMY_KEY, new VectorSnapshotInfo(placeIndex, data.d));
        return snapshot;
    }

    public def restoreSnapshot(snapshot:DistObjectSnapshot) {
        val dupSnapshotInfo:VectorSnapshotInfo = snapshot.load(DUMMY_KEY) as VectorSnapshotInfo;
        new Vector(dupSnapshotInfo.data).copyTo(dupV());
        sync();
    }
}
