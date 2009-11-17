package x10.util;

import x10.runtime.PlaceLocalStorage;
import x10.runtime.PlaceLocalHandle;

import x10.util.Pair;


/**
 * @author Dave Cunningham
*/
class DistributedRail[T] implements Settable[Int,T], Iterable[T] {
    global val handle : PlaceLocalHandle[Rail[T]];
    global val firstPlace : Place;
    global val clock : Clock;

    public def this (c:Clock, dist:Dist, len:Int, vr:ValRail[T]) {
        handle = PlaceLocalStorage.createDistributedObject[Rail[T]](dist, ()=>Rail.make(len,vr));
        firstPlace = dist(0);
        clock = c;
    }

    public def this (c:Clock, dist:Dist, len:Int, init:(Int)=>T) {
        val vr = ValRail.make(len, init);
        handle = PlaceLocalStorage.createDistributedObject[Rail[T]](dist, ()=>Rail.make(len,vr));
        firstPlace = dist(0);
        clock = c;
    }

    public static operator[S] (x:DistributedRail[S]) = x.handle.get() as ValRail[S];

    public global def apply () = handle.get();

    public global def apply (i:Int) = handle.get()(i);

    public global def set (v:T, i:Int) = handle.get()(i) = v;

    public global def iterator () = handle.get().iterator();

    // op must be commutative
    public global def collectiveReduce (op:(T,T)=>T) {
        // naive implementation
        clock.next();
        val home = here;
        val local_ = handle.get();
        {
            val local = local_ as ValRail[T];
            at (firstPlace) {
                val master = handle.get();
                atomic for (var i:Int=0 ; i<master.length ; ++i) {
                    master(i) = op(master(i), local(i));
                }
            }
        }
        clock.next();
        if (firstPlace!=here)
            finish local_.copyFrom[T](0, firstPlace, ()=>Pair[Rail[T],Int](handle.get(),0), local_.length);
        clock.next();
    }

}

