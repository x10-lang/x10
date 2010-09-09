// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

/**
 * This class represents an array with raw chunk in each place,
 * initialized at its place of access via a PlaceLocalHandle.
 *
 * @author bdlucas
 */

import x10.runtime.PlaceLocalHandle;
import x10.runtime.PlaceLocalStorage;

final class DistArray[T] extends BaseArray[T] {

    private static class LocalState[T] {
        val layout:RectLayout;
        val raw:Rail[T]!;
        
        def this(l:RectLayout, r:Rail[T]!) {
            layout = l;
            raw = r;
        }
    };

    private global val localHandle:PlaceLocalHandle[LocalState[T]];
    final protected global def raw():Rail[T]! = localHandle.get().raw;
    final protected global def layout() = localHandle.get().layout;

    //
    // high-performance methods here to facilitate inlining
    // XXX but ref to here and rail accesses make this not so high performance
    //

    final public safe global def apply(i0: int): T {
        if (checkPlace) checkPlace(i0);
        if (checkBounds) checkBounds(i0);
        return raw()(layout().offset(i0));
    }

    final public safe global def apply(i0: int, i1: int): T {
        if (checkPlace) checkPlace(i0, i1);
        if (checkBounds) checkBounds(i0, i1);
        return raw()(layout().offset(i0,i1));
    }

    final public safe global def apply(i0: int, i1: int, i2: int): T {
        if (checkPlace) checkPlace(i0, i1, i2);
        if (checkBounds) checkBounds(i0, i1, i2);
        return raw()(layout().offset(i0,i1,i2));
    }

    final public safe global def apply(i0: int, i1: int, i2: int, i3: int): T {
        if (checkPlace) checkPlace(i0, i1, i2, i3);
        if (checkBounds) checkBounds(i0, i1, i2, i3);
        return raw()(layout().offset(i0,i1,i2,i3));
    }


    //
    // high-performance methods here to facilitate inlining
    // XXX but ref to here and rail accesses make this not so high performance
    //

    final public safe global def set(v: T, i0: int): T {
        if (checkPlace) checkPlace(i0);
        if (checkBounds) checkBounds(i0);
        raw()(layout().offset(i0)) = v;
        return v;
    }

    final public safe global def set(v: T, i0: int, i1: int): T {
        if (checkPlace) checkPlace(i0, i1);
        if (checkBounds) checkBounds(i0, i1);
        raw()(layout().offset(i0,i1)) = v;
        return v;
    }

    final public safe global def set(v: T, i0: int, i1: int, i2: int): T {
        if (checkPlace) checkPlace(i0, i1, i2);
        if (checkBounds) checkBounds(i0, i1, i2);
        raw()(layout().offset(i0,i1,i2)) = v;
        return v;
    }

    final public safe global def set(v: T, i0: int, i1: int, i2: int, i3: int): T {
        if (checkPlace) checkPlace(i0, i1, i2, i3);
        if (checkBounds) checkBounds(i0, i1, i2, i3);
        raw()(layout().offset(i0,i1,i2,i3)) = v;
        return v;
    }

    def this(dist: Dist, init: (Point(dist.rank))=>T): DistArray[T]{self.dist==dist} {
        super(dist);

        val plsInit:()=>LocalState[T]! = () => {
            val region = dist.get(here);
            val localLayout = layout(region);
            val localRaw = Rail.make[T](localLayout.size());
            
                for (pt  in region) {
                    localRaw(localLayout.offset(pt)) = init(pt);
                }
           
	    return new LocalState[T](localLayout, localRaw);
        };

        localHandle = PlaceLocalStorage.createDistributedObject[LocalState[T]](dist, plsInit);
    }
    def this(dist: Dist): DistArray[T]{self.dist==dist} {
        super(dist);

        val plsInit:()=>LocalState[T]! = () => {
            val region = dist.get(here);
            val localLayout = layout(region);
            val localRaw = Rail.make[T](localLayout.size());
          
	    return new LocalState[T](localLayout, localRaw);
        };

        localHandle = PlaceLocalStorage.createDistributedObject[LocalState[T]](dist, plsInit);
    }


    /*
     * restriction view
     */

    public safe global def restriction(d: Dist) {
        if (d.constant)
            return new LocalArray[T](this, d as Dist{constant, here==self.onePlace});
        else
            return new DistArray[T](this, d);
    }

    def this(a: DistArray[T], d: Dist) {
        super(d);
	localHandle = PlaceLocalStorage.createDistributedObject[LocalState[T]](d, 
		    () => a.localHandle.get());
    }

}
