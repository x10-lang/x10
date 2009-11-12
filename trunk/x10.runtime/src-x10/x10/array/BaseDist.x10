// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.util.ArrayList;
import x10.util.GrowableRail;
import x10.util.Set;

/**
 * The BaseDist class is the base of the hierarchy of classes that
 * implement Dist. BaseDist provides a set of factory methods, and
 * also provides some function common to all Dist implementations,
 * such as default implementations of some Dist methods. Specific
 * Dist implementation classes may override these methods with more
 * efficient implementations.
 *
 * @author bdlucas
 */

public class BaseDist extends Dist /*implements Map[Place,Region]*/ {

    // XTENLANG-49
    static type PolyRegion(rank:nat) = PolyRegion{self.rank==rank};
    static type PolyRegionListBuilder(rank:nat) = PolyRegionListBuilder{self.rank==rank};
    static type PolyRow(rank:nat) = PolyRow{self.rank==rank};
    static type PolyMat(rank:nat) = PolyMat{self.rank==rank};

    //
    // factories - place is all applicable places
    //

    // There's only one unique distribution
    private static val UNIQUE = makeUnique1(Place.places) as Dist(1){rect};
    public static def makeUnique1(): Dist(1){rect} {
        return UNIQUE;
    }

    public static def makeUnique1(ps: Rail[Place]): Dist(1) { // XTENLANG-4

        // regions
        val init = (i:nat) => Region.makeRectangular(i, i);
        val regions = Rail.makeVal[Region](ps.length, init);

        // overall region
        val overall = Region.makeRectangular(0, ps.length-1);

        return new BaseDist(overall, ps, regions);
    }

    public static def makeBlockCyclic1(r: Region, axis: int, blockSize: int): Dist(r) { // XTENLANG-4

        if (blockSize<=0)
            throw new IllegalArgumentException("blocksize is " + blockSize + "; it must be >0");

        val b = r.boundingBox();
        val min = b.min()(axis);
        val max = b.max()(axis);

        val init = (i:nat) => Region.makeEmpty(r.rank);
        var regions:Rail[Region]! = Rail.makeVar[Region](Place.MAX_PLACES, init);

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

    public static def makeConstant1(r: Region): Dist(r) { // XTENLANG-4
        return makeConstant(r, here);
    }



    //
    // factories - place is a parameter
    //

    incomplete public static def makeUnique1(ps: Set[Place]): Dist(1); // XTENLANG-4

    public static def makeConstant1(r: Region, p: Place): Dist(r) { // XTENLANG-4
        return new BaseDist(r, [p], [r]);
    }

    incomplete public static def makeCyclic1(r: Region, axis: int, ps: Set[Place]): Dist(r); // XTENLANG-4

    incomplete public static def makeBlock1(r: Region, axis: int, ps: Set[Place]): Dist(r); // XTENLANG-4

    incomplete public static def makeBlockCyclic1(r: Region, axis: int, blockSize: int, ps: Set[Place]): Dist(r); // XTENLANG-4


    //
    // mapping places to region
    //

    public global def places(): ValRail[Place] {
        return places;
    }

    public global def regions(): ValRail[Region] {
        return regions;
    }

    public global def get(p: Place): Region(rank) {
        return regionMap(p.id) as Region(rank); // XXXX
    }

    public global def apply(p: Place): Region(rank) = get(p);


    //
    // mapping points to places
    //

    public global def apply(pt: Point/*(rank)*/): Place {
        for (var i:int=0; i<regionMap.length; i++)
            if (regionMap(i).contains(pt as Point(rank)))
                return Place.places(i);
        throw new ArrayIndexOutOfBoundsException("point " + pt + " not contained in distribution");
    }

    public global def apply(i0: int) = apply([i0] as Point(rank));
    public global def apply(i0: int, i1: int) = apply([i0,i1] as Point(rank));
    public global def apply(i0: int, i1: int, i2: int) = apply([i0,i1,i2] as Point(rank));
    public global def apply(i0: int, i1: int, i2: int, i3: int) = apply([i0,i1,i2,i3] as Point(rank));


    //
    // Dist op Region
    // Dist op Place
    //

    public global def restriction(r: Region(rank)): Dist(rank) {

        // XXX need to throw exception if r is not contained in this.region
        // XXX throw away places that map to empty regions!!!

        // places
        val ps = this.places;

        // regions
        val init = (i:nat) => (this.regions(i) as Region(rank)).intersection(r);
        val rs = Rail.makeVal[Region](this.regions.length, init);

        return new BaseDist(r, ps, rs);
    }

    public global def restriction(p: Place): Dist(rank) {
        val ps = [p];
        val rs = Rail.makeVal[Region](1, (nat)=>get(p));
        return new BaseDist(region.intersection(rs(0) as Region(rank)), ps, rs) as Dist(rank);
    }

    incomplete public global def intersection(r: Region(rank)): Dist(rank);

    incomplete public global def difference(r: Region(rank)): Dist(rank);



    //
    // Dist op Dist
    //

    public global def intersection(that: Dist(rank)): Dist(rank) {

        // places
        val ps = this.places;

        // regions
        val init: (nat) => Region{self.rank==this.rank} = (i:nat):Region{self.rank==this.rank} => {
            val r1 = this.regions(i) as Region(rank);
            val r2 = that.get(this.places(i)) as Region(rank);
            return r1.intersection(r2);
        };

        val rs: ValRail[Region(rank)] = Rail.makeVal[Region(rank)](regions.length, init);

        // overall region
        var overall: Region(rank) = Region.makeEmpty(rank);
        for (r in rs)
            overall = overall.union(r) as Region(rank);

        return new BaseDist(overall as Region(this.rank), ps, rs);
    }

    public global def difference(that: Dist(rank)): Dist(rank) {

        // places
        val ps = this.places;

        // regions
        val init: (nat)=>Region(rank) = (i:nat): Region(rank) => {
            val r1 = this.regions(i) as Region(rank);
            val r2 = that.get(this.places(i)) as Region(rank);
            return r1.difference(r2);
        };
        val rs = Rail.makeVal[Region(rank)](this.regions.length, init);

        // overall region
        var overall: Region(rank) = Region.makeEmpty(rank);
        for (r in rs)
            overall = overall.union(r) as Region(rank);

        return new BaseDist(overall, ps, rs);
    }

    public global def overlay(that: Dist(rank)): Dist(rank) {

        // places
        val ps = Place.places;

        // regions
        val init = (i:nat): Region(rank) => {
            val p = ps(i);
            val r = this.get(p) as Region(rank); // XXXX
            return r.difference(that.region).union(that.get(p));
        };
        val rs = Rail.makeVal[Region(rank)](ps.length, init);

        return new BaseDist(this.region.union(that.region), ps, rs) as Dist(rank);
    }

    public global def union(that: Dist(rank)): Dist(rank) {

        // places
        val ps = Place.places;

        // regions
        val init = (i:nat): Region(rank) => {
            val r1 = that.get(ps(i)) as Region(rank); // XXXX
            val r2 = this.get(ps(i)) as Region(rank); // XXXX
            return r2.union(r1);
        };
        val rs = Rail.makeVal[Region(rank)](ps.length, init);

        // overall region
        var overall: Region(rank) = Region.makeEmpty(rank);
        for (r:Region(rank) in rs)
            overall = overall.disjointUnion(r) as Region(rank);

        return new BaseDist(overall, ps, rs) as Dist(rank);
    }

    public global def isSubdistribution(that: Dist(rank)): boolean {
        for (p:Place in Place.places)
            if (!that.get(p).contains(this.get(p)))
                return false;
        return true;
    }


    //
    // basic info
    //

    // XXX should allow places to be in any order??
    protected static def isUnique(places: Rail[Place]!): boolean {
        if (places.length!=Place.MAX_PLACES)
            return false;
        for (var i: int = 0; i<places.length; i++) {
            if (places(i).id!=i)
                return false;
        }
        return true;
    }

    protected static def isConstant(places: Rail[Place]!): boolean {
        for (p:Place in places)
            if (p!=places(0))
                return false;
        return true;
    }

    protected static def onePlace(places: Rail[Place]!): Place {
        return places(0);
    }

    public global def equals(thatObj:Object): boolean {
	if (!(thatObj instanceof Dist)) return false;
        val that:Dist = thatObj as Dist;
        for (p:Place in Place.places)
            if (!this.get(p).equals(that.get(p)))
                return false;
        return true;
    }


    public global def contains(p:Point) = region.contains(p);


    //
    // places and regions only contain entries for places actually
    // mapped by this Dist. This is guaranteed by the constructor.
    // XXX  revisit whether these are actually needed.
    //
    // regionMap contains an entry for each place for efficient
    // access.
    //

    protected global val places: ValRail[Place];
    protected global val regions: ValRail[Region];
    private global val regionMap: ValRail[Region];

    protected def this(r: Region, ps: Rail[Place]!, rs: Rail[Region]!): BaseDist{self.region==r} {

        super(r, isUnique(ps), isConstant(ps), onePlace(ps));

        // remove empty regions
        val rl = new ArrayList[Region]();
        // FIXME: IP: work around the fact that we cannot create collections of structs
        //val pl = new ArrayList[Place]();
        val pl = new GrowableRail[Place]();
        for (var i:int=0; i<rs.length; i++) {
            if (!rs(i).isEmpty()) {
                rl.add(rs(i));
                pl.add(ps(i));
            }
        }
        this.regions = rl.toValRail();
        this.places = pl.toValRail();

        // compute the map
        val empty = Region.makeEmpty(rank);
        val regionMap = Rail.makeVar[Region](Place.MAX_PLACES, (nat)=>empty);
        for (var i: int = 0; i<this.places.length; i++)
            regionMap(this.places(i).id) = this.regions(i);
        this.regionMap = Rail.makeVal[Region](regionMap.length, (i:nat) => regionMap(i));
    }


    //
    //
    //

    public global def toString(): String {
        var s: String = "Dist(";
        var first: boolean = true;
        for (p:Place in places) {
            if (!first) s += ",";
            s += p.id + "->" + get(p);
            first = false;
        }
        s += ")";
        return s;
    }

}

