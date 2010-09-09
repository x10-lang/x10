package x10.runtime;

public class AnyRail<T> implements Indexable<Integer,T>, Fun_0_1<Integer,T> {
    public final int length;
    
    protected final Object value;
    protected final Type type;
    
    protected AnyRail(Type type, int length) {
        this.length = length;
        this.type = type;
        
        if (type.isBoolean())
            this.value = new boolean[length];
        else if (type.isByte())
            this.value = new byte[length];
        else if (type.isShort())
            this.value = new short[length];
        else if (type.isInt())
            this.value = new int[length];
        else if (type.isChar())
            this.value = new char[length];
        else if (type.isLong())
            this.value = new long[length];
        else if (type.isFloat())
            this.value = new float[length];
        else if (type.isDouble())
            this.value = new double[length];
        else
            this.value = new Object[length];
    }
    
    protected AnyRail(Type type, int length, Object array) {
        this.length = length;
        this.type = type;
        this.value = array;
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
    
    public int length() {
        return length;
    }
    
    public T get(Integer i) {
        return ((T[]) value)[i];
    }
    
    public T apply(Integer i) {
        return ((T[]) value)[i];
    }
    
    public T get(int i) {
        return ((T[]) value)[i];
    }
    
    public T apply(int i) {
        return ((T[]) value)[i];
    }
}
