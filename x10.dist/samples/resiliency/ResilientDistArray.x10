/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2013.
 */
import x10.regionarray.*;

/**
 * ResilientDistArray which supports snapshot and restore
 * Currently limited methods necessary for ResilientHeatTransfer are implemented
 * @author kawatiya
 */
public class ResilientDistArray[T](region:Region) implements (Point(region.rank))=>T {
    public property rank():Int = region.rank;
    public var dist:Dist; // mutable (but should not be set from outside)
    private var da:DistArray[T](rank);
    private def this(dist:Dist, da:DistArray[T](dist.rank)) {
        property(dist.region);
        this.dist = dist; this.da = da;
    }
    public static def make[T](dist:Dist, init:(Point(dist.rank))=>T) = new ResilientDistArray[T](dist, DistArray.make[T](dist, init));
    public static def make[T](dist:Dist){T haszero} = new ResilientDistArray[T](dist, DistArray.make[T](dist));
    public final operator this(pt:Point(rank)) = da(pt);
    public final operator this(i0:long, i1:long){rank==2} = da(i0, i1);
    public final operator this(pt:Point(rank))=(v:T):T { da(pt) = v; return v; }
    public final operator this(i0:long, i1:long)=(v:T){rank==2}:T { da(i0, i1) = v; return v; }
    public final def map[S,U](dst:ResilientDistArray[S](rank), src:ResilientDistArray[U](rank), filter:Region(rank), op:(T,U)=>S):ResilientDistArray[S](rank) {
        da.map(dst.da, src.da, filter, op); return dst;
    }
    public final def reduce(op:(T,T)=>T, unit:T):T =  da.reduce(op, unit);
    
    /*
     * Snapshot mechanism
     * TODO: This currently uses Place0 to keep a snapshot, but should be imporoved to use resilient store
     */
    private val ss = at (Place.FIRST_PLACE) GlobalRef[Cell[Array[T](region)]](new Cell[Array[T](region)](null));
    
    public def snapshot() {
        at (ss) {
            val a = new Array[T](region, (p:Point)=>at (dist(p)) da(p));
            ss()() = a; // replace only when "a" is created successfully
        }
    }
    
    public def restore(dist:Dist){dist.region==region} {
        this.dist = dist;
        da = DistArray.make[T](dist, (p:Point)=>at (ss) ss()()(p));
    }
    
    public def remake(dist:Dist, init:(Point(dist.rank))=>T){dist.region==region} {
        this.dist = dist;
        da = DistArray.make[T](dist, init);
    }
    
    public def remake(dist:Dist){dist.region==region,T haszero} {
        this.dist = dist;
        da = DistArray.make[T](dist);
    }
}
static public type ResilientDistArray[T](r:Int) = ResilientDistArray[T]{self.rank==r};
