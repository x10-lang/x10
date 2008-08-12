package x10.lang;

import x10.lang.Object;

import x10.array.BaseArray_T;

public abstract value class Array_T(

    // Region properties
    Region region,
    int rank,
    boolean rect,
    boolean zeroBased,
    boolean rail,

    // Dist properties
    Dist dist,
    boolean unique,
    boolean constant,
    nullable<place> onePlace

) implements Indexable_T, Settable_T, ArithmeticOps_Array_T {


    //
    // factories
    //

    public static Array_T make(Dist dist, nullable<Indexable_T> init) {
        return BaseArray_T.make(dist, init);
    }

    public static Array_T make(Region region, nullable<Indexable_T> init) {
        return BaseArray_T.make(region, init);
    }

    public static Array_T make(Region region, nullable<Indexable_T> init, boolean value) {
        return BaseArray_T.make(region, init, value);
    }

    public static Array_T make(T [] r) {
        return BaseArray_T.make(r);
    }

    // XXX temp workaround b/c nullable<T> fails typechecking for libraries
    private static class NO_INIT implements Indexable_T {public T get(Point pt) {throw new Error();}}
    public static final Indexable_T NO_INIT = new NO_INIT();


    //
    //
    //

    public Region.Iterator iterator() {
        return region.iterator();
    }


    //
    // views
    //

    public abstract Array_T restriction(Region r);
    public abstract Array_T restriction(place p);
    
    public Array_T $bar(Region r) {
        return restriction(r);
    }

    public Array_T $bar(place p) {
        return restriction(p);
    }
    

    //
    // value access
    //

    public abstract T get(Point pt);
    public abstract T get(int i0);
    public abstract T get(int i0, int i1);
    public abstract T get(int i0, int i1, int i2);

    public abstract void set(Point pt, T v);
    public abstract void set(int i0, T v);
    public abstract void set(int i0, int i1, T v);
    public abstract void set(int i0, int i1, int i2, T v);


    //
    //
    //

    protected Array_T(Dist dist) {

        // dist properties
        this.dist = dist;
        this.unique = dist.unique;
        this.constant = dist.constant;
        this.onePlace = dist.onePlace;

        // region properties
        this.region = dist.region;
        this.rank = region.rank;
        this.rect = region.rect;
        this.zeroBased = region.zeroBased;
        this.rail = region.rail;
    }

}
