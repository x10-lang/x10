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
import x10.rtt.NamedType;
import x10.rtt.ParameterizedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;
import x10.rtt.UnresolvedType;
import x10.serialization.X10JavaDeserializer;
import x10.serialization.X10JavaSerializable;
import x10.serialization.X10JavaSerializer;

@SuppressWarnings("rawtypes")
public final class Rail<T> extends x10.core.Ref implements x10.lang.Iterable, 
                                                           x10.core.fun.Fun_0_1,
                                                           X10JavaSerializable {
    
    // synthetic types for parameter mangling
    public static final class __initFunInt { }
    public static final class __initFunLong { }
    public static final class __initVal { }

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public static final RuntimeType<Rail> $RTT = 
            NamedType.<Rail> make("x10.lang.Rail", /* base class */
                                  Rail.class,
                                  RuntimeType.INVARIANTS(1),
                                  new Type[] {
                                      ParameterizedType.make(x10.lang.Iterable.$RTT, UnresolvedType.PARAM(0)),
                                      ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, Types.INT, UnresolvedType.PARAM(0)),
                                      ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, Types.LONG, UnresolvedType.PARAM(0)) 
            });

    private Type T;
    
    public Type<?> $getParam(int i) { 
        return i == 0 ? T : null;
    }

    public RuntimeType<?> $getRTT() {
        return $RTT;
    }

    public long size;

    public x10.core.IndexedMemoryChunk<T> raw;

    /*
     * Constructors
     */
    
    // constructor just for allocation
    public Rail(java.lang.System[] $dummy, Type T) {
        this.T = T;
    }

    public Rail(Type T) {
        this.T = T;
        this.size = 0L;
        this.raw = x10.core.IndexedMemoryChunk.<T> allocate(T, 0, false);
    }

    // For Java interop:  value is a Java[] of some form.
    public Rail(Type T, int size, Object value) {
        this.T = T;
        this.size = size;
        this.raw = new x10.core.IndexedMemoryChunk<T>(T, size, value);
    }

    public Rail(Type T, int size) {
        this.T = T;
        this.size = size;
        this.raw =  x10.core.IndexedMemoryChunk.<T> allocate(T, size, true);
    }

    public Rail(Type T, int size, T init, __initVal $dummy) {
        this.T = T;
        this.size = size;
        this.raw = x10.core.IndexedMemoryChunk.<T> allocate(T, size, false);
        
        for (int i=0; i<size; i++) {
            this.raw.$set(i, init);
        }
    }

    public Rail(Type T, int size, x10.core.fun.Fun_0_1<x10.core.Int, T> init, __initFunInt $dummy) {
        this.T = T;
        this.size = size;
        this.raw = x10.core.IndexedMemoryChunk.<T> allocate(T, size, false);

        for (int i=0; i<size; i++) {
            T v =  init.$apply(x10.core.Int.$box(i), Types.INT);
            this.raw.$set(i, v);
        }
    }

    public Rail(Type T, long size) {
        this.T = T;
        this.size = size;
        this.raw = x10.core.IndexedMemoryChunk.<T> allocate(T, size, true);
    }

    public Rail(Type T, long size, T init, __initVal $dummy) {
        this.T = T;
        this.size = size;
        this.raw = x10.core.IndexedMemoryChunk.<T> allocate(T, size, true);

        for (int i=0; i<size; i++) {
            this.raw.$set(i, init);
        }    
    }

    public Rail(Type T, long size, x10.core.fun.Fun_0_1<x10.core.Long, T> init,
                __initFunLong $dummy) {
        this.T = T;
        this.size = size;
        this.raw = x10.core.IndexedMemoryChunk.<T> allocate(T, size, false);

        for (long i=0; i<size; i++) {
            T v =  init.$apply(x10.core.Long.$box(i), Types.LONG);
            this.raw.$set((int)i, v);
        }
    }

    public Rail(Type T, x10.core.IndexedMemoryChunk<T> backingStore) {
        this.T = T;
        this.size = backingStore.length;
        this.raw = backingStore;
    }

    public Rail(Type T, Rail<T> src) {
        this.T = T;
        
        this.size = src.size;
        this.raw = x10.core.IndexedMemoryChunk.<T> allocate(T, (int)size, false);
        x10.core.IndexedMemoryChunk.<T> copy(src.raw, 0, raw, 0, (int)size);
    }

    public Rail(Type T, x10.lang.Unsafe.Token id$123, long size, boolean allocateZeroed) {
        this.T = T;
        this.size = size;
        this.raw = x10.core.IndexedMemoryChunk.<T> allocate(T, size, allocateZeroed);
    }

    /*
     * Serialization
     */

    public void $_serialize(X10JavaSerializer serializer) throws java.io.IOException {
        serializer.write((X10JavaSerializable) this.T);
        if (raw instanceof X10JavaSerializable) {
            serializer.write((X10JavaSerializable) this.raw);
        } else {
            serializer.write(this.raw);
        }
        serializer.write(this.size);
    }
    
    @SuppressWarnings("unchecked")
    public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws java.io.IOException {
        Rail obj = new Rail((java.lang.System[]) null, (Type) null);
        deserializer.record_reference(obj);
        return $_deserialize_body(obj, deserializer);
    }
    
    public static <T> X10JavaSerializable $_deserialize_body(Rail<T> obj, 
                                                             X10JavaDeserializer deserializer) throws java.io.IOException {
       obj.T = (Type) deserializer.readRef();
       obj.raw = deserializer.readRef();
       obj.size = deserializer.readLong();
       return obj;
   }

   /*
    * Source-level instance methods
    */
   
    // dispatcher for method abstract public (Z1)=>U.operator()(a1:Z1){}:U
    public java.lang.Object $apply(java.lang.Object a1, Type t1) {
        if (t1.equals(Types.LONG)) {
            return $apply$G(x10.core.Long.$unbox(a1));
        }
        if (t1.equals(Types.INT)) {
            return $apply$G(x10.core.Int.$unbox(a1));
        }
        throw new java.lang.Error("dispatch mechanism not completely implemented for contra-variant types.");
    }

    public T $apply$G(int index) {
        return raw.$apply$G(index);
    }

    public T $apply$G(long index) {
        return raw.$apply$G((int)index);
    }

    public T $set__1x10$lang$Rail$$T$G(int index, T v) {
        raw.$set(index, v);
        return v;
    }

    public T $set__1x10$lang$Rail$$T$G(long index, T v) {
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
       return new RailIterator<T>(T, this, null);
    }
    
    public x10.lang.LongRange range() {
        return new LongRange(0, size-1);
    }

    public x10.core.IndexedMemoryChunk<T> raw() {
        return raw;
    }

    public String toString() {
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
    
    /*
     * Static methods
     */

    public static <T> void copy__0$1x10$lang$Rail$$T$2__1$1x10$lang$Rail$$T$2(Type T,
                                                                              Rail<T> src,
                                                                              Rail<T> dst) {

        if (src.size != dst.size) {
            throw new java.lang.IllegalArgumentException("source and destination do not have equal sizes");
        }

        x10.core.IndexedMemoryChunk.<T> copy(src.raw, 0, dst.raw, 0, (int)src.size);
    }

    public static <T> void copy__0$1x10$lang$Rail$$T$2__2$1x10$lang$Rail$$T$2(Type T,
                                                                              Rail<T> src,
                                                                              int srcIndex,
                                                                              Rail<T> dst,
                                                                              int dstIndex, 
                                                                              int numElems) {
        x10.core.IndexedMemoryChunk.<T> copy(src.raw, srcIndex, dst.raw, dstIndex, numElems);
    }

    public static <T> void copy__0$1x10$lang$Rail$$T$2__2$1x10$lang$Rail$$T$2(Type T,
                                                                              Rail<T> src,
                                                                              long srcIndex,
                                                                              Rail<T> dst,
                                                                              long dstIndex, 
                                                                              long numElems) {
        x10.core.IndexedMemoryChunk.<T> copy(src.raw, (int)srcIndex, dst.raw, (int)dstIndex, (int)numElems);
    }

}
