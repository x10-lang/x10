package x10.array;

import x10.lang.*;

import x10.lang.Object;


abstract public class BaseArray_T extends Array_T {


    public static Array_T make(Dist dist, Indexable_T init) {
        return make(dist, init, false);
    }

    public static Array_T make(Region region, Indexable_T init, boolean value) {
        Dist dist = Dist.makeConstant(region);
        return make(dist, init, value);
    }

    public static Array_T make(Region region, Indexable_T init) {
        return make(region, init, false);
    }

    public static Array_T make(final T [] rail) {
        Region r = Region.makeRectangular(0, rail.length-1);
        class Init implements Indexable_T {
            public T get(Point pt) {
                return rail[pt.get(0)];
            }
        }
        return make(r, new Init());
    }

    
    private static Array_T make(Dist dist, Indexable_T init, boolean value) {
        if (value)
            return new ArrayV_T(dist, init, value);
        else if (dist.constant)
            return new Array1_T(dist, init, value);
        else
            // XXX revisit after semantics of distributed objects are known
            // Array1_T should work, and perform better
            return new ArrayN_T(dist, init, value);
    }


    //
    // expose these if performance demands
    //

    protected abstract T [] raw();
    protected abstract RectLayout layout();


    //
    // low-perfomance methods here
    // high-performance methods are in subclass to facilitate inlining
    //     

    public final T get(Point pt) {
        return raw()[layout().offset(pt)];
    }

    public final void set(Point pt, T v) {
        raw()[layout().offset(pt)] = v;
    }

    public final void set(Point pt, Indexable_T f) {
        throw U.unsupported();
    }

    public final void localSet(Point pt, Indexable_T f) {
        throw U.unsupported();
    }


    //
    //
    //

    public Array_T view(Region r) {
        throw U.unsupported();
    }


    //
    //
    //

    public Array_T add(Array_T x) {
        throw U.unsupported();
    }

    public Array_T sub(Array_T x) {
        throw U.unsupported();
    }

    public Array_T mul(Array_T x) {
        throw U.unsupported();
    }

    public Array_T div(Array_T x) {
        throw U.unsupported();
    }

    public Array_T cosub(Array_T x) {
        throw U.unsupported();
    }

    public Array_T codiv(Array_T x) {
        throw U.unsupported();
    }

    public Array_T neginv() {
        throw U.unsupported();
    }

    public Array_T mulinv() {
        throw U.unsupported();
    }


    public boolean eq(Array_T x) {
        throw U.unsupported();
    }

    public boolean lt(Array_T x) {
        throw U.unsupported();
    }

    public boolean gt(Array_T x) {
        throw U.unsupported();
    }

    public boolean le(Array_T x) {
        throw U.unsupported();
    }

    public boolean ge(Array_T x) {
        throw U.unsupported();
    }

    public boolean ne(Array_T x) {
        throw U.unsupported();
    }


    //
    // for now since we only have RectLayouts we hard-code that here
    // for efficiency, since RectLayout is a final value class.
    //
    // if/when we have other layouts, this might need to be a generic
    // type parameter, i.e. BaseArray[T,L] where L is a layout class
    //

    protected RectLayout layout(Region r) {
        BaseRegion br = (BaseRegion) r;
        return (RectLayout) RectLayout.make(br.min(), br.max());
    }

    //
    // XXX more efficient if ValArray_T subclass for value case?
    //

    protected final boolean value;  // whether we're a value array

    protected BaseArray_T(final Dist dist, boolean value) {
        super(dist);
        this.value = value;
    }
}
