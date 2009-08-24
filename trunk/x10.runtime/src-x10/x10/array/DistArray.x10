// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

/**
 * This class represents an array with raw chunk in each place,
 * initialized at its place access via a PlaceLocalHandle.
 *
 * @author bdlucas
 */

import x10.runtime.PlaceLocalHandle;
import x10.runtime.PlaceLocalStorage;

final value class DistArray[T] extends BaseArray[T] {

    private static class LocalState[T] {
        val layout:RectLayout;
        val raw:Rail[T];
        
        def this(l:RectLayout, r:Rail[T]) {
            layout = l;
            raw = r;
        }
    };

    private val localHandle:PlaceLocalHandle[LocalState[T]];

    final protected def raw(): Rail[T] {
        return localHandle.get().raw;
    }

    final protected def layout(): RectLayout {
        return localHandle.get().layout;
    }

    //
    // high-performance methods here to facilitate inlining
    // XXX but ref to here and rail accesses make this not so high performance
    //

    final public safe def apply(i0: int): T {
        if (checkPlace) checkPlace(i0);
        if (checkBounds) checkBounds(i0);
        return raw()(layout().offset(i0));
    }

    final public safe def apply(i0: int, i1: int): T {
        if (checkPlace) checkPlace(i0, i1);
        if (checkBounds) checkBounds(i0, i1);
        return raw()(layout().offset(i0,i1));
    }

    final public safe def apply(i0: int, i1: int, i2: int): T {
        if (checkPlace) checkPlace(i0, i1, i2);
        if (checkBounds) checkBounds(i0, i1, i2);
        return raw()(layout().offset(i0,i1,i2));
    }

    final public safe def apply(i0: int, i1: int, i2: int, i3: int): T {
        if (checkPlace) checkPlace(i0, i1, i2, i3);
        if (checkBounds) checkBounds(i0, i1, i2, i3);
        return raw()(layout().offset(i0,i1,i2,i3));
    }


    //
    // high-performance methods here to facilitate inlining
    // XXX but ref to here and rail accesses make this not so high performance
    //

    final public safe def set(v: T, i0: int): T {
        if (checkPlace) checkPlace(i0);
        if (checkBounds) checkBounds(i0);
        raw()(layout().offset(i0)) = v;
        return v;
    }

    final public safe def set(v: T, i0: int, i1: int): T {
        if (checkPlace) checkPlace(i0, i1);
        if (checkBounds) checkBounds(i0, i1);
        raw()(layout().offset(i0,i1)) = v;
        return v;
    }

    final public safe def set(v: T, i0: int, i1: int, i2: int): T {
        if (checkPlace) checkPlace(i0, i1, i2);
        if (checkBounds) checkBounds(i0, i1, i2);
        raw()(layout().offset(i0,i1,i2)) = v;
        return v;
    }

    final public safe def set(v: T, i0: int, i1: int, i2: int, i3: int): T {
        if (checkPlace) checkPlace(i0, i1, i2, i3);
        if (checkBounds) checkBounds(i0, i1, i2, i3);
        raw()(layout().offset(i0,i1,i2,i3)) = v;
        return v;
    }


    //
    //
    //

    def this(dist: Dist, val init: Box[(Point)=>T]): DistArray[T]{self.dist==dist} {
        super(dist);

        val plsInit:(Place)=>LocalState[T] = (p:Place) => {
            val region = dist.get(p);
            val localLayout = layout(region);
            val localRaw = Rail.makeVar[T](localLayout.size());
            if (init != null) {
                val f = at (init.location) { init as (Point) => T };
                for (pt:Point in region) {
                    localRaw(localLayout.offset(pt)) = f(pt);
                }
            }
	    return new LocalState[T](localLayout, localRaw);
        };

        localHandle = PlaceLocalStorage.createDistributedObject(dist, plsInit);
    }


    /*
     * restriction view
     */

    public safe def restriction(d: Dist) {
        if (d.constant)
            return new LocalArray[T](this, d as Dist{constant});
        else
            return new DistArray[T](this, d);
    }

    def this(a: DistArray[T], d: Dist) {
        super(d);

	localHandle = PlaceLocalStorage.createDistributedObject(d, (p:Place) => {
	    return a.localHandle.get();
        });
    }

}
