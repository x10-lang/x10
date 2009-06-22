package x10.array;

import x10.lang.*;

import x10.lang.Object;


//
// a rail of pieces, one entry per place
// each piece initialized in the place of creation
//
// suitable for "simulated distribution" for non-constant distributions
// or if non-replicated fields are not available in distrbuted object design
// performance hit for 1) determining here and 2) indexing rail to get piece
//
// XXX whether this is needed depends on semantics of distributed objects
//

final class ArrayN_T extends BaseArray_T {


    private final T [][] raws;
    private final RectLayout [] layouts;

    protected final T [] raw() {
        return raws[here.id];
    }

    protected final RectLayout layout() {
        return layouts[here.id];
    }

    //
    // high-performance methods here to facilitate inlining
    // XXX but ref to here and rail accesses make this not so high performance
    //

    public final T get(int i0) {
        return raw()[layout().offset(i0)];
    }

    public final T get(int i0, int i1) {
        return raw()[layout().offset(i0,i1)];
    }

    public final T get(int i0, int i1, int i2) {
        return raw()[layout().offset(i0,i1,i2)];
    }


    //
    // high-performance methods here to facilitate inlining
    // XXX but ref to here and rail accesses make this not so high performance
    //

    public final void set(int i0, T v) {
        raw()[layout().offset(i0)] = v;
    }

    public final void set(int i0, int i1, T v) {
        raw()[layout().offset(i0,i1)] = v;
    }

    public final void set(int i0, int i1, int i2, T v) {
        raw()[layout().offset(i0,i1,i2)] = v;
    }

    //
    //
    //

    ArrayN_T(final Dist dist, final nullable<Indexable_T> init, boolean value) {

        super(dist, value);

        raws = new T[place.MAX_PLACES][];
        layouts = new RectLayout[place.MAX_PLACES];

        finish {
            for (int i=0; i<dist.places().length; i++) {
                async (dist.places()[i]) {
                    Region r = dist.get(here);
                    RectLayout layout = layout(r);
                    layouts[here.id] = layout;
                    int n = layout.size();
                    T [] raw = new T[n];
                    raws[here.id] = raw;
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
        return new ArrayN_T(this, d);
    }

    ArrayN_T(final ArrayN_T a, Dist d) {

        super(d, a.value);

        raws = new T[place.MAX_PLACES][];
        layouts = new RectLayout[place.MAX_PLACES];

        finish {
            for (int i=0; i<dist.places().length; i++) {
                async (dist.places()[i]) {
                    this.layouts[here.id] = a.layouts[here.id];
                    this.raws[here.id] = a.raws[here.id];
                }
            }
        }
    }

}
