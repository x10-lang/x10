// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

/**
 * a single raw piece, initialized only in place of creation
 * suitable for value arrays
 *
 * XXX raw must be a ValRail?
 */

final value class ArrayV[T] extends BaseArray[T] {

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
        checkBounds(i0);
        return raw(layout.offset(i0));
    }

    final public def apply(i0: int, i1: int): T {
        checkBounds(i0, i1);
        return raw(layout.offset(i0,i1));
    }

    final public def apply(i0: int, i1: int, i2: int): T {
        checkBounds(i0, i1, i2);
        return raw(layout.offset(i0,i1,i2));
    }

    final public def apply(i0: int, i1: int, i2: int, i3: int): T {
        checkBounds(i0, i1, i2, i3);
        return raw(layout.offset(i0,i1,i2,i3));
    }


    //
    // illegal for value array
    //

    final public def set(v: T, i0: int): void {
        throw U.illegal();
    }

    final public def set(v: T, i0: int, i1: int): void {
        throw U.illegal();

    }

    final public def set(v: T, i0: int, i1: int, i2: int): void {
        throw U.illegal();
    }

    final public def set(v: T, i0: int, i1: int, i2: int, i3: int): void {
        throw U.illegal();
    }


    //
    //
    //

    def this(dist: Dist, val init: (Point)=>T): ArrayV[T] {

        super(dist);

        layout = layout(region);
        val n = layout.size();
        raw = Rail.makeVar[T](n);

        if (init!=null)
            for (p: Point in region)
                raw(layout.offset(p)) = init(p);
    }


    /**
     * restriction view
     */

    public def restriction(d: Dist): Array[T] {
        return new ArrayV[T](this, d);
    }

    def this(a: ArrayV[T], d: Dist) {

        super(d);

        this.layout = a.layout;
        this.raw = a.raw;
    }

}
