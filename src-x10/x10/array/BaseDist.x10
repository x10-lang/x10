// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.util.ArrayList;
import x10.util.Set;


public value class BaseDist extends Dist /*implements Map[Place,Region]*/ {

    // XTENLANG-49
    static type PolyRegion(rank:nat) = PolyRegion{self.rank==rank};
    static type PolyRegionList(rank:nat) = PolyRegionList{self.rank==rank};
    static type Halfspace(rank:nat) = Halfspace{self.rank==rank};
    static type HalfspaceList(rank:nat) = HalfspaceList{self.rank==rank};

    //
    // factories - place is all applicable places
    //

    public static def makeUnique1(): Dist(1) {
        return makeUnique1(Place.places);
    }

    public static def makeUnique1(ps: Rail[Place]): Dist(1) { // XTENLANG-4

        // overall region
        val overall = Region.makeRectangular(0, ps.length-1);

        // regions
        val regions = Rail.makeVar[Region](ps.length);
        for (var i: int = 0; i<ps.length; i++)
            regions(i) = Region.makeRectangular(i, i);

        return new BaseDist(overall, ps, regions);
    }

    public static def makeBlockCyclic1(r: Region, axis: int, blockSize: int): Dist(r.rank) { // XTENLANG-4

        val b = r.boundingBox();
        val min = b.min()(axis);
        val max = b.max()(axis);

        var regions: Rail[Region] = Rail.makeVar[Region](Place.MAX_PLACES);
        for (var i: int = 0; i<Place.MAX_PLACES; i++)
            regions(i) = Region.makeEmpty(r.rank);

        for (var i: int = min, p: int = 0; i<=max; i+=blockSize, p++) {
            val r1 = Region.makeFull(axis);
            val r2 = Region.makeRectangular(i, i+blockSize-1);
            val r3 = Region.makeFull(r.rank-axis-1);
            var rr:Region(r.rank) = r1.product(r2).product(r3) as Region(r.rank);
            rr = rr.intersection(r);
            rr = (regions(p%Place.MAX_PLACES) as Region(r.rank)).union(rr);
            regions(p%Place.MAX_PLACES) = rr;
        }

        return new BaseDist(r, Place.places, regions);
    }

    public static def makeConstant1(r: Region): Dist(r.rank) { // XTENLANG-4
        return makeConstant(r, here);
    }



    //
    // factories - place is a parameter
    //

    incomplete public static def makeUnique1(ps: Set[Place]): Dist(1); // XTENLANG-4

    public static def makeConstant1(r: Region, p: Place): Dist(r.rank) { // XTENLANG-4
        return new BaseDist(r, [p], [r]);
    }

    incomplete public static def makeCyclic1(r: Region, axis: int, ps: Set[Place]): Dist(r.rank); // XTENLANG-4

    incomplete public static def makeBlock1(r: Region, axis: int, ps: Set[Place]): Dist(r.rank); // XTENLANG-4

    incomplete public static def makeBlockCyclic1(r: Region, axis: int, blockSize: int, ps: Set[Place]): Dist(r.rank); // XTENLANG-4


    //
    // mapping places to region
    //

    public def places(): Rail[Place] {
        return places;
    }

    public def regions(): Rail[Region] {
        return regions;
    }

    public def get(p: Place): Region(rank) {
        return regionMap(p.id) as Region(rank); // XXXX
    }

    public def apply(p: Place): Region(rank) = get(p);


    //
    // mapping points to places
    //

    public def apply(pt: Point/*(rank)*/): Place {
        for (var i:int=0; i<regionMap.length; i++)
            if (regionMap(i).contains(pt as Point(rank)))
                return Place.places(i);
        throw new ArrayIndexOutOfBoundsException("point " + pt + " not contained in distribution");
    }

    public def apply(i0: int) = apply([i0] to Point(rank));
    public def apply(i0: int, i1: int) = apply([i0,i1] to Point(rank));
    public def apply(i0: int, i1: int, i2: int) = apply([i0,i1,i2] to Point(rank));
    public def apply(i0: int, i1: int, i2: int, i3: int) = apply([i0,i1,i2,i3] to Point(rank));


    //
    // Dist op Region
    // Dist op Place
    //

    public def restriction(r: Region(rank)): Dist(rank) {

        // XXX need to throw exception if r is not contained in this.region
        // XXX throw away places that map to empty regions!!!

        // places
        val ps = Rail.makeVar[Place](this.places.length);
        for (var i: int = 0; i<this.places.length; i++)
            ps(i) = this.places(i);

        // regions
        val rs = Rail.makeVar[Region](this.regions.length);
        for (var i: int = 0; i<this.regions.length; i++)
            rs(i) = (this.regions(i) as Region(rank)).intersection(r);

        return new BaseDist(r, ps, rs);
    }

    public def restriction(p: Place): Dist(rank) {
        val ps = [p];
        //var rs: Rail[Region] = [get(p)]; XTENLANG-52
        val rs = Rail.makeVal[Region](1, (nat)=>get(p));
        return new BaseDist(region.intersection(rs(0) as Region(rank)), ps, rs);
    }

    incomplete public def intersection(r: Region(rank)): Dist(rank);

    incomplete public def difference(r: Region(rank)): Dist(rank);



    //
    // Dist op Dist
    //

    public def intersection(that: Dist(rank)): Dist(rank) {

        // places
        val ps = Rail.makeVar[Place](this.places.length);
        for (var i: int = 0; i<this.places.length; i++)
            ps(i) = this.places(i);

        // regions
        val rs = Rail.makeVar[Region](this.regions.length);
        var overall: Region(rank) = Region.makeEmpty(rank);
        for (var i: int = 0; i<this.regions.length; i++) {
            val r1 = this.regions(i) as Region(rank);
            val r2 = that.get(this.places(i)) as Region(rank);
            val r = r1.intersection(r2);
            rs(i) = r;
            overall = overall.union(r);
        }

        return new BaseDist(overall, ps, rs);
    }

    public def difference(that: Dist(rank)): Dist(rank) {

        // places
        val ps = Rail.makeVar[Place](this.places.length);
        for (var i: int = 0; i<this.places.length; i++)
            ps(i) = this.places(i);

        // regions
        val rs = Rail.makeVar[Region](this.regions.length);
        var overall: Region(rank) = Region.makeEmpty(rank);
        for (var i: int = 0; i<this.regions.length; i++) {
            val r1 = this.regions(i) as Region(rank);
            val r2 = that.get(this.places(i)) as Region(rank);
            val r = r1.difference(r2);
            rs(i) = r;
            overall = overall.union(r);
        }

        return new BaseDist(overall, ps, rs);
    }

    public def overlay(that: Dist(rank)): Dist(rank) {

        // places
        val ps = Place.places;

        // regions
        val rs = Rail.makeVar[Region](ps.length);
        for (var i: int = 0; i<ps.length; i++) {
            val p = ps(i);
            val r = this.get(p) as Region(rank); // XXXX
            rs(i) = r.difference(that.region).union(that.get(p));
        }

        return new BaseDist(this.region.union(that.region), ps, rs);
    }

    public def union(that: Dist(rank)): Dist(rank) {

        // places
        val ps = Place.places;

        // regions
        val rs = Rail.makeVar[Region](ps.length);
        var overall: Region(rank) = Region.makeEmpty(rank);
        for (var i: int = 0; i<ps.length; i++) {
            val r1 = that.get(ps(i)) as Region(rank); // XXXX
            val r2 = this.get(ps(i)) as Region(rank); // XXXX
            rs(i) = r2.union(r1);
            overall = overall.disjointUnion(rs(i) as Region(rank));
        }

        return new BaseDist(overall, ps, rs);
    }

    public def isSubdistribution(that: Dist(rank)): boolean {
        val ps = Place.places;
        for (var i: int = 0; i<ps.length; i++)
            if (!that.get(ps(i)).contains(this.get(ps(i))))
                return false;
        return true;
    }


    //
    // basic info
    //

    // XXX should allow places to be in any order??
    protected static def isUnique(places: Rail[Place]): boolean {
        if (places.length!=Place.MAX_PLACES)
            return false;
        for (var i: int = 0; i<places.length; i++) {
            if (places(i).id!=i)
                return false;
        }
        return true;
    }

    protected static def isConstant(places: Rail[Place]): boolean {
        val p = places(0);
        for (var i: int = 1; i<places.length; i++) {
            if (places(i)!=p)
                return false;
        }
        return true;
    }

    protected static def onePlace(places: Rail[Place]): Place {
        return places(0);
    }

    public def equals(that: Dist/*(rank)*/): boolean {
        for (var i: int = 0; i<Place.MAX_PLACES; i++) {
            val p = Place.places(i);
            if (!this.get(p).equals(that.get(p)))
                return false;
        }
        return true;
    }


    public def contains(p:Point) = region.contains(p);


    //
    // places and regions only contain entries for places actually
    // mapped by this Dist. This is guaranteed by the constructor.
    // XXX  revisit whether these are actually needed.
    //
    // regionMap contains an entry for each place for efficient
    // access.
    //

    protected val places: Rail[Place];
    protected val regions: Rail[Region];
    private val regionMap: Rail[Region] = Rail.makeVar[Region](Place.MAX_PLACES);

    /*
    protected def this(region: Region, unique: boolean, constant: boolean, onePlace: Place): BaseDist {
        super(region, unique, constant, onePlace);
    }
    */

    protected def this(r: Region, ps: Rail[Place], rs: Rail[Region]): BaseDist{self.rank==r.rank} {

        super(r, isUnique(ps), isConstant(ps), onePlace(ps));

        // remove empty regions
        val rl = new ArrayList[Region]();
        val pl = new ArrayList[Place]();
        for (var i: int = 0; i<rs.length; i++) {
            if (/*rs(i)!=null &&*/ !rs(i).isEmpty()) {
                rl.add(rs(i) as Region);
                pl.add(ps(i));
            }
        }
        this.regions = rl.toArray();
        this.places = pl.toArray();

        // compute the map
        for (var i: int = 0; i<regionMap.length; i++)
            regionMap(i) = Region.makeEmpty(rank);
        for (var i: int = 0; i<this.places.length; i++)
            regionMap(this.places(i).id) = this.regions(i);
    }


    //
    //
    //

    public def toString(): String {
        var s: String = "Dist(";
        var first: boolean = true;
        for (var i: int = 0; i<places.length; i++) {
            if (!first) s += ",";
            s += places(i).id + "->" + get(places(i));
            first = false;
        }
        s += ")";
        return s;
    }

}

