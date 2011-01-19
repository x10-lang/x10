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

import x10.core.fun.VoidFun_0_0;
import x10.rtt.RuntimeType;
import x10.rtt.RuntimeType.Variance;
import x10.rtt.Type;

public final class IndexedMemoryChunk<T> extends x10.core.Struct {
    public final int length;
    public final Object value;
    public final Type<T> type;

    public IndexedMemoryChunk(Type<T> type) {
        this.length = 0;
        this.type = type;
        this.value = null;
    }
    public IndexedMemoryChunk(Type<T> type, int length, Object value) {
        this.length = length;
        this.type = type;
        this.value = value;
    }

    private IndexedMemoryChunk(Type<T> type, int length, boolean zeroed) {
        this(type, length, type.makeArray(length));
        if (zeroed) {
            if (!x10.rtt.Types.hasNaturalZero(type)) {
                Object zeroValue = x10.rtt.Types.zeroValue(type);
                java.util.Arrays.fill((Object[]) value, zeroValue);
            }
        }
    }

    public static <T> IndexedMemoryChunk<T> allocate(Type<T> type, int length, boolean zeroed) {
        return new IndexedMemoryChunk<T>(type, length, zeroed);
    }

    public java.lang.String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IndexedMemoryChunk(");
        int sz = Math.min(length, 10);
        for (int i = 0; i < sz; i++) {
            if (i > 0)
                sb.append(",");
            sb.append($apply$G(i));
        }
        if (sz < length) sb.append("...(omitted " + (length - sz) + " elements)");
        sb.append(")");
        return sb.toString();
    }

    public T $apply$G(int i) {
        return type.getArray(value, i);
    }

    public void $set(T v, int i) {
        type.setArray(value, i, v);
    }

    public static <T> void asyncCopy(IndexedMemoryChunk<T> src, int srcIndex, 
                                     RemoteIndexedMemoryChunk<T> dst, int dstIndex,
                                     int numElems) {
        System.arraycopy(src.value, srcIndex, dst.value, dstIndex, numElems);
    }

    public static <T> void asyncCopy(IndexedMemoryChunk<T> src, int srcIndex, 
                                     RemoteIndexedMemoryChunk<T> dst, int dstIndex,
                                     int numElems, VoidFun_0_0 notifier) {
        System.arraycopy(src.value, srcIndex, dst.value, dstIndex, numElems);
        notifier.$apply();
    }

    public static <T> void asyncCopy(RemoteIndexedMemoryChunk<T> src, int srcIndex, 
                                     IndexedMemoryChunk<T> dst, int dstIndex,
                                     int numElems) {
        System.arraycopy(src.value, srcIndex, dst.value, dstIndex, numElems);
    }

    public static <T> void asyncCopy(RemoteIndexedMemoryChunk<T> src, int srcIndex, 
                                     IndexedMemoryChunk<T> dst, int dstIndex,
                                     int numElems, VoidFun_0_0 notifier) {
        System.arraycopy(src.value, srcIndex, dst.value, dstIndex, numElems);
        notifier.$apply();
    }

    public static <T> void copy(IndexedMemoryChunk<T> src, int srcIndex, 
                                IndexedMemoryChunk<T> dst, int dstIndex,
                                int numElems) {
        System.arraycopy(src.value, srcIndex, dst.value, dstIndex, numElems);
    }

    @Override
    public boolean _struct_equals(Object o) {
        return o != null && this.value == ((IndexedMemoryChunk<?>) o).value;
    }

    public static final RuntimeType<IndexedMemoryChunk<?>> _RTT = new RuntimeType<IndexedMemoryChunk<?>>(
        IndexedMemoryChunk.class,
        new RuntimeType.Variance[] { Variance.INVARIANT }
    ) {
        @Override
        public java.lang.String typeName() {
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


    // Methods to get the backing array.   May be called by generated code.
    public Object getBackingArray() { return value; }

    public boolean[] getBooleanArray() { return (boolean[]) value; }
    public byte[] getByteArray() { return (byte[]) value; }
    public short[] getShortArray() { return (short[]) value; }
    public char[] getCharArray() { return (char[]) value; }
    public int[] getIntArray() { return (int[]) value; }
    public long[] getLongArray() { return (long[]) value; }
    public float[] getFloatArray() { return (float[]) value; }
    public double[] getDoubleArray() { return (double[]) value; }
    public Object[] getObjectArray() { return (Object[]) value; }

    // this is broken
    /*
    public Object[] getBoxedArray() {
        if (value instanceof boolean[]) {
            boolean[] a = (boolean[]) value;
            Boolean[] b = new Boolean[a.length];
            for (int i = 0; i < a.length; i++) b[i] = a[i];
        }
        if (value instanceof byte[]) {
            byte[] a = (byte[]) value;
            Byte[] b = new Byte[a.length];
            for (int i = 0; i < a.length; i++) b[i] = a[i];
        }
        if (value instanceof char[]) {
            char[] a = (char[]) value;
            Character[] b = new Character[a.length];
            for (int i = 0; i < a.length; i++) b[i] = a[i];
        }
        if (value instanceof short[]) {
            short[] a = (short[]) value;
            Short[] b = new Short[a.length];
            for (int i = 0; i < a.length; i++) b[i] = a[i];
        }
        if (value instanceof int[]) {
            int[] a = (int[]) value;
            Integer[] b = new Integer[a.length];
            for (int i = 0; i < a.length; i++) b[i] = a[i];
        }
        if (value instanceof long[]) {
            long[] a = (long[]) value;
            Long[] b = new Long[a.length];
            for (int i = 0; i < a.length; i++) b[i] = a[i];
        }
        if (value instanceof float[]) {
            float[] a = (float[]) value;
            Float[] b = new Float[a.length];
            for (int i = 0; i < a.length; i++) b[i] = a[i];
        }
        if (value instanceof double[]) {
            double[] a = (double[]) value;
            Double[] b = new Double[a.length];
            for (int i = 0; i < a.length; i++) b[i] = a[i];
        }
        return (Object[]) value;
    }
    */

}
