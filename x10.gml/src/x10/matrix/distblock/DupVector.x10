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

import x10.compiler.Inline;
import x10.regionarray.Dist;
import x10.util.Timer;
import x10.util.StringBuilder;

import x10.matrix.blas.BLAS;
import x10.matrix.Vector;
import x10.matrix.ElemType;

import x10.matrix.util.Debug;

import x10.util.resilient.localstore.Snapshottable;
import x10.util.resilient.VectorSnapshotInfo;

import x10.util.Team;
import x10.util.ArrayList;
import x10.util.resilient.localstore.Cloneable;
import x10.matrix.util.ElemTypeTool;

public type DupVector(m:Long)=DupVector{self.M==m};
public type DupVector(v:DupVector)=DupVector{self==v};

public class DupVector(M:Long) implements Snapshottable {
    public var dupV:PlaceLocalHandle[DupVectorLocalState];
    public var team:Team;
    private var places:PlaceGroup;
    
    /*
     * Time profiling
     */
    transient var commTime:Long = 0;
    transient var calcTime:Long = 0;   
    
    public def this(vs:PlaceLocalHandle[DupVectorLocalState], pg:PlaceGroup, team:Team) {
        val m = vs().vec.M;
        property(m);
        dupV  = vs;
        this.team = team;
        places = pg;
    }

    public static def make(v:Vector, pg:PlaceGroup, team:Team):DupVector(v.M){
        val hdl = PlaceLocalHandle.make[DupVectorLocalState](pg, () => new DupVectorLocalState(v, pg.indexOf(here)));
        return new DupVector(hdl, pg, team) as DupVector(v.M);
    }
    
    public static def make(v:Vector):DupVector(v.M) = make (v, Place.places(), Team.WORLD);
        
    public static def make(m:Long, pg:PlaceGroup, team:Team) {
        val hdl = PlaceLocalHandle.make[DupVectorLocalState](pg, ()=> new DupVectorLocalState(Vector.make(m), pg.indexOf(here)) );
        return new DupVector(hdl, pg, team) as DupVector(m);
    }
    
    public static def make(m:Long) = make(m, Place.places(), Team.WORLD);
    
    public def alloc(m:Long, pg:PlaceGroup, team:Team):DupVector(m) = make(m, pg, team);
    public def alloc(pg:PlaceGroup, team:Team) = alloc(M, pg, team);
    public def alloc(m:Long):DupVector(m) = make(m, Place.places(), Team.WORLD);
    public def alloc() = alloc(M, Place.places(), team.WORLD);
    
    
    public def clone():DupVector(M) {
        val bs = PlaceLocalHandle.make[DupVectorLocalState](places, 
                ()=>dupV().clone());    
        return new DupVector(bs, places, team) as DupVector(M);
    }
    
    public def reset() {
        finish ateach(Dist.makeUnique(places)) {
            dupV().vec.reset();
        }
    }
    
    public def reset_local() {
        dupV().vec.reset();
    }
    

    public def init(dv:ElemType) : DupVector(this) {
        finish ateach(Dist.makeUnique(places)) {
            dupV().vec.init(dv);
        }
        return this;
    }
    
    public def initRandom() : DupVector(this) {
        dupV().vec.initRandom();
        sync();
        return this;
    }
    
    public def init_local(root:Place, dv:ElemType) {
    	if (here.id == root.id)
    		dupV().vec.init(dv);
    	sync_local(root);
    }
    
    public def init_local(root:Place, f:(Long)=>ElemType) {
    	if (here.id == root.id)
    		dupV().vec.init(f);
        sync_local(root);
    }
    
    public def initRandom_local(root:Place) {
    	if (here.id == root.id)
            dupV().vec.initRandom();
        sync_local(root);
    }
    
    public def initRandom(lo:Int, up:Int) : DupVector(this) {
        dupV().vec.initRandom(lo, up);
        sync();
        return this;
    }
    
    public def init(f:(Long)=>ElemType) : DupVector(this) {
        dupV().vec.init(f);
        sync();
        return this;
    }

    public def copyTo(dst:DupVector(M)):void {
        finish ateach(Dist.makeUnique(places)) {
            dupV().vec.copyTo(dst.dupV().vec);
        }
    }
    
    public def copyTo_local(dst:DupVector(M)):void {
        dupV().vec.copyTo(dst.dupV().vec);
    }

    public def copyFrom(vec:Vector(M)): DupVector(this) {
        vec.copyTo(local());
        sync();
        return this;
    }
    
    public def copyFrom_local(vec:Vector(M)): DupVector(this) {
        vec.copyTo(local());
        return this;
    }
    
    public def copyFrom_local(vec:DupVector(M)): DupVector(this) {
        vec.local().copyTo(local());
        return this;
    }
    
    public  operator this(x:Long):ElemType = dupV().vec(x);

    public operator this(x:Long)=(dv:ElemType):ElemType {
        finish ateach(Dist.makeUnique(places)) {
            //Remote capture: x, y, d
            dupV().vec(x) = dv;    
        }
        return dv;
    }
    
    public def local() = dupV().vec as Vector(M);

    /**
     * Scale all elements: this *= alpha
     * All copies are updated concurrently.
     */
    public def scale(alpha:ElemType)
        = map((x:ElemType)=>{alpha * x});

    public def scale_local(alpha:ElemType)
        = map_local((x:ElemType)=>{alpha * x});
    
    /**
     * this = alpha * V
     */
    public def scale(alpha:Double, V:DupVector(M))
        = map(V, (v:Double)=> {alpha * v});
    
    public def scale_local(alpha:Double, V:DupVector(M))
        = map_local(V, (v:Double)=> {alpha * v});
    
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
    
    public def cellAdd_local(V:DupVector(M))
        = map_local(this, V, (x:Double, v:Double)=> {x + v});
    
    public def cellAdd_local(V:DupVector(M), L:DupVector(M))
        = map_local(V, L, (x:Double, v:Double)=> {x + v});

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
    
    public def cellSub_local(V:DupVector(M))
        = map_local(this, V, (x:Double, v:Double)=> {x - v});
    
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
    
    public def cellMult_local(V:DupVector(M))
        = map_local(this, V, (x:Double, v:Double)=> {x * v});
    

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
    
    public def dot_local(v:DupVector(M)):Double {
        return dot(v);
    }
    public def dot_local(v:Vector(M)):Double {
        return local().dot(v);
    }

    /**
     * L2-norm (Euclidean norm) of this vector, i.e. the square root of the
     * sum of squares of all elements
     */
    public def norm():Double {
        return local().norm();
    }
    
    public def norm_local():Double {
        return norm();
    }
    
    public def scaleAdd_local(alpha:ElemType, v:DupVector(M)) {
        BLAS.compAxpy(this.M, alpha, v.local().d, this.local().d);
        return this;
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
    public def mult_local(root:Place, vB:DistVector, mA:DistBlockMatrix(vB.M, this.M)) = DistDupVectorMult.comp_local(root, vB, mA, this, false);
    
    public def mult(mA:DistBlockMatrix(this.M), vB:DupVector(mA.N))  = DistDupVectorMult.comp(mA, vB, this, false);
    public def mult(vB:DupVector, mA:DistBlockMatrix(vB.M, this.M))  = DistDupVectorMult.comp(vB, mA, this, false);
    
    public operator this % (that:DistBlockMatrix(M)) = 
        DistDupVectorMult.comp(this, that, DistVector.make(that.N, that.getAggColBs(), places, team), true);    
    public operator (that:DistBlockMatrix{self.N==this.M}) % this = 
        DistDupVectorMult.comp(that , this, DistVector.make(that.M, that.getAggRowBs(), places, team), true);


    public def sync():void {
        /* Timing */ val st = Timer.milliTime();
        val root = here;
        finish for (place in places) at (place) async {
            var src:Rail[ElemType] = null;
            val dst = dupV().vec.d;
            if (here.id == root.id){
                src = dupV().vec.d;
            }
            team.bcast(root, src, 0, dst, 0, M);
        }
        /* Timing */ commTime += Timer.milliTime() - st;
    }
    
    
    public def sync_local(root:Place):void {
        /* Timing */ val st = Timer.milliTime();
        var src:Rail[ElemType] = null;
        val dst = dupV().vec.d;
        if (here.id == root.id){
            src = dupV().vec.d;
        }
        team.bcast(root, src, 0, dst, 0, M);  
        /* Timing */ commTime += Timer.milliTime() - st;
    }
    

    public def reduce(op:Int): void {
        /* Timing */ val st = Timer.milliTime();        
        val root = here;
        finish for (place in places) at (place) async {
        	val src = dupV().vec.d;
            val dst = dupV().vec.d; // if null at non-root, Team hangs
        	team.reduce(root, src, 0, dst, 0, M, op);
        }
        /* Timing */ commTime += Timer.milliTime() - st;
    }
    
    public def reduceSum(): void {
        /* Timing */ val st = Timer.milliTime();        
        reduce(Team.ADD);
        /* Timing */ commTime += Timer.milliTime() - st;
    }
    
    public def allReduce(op:Int): void {
    	finish for (place in places) at (place) async {
    		val src = dupV().vec.d;
    		val dst = dupV().vec.d; 
    		team.allreduce(src, 0, dst, 0, M, op);
    	}
    }
    
    public def allReduceSum(): void {
        /* Timing */ val st = Timer.milliTime();        
        allReduce(Team.ADD);
        /* Timing */ commTime += Timer.milliTime() - st;
    }
    
    public def sum_local():ElemType {
        return sum_local((a:ElemType)=>{ a });
    }
    
    public def sum_local(elemOp:(a:ElemType)=>ElemType):ElemType {
    	return dupV().vec.reduce((a:ElemType,b:ElemType)=> {a+b}, ElemTypeTool.zero, elemOp);
    }

    public def likeMe(that:DupVector): Boolean = (this.M==that.M);
        
    public def checkSync():Boolean {
        val rootvec  = dupV().vec;
        var retval:Boolean = true;
        for (var p:Long=0; p < places.size() && retval; p++) {
            if (p == here.id()) continue;
            retval &= at(places(p)) {
                    val vec = dupV().vec;
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

    public final @Inline def map_local(op:(x:Double)=>Double):DupVector(this) {
        val d = local();
        d.map(op);
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

    public final @Inline def map_local(a:DupVector(M), op:(x:Double)=>Double):DupVector(this) {
        assert(likeMe(a));
        val d = local();
        val ad = a.local() as Vector(d.M);
        d.map(ad, op);
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

    public final @Inline def map_local(a:DupVector(M), b:DupVector(M), op:(x:Double,y:Double)=>Double):DupVector(this) {
        assert(likeMe(a));
   
        val d = local();
        val ad = a.local() as Vector(d.M);
        val bd = b.local() as Vector(d.M);
        d.map(ad, bd, op);
   
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
    public def remake(newPg:PlaceGroup, newTeam:Team, addedPlaces:ArrayList[Place]){
        assert (places.size() == newPg.size()); 
        for (sparePlace in addedPlaces){
            PlaceLocalHandle.addPlace[DupVectorLocalState](dupV, sparePlace, ()=>new DupVectorLocalState(Vector.make(M), newPg.indexOf(here)));
        }
        team = newTeam;
        places = newPg;
    }
    
    /*
     * Snapshot mechanism
     */
    public def makeSnapshot_local():Cloneable {
    	val data = dupV().vec.d;
        val placeIndex = dupV().placeIndex;
        return new VectorSnapshotInfo(placeIndex, data) as Cloneable;
    }
    
    public def restoreSnapshot_local(vec:Cloneable) {
        val vecInfo = vec as VectorSnapshotInfo;	
    	val srcRail = vecInfo.data;
        val dstRail = dupV().vec.d;
        Rail.copy(srcRail, 0, dstRail, 0, srcRail.size);        
    }    
}

class DupVectorLocalState {
    public var vec:Vector;
    public val placeIndex:Long; //FIXME: remove this field
    
    public def this(v:Vector, placeIndex:Long){
        this.vec = v;
        this.placeIndex = placeIndex;        
    }
    
    public def clone(){
        return new DupVectorLocalState(vec.clone(), placeIndex);
    }
    
    public def toString() = vec.toString();
    
}
