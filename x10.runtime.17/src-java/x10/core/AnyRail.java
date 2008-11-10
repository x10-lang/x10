/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.core;

import x10.core.Iterator;
import x10.core.Iterable;
import x10.core.fun.Fun_0_1;
import x10.types.Type;
import x10.types.Types;

public class AnyRail<T> extends Value implements Indexable<Integer,T>, Fun_0_1<Integer,T>, Iterable<T> {
    public final int length;
    
    public final Object value;
    public final Type<T> type;
    
    protected AnyRail(Type<T> type, int length) {
        this(type, length, type.makeArray(length));
    }
    
    protected AnyRail(Type<T> type, int length, Object array) {
        this.length = length;
        this.type = type;
        this.value = array;
    }
    
	public Iterator<T> iterator() {
		return new RailIterator();
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

    public Type<?> rtt_x10$lang$Fun_0_1_Z1() { return Types.INT; }
    public Type<?> rtt_x10$lang$Fun_0_1_U()  { return type; }
    
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
    	return value.toString();
    }
}
