/*
 * Created on Nov 14, 2004
 */
package x10.base;

/**
 * @author Christoph von Praun
 *
 */
public final class Allocator {

    public static final MemoryBlock allocUnsafe(int count, long size) {
        assert (size > 0);
        assert (count >= 0);
        return new MemoryBlockUnsafe(count, size); 
    }
    
    public static final MemoryBlock allocSafe(long size, Class cl) {
        assert (cl != null);
        assert (size >= 0);
        assert (size < Integer.MAX_VALUE);
        
        MemoryBlock mb;
        if (cl == Integer.TYPE) 
            mb = new MemoryBlockSafeIntArray((int) size);
        else if (cl == Double.TYPE)
            mb = new MemoryBlockSafeDoubleArray((int) size);
        else if (cl == Long.TYPE)
            mb = new MemoryBlockSafeLongArray((int) size);
        else {
            mb = null;
            throw new Error("Allocator:: allocSafe not implemented for type '" + cl + "'");
        }
        return mb;
    }
    public static final MemoryBlock allocSafeIntArray(int[] a) {      
        return new MemoryBlockSafeIntArray( a );
    }
    
    public static final MemoryBlock allocSafeLongArray(long[] a) {      
        return new MemoryBlockSafeLongArray( a );
    }
    
    public static final MemoryBlock allocSafeDoubleArray(double[] a) {      
        return new MemoryBlockSafeDoubleArray( a );
    }
    
    public static final int SIZE_BOOLEAN = 1; 
    public static final int SIZE_BYTE = 1; 
    public static final int SIZE_CHAR = 2; 
    public static final int SIZE_SHORT = 2; 
    public static final int SIZE_INT = 4; 
    public static final int SIZE_LONG = 8;
    public static final int SIZE_FLOAT = 4; 
    public static final int SIZE_DOUBLE = 8;
}
