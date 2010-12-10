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

import x10.lang.Place;
import x10.rtt.RuntimeType;
import x10.rtt.RuntimeType.Variance;
import x10.rtt.Type;

public final class RemoteIndexedMemoryChunk<T> extends x10.core.Struct {
    public final int length;
    public final Object value;
    public final Type<T> type;
    public final Place home;

    private RemoteIndexedMemoryChunk(Type<T> type, int length, Object value) {
        this.length = length;
        this.type = type;
        this.value = value;
        this.home = x10.lang.Runtime.home();
    }

    public static <T> RemoteIndexedMemoryChunk<T> wrap(IndexedMemoryChunk<T> chunk) {
        return new RemoteIndexedMemoryChunk<T>(chunk.type, chunk.length, chunk.value);
    }

    @Override
    public boolean _struct_equals(Object o) {
        if (!(o instanceof RemoteIndexedMemoryChunk<?>)) return false;
        RemoteIndexedMemoryChunk<?> that = (RemoteIndexedMemoryChunk<?>)o;
        return this.value == that.value && this.home == that.home;
    }

    public static final RuntimeType<RemoteIndexedMemoryChunk<?>> _RTT = new RuntimeType<RemoteIndexedMemoryChunk<?>>(
        RemoteIndexedMemoryChunk.class,
        Variance.INVARIANT
    ) {
        @Override
        public java.lang.String typeName() {
            return "x10.util.RemoteIndexedMemoryChunk";
        }
    };
    
    @Override
    public RuntimeType<RemoteIndexedMemoryChunk<?>> getRTT() {
        return _RTT;
    }

    @Override
    public Type<?> getParam(int i) {
        return i == 0 ? type : null;
    }


    // Methods to get the backing array.   May be called by generated code.
    // Only useful in singleVM runtime.  
    // Will be removed in multi-VM.
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

}
