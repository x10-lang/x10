package x10.array;

import x10.lang.*;

import x10.lang.Object;


public class BaseArray_T extends Array_T {


    public static Array_T make(Dist dist, Indexable_T init) {
        return new BaseArray_T(dist, init, false);
    }

    public static Array_T make(Region region, Indexable_T init, boolean value) {
        Dist dist = Dist.makeConstant(region);
        return new BaseArray_T(dist, init, value);
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


    //
    // XXX doesn't make sense for raw to be public if layout is not
    // either make raw private, or if needed for performance
    // make layout mechanism public
    //

    public T [] raw(place place) {
        return pieces[place.id];
    }

    private RectLayout layout(place place) {
        return layouts[place.id];
    }


    //
    // XXX performance -  need subclass for value?
    //     

    public T get(Point pt) {
        return raw(here)[layout(here).offset(pt)];
    }

    public void set(Point pt, T v) {
        raw(here)[layout(here).offset(pt)] = v;
    }

    public void set(Point pt, Indexable_T f) {
        throw U.unsupported();
    }

    public void localSet(Point pt, Indexable_T f) {
        throw U.unsupported();
    }


    //
    // varargs versions
    //

    public T get(int i0) {
        return raw(here)[layout(here).offset(i0)];
    }

    public T get(int i0, int i1) {
        return raw(here)[layout(here).offset(i0,i1)];
    }

    public T get(int i0, int i1, int i2) {
        return raw(here)[layout(here).offset(i0,i1,i2)];
    }


    //
    // XXX disallow set after constructor if value?
    //

    public void set(int i0, T v) {
        raw(here)[layout(here).offset(i0)] = v;
    }

    public void set(int i0, int i1, T v) {
        raw(here)[layout(here).offset(i0,i1)] = v;
    }

    public void set(int i0, int i1, int i2, T v) {
        raw(here)[layout(here).offset(i0,i1,i2)] = v;
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
    // XXX revisit pieces vs a non-distributed field per place which
    // would make get() more efficient (no indexing into pieces[]).
    // but then how to do "simulated distribution"??
    //

    private boolean value;  // whether we're a value array
    private T [][] pieces;  // local raw storage for each place
    RectLayout [] layouts;  // layout for each place


    //
    // for now since we only have RectLayouts we hard-code that here
    // for efficiency, since RectLayout is a final value class.
    //
    // if/when we have other layouts, this might need to be a generic
    // type parameter, i.e. BaseArray[T,L] where L is a layout class
    //

    RectLayout layout(Region r) {
        BaseRegion br = (BaseRegion) r;
        return (RectLayout) RectLayout.make(br.min(), br.max());
    }


    //
    // XXX more efficient if ValArray_T subclass for value case?
    //

    protected BaseArray_T(final Dist dist, final nullable<Indexable_T> init, boolean value) {

        super(dist);

        this.value = value;
        pieces = new T[place.MAX_PLACES][];
        layouts = new RectLayout[place.MAX_PLACES];

        if (value) {

            RectLayout layout = layout(region);
            int n = region.size();
            T [] piece = new T[n];

            if (init!=/*null*/NO_INIT) {
                for (int j=0; j<n; j++)
                    piece[j] = init.get(layout.coord(j));
            }

            for (int i=0; i<place.MAX_PLACES; i++) {
                pieces[i] = piece;
                layouts[i] = layout;
            }

        } else {

            // XXX more succinct using X10...
            finish {
                layouts = new RectLayout[place.MAX_PLACES];
                for (int i=0; i<dist.places().length; i++) {
                    final place p = dist.places()[i];
                    async (p) {
                        RectLayout layout = layout(dist.get(p));
                        layouts[p.id] = layout;
                        int n = layout.size();
                        T [] piece = new T[n];
                        pieces[p.id] = piece;
                        if (init!=/*null*/NO_INIT) {
                            for (int j=0; j<n; j++)
                                piece[j] = init.get(layout.coord(j));
                        }
                    }
                }
            }
        }
    }
}
