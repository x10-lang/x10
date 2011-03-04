/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.array;

import x10.util.ArrayList;
import x10.util.GrowableRail;
import x10.util.Set;
import x10.compiler.Incomplete;

/**
 * The BaseDist class is the base of the hierarchy of classes that
 * implement Dist. BaseDist provides a set of factory methods, and
 * also provides some function common to all Dist implementations,
 * such as default implementations of some Dist methods. Specific
 * Dist implementation classes may override these methods with more
 * efficient implementations.
 */
public class BaseDist extends Dist /*implements Map[Place,Region]*/ {

    //
    // factories - place is all applicable places
    //
    public static def makeUnique1(ps:Sequence[Place]):Dist(1) { // XTENLANG-4
        val size = ps.size();
        // regions
        val init = (i:Int) => Region.makeRectangular(i, i);
        val regions = new Array[Region(1)](size, init).sequence();

        // overall region
        val overall = Region.makeRectangular(0, size-1);

        return new BaseDist(overall, ps, regions);
    }
    
/*

    public static def makeBlockCyclic1(r: Region, axis: int, blockSize: int): Dist(r) { // XTENLANG-4

        if (blockSize<=0)
            throw new IllegalArgumentException("blocksize is " + blockSize + "; it must be >0");

        val b = r.boundingBox();
        val min = b.min()(axis);
        val max = b.max()(axis);

        val init = (i:Int) => Region.makeEmpty(r.rank);
        val regions = Rail.make[Region(r.rank)](Place.MAX_PLACES, init);

        for (var i: int = min, p: int = 0; i<=max; i+=blockSize, p++) {
            val r1 = Region.makeFull(axis);
            val r2 = Region.makeRectangular(i, i+blockSize-1);
            val r3 = Region.makeFull(r.rank-axis-1);
            var rr:Region(r.rank) = r1.product(r2).product(r3) as Region(r.rank);
            rr = rr.intersection(r) as Region(r.rank);
            rr = (regions(p%Place.MAX_PLACES) as Region(r.rank)).union(rr) as Region(r.rank);
            regions(p%Place.MAX_PLACES) = rr;
        }

        return new BaseDist(r, Place.places, regions);
    }
*/
    public static def makeConstant1(r: Region): Dist(r) { // XTENLANG-4
        return makeConstant(r, here);
    }



    //
    // factories - place is a parameter
    //

    @Incomplete public static def makeUnique1(ps: Set[Place]): Dist(1) { // XTENLANG-4
        throw new UnsupportedOperationException();
    }

    public static def makeConstant1(r: Region, p: Place): Dist(r) { // XTENLANG-4
        return new BaseDist(r, new Array[Place][p], new Array[Region(r.rank)][r]);
    }

    @Incomplete public static def makeCyclic1(r: Region, axis: int, ps: Set[Place]): Dist(r) { // XTENLANG-4
        throw new UnsupportedOperationException();
    }
    @Incomplete public static def makeBlock1(r: Region, axis: int, ps: Set[Place]): Dist(r) { // XTENLANG-4
        throw new UnsupportedOperationException();
    }
    @Incomplete public static def makeBlockCyclic1(r: Region, axis: int, blockSize: int, ps: Set[Place]): Dist(r) { // XTENLANG-4
        throw new UnsupportedOperationException();
    }

    //
    // mapping places to region
    //

    public def places():Sequence[Place] = places.sequence();
    public def numPlaces():int = places.size;
    public def regions():Sequence[Region(rank)] = regions.sequence();
    public def get(p: Place): Region(rank) {
        return regionMap(p.id) as Region(rank); // XXXX
    }


    //
    // mapping points to places
    //

    public def apply(pt:Point(rank)):Place {
        for (var i:int=0; i<regionMap.size; i++) {
            if (regionMap(i).contains(pt)) {
                return Place.place(i);
            }
        }
	raiseBoundsError(pt);
	return here; // UNREACHABLE, but front-end doesn't understand @NoReturn
    }

    //
    // Dist op Region
    // Dist op Place
    //

    public def restriction(r: Region(rank)): Dist(rank) {

        // XXX need to throw exception if r is not contained in this.region
        // XXX throw away places that map to empty regions!!!

        // places
        val ps = this.places;

        // regions
        val init = (i:Int):Region(rank) => this.regions(i).intersection(r);
        val rs = new Array[Region(rank)](this.regions.size, init);

        return new BaseDist(r, ps, rs);
    }

    public def restriction(p: Place):Dist(rank) {
        val ps = new Array[Place][p];
        val rs = new Array[Region(rank)][get(p)];
        return new BaseDist(region.intersection(rs(0)) as Region(rank), ps, rs);
    }

    //incomplete public def intersection(r: Region(rank)): Dist(rank);

    //incomplete public def difference(r: Region(rank)): Dist(rank);



    //
    // Dist op Dist
    //

    /*public def intersection(that: Dist(rank)): Dist(rank) {

        // places
        val ps = this.places;

        // regions
        val init: (Int) => Region{self.rank==this.rank} = (i:Int):Region{self.rank==this.rank} => {
            val r1 = this.regions(i) as Region(rank);
            val r2 = that.get(this.places(i)) as Region(rank);
            return r1.intersection(r2);
        };

        val rs: Rail[Region(rank)] = Rail.make[Region(rank)](regions.length, init);

        // overall region
        var overall: Region(rank) = Region.makeEmpty(rank);
        for (r in rs)
            overall = overall.union(r) as Region(rank);

        return new BaseDist(overall, ps, rs);
    }
*/
    /*
    public def difference(that: Dist(rank)): Dist(rank) {

        // places
        val ps = this.places;

        // regions
        val init: (Int)=>Region(rank) = (i:Int): Region(rank) => {
            val r1 = this.regions(i) as Region(rank);
            val r2 = that.get(this.places(i)) as Region(rank);
            return r1.difference(r2);
        };
        val rs = Rail.make[Region(rank)](this.regions.length, init);

        // overall region
        var overall: Region(rank) = Region.makeEmpty(rank);
        for (r in rs)
            overall = overall.union(r) as Region(rank);

        return new BaseDist(overall, ps, rs);
    }

    public def overlay(that: Dist(rank)): Dist(rank) {

        // places
        val ps = Place.places;

        // regions
        val init = (i:Int): Region(rank) => {
            val p = ps(i);
            val r = this.get(p) as Region(rank); // XXXX
            return r.difference(that.region).union(that.get(p));
        };
        val rs = Rail.make[Region(rank)](ps.length, init);

        return new BaseDist(this.region.union(that.region), ps, rs) as Dist(rank);
    }
    public def union(that: Dist(rank)): Dist(rank) {

        // places
        val ps = Place.places;

        // regions
        val init = (i:Int): Region(rank) => {
            val r1 = that.get(ps(i)) as Region(rank); // XXXX
            val r2 = this.get(ps(i)) as Region(rank); // XXXX
            return r2.union(r1);
        };
        val rs = Rail.make[Region(rank)](ps.length, init);

        // overall region
        var overall: Region(rank) = Region.makeEmpty(rank);
        for (r:Region(rank) in rs)
            overall = overall.disjointUnion(r) as Region(rank);

        return new BaseDist(overall, ps, rs) as Dist(rank);
    }
*/

    //
    // basic info
    //

    // XXX should allow places to be in any order??
    protected static def isUnique(places: Sequence[Place]): boolean {
    	val size = places.size();
        if (size!=Place.MAX_PLACES)
            return false;
        for (var i: int = 0; i<size; i++) {
            if (places(i).id!=i)
                return false;
        }
        return true;
    }

    protected static def isConstant(places: Sequence[Place]): boolean {
        for (p in places)
            if (p!=places(0))
                return false;
        return true;
    }

    protected static def onePlace(places: Sequence[Place]): Place {
        return places.size==0? here : places(0);
    }

    public def equals(thatObj:Any): boolean {
	if (!(thatObj instanceof Dist)) return false;
        val that:Dist = thatObj as Dist;
        for (p in Place.places())
            if (!this.get(p).equals(that.get(p)))
                return false;
        return true;
    }


    //
    // places and regions only contain entries for places actually
    // mapped by this Dist. This is guaranteed by the constructor.
    // XXX  revisit whether these are actually needed.
    //
    // regionMap contains an entry for each place for efficient
    // access.
    //

    protected val places:Array[Place]{rail};
    protected val regions:Array[Region(rank)]{rail};
    private val regionMap:Array[Region(rank)]{rail};

    public def this(r: Region, ps:Array[Place](1), rs:Array[Region(r.rank)](1)): BaseDist{self.region==r}{
    	this(r,ps.sequence(), rs.sequence());
    }
    public def this(r: Region, ps: Sequence[Place], rs: Sequence[Region(r.rank)]): BaseDist{self.region==r} {

        super(r, isUnique(ps), isConstant(ps), onePlace(ps));

        // remove empty regions
        val rr = new ArrayList[Region(r.rank)]();
        // FIXME: IP: work around the fact that we cannot create collections of structs
        //val pl = new ArrayList[Place]();
        val pr = new GrowableRail[Place]();
        for (var i:int=0; i<rs.size(); i++) {
            if (!rs(i).isEmpty()) {
                rr.add(rs(i));
                pr.add(ps(i));
            }
        }
        this.regions = new Array[Region(rank)](rr.size(), (i:int)=>rr(i) as Region(this.rank)); 
        this.places = new Array[Place](pr.length(), (i:int)=>pr(i)); 

        // compute the map
        val empty = Region.makeEmpty(rank);
        this.regionMap = new Array[Region(rank)](Place.MAX_PLACES, (Int)=> empty);
        for (var i: int = 0; i<this.places.size; i++)
            regionMap(this.places(i).id) = this.regions(i);
      
    }
}

