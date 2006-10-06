/*
 * Created on Nov 14, 2004
 */
package x10.base;

/**
 * @author Christoph von Praun
 *
 */
public final class Allocator {

private static final boolean useSunMiscUnsafe =false;
    public static final MemoryBlock allocUnsafe(int count, int[] ranks, Class cl) {
     
        assert (count >= 0);
        if(useSunMiscUnsafe)
           {
           long size;
           if (cl == Boolean.TYPE) 
              size = Allocator.SIZE_BOOLEAN;
           else if (cl == Character.TYPE) 
              size = Allocator.SIZE_CHAR;
           else if (cl == Byte.TYPE) 
              size = Allocator.SIZE_BYTE;
           else if (cl == Short.TYPE) 
              size = Allocator.SIZE_SHORT;
           else if (cl == Integer.TYPE) 
              size = Allocator.SIZE_INT;
           else if (cl == Float.TYPE)
              size = Allocator.SIZE_FLOAT;
           else if (cl == Double.TYPE)
              size = Allocator.SIZE_DOUBLE;
           else if (cl == Long.TYPE)
              size = Allocator.SIZE_LONG;
           else if (! cl.isPrimitive()) 
              size = Allocator.SIZE_LONG;
           else
              {
              throw new Error("Allocator:: size not unknown " + cl + "'");
              }
           return new MemoryBlockUnsafe(count, ranks, size); 
           }
        else 
           {
           assert (count < Integer.MAX_VALUE);
        
           MemoryBlockSafe mb;
           if (cl == Boolean.TYPE) 
              mb = new MemoryBlockSafeBooleanArray((int) count);
           else if (cl == Character.TYPE) 
              mb = new MemoryBlockSafeCharArray((int) count);
           else if (cl == Byte.TYPE) 
              mb = new MemoryBlockSafeByteArray((int) count);
           else if (cl == Short.TYPE) 
              mb = new MemoryBlockSafeShortArray((int) count);
           else if (cl == Integer.TYPE) 
              mb = new MemoryBlockSafeIntArray((int) count);
           else if (cl == Float.TYPE)
              mb = new MemoryBlockSafeFloatArray((int) count);
           else if (cl == Double.TYPE)
              mb = new MemoryBlockSafeDoubleArray((int) count);
           else if (cl == Long.TYPE)
              mb = new MemoryBlockSafeLongArray((int) count);
           else if (! cl.isPrimitive()) 
              mb = new MemoryBlockSafeObjectArray((int) count);
           else
              {
              mb = null;
              throw new Error("Allocator:: allocSafe not unknown " + cl + "'");
              }
           int[] descriptor = new int[ranks.length+1];
	   descriptor[0] = ranks.length;
	   for(int i=0;i < ranks.length;++i) descriptor[1+i] = ranks[i];
           mb.setDescriptor(descriptor);
           return mb; 
           }
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
