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

import x10.core.fun.Fun_0_1;
import x10.rtt.ParameterizedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;
import x10.rtt.UnresolvedType;
import x10.rtt.RuntimeType.Variance;

public final class ValRail<T> implements AnyRail<T> {
    public final int length;
    
    public final Object value;
    public final Type<T> type;
    
    public ValRail(Type<T> type, int length) {
        this(type, length, type.makeArray(length));
    }
    
    public ValRail(Type<T> type, int length, Object array) {
        this.length = length;
        this.type = type;
        this.value = array;
    }
    
    // added to support views.
    public ValRail(Type<T> type, Rail<T> rail) {
        this.length = rail.length;
        this.type = type;
        this.value = rail;
    }
    
	public Iterator<T> iterator() {
		return new RailIterator();
	}

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof ValRail) {
            ValRail<?> that = (ValRail<?>)o;
            if (length != that.length) return false;
            for (int i=0; i<length; i++) {
                if (!this.get(i).equals(that.get(i))) return false;
            }
            return true;
        }
        return false;
    }

    protected class RailIterator implements Iterator<T> {
		int i = 0;

		public boolean hasNext() {
			return i < length;
		}

		public T next() {
			return apply(i++);
		}
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

    public Integer length() {
    	return length;
    }
    
    public T get(Integer i) {
    	return apply(i);
    }
    
    public T apply(Integer i) {
    	return type.getArray(value, i);
    }
    
    protected T set$(T v, Integer i) {
    	return type.setArray(value, i, v);
    }
    
    public boolean isZero() {
    	boolean zero = true;
		for (int i = 0; i < length && zero; ++i) {
			zero &= get(i) == type.zeroValue();
		}
		return zero;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < length; i++) {
            if (i > 0)
                sb.append(", ");
            sb.append(apply(i));
        }
        sb.append("]");
        return sb.toString();
    }
  
    //
    // Runtime type information
    //

    public static final RuntimeType _RTT = new RuntimeType(
        ValRail.class,
        new Variance[] {Variance.COVARIANT},
        new Type<?>[] {new ParameterizedType(Fun_0_1._RTT, Types.INT, new UnresolvedType(0))}
    );
    
    public RuntimeType<?> getRTT() {
        return _RTT;
    }
    public Type<?> getParam(int i) {
        return i == 0 ? type : null;
    }
}
