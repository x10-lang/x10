/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.core;

import x10.core.fun.Fun_0_1;
import x10.types.Type;
import x10.types.Types;

public final class ValRail<T> extends Value implements AnyRail<T> {
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
    
    public ValRail(Type<T> type, Rail<T> rail) {
        this.length = rail.length;
        this.type = type;
        this.value = rail;
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
    	return value.toString();
    }

    
    //
    // boxed rail
    //
    
    @Override
    public Ref box$() {
    	return new BoxedValRail(type, this);
    }
    
    public static class BoxedValRail<T> extends Box<ValRail<T>> implements Indexable<Integer,T>, Fun_0_1<Integer,T> {
    	public BoxedValRail(Type<T> T, ValRail<T> v) {
    		super(new ValRail.RTT(T), v);
		}

		public T apply(Integer o) {
			return this.value.apply(o);
		}

		public Type<?> rtt_x10$lang$Fun_0_1_U() {
			throw new RuntimeException();
		}

		public Type<?> rtt_x10$lang$Fun_0_1_Z1() {
			throw new RuntimeException();
		}
    }

    
    //
    // Runtime type information
    //
    
    static public class RTT<T> extends x10.types.RuntimeType<ValRail<T>> {
        Type<T> type;
        
        public RTT(Type<T> type) {
            super(ValRail.class);
            this.type = type;
        }

        public boolean instanceof$(java.lang.Object o) {
            if (!(o instanceof ValRail))
                return false;
            ValRail r = (ValRail) o;
            if (! r.type.isSubtype(type)) // covariant
                return false;
            return true;
        }
        
        
        public boolean isSubtype(Type<?> type) {
            if (type instanceof ValRail.RTT) {
                ValRail.RTT r = (ValRail.RTT) type;
                return r.type.equals(this.type);
            }
//            if (type instanceof Fun_0_1.RTT) {
//                Fun_0_1.RTT r = (Fun_0_1.RTT) type;
//                return r.I.equals(Types.INT) && r.V.equals(this.type);
//            }
            return false;
        }
    }

    public Type<?> rtt_x10$lang$Fun_0_1_Z1() { return Types.INT; }
    public Type<?> rtt_x10$lang$Fun_0_1_U()  { return type; }
}
