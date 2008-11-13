// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

/**
 * This class represents an array with raw chunk in each place,
 * initialized at its place.
 *
 * The per-chunk information is stored in a single field in the
 * object. This is suitable for distributed arrays if the raw and
 * layout fields are per-place. Since this is not currently the case
 * we must use the ArrayN implementation instead, but there is a
 * performance hit as a result.
 *
 * It is also suitable for single-process implementation for constant
 * distributions
 *
 * @author bdlucas
 */

final value class Array1[T] extends BaseArray[T] {

    private val raw: Rail[T];
    private val layout: RectLayout;

    final protected def raw(): Rail[T] {
        return raw;
    }

    final protected def layout(): RectLayout {
        return layout;
    }


    //
    // high-performance methods here to facilitate inlining
    //

    final public def apply(i0: int): T {
        if (checkBounds) checkBounds(i0);
        return raw(layout.offset(i0));
    }

    final public def apply(i0: int, i1: int): T {
        if (checkBounds) checkBounds(i0, i1);
        return raw(layout.offset(i0,i1));
    }

    final public def apply(i0: int, i1: int, i2: int): T {
        if (checkBounds) checkBounds(i0, i1, i2);
        return raw(layout.offset(i0,i1,i2));
    }

    final public def apply(i0: int, i1: int, i2: int, i3: int): T {
        if (checkBounds) checkBounds(i0, i1, i2, i3);
        return raw(layout.offset(i0,i1,i2,i3));
    }


    //
    // high-performance methods here to facilitate inlining
    //

    final public def set(v: T, i0: int): T {
        if (checkBounds) checkBounds(i0);
        raw(layout.offset(i0)) = v;
        return v;
    }

    final public def set(v: T, i0: int, i1: int): T {
        if (checkBounds) checkBounds(i0, i1);
        raw(layout.offset(i0,i1)) = v;
        return v;
    }

    final public def set(v: T, i0: int, i1: int, i2: int): T {
        if (checkBounds) checkBounds(i0, i1, i2);
        raw(layout.offset(i0,i1,i2)) = v;
        return v;
    }

    final public def set(v: T, i0: int, i1: int, i2: int, i3: int): T {
        if (checkBounds) checkBounds(i0, i1, i2, i3);
        raw(layout.offset(i0,i1,i2,i3)) = v;
        return v;
    }


    //
    // XXX only works for single-process shared-memory implementation
    //

    def this(dist: Dist{constant}, init: (Point)=>T): Array1[T]{self.dist==dist} {

        super(dist);

        //finish {
            //async (dist.onePlace) {
        	val r = dist.get(/*here*/dist.onePlace);
                layout = layout(r);
                val n = layout.size();
                raw = Rail.makeVar[T](n);
                if (init!=null) {
                    for (p:Point in r)
                        raw(layout.offset(p)) = init(p);
                }
            //}
        //}
    }


    /*

    // suitable for distributed arrays if the raw and layout fields are per-place
    // won't compile now b/c it thinks raw is being re-initialized...

    def this(dist: Dist, init: (Point)=>T): Array1[T] {

        super(dist);

        finish {
            for (var i: int = 0; i<dist.places().length; i++) {
                async (dist.places()(i)) {
                    val r = dist.get(here);
                    layout = layout(r);
                    val n = layout.size();
                    raw = Rail.makeVar[T](n);
                    if (init!=null) {
                        for (p:Point in r)
                            raw(layout.offset(p)) = init(p);
                    }
                }
            }
        }
    }
    */


    /*
     * restriction view
     */

    public def restriction(d: Dist): Array[T] {
        return new Array1[T](this, d as Dist{constant});
    }

    def this(a: Array1[T], d: Dist{constant}) {

        super(d);

        //finish {
            //async (dist.onePlace) {
                this.layout = a.layout;
                this.raw = a.raw;
            //}
        //}
    }

    /*
    def this(a: Array1[T], d: Dist) {

        super(d);

        finish {
            for (var i: int = 0; i<dist.places().length; i++) {
                async (dist.places()(i)) {
                    this.layout = a.layout;
                    this.raw = a.raw;
                }
            }
        }
    }
    */


}
