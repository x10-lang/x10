package x10.array;

import x10.lang.*;

import x10.lang.Object;


//
// a raw piece in each place, initialized at its place
// suitable for distributed arrays if the raw and layout fields are per-place
// suitable for "simulated distribution" for constant distributions
//
// XXX whether this works depends on distributed object semantics
// XXX raw and layout must be non-final? but want final for performance...
//

final class Array1_T extends BaseArray_T {

    private T [] raw;
    private RectLayout layout;

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
    // high-performance methods here to facilitate inlining
    //

    public final void set(int i0, T v) {
        raw[layout.offset(i0)] = v;
    }

    public final void set(int i0, int i1, T v) {
        raw[layout.offset(i0,i1)] = v;
    }

    public final void set(int i0, int i1, int i2, T v) {
        raw[layout.offset(i0,i1,i2)] = v;
    }


    //
    //
    //

    Array1_T(final Dist dist, final nullable<Indexable_T> init, boolean value) {

        super(dist, value);

        finish {
            for (int i=0; i<dist.places().length; i++) {
                async (dist.places()[i]) {
                    Region r = dist.get(here);
                    layout = layout(r);
                    int n = layout.size();
                    raw = new T[n];
                    if (init!=/*null*/NO_INIT) {
                        Region.Iterator it = r.iterator();
                        while (it.hasNext()) {
                            Point p = Point.make(it.next()); // XXX perf
                            raw[layout.offset(p)] = init.get(p);
                        }
                    }
                }
            }
        }
    }


    //
    // restriction view
    //

    public Array_T restriction(Dist d) {
        return new Array1_T(this, d);
    }

    Array1_T(final Array1_T a, Dist d) {

        super(d, a.value);

        finish {
            for (int i=0; i<dist.places().length; i++) {
                async (dist.places()[i]) {
                    this.layout = a.layout;
                    this.raw = a.raw;
                }
            }
        }
    }
}
