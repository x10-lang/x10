package x10.core;

import x10.array.Point;
import x10.core.fun.Fun_0_1;
import x10.types.Type;
import x10.types.Types;

public class AnyRail<T> implements Indexable<Integer,T>, Fun_0_1<Integer,T> {
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
    
    public int length() {
        return length;
    }
    
    public T get(Integer i) {
        return type.getArray(value, i);
    }
    
    public T apply(Integer i) {
        return type.getArray(value, i);
    }
}
