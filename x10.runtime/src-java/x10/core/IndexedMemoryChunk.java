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

package x10.core;

import java.util.Arrays;

import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.RuntimeType.Variance;

public final class IndexedMemoryChunk<T> extends x10.core.Struct {
    public final int length;
    public final Object value;
    public final Type<T> type;

    public IndexedMemoryChunk(Type<T> type, int length, Object value) {
        this.length = length;
        this.type = type;
        this.value = value;
    }

    private IndexedMemoryChunk(Type<T> type, int length) {
        this(type, length, type.makeArray(length));
    }

    public static <T> IndexedMemoryChunk<T> allocate(Type<T> type, int length) {
        return new IndexedMemoryChunk<T>(type, length);
    }

    public T apply$G(int i) {
        return type.getArray(value, i);
    }

    public void set(T v, int i) {
        type.setArray(value, i, v);
    }

    public void _copyToLocal(int srcIndex, IndexedMemoryChunk<T> dst, int dstIndex, int numElems) {
        System.arraycopy(value, srcIndex, dst.value, dstIndex, numElems);
    }

    public void _copyFromLocal(int dstIndex, IndexedMemoryChunk<T> src, int srcIndex, int numElems) {
        System.arraycopy(src.value, srcIndex, value, dstIndex, numElems);
    }

    @Override
    public boolean _struct_equals(Object o) {
        return o != null && this.value == ((IndexedMemoryChunk<?>) o).value;
        /*
        if (!_RTT.instanceof$(o, type)) return false;
        IndexedMemoryChunk<?> chunk = (IndexedMemoryChunk<?>) o;
        Object chunk_value = chunk.value;
        if (value instanceof boolean[]) {
            return Arrays.equals((boolean[]) value, (boolean[]) chunk_value);
        }
        if (value instanceof byte[]) {
            return Arrays.equals((byte[]) value, (byte[]) chunk_value);
        }
        if (value instanceof char[]) {
            return Arrays.equals((char[]) value, (char[]) chunk_value);
        }
        if (value instanceof short[]) {
            return Arrays.equals((short[]) value, (short[]) chunk_value);
        }
        if (value instanceof int[]) {
            return Arrays.equals((int[]) value, (int[]) chunk_value);
        }
        if (value instanceof long[]) {
            return Arrays.equals((long[]) value, (long[]) chunk_value);
        }
        if (value instanceof float[]) {
            return Arrays.equals((float[]) value, (float[]) chunk_value);
        }
        if (value instanceof double[]) {
            return Arrays.equals((double[]) value, (double[]) chunk_value);
        }
        return Arrays.equals((Object[]) value, (Object[]) chunk_value);
        */
    }

    public String typeName() {
        return _RTT.typeName(this);
    }

    public static final RuntimeType<IndexedMemoryChunk<?>> _RTT = new RuntimeType<IndexedMemoryChunk<?>>(
        IndexedMemoryChunk.class,
        Variance.INVARIANT
    ) {
        @Override
        public String typeName() {
            return "x10.util.IndexedMemoryChunk";
        }
    };
    
    @Override
    public RuntimeType<IndexedMemoryChunk<?>> getRTT() {
        return _RTT;
    }

    @Override
    public Type<?> getParam(int i) {
        return i == 0 ? type : null;
    }

}
