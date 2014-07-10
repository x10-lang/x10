/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

import x10.util.Box;
import x10.compiler.Pinned;
import x10.regionarray.*;

/**
 * When test using producer-consumer paradigm.
 * This is a circular buffer implementation of a FIFO queue.
 *
 * @author kemal, 12/2004
 */
public class ConditionalAtomicQueue extends x10Test {
    private val root = GlobalRef[ConditionalAtomicQueue](this);
    transient private val siz: int;
    transient private val Q: Rail[T]; // The circular buffer
    transient private var nelems: int; // number of items in buffer Q
    transient private var tail: int; // next free slot to insert incoming items
    // at tail of queue
    transient private var head: int; // pointer to item to remove from the front

    public def this(): ConditionalAtomicQueue = {
        val sz = 3n;
        Q = new Rail[T](sz);
        siz = sz;
        nelems = 0n;
        tail = 0n;
        head = 0n;
    }

    /**
     * insert i at the tail end of fifo queue.
     */
    @Pinned def insert(var i: T): void = {
        Q(tail) = i;
        tail = inc(tail, siz);
        nelems++;
    }

    /**
     * remove an item from the queue
     */
    @Pinned def remove(): T = {
        var t: T = Q(head);
        head = inc(head, siz);
        nelems--;
        return t;
    }
    /**
     * increment x modulo n
     */
    static def inc(var x: int, var n: int): int = {
        var y: int = x+1n;
        return y == n ? 0n : y;
    }

    /**
     * true iff queue is empty
     */
    @Pinned def empty(): boolean = {
        chk(nelems> -1n);
        return nelems <= 0n;
    }

    /**
     * true iff queue is full
     */
    @Pinned def full(): boolean = {
        chk(nelems < siz+1n);
        return nelems >= siz;
    }

    @Pinned public def run(): boolean = {
        val N = T.N;
        val NP = Place.numPlaces();
        val D2  = MyDist.val_(N*NP);
        val received = DistArray.make[int](D2);
        val root = this.root;
        finish {
            // spawn producer activities on each place
            async 
                ateach (val [i]: Point in MyDist.unique()) {
                    for (val j in 0n..(N-1n)) {
                        val t = new T(i as int, j); // produce a T
                        async at(root) {
                            val me = root();
                            when (! me.full()) { me.insert(t); }
                        }
                    }
                }
            // spawn a single consumer activity in place P0
            async {
                for (val p in D2.region) {
                    var t: Box[T];
                    when (!empty()) { t = new Box[T](remove()); }
                    val t1 = t.value;
                    async   { t1.consume(); } // consume the T
                    val m =  t1.getval();
                    received(m) += 1n;
                    // remember how many times
                    // we received this item
                }
            }
        }

        // Ensure all messages were received exactly once
        for (val p in D2.region) chk(received(p) == 1n);

        // Ensure the FIFO queue is empty now
        chk(empty());

        return true;
    }

    public static def main(Rail[String])  {
        new ConditionalAtomicQueue().execute();
    }

    /**
     * T is the type of the item that is being produced and consumed
     */
    static class T {

        public static N: int = 2n;

        var val_: int; // the id of the item

        def this(var i: int, var j: int) = { // produce a T
            val_ = N*i+j;
        }

        public def consume(): void = { // consume a T
        }

        public def getval(): int = { return val_; }
    }

    /**
     * Utility routines to create simple common dists
     */
    static class MyDist {
        /**
         * create a simple 1D blocked dist
         */
        static def block(arraySize: long) = {
            return Dist.makeBlock(Region.make(0, arraySize-1), 0);
        }
        /**
         * create a unique dist (mapping each i to place i)
         */
        static def unique() =  {
            return Dist.makeUnique();
        }

        /**
         * create a constant-Here dist
         */
        static def val_(arraySize: long) = {
            return Region.make(0, arraySize-1)->here;
        }
    }
}
