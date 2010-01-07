package x10.util;

import x10.util.Pair;
import x10.util.HashMap;


/**
 * @author Dave Cunningham
*/
public final class DistributedRail[T] implements Settable[Int,T], Iterable[T] {
    global val data : PlaceLocalHandle[Rail[T]];
    global val firstPlace : Place;
    global val localRails = PlaceLocalStorage.createDistributedObject[HashMap[Activity, Rail[T]!]](Dist.makeUnique(), ()=>new HashMap[Activity, Rail[T]!]());
    global val original : ValRail[T];
    global val original_len : Int;

    global val done = PlaceLocalStorage.createDistributedObject[Cell[Boolean]](Dist.makeUnique(), ()=>new Cell[Boolean](false));

    public def this (len:Int, init:ValRail[T]) {
        val vr = ValRail.make(len, init);
        data = PlaceLocalStorage.createDistributedObject[Rail[T]](Dist.makeUnique(), ()=>Rail.make(len,vr));
        firstPlace = here;
        original = vr;
        original_len = len;
    }

    public def this (len:Int, init:(Int)=>T) {
        val vr = ValRail.make(len, init);
        data = PlaceLocalStorage.createDistributedObject[Rail[T]](Dist.makeUnique(), ()=>Rail.make(len,vr));
        firstPlace = here;
        original = vr;
        original_len = len;
    }

    public static safe operator[S] (x:DistributedRail[S]) = x() as ValRail[S];

    public global safe def apply () {
        val a = Runtime.activity();
        val r = localRails.get().getOrElse(a, null);
        if (r==null) {
            val r_ = Rail.make(original_len, original);
            localRails.get().put(a, r_);
            return r_;
        }
        return r;
    }

    public global safe def get() = data.get();

    public global safe def drop() { localRails.get().remove(Runtime.activity()); }

    public global safe def apply (i:Int) = this()(i);

    public global safe def set (v:T, i:Int) = this()(i) = v;

    public global safe def iterator () = this().iterator();

    private global def reduceLocal (op:(T,T)=>T) {
        val master = data.get();
        var first:Boolean = true;
        for (e in localRails.get().entries()) {
            val r = e.getValue();
            if (first) {
                finish r.copyTo(0, master, 0, r.length);
                first = false;
            } else {
                for (var i:Int=0 ; i<master.length ; ++i) {
                    master(i) = op(master(i), r(i));
                }
            }
        }
    }

    private global def reduceGlobal (op:(T,T)=>T) {
        if (firstPlace!=here) {
            val local_ = data.get();
            {
                val local = local_ as ValRail[T];
                val data_ = data;
                at (firstPlace) {
                    val master = data_.get();
                    atomic for (var i:Int=0 ; i<master.length ; ++i) {
                        master(i) = op(master(i), local(i));
                    }
                }
            }
            next; // every place has transmitted contents to master
            val handle = data; // avoid 'this' being serialised
            finish local_.copyFrom[T](0, firstPlace, ()=>Pair[Rail[T],Int](handle.get(),0), local_.length);
        } else {
            next;
        }
    }

    private global def bcastLocal (op:(T,T)=>T) {
        val master = data.get();
        for (e in localRails.get().entries()) {
            val r = e.getValue();
            finish r.copyFrom(0, master, 0, r.length);
        }
    }

    // op must be commutative
    public global def collectiveReduce (op:(T,T)=>T) {
        var i_won:Boolean = false;
        atomic {
            if (!done.get().value) {
                i_won = true;
                done.get().value = true;
            }
        }
        next; // activity rails populated at this place
        if (i_won) {
            // single thread per place mode
            reduceLocal(op);
            next; // every place has local rail populated
            reduceGlobal(op); // there's one 'next' in here too
            bcastLocal(op);
            done.get().value = false;
        } else {
            next;
            next;
        }
        next; // every place has finished reading from place 0
    }

}

