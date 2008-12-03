package x10.array;

import x10.lang.*;

import x10.lang.Object;


//
// a single raw piece, initialized only in place of creation
// suitable for value arrays
//
// XXX raw must be a ValRail?
//

final class ArrayV_T extends BaseArray_T {

    private final T [] raw;
    private final RectLayout layout;

    protected final T [] raw() {
        return raw;
    }

    protected final RectLayout layout() {
        return layout;
    }


    //
    // high-performance methods here to facilitate inlining
    //

    public final T get(int i0) {
        return raw[layout.offset(i0)];
    }

    public final T get(int i0, int i1) {
        return raw[layout.offset(i0,i1)];
    }

    public final T get(int i0, int i1, int i2) {
        return raw[layout.offset(i0,i1,i2)];
    }


    //
    // illegal for value array
    //

    public final void set(int i0, T v) {
        throw U.illegal();
    }

    public final void set(int i0, int i1, T v) {
        throw U.illegal();

    }

    public final void set(int i0, int i1, int i2, T v) {
        throw U.illegal();
    }


    //
    //
    //

    ArrayV_T(final Dist dist, final nullable<Indexable_T> init, boolean value) {

        super(dist, value);

        layout = layout(region);
        int n = region.size();
        raw = new T[n];

        if (init!=/*null*/NO_INIT) {

            Region.Iterator it = region.iterator();
            while (it.hasNext()) {
                Point p = Point.make(it.next()); // XXX perf
                raw[layout.offset(p)] = init.get(p);
            }
        }
    }


    //
    // restriction view
    //

    public Array_T restriction(Dist d) {
        return new ArrayV_T(this, d);
    }

    ArrayV_T(final ArrayV_T a, Dist d) {

        super(d, a.value);

        this.layout = a.layout;
        this.raw = a.raw;
    }

}
