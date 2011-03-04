/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.util;

import x10.util.Pair;
import x10.util.HashMap;


/**
 * @author Dave Cunningham
*/
public final class DistributedRail[T] implements Settable[Int,T], Iterable[T] {
    global val data : PlaceLocalHandle[Rail[T]];
    global val firstPlace : Place;
    global val localRails 
    = PlaceLocalHandle.make[HashMap[Activity, Rail[T]]](Dist.makeUnique(), 
    		()=>new HashMap[Activity, Rail[T]]());
    global val original : ValRail[T];
    global val original_len : Int;

    global val done = PlaceLocalHandle.make[Cell[Boolean]](Dist.makeUnique(), ()=>new Cell[Boolean](false));

    public def this (len:Int, init:ValRail[T]) {
        val vr = ValRail.make(len, init);
        data = PlaceLocalHandle.make[Rail[T]](Dist.makeUnique(), ()=>Rail.make(len,vr));
        firstPlace = here;
        original = vr;
        original_len = len;
    }

    public def this (len:Int, init:(Int)=>T) {
        val vr = ValRail.make(len, init);
        data = PlaceLocalHandle.make[Rail[T]](Dist.makeUnique(), ()=>Rail.make(len,vr));
        firstPlace = here;
        original = vr;
        original_len = len;
    }

    public static safe operator[S] (x:DistributedRail[S]) = x() as ValRail[S];

    public global safe def apply () {
        val a = Runtime.activity();
        val r:Rail[T] = localRails().getOrElse(a, null);
        if (r==null) {
            val r_ = Rail.make[T](original_len, original);
            localRails().put(a, r_);
            return r_;
        }
        return r;
    }

    public global safe def get() = data();

    public global safe def drop() { localRails().remove(Runtime.activity()); }

    public global safe def apply (i:Int) = this()(i);

    public global safe def set (v:T, i:Int) {
    	val t = this();
    	at (t) 
    	  t(i) = v;
    	return v;
    }

    public global safe def iterator () {
    	val t = this();
    	return at (t)
    	 t.iterator();
    }

    private global def reduceLocal (op:(T,T)=>T) {
        val master = data();
        var first:Boolean = true;
        for (e in localRails().entries()) {
            val r = at(e) e.getValue();
            if (first) {
                at (r) 
                  finish r.copyTo(0, master, 0, r.length);
                first = false;
            } else {
            	
                for (var i:Int=0 ; i<master.length ; ++i) {
                	val i0=i;
                    master(i) = op(master(i), at (r) r(i0));
                }
            }
        }
    }

    private global def reduceGlobal (op:(T,T)=>T) {
        if (firstPlace!=here) {
            val local_ = data();
            {
                val local = local_ as ValRail[T];
                val data_ = data;
                at (firstPlace) {
                    val master = data_();
                    atomic for (var i:Int=0 ; i<master.length ; ++i) {
                        master(i) = op(master(i), local(i));
                    }
                }
            }
            next; // every place has transmitted contents to master
            val handle = data; // avoid 'this' being serialised
            finish local_.copyFrom(0, firstPlace, ()=>Pair[Rail[T],Int](handle(),0), local_.length);
        } else {
            next;
        }
    }

    private global def bcastLocal (op:(T,T)=>T) {
        val master = data();
        for (e in localRails().entries()) {
        	
            val r = at (e) e.getValue();
            finish at (r) r.copyFrom(0, master, 0, r.length);
        	
        }
    }

    // op must be commutative
    public global def collectiveReduce (op:(T,T)=>T) {
        var i_won:Boolean = false;
        atomic {
            if (!done().value) {
                i_won = true;
                done().value = true;
            }
        }
        next; // activity rails populated at this place
        if (i_won) {
            // single thread per place mode
            reduceLocal(op);
            next; // every place has local rail populated
            reduceGlobal(op); // there's one 'next' in here too
            bcastLocal(op);
            done().value = false;
        } else {
            next;
            next;
        }
        next; // every place has finished reading from place 0
    }

}

