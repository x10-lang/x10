/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.util.concurrent;

public final class AtomicFloat {

    private val v:AtomicInteger;
    
    public def this() { this.v = new AtomicInteger(0.0f.toRawIntBits()); }

    public def this(v:Float) { this.v = new AtomicInteger(v.toRawIntBits()); }

    public def get() = Float.fromIntBits(v.get());
    
    public def set(v:Float) { this.v.set(v.toRawIntBits()); }
    
    public def compareAndSet(expect:Float, update:Float) {
        val exp = expect.toRawIntBits();
        val upd = update.toRawIntBits();
        return v.compareAndSet(exp,upd);
    }

    public def weakCompareAndSet(expect:Float, update:Float) {
        val exp = expect.toRawIntBits();
        val upd = update.toRawIntBits();
        return v.weakCompareAndSet(exp,upd);
    }
    
    public def getAndIncrement() = getAndAdd(1);

    public def getAndDecrement() = getAndAdd(-1);
    
    public def getAndAdd(delta:Float) {
        do {
            val exp = v.get();
            val expect = Float.fromIntBits(exp);
            val update = expect + delta;
            val upd = update.toRawIntBits();
            if (v.weakCompareAndSet(exp, upd)) {
                return expect;
            }
        } while (true);
    }
    
    public def incrementAndGet() = addAndGet(1);

    public def decrementAndGet() = addAndGet(-1);
    
    public def addAndGet(delta:Float) {
        do {
            val exp = v.get();
            val expect = Float.fromIntBits(exp);
            val update = expect + delta;
            val upd = update.toRawIntBits();
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
