package x10.array;

import x10.lang.Object;

import x10.lang.*;

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
    //
    //

    public Dist dist() {
        return dist;
    }

    public Region region() {
        return dist.region;
    }

    public int rank() {
        throw U.unsupported();
    }


    //
    // XXX doesn't make sense for raw to be public if layout is not
    // either make raw private, or if needed for performance
    // make layout mechanism public
    //

    public T [] raw(place place) {
        return pieces[place.id];
    }

    private Layout layout(place place) {
        //return (Layout) (value? region() : dist.get(here));
        return layouts[place.id];
    }


    //
    // XXX performance
    //     need subclass for value?
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
        //U.xxx("raw(here) " + raw(here) + " layout(here) " + layout(here) + " value " + value);
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
    //
    //

    private Dist dist;
    private boolean value;

    private T [][] pieces;  // local raw storage for each place
    Layout [] layouts;      // layout for each place

    // XXX generalize this
    Layout layout(Region r) {
        return (Layout) r.boundingBox();
    }

    // XXX more efficient if ValArray_T subclass for value case?

    protected BaseArray_T(final Dist dist, final nullable<Indexable_T> init, boolean value) {

        this.dist = dist;
        this.value = value;

        pieces = new T[place.MAX_PLACES][];
        layouts = new Layout[place.MAX_PLACES];

        if (value) {

            Layout layout = layout(region());
            int n = region().size();
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
                layouts = new Layout[place.MAX_PLACES];
                for (int i=0; i<dist.places().length; i++) {
                    final place p = dist.places()[i];
                    async (p) {
                        Layout layout = layout(dist.get(p));
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
