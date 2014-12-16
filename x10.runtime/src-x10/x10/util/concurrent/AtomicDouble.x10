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

package x10.util.concurrent;

public final class AtomicDouble {

    private val v:AtomicLong;
    
    public def this() { this.v = new AtomicLong(0.0.toRawLongBits()); }

    public def this(v:Double) { this.v = new AtomicLong(v.toRawLongBits()); }

    public def get() = Double.fromLongBits(v.get());
    
    public def set(v:Double) { this.v.set(v.toRawLongBits()); }
    
    public def compareAndSet(expect:Double, update:Double) {
        val exp = expect.toRawLongBits();
        val upd = update.toRawLongBits();
        return v.compareAndSet(exp,upd);
    }

    public def weakCompareAndSet(expect:Double, update:Double) {
        val exp = expect.toRawLongBits();
        val upd = update.toRawLongBits();
        return v.weakCompareAndSet(exp,upd);
    }
    
    public def getAndIncrement() = getAndAdd(1);

    public def getAndDecrement() = getAndAdd(-1);
    
    public def getAndAdd(delta:Double) {
        do {
            val exp = v.get();
            val expect = Double.fromLongBits(exp);
            val update = expect + delta;
            val upd = update.toRawLongBits();
            if (v.weakCompareAndSet(exp, upd)) {
                return expect;
            }
        } while (true);
    }
    
    public def incrementAndGet() = addAndGet(1);

    public def decrementAndGet() = addAndGet(-1);
    
    public def addAndGet(delta:Double) {
        do {
            val exp = v.get();
            val expect = Double.fromLongBits(exp);
            val update = expect + delta;
            val upd = update.toRawLongBits();
            if (v.weakCompareAndSet(exp, upd)) {
                return update;
            }
        } while (true);
    }
    
    public def toString() = get().toString();

    public def intValue() = get() as Int;

    public def longValue() = get() as Long;
    
    public def floatValue() = get() as Float;
    
    public def doubleValue() = get() as Double;
}
