package x10.array;

import x10.lang.*;

import x10.util.Map_place_Region;
import x10.util.Set_place;


public class BaseDist extends Dist implements Map_place_Region {

    //
    // factories - place is all applicable places
    //

    public static Dist makeUnique() {

        Region r = Region.makeRectangular(0,place.MAX_PLACES-1);

        // places
        place [] places = new place[place.MAX_PLACES];
        for (int i=0; i<place.MAX_PLACES; i++)
            places[i] = place.factory.place(i);

        // regions
        Region [] regions = new Region[place.MAX_PLACES];
        for (int i=0; i<place.MAX_PLACES; i++)
            regions[i] = Region.makeRectangular(i, i);

        return new BaseDist(r, places, regions);
    }


    public static Dist makeConstant(Region r) {
        return makeConstant(r, here);
    }

    public static Dist makeCyclic(Region r, int axis) {
        throw U.unsupported("Dist.makeCyclic");
    }

    public static Dist makeBlock(Region r, int axis) {
        throw U.unsupported("Dist.makeBlock");
    }

    public static Dist makeBlockCyclic(Region r, int axis, int blockSize) {
        throw U.unsupported("Dist.makeBlockCyclic");
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
        return regionMap[p.id]; // XXX check?
    }


    //
    // region operations
    //

    public  Dist difference(Region r) {
        throw U.unsupported(this, "difference(Region)");
    }

    public  Dist difference(Dist d) {
        throw U.unsupported(this, "difference(Dist)");
    }

    public  Dist union(Dist d) {
        throw U.unsupported(this, "union(Dist)");
    }

    public  Dist intersection(Region r) {
        throw U.unsupported(this, "intersection(Region)");
    }

    public  Dist intersection(Dist d) {
        throw U.unsupported(this, "intersection(Dist)");
    }

    public  Dist overlay(Dist d) {
        throw U.unsupported(this, "overlay(Dist)");
    }

    public  boolean isSubDistribution(Dist d) {
        throw U.unsupported(this, "isSubDistribution(Dist)");
    }

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


    //
    //
    //

    protected place [] places;
    protected Region [] regions;

    private Region [] regionMap = new Region[place.MAX_PLACES];

    protected void initRegionMap() {
        for (int i=0; i<places.length; i++)
            regionMap[places[i].id] = regions[i];
    }

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

    protected BaseDist(Region region, boolean unique, boolean constant, nullable<place> onePlace) {
        super(region, unique, constant, onePlace);
    }

    protected BaseDist(Region r, place [] places, Region [] regions) {
        this(r, isUnique(places), isConstant(places), onePlace(places));
        this.places = places;
        this.regions = regions;
        initRegionMap();
    }

}    
