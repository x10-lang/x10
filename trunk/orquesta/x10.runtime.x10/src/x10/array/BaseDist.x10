package x10.array;

import java.util.Set;

import x10.lang.*;
import x10.util.Map_place_Region;


public class BaseDist extends Dist implements Map_place_Region {

    //
    // factories - place is all applicable places
    //

    public static Dist makeUnique() {
        return new UniqueDist();
    }

    public static Dist makeConstant(Region r) {
        return makeConstant(r, here);
    }

    public static Dist makeCyclic(Region r, int axis) {
        throw U.unsupported();
    }

    public static Dist makeBlock(Region r, int axis) {
        throw U.unsupported();
    }

    public static Dist makeBlockCyclic(Region r, int axis, int blockSize) {
        throw U.unsupported();
    }


    //
    // factories - place is a parameter
    //

    public static Dist makeUnique(Set ps) {
        throw U.unsupported();
    }

    public static Dist makeConstant(Region r, place p) {
        return new ConstantDist(r, p);
    }

    public static Dist makeCyclic(Region r, int axis, Set ps) {
        throw U.unsupported();
    }

    public static Dist makeBlock(Region r, int axis, Set ps) {
        throw U.unsupported();
    }

    public static Dist makeBlockCyclic(Region r, int axis, int blockSize, Set ps) {
        throw U.unsupported();
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
        throw U.unsupported();
    }

    public  Dist difference(Dist d) {
        throw U.unsupported();
    }

    public  Dist union(Dist d) {
        throw U.unsupported();
    }

    public  Dist intersection(Region r) {
        throw U.unsupported();
    }

    public  Dist intersection(Dist d) {
        throw U.unsupported();
    }

    public  Dist overlay(Dist d) {
        throw U.unsupported();
    }

    public  boolean isSubDistribution(Dist d) {
        throw U.unsupported();
    }

    public  Dist restriction(Dist d) {
        throw U.unsupported();
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

    protected BaseDist(Region region, boolean unique, boolean constant, nullable<place> onePlace) {
        super(region, unique, constant, onePlace);
    }
}    
