/*
 * Created on Nov 14, 2004
 */
package x10.base;

/**
 * @author Christoph von Praun
 *
 */
public final class Allocator {

    public static final MemoryBlock allocUnsafe(int count, int[] ranks, long size) {
        assert (size > 0);
        assert (count >= 0);
        return new MemoryBlockUnsafe(count, ranks, size); 
    }
    
    
    public static final MemoryBlock allocPinned(int count, long size) {
        assert (size > 0);
        assert (count >= 0);
        return new MemoryBlockPinned(count, size); 
    }
    
    public static final MemoryBlock allocSafe(long size, Class cl) {
        assert (cl != null);
        assert (size >= 0);
        assert (size < Integer.MAX_VALUE);
        
        MemoryBlock mb;
        if (cl == Boolean.TYPE) 
            mb = new MemoryBlockSafeBooleanArray((int) size);
        else if (cl == Character.TYPE) 
            mb = new MemoryBlockSafeCharArray((int) size);
        else if (cl == Byte.TYPE) 
            mb = new MemoryBlockSafeByteArray((int) size);
        else if (cl == Short.TYPE) 
            mb = new MemoryBlockSafeShortArray((int) size);
        else if (cl == Integer.TYPE) 
            mb = new MemoryBlockSafeIntArray((int) size);
        else if (cl == Float.TYPE)
            mb = new MemoryBlockSafeFloatArray((int) size);
        else if (cl == Double.TYPE)
            mb = new MemoryBlockSafeDoubleArray((int) size);
        else if (cl == Long.TYPE)
            mb = new MemoryBlockSafeLongArray((int) size);
        else if (! cl.isPrimitive()) 
            mb = new MemoryBlockSafeObjectArray((int) size);
        else {
            mb = null;
            throw new Error("Allocator:: allocSafe not unknown " + cl + "'");
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
    
    public static final MemoryBlock allocSafeObjectArray(Object[] a) {      
        return new MemoryBlockSafeObjectArray( a );
    }
    
    public static final int SIZE_BOOLEAN = 1; 
    public static final int SIZE_BYTE = 1; 
    public static final int SIZE_CHAR = 2; 
    public static final int SIZE_SHORT = 2; 
    public static final int SIZE_INT = 4; 
    public static final int SIZE_PTR = 4; // FIXME: 64 bit architectures... 
    public static final int SIZE_LONG = 8;
    public static final int SIZE_FLOAT = 4; 
    public static final int SIZE_DOUBLE = 8;
}
