/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 *  (C) Copyright Sara Salem Hamouda 2014-2016.
 */

package x10.matrix.distblock;

import x10.util.Timer;
import x10.compiler.Inline;
import x10.regionarray.Dist;
import x10.util.Pair;
import x10.util.StringBuilder;
import x10.util.ArrayList;

import x10.matrix.Matrix;
import x10.matrix.util.Debug;
import x10.matrix.ElemType;
import x10.matrix.Vector;

import x10.matrix.util.MathTool;
import x10.matrix.block.Grid;
import x10.matrix.block.BlockMatrix;

import x10.util.resilient.localstore.Snapshottable;
import x10.util.resilient.VectorSnapshotInfo;

import x10.util.RailUtils;
import x10.util.Team;
import x10.util.resilient.localstore.Cloneable;

public type DistVector(m:Long)=DistVector{self.M==m};
public type DistVector(v:DistVector)=DistVector{self==v};

public class DistVector(M:Long) implements Snapshottable {
    public var distV:PlaceLocalHandle[DistVectorLocalState];
    
    /*
     * Time profiling
     */
    transient var commTime:Long = 0;
    transient var calcTime:Long = 0;    
    private var team:Team;
    private var places:PlaceGroup;
    
    public def places() = places;

    public def this(m:Long, vs:PlaceLocalHandle[DistVectorLocalState], pg:PlaceGroup) {
        property(m);
        distV  = vs;        
        team = new Team(pg);
        places = pg;
    }
    
    public def this(m:Long, vs:PlaceLocalHandle[DistVectorLocalState], pg:PlaceGroup, team:Team) {
        property(m);
        distV  = vs;        
        this.team = team;
        places = pg;
    }
    
    public def getOffset():Int{
         return distV().offsets(distV().placeIndex);
    }
    
    public def getSegSize():Rail[Int] = distV().segSize;
    
    public static def make(m:Long, segNum:Long, pg:PlaceGroup, team:Team):DistVector(m) {
        assert (segNum == pg.size()) :
            "number of vector segments must be equal to number of places";    
        val segsz = new Rail[Int](segNum, (i:Long)=>Grid.compBlockSize(m, segNum, i as Int) as Int);
        val offsets = RailUtils.scanExclusive(segsz, (x:Int, y:Int) => x+y, 0n);        
        val hdv = PlaceLocalHandle.make[DistVectorLocalState](pg,
                ()=>new DistVectorLocalState(Vector.make(Grid.compBlockSize(m, segNum, pg.indexOf(here))),segsz,offsets, pg.indexOf(here)) );        
        return new DistVector(m, hdv, pg, team) as DistVector(m);
    }
    
    public static def make(m:Long, segNum:Long):DistVector(m) = make (m, segNum, Place.places(), Team.WORLD); 
    
    public static def make(m:Long, pg:PlaceGroup, team:Team) = make (m, pg.size(), pg, team);
    
    public static def make(m:Long) = make (m, Place.places(), Team.WORLD);

    public static def make(m:Long, segsz:Rail[Int], pg:PlaceGroup, team:Team):DistVector(m) {   
        assert (segsz.size == pg.size()) :
            "number of vector segments must be equal to number of places";    
        val offsets = RailUtils.scanExclusive(segsz, (x:Int, y:Int) => x+y, 0n);
        val hdv = PlaceLocalHandle.make[DistVectorLocalState](pg,
            ()=>new DistVectorLocalState(Vector.make(segsz(pg.indexOf(here))),segsz,offsets, pg.indexOf(here)) );        
        return new DistVector(m, hdv, pg, team) as DistVector(m);
    }
    
    public static def make(m:Long, segsz:Rail[Int]):DistVector(m) = make (m, segsz, Place.places(), Team.WORLD);

    public def alloc(m:Long, pg:PlaceGroup, team:Team):DistVector(m) = make(m, pg, team);
    public def alloc(pg:PlaceGroup) = alloc(M, pg, new Team(pg));
    
    public def alloc(m:Long):DistVector(m) = make(m, Place.places(), Team.WORLD);
    public def alloc() = alloc(M, Place.places(), Team.WORLD);
    
    
    public def clone():DistVector(M) {
        val dv = PlaceLocalHandle.make[DistVectorLocalState](places, 
                ()=>distV().clone());    
        return new DistVector(M, dv, places) as DistVector(M);
    }
    
    public def reset() {
        finish ateach(Dist.makeUnique(places)) {
            distV().vec.reset();
        }
    }
    
    public def reset_local() {
        distV().vec.reset();
    }

    public def init(dv:ElemType) : DistVector(this) {
        finish ateach(Dist.makeUnique(places)) {
            distV().vec.init(dv);
        }
        return this;
    }
    
    public def init_local(dv:ElemType) : DistVector(this) {
        distV().vec.init(dv);
        return this;
    }
    
    public def initRandom() : DistVector(this) {
        finish ateach(Dist.makeUnique(places)) {
            distV().vec.initRandom();
        }
        return this;
    }

    public def initRandom_local() : DistVector(this) {        
        distV().vec.initRandom();
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
            distV().vec.initRandom(min, max);
        }
        return this;
    }
    
    public def initRandom_local(min:Long, max:Long):DistVector(this) {
        distV().vec.initRandom(min, max);    
        return this;
    }
    
    public def init(f:(Long)=>ElemType):DistVector(this) {
        finish ateach(Dist.makeUnique(places)) {
            distV().vec.init(f);
        }
        return this;
    }
    
    public def init_local(f:(Long)=>ElemType):DistVector(this) {        
        distV().vec.init(f);        
        return this;
    }
    
    public def copyTo(dst:DistVector(M)):void {
        finish ateach(Dist.makeUnique(places)) {
            distV().vec.copyTo(dst.distV().vec);
        }
    }
    
    public def copyTo_local(dst:DistVector(M)):void {
        distV().vec.copyTo(dst.distV().vec);
    }
    

    public def copyTo(vec:Vector(M)):void {
        val gr = new GlobalRail[ElemType](vec.d);
        finish for (place in places) at (place) async {
            val src = distV().vec.d;
            var dst:Rail[ElemType] = null;
            if (here == gr.home){
                dst = gr();
            }   
            team.gatherv(gr.home, src, 0, dst, 0, getSegSize());
        }
    }
    
    /**
     * Gather all elements of this distributed vector into the given (local)
     * vector at the root place.  On return from this method, vec will contain
     * the elements of this vector only at the root place. The Vector vec
     * is not used at other places.
     */
    public def copyTo_local(root:Place, vec:Vector(M)):void {
        val src = distV().vec.d;
        var dst:Rail[ElemType] = null;
        if (here.id == root.id){
            dst = vec.d;
        }
        team.gatherv(root, src, 0, dst, 0, getSegSize());
    }


    public def copyFrom(vec:Vector(M)): void {
        val gr = new GlobalRail[ElemType](vec.d);
        finish for (place in places) at (place) async {
            var src:Rail[ElemType] = null;
            val dst = distV().vec.d;
            if (here == gr.home){
                src = gr();
            }         
            team.scatterv(gr.home, src, 0, dst, 0, getSegSize());
        }
    }
    
    public def copyFrom_local(root:Place, vec:Vector(M)): DistVector(this)  {
        var src:Rail[ElemType] = null;
        val dst = distV().vec.d;
        if (here.id == root.id){
            src = vec.d;
        }
        team.scatterv(root,src, 0, dst, 0, getSegSize());
        return this;
    }
    
    public def copyFrom_local(src:Vector(M)): DistVector(this)  {
    	val offset=getOffset() as Long;
    	val size = getSegSize()(distV().placeIndex as Long) as Long;
        val dist = distV().vec;
        Rail.copy(src.d, offset, dist.d, 0, size);    
        return this;
    }
    
    public def copyFrom_local(src:DistVector(M)): DistVector(this) {
        val offset=getOffset() as Long;
        val size = getSegSize()(distV().placeIndex as Long) as Long;
        val dist = distV().vec;
        val srcdist = src.distV().vec;
        Rail.copy(srcdist.d, 0, dist.d, 0, size);
        return this;
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

    private def find(var pos:Long, segments:Rail[Int]):Pair[Long, Long] {        
        for (var i:Long=0; i<segments.size; i++) {
            if (pos < segments(i))
                return new Pair[Long,Long](i, pos);
            pos -= segments(i);
        }
        throw new UnsupportedOperationException("Error in searching index in vector");
    }
    
    protected def find(var pos:Long):Pair[Long, Long] {
        assert (pos < M) : "Vector data access out of bounds";
        return find(pos, getSegSize());
    }
    
    public  operator this(x:Long):ElemType {
        val loc = find(x);
        val seg = loc.first as Int;
        val off = loc.second;
        val p = places(seg);
        if (p == here) {
            return distV().vec(off);
        } else {
            val dat = at(places(seg)) distV().vec(off);
            return dat;
        }
    }

    public operator this(x:Long)=(dv:ElemType):ElemType {
        val loc = find(x);
        val seg = loc.first as Int;
        val off = loc.second;
        val p = places(seg);
        if (p == here) {
            distV().vec(off)=dv;
        } else {
            at(p) distV().vec(off)=dv;
        }
        return dv;
    }

    /**
     * Scaling method. All copies are updated concurrently
     */
    public def scale(a:ElemType) {
        finish ateach(Dist.makeUnique(places)) {
            distV().vec.scale(a);
        }
        return this;
    }
    
    public def scale_local(a:ElemType) {
        distV().vec.scale(a);
    }

    /**
     * Concurrently perform cellwise addition on all copies.
     */
    public def cellAdd(that:DistVector(M))  {
        //assert (this.M==A.M&&this.N==A.N);
        finish ateach(Dist.makeUnique(places)) {
            val dst = distV().vec;
            val src = that.distV().vec as Vector(dst.M);
            dst.cellAdd(src);
        }
        return this;
    }
    
    public def cellAdd_local(that:DistVector(M))  {
        val dst = distV().vec;
        val src = that.distV().vec as Vector(dst.M);
        dst.cellAdd(src);
        return this;
    }

    public def cellAdd(dv:ElemType)  {
        finish ateach(Dist.makeUnique(places)) {
            distV().vec.cellAdd(dv);
        }
        return this;
    }

    public def cellAdd_local(dv:ElemType)  {
        distV().vec.cellAdd(dv);
        return this;
    }

    /**
     * Concurrently perform cellwise subtraction on all copies
     */
    public def cellSub(A:DistVector(M)) {
        finish ateach(Dist.makeUnique(places)) {
            val dst = distV().vec;
            val src = A.distV().vec as Vector(dst.M);
            dst.cellSub(src);
        }
        return this;
    }
    
    public def cellSub_local(A:DistVector(M)) {
        val dst = distV().vec;
        val src = A.distV().vec as Vector(dst.M);
        dst.cellSub(src);
        return this;
    }
    
    /**
     * Perform cell-wise subtraction  this = this - dv.
     */
    public def cellSub(dv:ElemType):DistVector(this) {
        finish ateach(Dist.makeUnique(places)) {
            distV().vec.cellSub(dv);
        }
        return this;
    }

    public def cellSub_local(dv:ElemType):DistVector(this) {
        distV().vec.cellSub(dv);
        return this;
    }

    /**
     * Cellwise multiplication. All copies are modified with
     * the corresponding vector copies.
     */
    public def cellMult(A:DistVector(M)) {
        finish ateach(Dist.makeUnique(places)) {
            val dst = this.distV().vec;
            val src = A.distV().vec as Vector(dst.M);
            dst.cellMult(src);
        }
        return this;
    }
    
    public def cellMult_local(A:DistVector(M)){
        val dst = this.distV().vec;
        val src = A.distV().vec as Vector(dst.M);
        dst.cellMult(src);
        return this;
    }
    

    /**
     * Cellwise division. All copies are modified with
     * the corresponding vector copies.
     */    
    public def cellDiv(A:DistVector(M)) {
        finish ateach(Dist.makeUnique(places)) {
            val dst = this.distV().vec;
            val src = A.distV().vec as Vector(dst.M);
            dst.cellDiv(src);
        }
        return this;
    }
    
    public def cellDiv_local(A:DistVector(M)) {
        val dst = this.distV().vec;
        val src = A.distV().vec as Vector(dst.M);
        dst.cellDiv(src);
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
            val segSize = getSegSize();
            for (p in 0..(places.size()-1)) {
                val offset = off;
                val s = segSize(p);
                at(places(p)) async {
                    val dist = distV().vec;
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

    
    public def dot_local(v:DupVector(M)):ElemType {
        val offset=getOffset();
        val dist = distV().vec;
        val dup = v.local();
        var myDot:ElemType = 0.0 as ElemType;
        val s = getSegSize()(distV().placeIndex);
        for (i in 0..(s-1))
            myDot += dist(i) * dup(offset+i);
        val result = team.allreduce(myDot, Team.ADD);
        return result;
    }

    public def dot_local(v:DistVector(M)):ElemType {
        val dist = distV().vec;
        val vdist = v.distV().vec;
        var myDot:ElemType = 0.0 as ElemType;
        val s = getSegSize()(distV().placeIndex);
        for (i in 0..(s-1))
            myDot += dist(i) * vdist(i);
        val result = team.allreduce(myDot, Team.ADD);
        return result;
    }
    
    public def sum_local(elemOp:(a:ElemType)=>ElemType):ElemType {
        val dist = distV().vec;
        var mySum:ElemType = 0.0 as ElemType;
        val s = getSegSize()(distV().placeIndex);
        for (i in 0..(s-1))
            mySum += elemOp (dist(i));
        val result = team.allreduce(mySum, Team.ADD);
        return result;
    }
    
    public def sum_local():ElemType {
        return sum_local ((a:ElemType)=>{ a });
    }
    
    public def max_local(op:(x:ElemType)=>ElemType):ElemType {
        val dist = distV().vec;
        var myMax:ElemType = op( dist(0) as ElemType );
        val s = getSegSize()(distV().placeIndex);
        for (i in 1..(s-1)){
        	val distITemp = op( dist(i) );
            myMax = ( distITemp > myMax ) ? distITemp : myMax;
        }
        val result = team.allreduce(myMax, Team.MAX);
        return result;
    }
    
    public def max_local():ElemType {
       return max_local ((a:ElemType)=> {a});
    }
        
    // Multiplication operations 

    public def mult(mA:DistBlockMatrix(M), vB:DupVector(mA.N), plus:Boolean):DistVector(this) =
        DistDupVectorMult.comp(mA, vB, this, plus);
    
    public def mult(vB:DupVector, mA:DistBlockMatrix(vB.M, this.M), plus:Boolean):DistVector(this) =
        DistDupVectorMult.comp(vB, mA, this, plus);

    public def mult(mA:DistBlockMatrix(M), vB:DupVector(mA.N))      = DistDupVectorMult.comp(mA, vB, this, false);
    public def mult(vB:DupVector, mA:DistBlockMatrix(vB.M, this.M)) = DistDupVectorMult.comp(vB, mA, this, false);
    public def mult_local(mA:DistBlockMatrix(M), vB:DupVector(mA.N)) = DistDupVectorMult.comp_local(mA, vB, this, false);

    //FIXME: review the correctness of using places here
    public operator this % (that:DistBlockMatrix(this.M)) = 
         DistDupVectorMult.comp(this, that, DupVector.make(that.N, places, team), false);
    //FIXME: review the correctness of using places here
    public operator (that:DistBlockMatrix{self.N==this.M}) % this = 
        DistDupVectorMult.comp(that, this, DupVector.make(that.M, places, team), false);

    /** Get the sum of all elements in this vector. */
    public def sum():ElemType = reduce((a:ElemType,b:ElemType)=>{a+b}, 0.0 as ElemType);
    
    public def likeMe(that:DistVector): Boolean  {
        if (this.M!=that.M) return false;
        val segSize = getSegSize();
        for (var i:Long=0; i<segSize.size; i++)
            if (segSize(i) != that.getSegSize()(i)) return false;
        return true;
    }
        
    public def equals(dv:DistVector(this.M)):Boolean {
        var ret:Boolean = true;   
        if (dv.places().equals(places)){
            for (var p:Long=0; p<places.size() &&ret; p++) {
                val pindx = p;
                ret &= at(places(pindx)) {
                    val srcv = distV().vec;
                    val tgtv = dv.distV().vec as Vector(srcv.M);
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
            ret &= at(places(pindx)) distV().vec.equals(dval);
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
            val d = distV().vec;
            d.map(op);
        }
        calcTime += Timer.milliTime() - stt;
        return this;
    }

    public final @Inline def map_local(op:(x:ElemType)=>ElemType):DistVector(this) {
        val d = distV().vec;
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
    public final @Inline def map(a:DistVector(M), op:(x:ElemType)=>ElemType):DistVector(this) {
        assert(likeMe(a));
        val stt = Timer.milliTime();
        finish ateach(Dist.makeUnique(places)) {
            val d = distV().vec;
            val ad = a.distV().vec as Vector(d.M);
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
            val d = distV().vec;
            val ad = a.distV().vec as Vector(d.M);
            val bd = b.distV().vec as Vector(d.M);
            d.map(ad, bd, op);
        }
        calcTime += Timer.milliTime() - stt;
        return this;
    }

    public final @Inline def map_local(a:DistVector(M), op:(x:ElemType)=>ElemType):DistVector(this) {
        assert(likeMe(a));
        val d = distV().vec;
        val ad = a.distV().vec as Vector(d.M);
        d.map(ad, op);
        return this;
    }
    
    public final @Inline def map_local(a:DistVector(M), b:DistVector(M), op:(x:ElemType,y:ElemType)=>ElemType):DistVector(this) {
        assert(likeMe(a));
        
        val d = distV().vec;
        val ad = a.distV().vec as Vector(d.M);
        val bd = b.distV().vec as Vector(d.M);
        d.map(ad, bd, op);
        
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
                val d = distV().vec;
                offer d.reduce(op, unit);
            }
        };
        calcTime += Timer.milliTime() - stt;
        return result;
    }

    public def getCalcTime() = calcTime;
    public def getCommTime() = commTime;
    
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
            output.add(at (p) { distV().vec.toString()});
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
    public def remake(segsz:Rail[Int], newPg:PlaceGroup, newTeam:Team, addedPlaces:ArrayList[Place]){
        assert (places.size() == newPg.size());
        assert (segsz.size == newPg.size()) :
            "number of vector segments must be equal to number of places";
        val oldSnapshotSegSize = distV().snapshotSegSize;
        val oldSnapshotOffsets = distV().snapshotOffsets;
                
        val offsets = RailUtils.scanExclusive(segsz, (x:Int, y:Int) => x+y, 0n);

        for (sparePlace in addedPlaces){
            PlaceLocalHandle.addPlace[DistVectorLocalState](
                distV, sparePlace, ()=>new DistVectorLocalState(
                Vector.make(segsz(newPg.indexOf(here))),segsz,offsets, newPg.indexOf(here), 
                oldSnapshotSegSize, oldSnapshotOffsets)
            );
        }
        
        team = newTeam;
        places = newPg;
    }
    
    /**
     * Remake the DistVector over a new PlaceGroup
     */
    public def remake(newPg:PlaceGroup, newTeam:Team, addedPlaces:ArrayList[Place]){
        val m = M;        
        val segNum = newPg.size;
        val slst = new Rail[Int](segNum, (i:Long)=>Grid.compBlockSize(m, segNum, i as Int) as Int);
        remake (slst, newPg, newTeam, addedPlaces);
    }

    public def makeSnapshot_local():Cloneable {
    	val i = distV().placeIndex;
        val data = distV().vec.d;
        distV().snapshotSegSize = distV().segSize;
        distV().snapshotOffsets = distV().offsets;    
        return new VectorSnapshotInfo(i, data);
    }
    
    public def restoreSnapshot_local(vec:Cloneable) {
        val storedSegment = vec as VectorSnapshotInfo;
        val srcRail = storedSegment.data;
        val dstRail = distV().vec.d;
        Rail.copy(srcRail, 0, dstRail, 0, srcRail.size);
    }
}

class DistVectorLocalState {
    public var vec:Vector;
    public var segSize:Rail[Int];
    public var offsets:Rail[Int];
    public var snapshotSegSize:Rail[Int];
    public var snapshotOffsets:Rail[Int];
    public var placeIndex:Long;
    
    public def this(vec:Vector, segSize:Rail[Int], offsets:Rail[Int], placeIndex:Long){
        this.vec = vec;
        this.segSize = segSize;
        this.offsets = offsets;
        this.placeIndex = placeIndex;
    }
    
    public def this(vec:Vector, segSize:Rail[Int], offsets:Rail[Int], placeIndex:Long, snapshotSegSize:Rail[Int], snapshotOffsets:Rail[Int]){
        this.vec = vec;
        this.segSize = segSize;
        this.offsets = offsets;
        this.placeIndex = placeIndex;
        this.snapshotSegSize = snapshotSegSize;
        this.snapshotOffsets = snapshotOffsets;
    }
    
    public def clone():DistVectorLocalState{
        return new DistVectorLocalState(vec.clone(), new Rail[Int](segSize), new Rail[Int](offsets), placeIndex );
    }    
}
