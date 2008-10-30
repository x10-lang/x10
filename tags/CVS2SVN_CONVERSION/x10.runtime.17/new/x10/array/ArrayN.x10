// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

/*
 * a rail of pieces, one entry per place
 * each piece initialized in the place of creation
 *
 * suitable for "simulated distribution" for non-constant distributions
 * or if non-replicated fields are not available in distrbuted object design
 * performance hit for 1) determining here and 2) indexing rail to get piece
 *
 * XXX whether this is needed depends on semantics of distributed objects
 */

final value class ArrayN[T] extends BaseArray[T] {

    //
    // Work around compiler issue XTENLANG-17
    // Rail[Rail[T]] is not supported
    // XXX this is performance-critical code
    //

    class RailT {
        var r: Rail[T];
        def this(n:int) {
            r = Rail.makeVar[T](n);
        }
        public def toString() = "RailT"; // XTENLANG-89
    }

    private val raws: Rail[RailT];
    private val layouts: Rail[RectLayout];

    final protected def raw(): Rail[T] {
        return raws(here.id).r;
    }

    final protected def layout(): RectLayout {
        return layouts(here.id);
    }

    //
    // high-performance methods here to facilitate inlining
    // XXX but ref to here and rail accesses make this not so high performance
    //

    final public def apply(i0: int): T {
        checkBounds(i0);
        return raw()(layout().offset(i0));
    }

    final public def apply(i0: int, i1: int): T {
        checkBounds(i0, i1);
        return raw()(layout().offset(i0,i1));
    }

    final public def apply(i0: int, i1: int, i2: int): T {
        checkBounds(i0, i1, i2);
        return raw()(layout().offset(i0,i1,i2));
    }

    final public def apply(i0: int, i1: int, i2: int, i3: int): T {
        checkBounds(i0, i1, i2, i3);
        return raw()(layout().offset(i0,i1,i2,i3));
    }


    //
    // high-performance methods here to facilitate inlining
    // XXX but ref to here and rail accesses make this not so high performance
    //

    final public def set(v: T, i0: int): T {
        checkBounds(i0);
        raw()(layout().offset(i0)) = v;
        return v;
    }

    final public def set(v: T, i0: int, i1: int): T {
        checkBounds(i0, i1);
        raw()(layout().offset(i0,i1)) = v;
        return v;
    }

    final public def set(v: T, i0: int, i1: int, i2: int): T {
        checkBounds(i0, i1, i2);
        raw()(layout().offset(i0,i1,i2)) = v;
        return v;
    }

    final public def set(v: T, i0: int, i1: int, i2: int, i3: int): T {
        checkBounds(i0, i1, i2, i3);
        raw()(layout().offset(i0,i1,i2,i3)) = v;
        return v;
    }


    //
    //
    //

    incomplete def complain(): void;

    def this(val dist: Dist, val init: (Point)=>T): ArrayN[T]{self.dist==dist} {

        super(dist);

        raws = Rail.makeVar[RailT](Place.MAX_PLACES);
        layouts = Rail.makeVar[RectLayout](Place.MAX_PLACES);

        // XXX do we really need to do this at all places?

        finish {
            for (var i: int = 0; i<Place.MAX_PLACES; i++) {
                async (Place.places(i)) {
                    val r: Region = dist.get(here);
                    val layout: RectLayout = layout(r);
                    layouts(here.id) = layout;
                    val n = layout.size();
                    val raw: RailT = new RailT(n);
                    raws(here.id) = raw;
                    if (init!=null) {
                        for (p:Point in r)
                            raw.r(layout.offset(p)) = init(p);
                    }
                }
            }
        }
    }


    /*
     * restriction view
     */

    public def restriction(d: Dist) {
        return new ArrayN[T](this, d);
    }

    def this(val a: ArrayN[T], d: Dist) {

        super(d);

        raws = Rail.makeVar[RailT](Place.MAX_PLACES);
        layouts = Rail.makeVar[RectLayout](Place.MAX_PLACES);

        // XXX do we really need to do this at all places?

        finish {
            for (var i: int = 0; i<Place.MAX_PLACES; i++) {
                async (Place.places(i)) {
                    this.layouts(here.id) = a.layouts(here.id);
                    this.raws(here.id) = a.raws(here.id);
                }
            }
        }
    }

}
