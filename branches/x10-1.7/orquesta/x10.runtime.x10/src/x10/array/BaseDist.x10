package x10.array;

import x10.lang.*;

import x10.util.Map_place_Region;
import x10.util.Set_place;
import x10.util.ArrayList_Region;
import x10.util.ArrayList_place;


public class BaseDist extends Dist implements Map_place_Region {

    // XXX make this a static variable - but that tickles 1.5 compiler
    // crash so do this for now
    private static place [] allPlaces() {
        place [] ps = new place[place.MAX_PLACES];
        for (int i=0; i<place.MAX_PLACES; i++)
            ps[i] = place.factory.place(i);
        return ps;
    }


    //
    // factories - place is all applicable places
    //

    public static Dist makeUnique() {

        // overall region
        Region overall = Region.makeRectangular(0,place.MAX_PLACES-1);

        // places
        place [] places = allPlaces();

        // regions
        Region [] regions = new Region[place.MAX_PLACES];
        for (int i=0; i<place.MAX_PLACES; i++)
            regions[i] = Region.makeRectangular(i, i);

        return new BaseDist(overall, places, regions);
    }


    public static Dist makeBlockCyclic(Region r, int axis, int blockSize) {

        Region b = r.boundingBox();
        int min = b.min()[axis];
        int max = b.max()[axis];

        place [] places = allPlaces();

        Region [] regions = new Region[place.MAX_PLACES];
        for (int i=0; i<place.MAX_PLACES; i++)
            regions[i] = Region.makeEmpty(r.rank);

        for (int i=min, p=0; i<=max; i+=blockSize, p++) {
            Region r1 = Region.makeFull(axis);
            Region r2 = Region.makeRectangular(i, i+blockSize-1);
            Region r3 = Region.makeFull(r.rank-axis-1);
            Region rr = r1.product(r2).product(r3).intersection(r);
            regions[p%place.MAX_PLACES] = regions[p%place.MAX_PLACES].union(rr);
        }

        return new BaseDist(r, places, regions);
    }

    public static Dist makeConstant(Region r) {
        return makeConstant(r, here);
    }



    //
    // factories - place is a parameter
    //

    public static Dist makeUnique(Set_place ps) {
        throw U.unsupported("makeUnique");
    }

    public static Dist makeConstant(Region r, place p) {
        Dist result = new BaseDist(r, new place [] {p}, new Region [] {r});
        return result;
    }

    public static Dist makeCyclic(Region r, int axis, Set_place ps) {
        throw U.unsupported("makeCyclic");
    }

    public static Dist makeBlock(Region r, int axis, Set_place ps) {
        throw U.unsupported("makeBlock");
    }

    public static Dist makeBlockCyclic(Region r, int axis, int blockSize, Set_place ps) {
        throw U.unsupported("makeBlockCyclic");
    }


    //
    // mapping places to region
    //

    public Map_place_Region regionMap() {
        return this;
    }

    public place [] places() {
        return places;
    }

    public Region [] regions() {
        return regions;
    }

    public Region get(place p) {
        return regionMap[p.id];
    }



    //
    // Dist op Region
    // Dist op Place
    //

    public Dist restriction(Region r) {

        // XXX need to throw exception if r is not contained in this.region
        // XXX throw away places that map to empty regions!!!

        // places
        place [] ps = new place[this.places.length];
        for (int i=0; i<this.places.length; i++)
            ps[i] = this.places[i];

        // regions
        Region [] rs = new Region[this.regions.length];
        for (int i=0; i<this.regions.length; i++)
            rs[i] = this.regions[i].intersection(r);

        return new BaseDist(r, ps, rs);
    }

    public Dist restriction(place p) {
        place [] ps = new place[] {p};
        Region [] rs = new Region[] {get(p)};
        return new BaseDist(region.intersection(rs[0]), ps, rs);
    }

    public  Dist intersection(Region r) {
        throw U.unsupported(this, "intersection(Region)");
    }

    public  Dist difference(Region r) {
        throw U.unsupported(this, "intersection(Region)");
    }



    //
    // Dist op Dist
    //

    public Dist intersection(Dist that) {

        // places
        place [] ps = new place[this.places.length];
        for (int i=0; i<this.places.length; i++)
            ps[i] = this.places[i];

        // regions
        Region [] rs = new Region[this.regions.length];
        Region overall = Region.makeEmpty(rank);
        for (int i=0; i<this.regions.length; i++) {
            Region r = this.regions[i].intersection(that.get(this.places[i]));
            rs[i] = r;
            overall = overall.union(r);
        }

        return new BaseDist(overall, ps, rs);
    }

    public Dist difference(Dist that) {

        // places
        place [] ps = new place[this.places.length];
        for (int i=0; i<this.places.length; i++)
            ps[i] = this.places[i];

        // regions
        Region [] rs = new Region[this.regions.length];
        Region overall = Region.makeEmpty(rank);
        for (int i=0; i<this.regions.length; i++) {
            Region r = this.regions[i].difference(that.get(this.places[i]));
            rs[i] = r;
            overall = overall.union(r);
        }

        return new BaseDist(overall, ps, rs);
    }

    public Dist overlay(Dist that) {

        // places
        place [] ps = allPlaces();

        // regions
        Region [] rs = new Region[ps.length];
        for (int i=0; i<ps.length; i++) {
            place p = ps[i];
            rs[i] = this.get(p).difference(that.region).union(that.get(p));
        }

        return new BaseDist(this.region.union(that.region), ps, rs);
    }

    public Dist union(Dist that) {

        // places
        place [] ps = allPlaces();

        // regions
        Region [] rs = new Region[ps.length];
        Region overall = Region.makeEmpty(rank);
        for (int i=0; i<ps.length; i++) {
            rs[i] = this.get(ps[i]).union(that.get(ps[i]));
            overall = overall.disjointUnion(rs[i]);
        }

        return new BaseDist(overall, ps, rs);
    }

    public  boolean isSubdistribution(Dist that) {
        place [] ps = allPlaces();
        for (int i=0; i<ps.length; i++)
            if (!that.get(ps[i]).contains(this.get(ps[i])))
                return false;
        return true;
    }


    //
    // basic info
    //

    // XXX should allow places to be in any order??
    protected static boolean isUnique(place [] places) {
        if (places.length!=place.MAX_PLACES)
            return false;
        for (int i=0; i<places.length; i++) {
            if (places[i].id!=i)
                return false;
        }
        return true;
    }

    protected static boolean isConstant(place [] places) {
        place p = places[0];
        for (int i=1; i<places.length; i++) {
            if (places[i]!=p)
                return false;
        }
        return true;
    }

    protected static place onePlace(place [] places) {
        return places[0];
    }

    public boolean equals(Dist that) {
        for (int i=0; i<place.MAX_PLACES; i++) {
            place p = place.factory.place(i);
            if (!this.get(p).equals(that.get(p)))
                return false;
        }
        return true;
    }


    //
    // places, regions, and regionMap only contain entries places
    // actually mapped by this Dist. This is guaranteed by the
    // constructor.
    //

    protected place [] places;
    protected Region [] regions;
    private Region [] regionMap = new Region[place.MAX_PLACES];

    protected BaseDist(Region region, boolean unique, boolean constant, nullable<place> onePlace) {
        super(region, unique, constant, onePlace);
    }

    protected BaseDist(Region r, place [] ps, nullable<Region> [] rs) {

        this(r, isUnique(ps), isConstant(ps), onePlace(ps));

        // remove empty regions
        ArrayList_Region rl = new ArrayList_Region();
        ArrayList_place pl = new ArrayList_place();
        for (int i=0; i<rs.length; i++) {
            if (rs[i]!=null && !rs[i].isEmpty()) {
                rl.add((Region)rs[i]);
                pl.add(ps[i]);
            }
        }
        this.regions = rl.toArray();
        this.places = pl.toArray();

        // compute the map
        for (int i=0; i<regionMap.length; i++)
            regionMap[i] = Region.makeEmpty(rank);
        for (int i=0; i<this.places.length; i++)
            regionMap[this.places[i].id] = this.regions[i];
    }


    //
    //
    //

    public String toString() {
        String s = "Dist(";
        boolean first = true;
        for (int i=0; i<places.length; i++) {
            if (!first) s += ",";
            s += places[i].id + "->" + get(places[i]);
            first = false;
        }
        s += ")";
        return s;
    }

}    
