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

package x10.core;

import java.lang.reflect.Field;

import sun.misc.Unsafe;
import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.serialization.X10JavaDeserializer;
import x10.serialization.X10JavaSerializable;
import x10.serialization.X10JavaSerializer;

public class Deque extends Ref {

    private Object writeReplace() throws java.io.ObjectStreamException {
        return new x10.serialization.SerializationProxy(this);
    }

    public void $_serialize(X10JavaSerializer $serializer) throws java.io.IOException {
        // TODO need check
        $serializer.write(base);
        $serializer.write(sp);
        $serializer.write((Object)queue);
    }
    public static X10JavaSerializable $_deserialize_body(Deque $_obj, X10JavaDeserializer $deserializer) throws java.io.IOException {
        // TODO need check
        $_obj.base = $deserializer.readInt();
        $_obj.sp = $deserializer.readInt();
        $_obj.queue = (Object[]) $deserializer.readObject();
        return $_obj;
    }
    public static X10JavaSerializable $_deserializer(X10JavaDeserializer $deserializer) throws java.io.IOException {
        Deque $_obj = new Deque((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
    }

    /**
     * Capacity of work-stealing queue array upon initialization.
     * Must be a power of two. Initial size must be at least 2, but is
     * padded to minimize cache effects.
     */
    private static final int INITIAL_QUEUE_CAPACITY = 1 << 13;

    /**
     * Maximum work-stealing queue array size.  Must be less than or
     * equal to 1 << 28 to ensure lack of index wraparound. (This
     * is less than usual bounds, because we need leftshift by 3
     * to be in int range).
     */
    private static final int MAXIMUM_QUEUE_CAPACITY = 1 << 28;

    /**
     * The work-stealing queue array. Size must be a power of two.
     * Initialized when thread starts, to improve memory locality.
     */
    private Object[] queue;

    /**
     * Index (mod queue.length) of next queue slot to push to or pop
     * from. It is written only by owner thread, via ordered store.
     * Both sp and base are allowed to wrap around on overflow, but
     * (sp - base) still estimates size.
     */
    private volatile int sp;

    /**
     * Index (mod queue.length) of least valid queue slot, which is
     * always the next position to steal from if nonempty.
     */
    private volatile int base;

    // constructor just for allocation
    public Deque(java.lang.System[] $dummy) {super($dummy);}

    public final Deque x10$lang$Deque$$init$S() {
        queue = new Object[INITIAL_QUEUE_CAPACITY];
        return this;
    }
    
    /**
     * Creates a Deque.
     */
    public Deque() {
        // Allocate while starting to improve chances of thread-local
        // isolation
        queue = new Object[INITIAL_QUEUE_CAPACITY];
    }

    // Intrinsics-based support for queue operations.  

    /**
     * Add in store-order the given task at given slot of q to
     * null. Caller must ensure q is nonnull and index is in range.
     */
    private void setSlot(Object[] q, int i, Object t){
    	// DAVE G: Egregious hack to get code running on IBM SDKs that don't have putOrderedObject.
        // _unsafe.putOrderedObject(q, (i << qShift) + qBase, t);
        _unsafe.putObject(q, (i << qShift) + qBase, t);
    }

    /**
     * CAS given slot of q to null. Caller must ensure q is nonnull
     * and index is in range.
     */
    private boolean casSlotNull(Object[] q, int i, Object t) {
        return _unsafe.compareAndSwapObject(q, (i << qShift) + qBase, t, null);
    }

    /**
     * Sets sp in store-order.
     */
    private void storeSp(int s) {
    	// DAVE G: Egregious hack to get code running on IBM SDKs that don't have putOrderedInt.
    	// _unsafe.putOrderedInt(this, spOffset, s);
    	_unsafe.putInt(this, spOffset, s);
    }

    // Main queue methods

    /**
     * Pushes a task. Called only by current thread.
     * @param t the task. Caller must ensure nonnull
     */
    public final void push(Object t) {
        Object[] q = queue;
        int mask = q.length - 1;
        int s = sp;
        setSlot(q, s & mask, t);
        storeSp(++s);
        if ((s -= base) == 1)
            ;
        else if (s >= mask)
            growQueue();
    }

    /**
     * Tries to take a task from the base of the queue, failing if
     * either empty or contended.
     * @return a task, or null if none or contended.
     */
    public final Object steal() {
        Object t;
        Object[] q;
        int i;
        int b;
        if (sp != (b = base) &&
            (q = queue) != null && // must read q after b
            (t = q[i = (q.length - 1) & b]) != null &&
            casSlotNull(q, i, t)) {
            base = b + 1;
            return t;
        }
        return null;
    }

    /**
     * Returns a popped task, or null if empty. Ensures active status
     * if nonnull. Called only by current thread.
     */
    public final Object poll() {
        int s = sp;
        while (s != base) {
        	Object[] q = queue;
            int mask = q.length - 1;
            int i = (s - 1) & mask;
            Object t = q[i];
            if (t == null || !casSlotNull(q, i, t))
                break;
            storeSp(s - 1);
            return t;
        }
        return null;
    }

    /**
     * Returns next task to pop.
     */
    public final Object peekTask() {
        Object[] q = queue;
        return q == null? null : q[(sp - 1) & (q.length - 1)];
    }

    /**
     * Doubles queue array size. Transfers elements by emulating
     * steals (deqs) from old array and placing, oldest first, into
     * new array.
     */
    private void growQueue() {
        Object[] oldQ = queue;
        int oldSize = oldQ.length;
        int newSize = oldSize << 1;
        if (newSize > MAXIMUM_QUEUE_CAPACITY)
            throw new RuntimeException("Queue capacity exceeded");
        Object[] newQ = queue = new Object[newSize];

        int b = base;
        int bf = b + oldSize;
        int oldMask = oldSize - 1;
        int newMask = newSize - 1;
        do {
            int oldIndex = b & oldMask;
            Object t = oldQ[oldIndex];
            if (t != null && !casSlotNull(oldQ, oldIndex, t))
                t = null;
            setSlot(newQ, b & newMask, t);
        } while (++b != bf);
    }

    /**
     * Returns an estimate of the number of tasks in the queue.
     */
    public final int size$O() {
        int n = sp - base;
        return n < 0? 0 : n; // suppress momentarily negative values
    }


    //
    // Runtime type information
    //
    public static final RuntimeType<Deque> $RTT = NamedType.<Deque> make(
        "x10.lang.Deque",
        Deque.class 
    );
    public RuntimeType<Deque> $getRTT() {return $RTT;}
    public Type<?> $getParam(int i) { return null; }

    // Temporary Unsafe mechanics for preliminary release

    static final Unsafe _unsafe;
    static final long baseOffset;
    static final long spOffset;
    static final long qBase;
    static final int qShift;
    static {
        try {
            if (Deque.class.getClassLoader() != null) {
                Field f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                _unsafe = (Unsafe)f.get(null);
            }
            else
                _unsafe = Unsafe.getUnsafe();
            baseOffset = _unsafe.objectFieldOffset
                (Deque.class.getDeclaredField("base"));
            spOffset = _unsafe.objectFieldOffset
                (Deque.class.getDeclaredField("sp"));
            qBase = _unsafe.arrayBaseOffset(Object[].class);
            int s = _unsafe.arrayIndexScale(Object[].class);
            if ((s & (s-1)) != 0)
                throw new Error("data type scale not a power of two");
            qShift = 31 - Integer.numberOfLeadingZeros(s);
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize intrinsics", e);
        }
    }
}
