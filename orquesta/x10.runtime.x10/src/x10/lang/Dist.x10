package x10.lang;

import java.util.Map;
import java.util.Set;

import x10.array.BaseDist;

public abstract value class Dist {

    public static final Dist UNIQUE = makeUnique();

    //
    // properties
    // XXX change impl to support this
    //

    //public final int rank;
    //public final boolean rect;
    //public final boolean zeroBased;
    //public final boolean rail;

    //public final place onePlace;
    //public final boolean constant;
    //public final boolean unique;


    //
    // basic info
    // XXX should be property?
    //

    public abstract Region region();


    //
    // factories - place is all applicable places
    //

    public static Dist makeUnique() {
        return BaseDist.makeUnique();
    }

    public static Dist makeConstant(Region r) {
        return BaseDist.makeConstant(r);
    }

    public static Dist makeCyclic(Region r, int axis) {
        return BaseDist.makeCyclic(r, axis);
    }

    public static Dist makeBlock(Region r, int axis) {
        return BaseDist.makeBlock(r, axis);
    }

    public static Dist makeBlockCyclic(Region r, int axis, int blockSize) {
        return BaseDist.makeBlockCyclic(r, axis, blockSize);
    }

    //
    // factories - place is a parameter
    //

    public static Dist makeUnique(Set ps) {
        return BaseDist.makeUnique(ps);
    }

    public static Dist makeConstant(Region r, place p) {
        return BaseDist.makeConstant(r, p);
    }

    public static Dist makeCyclic(Region r, int axis, Set ps) {
        return BaseDist.makeCyclic(r, axis, ps);
    }

    public static Dist makeBlock(Region r, int axis, Set ps) {
        return BaseDist.makeBlock(r, axis, ps);
    }

    public static Dist makeBlockCyclic(Region r, int axis, int blockSize, Set ps) {
        return BaseDist.makeBlockCyclic(r, axis, blockSize, ps);
    }


    //
    // mapping places to regions
    //

    public abstract Map regionMap();	// place->region
    public abstract place [] places();	// essentially regionMap().keys()
    public abstract Region [] regions();// essentially regionMap().values()
    public abstract Region get(place p);


    //
    // region operations
    //

    public abstract Dist difference(Region r);
    public abstract Dist difference(Dist d);
    public abstract Dist union(Dist d);
    public abstract Dist intersection(Region r);
    public abstract Dist intersection(Dist d);
    public abstract Dist overlay(Dist d);
    public abstract boolean isSubDistribution(Dist d);
    public abstract Dist restriction(Dist d);

}


