/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 *  (C) Copyright Sara Salem Hamouda 2014-2015.
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

import x10.util.resilient.iterative.DistObjectSnapshot;
import x10.util.resilient.iterative.Snapshottable;
import x10.util.resilient.VectorSnapshotInfo;

import x10.util.RailUtils;
import x10.util.Team;

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

    public def init(dv:ElemType) : DistVector(this) {
        finish ateach(Dist.makeUnique(places)) {
            distV().vec.init(dv);
        }
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
        val root = here;
        val gr = new GlobalRail[ElemType](vec.d);
        finish for (place in places) at (place) async {
            val src = distV().vec.d;
            var dst:Rail[ElemType] = null;
            if (here.id == root.id){
                dst = gr();
            }   
distV().gathervTime -= Timer.milliTime(); 
            team.gatherv(root, src, 0, dst, 0, getSegSize());
distV().gathervTime += Timer.milliTime();
        }
    }
    
    public def copyTo(root:Place, vec:Vector(M)):void {
        val src = distV().vec.d;
        var dst:Rail[ElemType] = null;
        if (here.id == root.id){
            dst = vec.d;
        }
distV().gathervTime -= Timer.milliTime();
        team.gatherv(root, src, 0, dst, 0, getSegSize());
distV().gathervTime += Timer.milliTime();
    }


    public def copyFrom(vec:Vector(M)): void {
        val root = here;
        val gr = new GlobalRail[ElemType](vec.d);
        finish for (place in places) at (place) async {
            var src:Rail[ElemType] = null;
            val dst = distV().vec.d;
            if (here.id == root.id){
                src = gr();
            }
distV().scattervTime -= Timer.milliTime();           
            team.scatterv(root,src, 0, dst, 0, getSegSize());
distV().scattervTime += Timer.milliTime();
        }
    }
    
    public def copyFrom_local(root:Place, vec:Vector(M)): void {
        var src:Rail[ElemType] = null;
        val dst = distV().vec.d;
        if (here.id == root.id){
            src = vec.d;
        }
distV().scattervTime -= Timer.milliTime();
        team.scatterv(root,src, 0, dst, 0, getSegSize());
distV().scattervTime += Timer.milliTime();
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
        val dat = at(places(seg)) distV().vec(off);
        return dat;
    }

    public operator this(x:Long)=(dv:ElemType):ElemType {
        val loc = find(x);
        val seg = loc.first as Int;
        val off = loc.second;
        at(places(seg)) distV().vec(off)=dv;
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

    public def cellAdd(dv:ElemType)  {
        finish ateach(Dist.makeUnique(places)) {
            distV().vec.cellAdd(dv);
        }
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

    
    /**
     * Perform cell-wise subtraction  this = this - dv.
     */
    public def cellSub(dv:ElemType):DistVector(this) {
        finish ateach(Dist.makeUnique(places)) {
            distV().vec.cellSub(dv);
        }
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
distV().allReduceTime -= Timer.milliTime();
        val result = team.allreduce(myDot, Team.ADD);
distV().allReduceTime += Timer.milliTime();
        return result;
    }

    public def sum_local():ElemType {
        val offset=getOffset();
        val dist = distV().vec;
        var mySum:ElemType = 0.0 as ElemType;
        val s = getSegSize()(distV().placeIndex);
        for (i in 0..(s-1))
            mySum += dist(i);
distV().allReduceTime -= Timer.milliTime();
        val result = team.allreduce(mySum, Team.ADD);
distV().allReduceTime += Timer.milliTime();
        return result;
    }
    
    // Multiplication operations 

    public def mult(mA:DistBlockMatrix(M), vB:DupVector(mA.N), plus:Boolean):DistVector(this) =
        DistDupVectorMult.comp(mA, vB, this, plus);
    
    public def mult(vB:DupVector, mA:DistBlockMatrix(vB.M, this.M), plus:Boolean):DistVector(this) =
        DistDupVectorMult.comp(vB, mA, this, plus);

    public def mult(mA:DistBlockMatrix(M), vB:DupVector(mA.N))      = DistDupVectorMult.comp(mA, vB, this, false);
    public def mult(vB:DupVector, mA:DistBlockMatrix(vB.M, this.M)) = DistDupVectorMult.comp(vB, mA, this, false);
    public def mult_local(mA:DistBlockMatrix(M), vB:DupVector(mA.N)) {
        distV().multTime -= Timer.milliTime();
        distV().multTimeIt(distV().multTimeIndex) -= Timer.milliTime();        
        
        val result = DistDupVectorMult.comp_local(mA, vB, this, false);
        
        distV().multTime += Timer.milliTime();
        distV().multTimeIt(distV().multTimeIndex++) += Timer.milliTime();
        
        return result;
    }

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
        assert (segsz.size == newPg.size()) :
            "number of vector segments must be equal to number of places";
        val oldPlaces = places;
        val oldSnapshotSegSize = distV().snapshotSegSize;
        val oldSnapshotOffsets = distV().snapshotOffsets;
        
        var spareUsed:Boolean = false;
        if (newPg.size() == oldPlaces.size() && addedPlaces.size() != 0){
            spareUsed = true;
        }
        
        val offsets = RailUtils.scanExclusive(segsz, (x:Int, y:Int) => x+y, 0n);
        if (!spareUsed){
            PlaceLocalHandle.destroy(oldPlaces, distV, (Place)=>true);
            distV = PlaceLocalHandle.make[DistVectorLocalState](newPg,
                ()=>new DistVectorLocalState(Vector.make(segsz(newPg.indexOf(here))),segsz,offsets, newPg.indexOf(here), 
                    oldSnapshotSegSize, oldSnapshotOffsets) );
        }
        else {
            for (sparePlace in addedPlaces){
                Console.OUT.println("Adding place["+sparePlace+"] to DistVector PLH ...");
                PlaceLocalHandle.addPlace[DistVectorLocalState](
                    distV, sparePlace, ()=>new DistVectorLocalState(
                    Vector.make(segsz(newPg.indexOf(here))),segsz,offsets, newPg.indexOf(here), 
                    oldSnapshotSegSize, oldSnapshotOffsets)
                );
            }
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

    /**
     * Create a snapshot for the DistVector data 
     * @return a snapshot for the DistVector data stored in a resilient store
     */
    public def makeSnapshot():DistObjectSnapshot {
        //val startTime = Timer.milliTime();
        val snapshot = DistObjectSnapshot.make();
        finish ateach(pl in Dist.makeUnique(places)){
            val i = distV().placeIndex;
            val data = distV().vec.d;
            //the segSize should only be saved only at place 0
            val distVecInfo = new VectorSnapshotInfo(i, data);
            snapshot.save(i, distVecInfo);
            
            distV().snapshotSegSize = distV().segSize;
            distV().snapshotOffsets = distV().offsets;                   
        }
        //Console.OUT.println("DistVector.SnapshotTime["+(Timer.milliTime() - startTime)+"]");
        return snapshot;
    }
    
    public def restoreSnapshot(snapshot:DistObjectSnapshot, localViewFlag:Boolean) {
        //val startTime = Timer.milliTime();
        val currentSegSizes = distV().segSize;
        val snapshotSegSize = distV().snapshotSegSize;
    
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

        if (!localViewFlag){
            if (!segmentsChanged)
                restoreSnapshotSegmentBySegment(snapshot);
            else
                restoreSnapshotElementByElement(snapshot);
        }
        else {
            if (!segmentsChanged)
                restoreSnapshotSegmentBySegment_local(snapshot);
            else
                restoreSnapshotElementByElement_local(snapshot);
        }
        //Console.OUT.println("DistVector.RestoreTime["+(Timer.milliTime() - startTime)+"]");
    }
    
    /**
     * Restore the DistVector data using the provided snapshot object 
     * @param snapshot a snapshot from which to restore the data
     */
    public def restoreSnapshot(snapshot:DistObjectSnapshot) {
        //val startTime = Timer.milliTime();
        restoreSnapshot(snapshot, false);
        //Console.OUT.println("DistVector.RestoreTime["+(Timer.milliTime() - startTime)+"]");
    }
    
    private def restoreSnapshotSegmentBySegment(snapshot:DistObjectSnapshot) {
        //val startTime = Timer.milliTime();
        finish ateach(Dist.makeUnique(places)) {
            //segments should be stored in places with the same order 
            //segment place index will remain the same
            val segmentPlaceIndex =  distV().placeIndex;
            val storedSegment = snapshot.load(segmentPlaceIndex) as VectorSnapshotInfo;
            val srcRail = storedSegment.data;
            val dstRail = distV().vec.d;
            Rail.copy(srcRail, 0, dstRail, 0, srcRail.size);
        }        
        //Console.OUT.println("DistVector.RestoreTimeSegmentBySegment["+(Timer.milliTime() - startTime)+"]");
    }
    
    private def restoreSnapshotElementByElement(snapshot:DistObjectSnapshot) {
        //val startTime = Timer.milliTime();    
        finish ateach(Dist.makeUnique(places)) {
            val newSegSize = distV().segSize;
            val snapshotSegSize = distV().snapshotSegSize;            

            val newSegmentsOffsets = distV().offsets;
            val oldSegmentsOffsets = distV().snapshotOffsets;
        
            val segmentPlaceIndex = distV().placeIndex;
            val low = newSegmentsOffsets(segmentPlaceIndex);
            val high = low + newSegSize(segmentPlaceIndex);
            
            var offset:Long = 0;
            for (var i:Long = 0; i < snapshotSegSize.size; i++) {
                val low_old = oldSegmentsOffsets(i);
                val high_old = low_old + snapshotSegSize(i);
                
                var overlapFound:Boolean = false;
                if (high_old > low && low_old < high) {
                   //calculate the overlapping interval
                   var startIndex:Long = low;
                   var endIndex:Long = high;
                   if (low_old > low)
                       startIndex = low_old;
                   if (high_old < high)
                       endIndex = high_old;
                   //load the old segment from resilient store
                   var storedSegment:VectorSnapshotInfo = snapshot.load(i) as VectorSnapshotInfo;
                   val srcRail = storedSegment.data;
                   val dstRail = distV().vec.d;
                   
                   val elemCount = endIndex - startIndex;
             
                   var srcOffset:Long = 0;
                   if (low_old < low)
                       srcOffset = low - low_old;
                   
                   Rail.copy(srcRail, srcOffset, dstRail, offset, elemCount);
                   offset+= elemCount;
                   
                   overlapFound = true;
                } else if (overlapFound) {
                    break; // no more overlapping segments exist
                }
            }
        }
        //Console.OUT.println("DistVector.RestoreTimeElementByElement["+(Timer.milliTime() - startTime)+"]");
    }
    
    //val snapshot = DistObjectSnapshot.make();
    public def makeSnapshot_local(snapshot:DistObjectSnapshot):void {
        val i = distV().placeIndex;
        val data = distV().vec.d;        
        val distVecInfo = new VectorSnapshotInfo(i, data);
        snapshot.save(i, distVecInfo);       
        
        distV().snapshotSegSize = distV().segSize;
        distV().snapshotOffsets = distV().offsets;    
    }
    
    public def restoreSnapshot_local(snapshot:DistObjectSnapshot) {        
        //val startTime = Timer.milliTime();
        restoreSnapshot(snapshot, true);
        //Console.OUT.println("DistVector.RestoreTime["+(Timer.milliTime() - startTime)+"]");
    }
    
    private def restoreSnapshotSegmentBySegment_local(snapshot:DistObjectSnapshot) {
        val segmentPlaceIndex = distV().placeIndex;
        val storedSegment = snapshot.load(segmentPlaceIndex) as VectorSnapshotInfo;
        val srcRail = storedSegment.data;
        val dstRail = distV().vec.d;
        Rail.copy(srcRail, 0, dstRail, 0, srcRail.size);
    }
    
    private def restoreSnapshotElementByElement_local(snapshot:DistObjectSnapshot) {
        //val startTime = Timer.milliTime();
        val newSegSize = distV().segSize;
        val snapshotSegSize = distV().snapshotSegSize;            

        val newSegmentsOffsets = distV().offsets;
        val oldSegmentsOffsets = distV().snapshotOffsets;
        
        val segmentPlaceIndex = distV().placeIndex;
        val low = newSegmentsOffsets(segmentPlaceIndex);
        val high = low + newSegSize(segmentPlaceIndex);
    
        var offset:Long = 0;
        for (var i:Long = 0; i < snapshotSegSize.size; i++) {
            val low_old = oldSegmentsOffsets(i);
            val high_old = low_old + snapshotSegSize(i);
    
            var overlapFound:Boolean = false;
            if (high_old > low && low_old < high) {
                //calculate the overlapping interval
                var startIndex:Long = low;
                var endIndex:Long = high;
                if (low_old > low)
                    startIndex = low_old;
                if (high_old < high)
                    endIndex = high_old;
                //load the old segment from resilient store
                var storedSegment:VectorSnapshotInfo = snapshot.load(i) as VectorSnapshotInfo;
                val srcRail = storedSegment.data;
                val dstRail = distV().vec.d;
                val elemCount = endIndex - startIndex;
    
                var srcOffset:Long = 0;
                if (low_old < low)
                    srcOffset = low - low_old;
    
                Rail.copy(srcRail, srcOffset, dstRail, offset, elemCount);
                offset+= elemCount;
    
                overlapFound = true;
            } else if (overlapFound) {
                break; // no more overlapping segments exist
            }
        }        
        //Console.OUT.println("DistVector.RestoreTimeElementByElement["+(Timer.milliTime() - startTime)+"]");
    }
    
    public def printTimes(prefix:String, printIterations:Boolean){
        val root = here;
        finish ateach(Dist.makeUnique(places)) {
            val size = 5;
            val src = new Rail[Double](size);
            src(0) = distV().multTime ;
            src(1) = distV().multComptTime;
            src(2) = distV().allReduceTime;
            src(3) = distV().scattervTime;
            src(4) = distV().gathervTime;     
            
            val dstMax = new Rail[Double](size);
            val dstMin = new Rail[Double](size);
    
            team.allreduce(src, 0, dstMax, 0, size, Team.MAX);
            team.allreduce(src, 0, dstMin, 0, size, Team.MIN);
            
            val maxIndexMultTime = team.indexOfMax(src(0), here.id as Int);            
    
            if (here.id == root.id){
                Console.OUT.println("["+prefix+"]  multTime: indexOfMax("+maxIndexMultTime+")  max: " + dstMax(0) + "  min: " + dstMin(0) );
                //Console.OUT.println("["+prefix+"]  multComptTime: max: " + dstMax(1) + "  min: " + dstMin(1) );
                //Console.OUT.println("["+prefix+"]  allReduceTime: max: " + dstMax(2) + "  min: " + dstMin(2) );
                //Console.OUT.println("["+prefix+"]  scattervTime: max: " + dstMax(3) + "  min: " + dstMin(3) );
                //Console.OUT.println("["+prefix+"]  gathervTime: max: " + dstMax(4) + "  min: " + dstMin(4) );
            }
            
            if (printIterations){
            val debugItSize = 1000;
            var descMultTime:String = prefix+"; multTime; p"+here.id+";";
            var descMultComptTime:String = prefix+"; multComptTime; p"+here.id+";";
            var descAllReduceTime:String = prefix+"; allReduceTime; p"+here.id+";";
            
            for (i in 0..(debugItSize-1)){
                descMultTime += distV().multTimeIt(i) + ";";
                descMultComptTime += distV().multComptTimeIt(i) + ";";
                descAllReduceTime += distV().allReduceTimeIt(i) + ";";
            }
            
            //var str:String = descMultTime + "\n" + descMultComptTime + "\n" + descAllReduceTime;
            
            Console.OUT.println(descMultTime);
            //Console.OUT.println(descMultComptTime);
            //Console.OUT.println(descAllReduceTime);
            }
            
            
        }    
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
        val debugItSize = 1000;
        multTimeIt = new Rail[Long](debugItSize);
        multComptTimeIt = new Rail[Long](debugItSize);
        allReduceTimeIt = new Rail[Long](debugItSize);
        scattervTimeIt = new Rail[Long](debugItSize);
        gathervTimeIt = new Rail[Long](debugItSize);   
    }
    
    public def this(vec:Vector, segSize:Rail[Int], offsets:Rail[Int], placeIndex:Long, snapshotSegSize:Rail[Int], snapshotOffsets:Rail[Int]){
        this.vec = vec;
        this.segSize = segSize;
        this.offsets = offsets;
        val debugItSize = 1000;
        this.placeIndex = placeIndex;
        multTimeIt = new Rail[Long](debugItSize);
        multComptTimeIt = new Rail[Long](debugItSize);
        allReduceTimeIt = new Rail[Long](debugItSize);
        scattervTimeIt = new Rail[Long](debugItSize);
        gathervTimeIt = new Rail[Long](debugItSize);
    
        this.snapshotSegSize = snapshotSegSize;
        this.snapshotOffsets = snapshotOffsets;
    }
    
    
    public def clone():DistVectorLocalState{
        return new DistVectorLocalState(vec.clone(), new Rail[Int](segSize), new Rail[Int](offsets), placeIndex );
    }
    
    public var multTime:Long;
    public var multComptTime:Long;
    public var allReduceTime:Long;
    public var scattervTime:Long;
    public var gathervTime:Long;
    
    
    public var multTimeIndex:Long;
    public var multComptTimeIndex:Long;
    public var allReduceTimeIndex:Long;
    public var scattervTimeIndex:Long;
    public var gathervTimeIndex:Long;
    
    public var multTimeIt:Rail[Long];
    public var multComptTimeIt:Rail[Long];
    public var allReduceTimeIt:Rail[Long];
    public var scattervTimeIt:Rail[Long];
    public var gathervTimeIt:Rail[Long];
    
    
    
}
