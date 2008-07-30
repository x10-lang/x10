package x10.array;

import x10.lang.*;

import java.util.Map;		// for Map
import java.util.Set;		// for Map
import java.util.Collection;	// for Map

public class BaseDist extends Dist implements Map {

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

    public Map regionMap() {
        return this;
    }

    public place [] places() {
        return places;
    }

    public Region [] regions() {
        return regions;
    }

    public Region get(place p) {
        return (Region) (regionMap().get(p));
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
    // java.util.Map (for region map)
    //

    public void clear() {
        throw U.unsupported();
    }

    public boolean containsKey(java.lang.Object key) {
        throw U.unsupported();
    }

    public boolean containsValue(java.lang.Object value) {
        throw U.unsupported();
    }

    public Set entrySet() {
        throw U.unsupported();
    }

    public boolean equals(java.lang.Object o) {
        throw U.unsupported();
    }

    public java.lang.Object get(java.lang.Object key) {
        place p = (place) key;
        Region r = regionMap[p.id]; // XXX check?
        return r;
    }

    public int hashCode() {
        throw U.unsupported();
    }

    public boolean isEmpty() {
        throw U.unsupported();
    }

    public Set keySet() {
        throw U.unsupported();
    }

    public java.lang.Object put(java.lang.Object key, java.lang.Object value) {
        throw U.unsupported();
    }

    public void putAll(Map t) {
        throw U.unsupported();
    }

    public java.lang.Object remove(java.lang.Object key) {
        throw U.unsupported();
    }

    public int size() {
        throw U.unsupported();
    }

    public Collection values() {
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
