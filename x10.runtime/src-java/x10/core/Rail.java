/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2013.
 */

package x10.core;

import x10.lang.LongRange;
import x10.lang.RailIterator;
import x10.lang.RailIterator.__0$1x10$lang$RailIterator$$T$2;

public final class Rail<T> extends x10.core.Ref implements x10.lang.Iterable, 
                                                           x10.core.fun.Fun_0_1,
                                                           x10.serialization.X10JavaSerializable {
    
    // synthetic types for parameter mangling
    public static final class __0$1x10$lang$Rail$$T$2 { }
    public static final class __1$1x10$lang$Int$3x10$lang$Rail$$T$2 { }
    public static final class __1$1x10$lang$Long$3x10$lang$Rail$$T$2 { }
    public static final class __1x10$lang$Rail$$T { }

    private static final long serialVersionUID = 1L;

    public static final x10.rtt.RuntimeType<Rail> $RTT = 
            x10.rtt.NamedType.<Rail> make("x10.lang.Rail", /* base class */
                                          Rail.class,
                                          x10.rtt.RuntimeType.INVARIANTS(1),
                                          new x10.rtt.Type[] {
                                               x10.rtt.ParameterizedType.make(x10.lang.Iterable.$RTT, x10.rtt.UnresolvedType.PARAM(0)),
                                               x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT,
                                                                              x10.rtt.UnresolvedType.PARAM(0)),
                                               x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.LONG,
                                                                              x10.rtt.UnresolvedType.PARAM(0)) 
            });

    private x10.rtt.Type T;

    public long size;

    public x10.core.IndexedMemoryChunk<T> raw;

    // constructor just for allocation
    public Rail(final java.lang.System[] $dummy, final x10.rtt.Type T) {
        x10.core.Rail.$initParams(this, T);
    }

    // creation method for java code (1-phase java constructor)
    public Rail(final x10.rtt.Type T) {
        this((java.lang.System[]) null, T);
        x10$lang$Rail$$init$S();
    }

    // creation method for java code (1-phase java constructor)
    public Rail(final x10.rtt.Type T, final int size) {
        this((java.lang.System[]) null, T);
        x10$lang$Rail$$init$S(size);
    }

    // creation method for java code (1-phase java constructor)
    public Rail(final x10.rtt.Type T, final int size, final T init, __1x10$lang$Rail$$T $dummy) {
        this((java.lang.System[]) null, T);
        x10$lang$Rail$$init$S(size, init, (x10.core.Rail.__1x10$lang$Rail$$T) null);
    }

    // #line 125 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10"
    // creation method for java code (1-phase java constructor)
    public Rail(final x10.rtt.Type T, final int size, final x10.core.fun.Fun_0_1<x10.core.Int, T> init,
                __1$1x10$lang$Int$3x10$lang$Rail$$T$2 $dummy) {
        this((java.lang.System[]) null, T);
        x10$lang$Rail$$init$S(size, init, (x10.core.Rail.__1$1x10$lang$Int$3x10$lang$Rail$$T$2) null);
    }

    // creation method for java code (1-phase java constructor)
    public Rail(final x10.rtt.Type T, final long size) {
        this((java.lang.System[]) null, T);
        x10$lang$Rail$$init$S(size);
    }

    // creation method for java code (1-phase java constructor)
    public Rail(final x10.rtt.Type T, final long size, final T init, __1x10$lang$Rail$$T $dummy) {
        this((java.lang.System[]) null, T);
        x10$lang$Rail$$init$S(size, init, (x10.core.Rail.__1x10$lang$Rail$$T) null);
    }

    // creation method for java code (1-phase java constructor)
    public Rail(final x10.rtt.Type T, final long size, final x10.core.fun.Fun_0_1<x10.core.Long, T> init,
            __1$1x10$lang$Long$3x10$lang$Rail$$T$2 $dummy) {
        this((java.lang.System[]) null, T);
        x10$lang$Rail$$init$S(size, init, (x10.core.Rail.__1$1x10$lang$Long$3x10$lang$Rail$$T$2) null);
    }

    // creation method for java code (1-phase java constructor)
    public Rail(final x10.rtt.Type T, final x10.core.IndexedMemoryChunk<T> backingStore,
            __0$1x10$lang$Rail$$T$2 $dummy) {
        this((java.lang.System[]) null, T);
        x10$lang$Rail$$init$S(backingStore, (x10.core.Rail.__0$1x10$lang$Rail$$T$2) null);
    }

    // creation method for java code (1-phase java constructor)
    public Rail(final x10.rtt.Type T, final x10.core.Rail<T> src, __0$1x10$lang$Rail$$T$2 $dummy) {
        this((java.lang.System[]) null, T);
        x10$lang$Rail$$init$S(src, (x10.core.Rail.__0$1x10$lang$Rail$$T$2) null);
    }

    // creation method for java code (1-phase java constructor)
    public Rail(final x10.rtt.Type T, final x10.lang.Unsafe.Token id$123, final long size, boolean allocateZeroed) {
        this((java.lang.System[]) null, T);
        x10$lang$Rail$$init$S(id$123, size);
    }

    public static <T> x10.serialization.X10JavaSerializable $_deserialize_body(x10.core.Rail<T> $_obj,
                                                                                x10.serialization.X10JavaDeserializer $deserializer)
            throws java.io.IOException {

        $_obj.T = (x10.rtt.Type) $deserializer.readRef();
        $_obj.raw = $deserializer.readRef();
        $_obj.size = $deserializer.readLong();
        return $_obj;
    }

    public static x10.serialization.X10JavaSerializable $_deserializer(x10.serialization.X10JavaDeserializer $deserializer)
            throws java.io.IOException {

        Rail $_obj = new Rail((java.lang.System[]) null, (x10.rtt.Type) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);

    }

    // initializer of type parameters
    public static void $initParams(final Rail $this, final x10.rtt.Type T) {
        $this.T = T;
    }

    public static <T> void copy__0$1x10$lang$Rail$$T$2__1$1x10$lang$Rail$$T$2(final x10.rtt.Type T,
                                                                              final x10.core.Rail<T> src,
                                                                              final x10.core.Rail<T> dst) {

        if (src.size != dst.size) {
            throw new java.lang.IllegalArgumentException("source and destination do not have equal sizes");
        }

        x10.core.IndexedMemoryChunk.<T> copy(src.raw, 0, dst.raw, 0, (int)src.size);
    }

    public static <T> void copy__0$1x10$lang$Rail$$T$2__2$1x10$lang$Rail$$T$2(final x10.rtt.Type T,
                                                                               final x10.core.Rail<T> src,
                                                                               final int srcIndex,
                                                                               final x10.core.Rail<T> dst,
                                                                               final int dstIndex, final int numElems) {
        x10.core.IndexedMemoryChunk.<T> copy(src.raw, srcIndex, dst.raw, dstIndex, numElems);
    }

    public static <T> void copy__0$1x10$lang$Rail$$T$2__2$1x10$lang$Rail$$T$2(final x10.rtt.Type T,
                                                                               final x10.core.Rail<T> src,
                                                                               final long srcIndex,
                                                                               final x10.core.Rail<T> dst,
                                                                               final long dstIndex, final long numElems) {
        x10.core.IndexedMemoryChunk.<T> copy(src.raw, (int)srcIndex, dst.raw, (int)dstIndex, (int)numElems);
    }

    public void $_serialize(x10.serialization.X10JavaSerializer $serializer) throws java.io.IOException {

        $serializer.write((x10.serialization.X10JavaSerializable) this.T);
        if (raw instanceof x10.serialization.X10JavaSerializable) {
            $serializer.write((x10.serialization.X10JavaSerializable) this.raw);
        } else {
            $serializer.write(this.raw);
        }
        $serializer.write(this.size);
    }

    // dispatcher for method abstract public (Z1)=>U.operator()(a1:Z1){}:U
    public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
        if (t1.equals(x10.rtt.Types.LONG)) {
            return $apply$G(x10.core.Long.$unbox(a1));
        }
        if (t1.equals(x10.rtt.Types.INT)) {
            return $apply$G(x10.core.Int.$unbox(a1));
        }
        throw new java.lang.Error("dispatch mechanism not completely implemented for contra-variant types.");
    }

    public T $apply$G(final int index) {
        return raw.$apply$G(index);
    }

    public T $apply$G(final long index) {
        return raw.$apply$G((int)index);
    }

    public x10.rtt.Type<?> $getParam(int i) {
        if (i == 0) return T;
        return null;
    }

    public x10.rtt.RuntimeType<?> $getRTT() {
        return $RTT;
    }

    public T $set__1x10$lang$Rail$$T$G(final int index, final T v) {
        raw.$set(index, v);
        return v;
    }

    public T $set__1x10$lang$Rail$$T$G(final long index, final T v) {
        raw.$set((int)index, v);
        return v;
    }

    public void clear() {
        raw.clear(0, (int)size);
    }

    public void clear(long start, long numElems) {
        raw.clear((int)start, (int)numElems);
    }

    public x10.lang.Iterator iterator() {
        final x10.lang.RailIterator<T> it = new x10.lang.RailIterator<T>((java.lang.System[]) null, T);
        it.x10$lang$RailIterator$$init$S(this, (__0$1x10$lang$RailIterator$$T$2)null);
        return it;
    }
    
    final public x10.lang.LongRange range() {
        return new LongRange(0, size-1);
    }

    public x10.core.IndexedMemoryChunk<T> raw() {
        return raw;
    }

    public java.lang.String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int sz = size > 10 ? 10 : (int)size;
        for (int i = 0; i < sz; i++) {
            if (i > 0)
                sb.append(",");
            sb.append($apply$G(i));
        }
        if (sz < size)
            sb.append("...(omitted " + (size - sz) + " elements)");
        sb.append("]");
        return sb.toString();

    }

    // constructor for non-virtual call
    final public x10.core.Rail<T> x10$lang$Rail$$init$S() {
        this.size = 0L;
        this.raw = x10.core.IndexedMemoryChunk.<T> allocate(T, 0, false);
        return this;
    }

    // constructor for non-virtual call
    final public x10.core.Rail<T> x10$lang$Rail$$init$S(final int size) {
        this.size = size;
        this.raw =  x10.core.IndexedMemoryChunk.<T> allocate(T, size, true);
        return this;
    }

    // constructor for non-virtual call
    final public x10.core.Rail<T> x10$lang$Rail$$init$S(final int size, final T init, __1x10$lang$Rail$$T $dummy) {
        this.size = size;
        this.raw = x10.core.IndexedMemoryChunk.<T> allocate(T, size, false);
        
        for (int i=0; i<size; i++) {
            this.raw.$set(i, init);
        }
        
        return this;
    }

    // constructor for non-virtual call
    final public x10.core.Rail<T> x10$lang$Rail$$init$S(final int size,
                                                         final x10.core.fun.Fun_0_1<x10.core.Int, T> init,
                                                         __1$1x10$lang$Int$3x10$lang$Rail$$T$2 $dummy) {
        this.size = size;
        this.raw = x10.core.IndexedMemoryChunk.<T> allocate(T, size, false);

        for (int i=0; i<size; i++) {
            T v =  init.$apply(x10.core.Int.$box(i), x10.rtt.Types.INT);
            this.raw.$set(i, v);
        }
        
        return this;
    }

    // constructor for non-virtual call
    final public x10.core.Rail<T> x10$lang$Rail$$init$S(final long size) {
        this.size = size;
        this.raw = x10.core.IndexedMemoryChunk.<T> allocate(T, size, true);

        return this;
    }

    // constructor for non-virtual call
    final public x10.core.Rail<T> x10$lang$Rail$$init$S(final long size, final T init, __1x10$lang$Rail$$T $dummy) {
        this.size = size;
        this.raw = x10.core.IndexedMemoryChunk.<T> allocate(T, size, true);

        for (int i=0; i<size; i++) {
            this.raw.$set(i, init);
        }            

        return this;
    }

    // constructor for non-virtual call
    final public x10.core.Rail<T> x10$lang$Rail$$init$S(final long size,
                                                         final x10.core.fun.Fun_0_1<x10.core.Long, T> init,
                                                         __1$1x10$lang$Long$3x10$lang$Rail$$T$2 $dummy) {
        this.size = size;
        this.raw = x10.core.IndexedMemoryChunk.<T> allocate(T, size, false);

        for (long i=0; i<size; i++) {
            T v =  init.$apply(x10.core.Long.$box(i), x10.rtt.Types.LONG);
            this.raw.$set((int)i, v);
        }
        
        return this;
    }

    // constructor for non-virtual call
    final public x10.core.Rail<T> x10$lang$Rail$$init$S(final x10.core.IndexedMemoryChunk<T> backingStore,
                                                         __0$1x10$lang$Rail$$T$2 $dummy) {
        this.size = backingStore.length;
        this.raw = backingStore;

        return this;
    }

    // constructor for non-virtual call
    final public x10.core.Rail<T> x10$lang$Rail$$init$S(final x10.core.Rail<T> src, __0$1x10$lang$Rail$$T$2 $dummy) {
        this.size = src.size;
        this.raw = x10.core.IndexedMemoryChunk.<T> allocate(T, (int)size, false);
        x10.core.IndexedMemoryChunk.<T> copy(src.raw, 0, raw, 0, (int)size);

        return this;
    }

    // constructor for non-virtual call
    final public x10.core.Rail<T> x10$lang$Rail$$init$S(final x10.lang.Unsafe.Token id$123, final long size) {
        this.size = size;
        this.raw = x10.core.IndexedMemoryChunk.<T> allocate(T, size, false);

        return this;
    }

}
