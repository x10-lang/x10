/*
 * Created on Nov 14, 2004
 */
package x10.base;

import sun.misc.Unsafe;
import java.lang.reflect.Field;

/**
 * @author Christoph von Praun, Patrick Gallop
 */
public class MemoryBlockUnsafe extends MemoryBlock {
    
    private final long     address_;     // address of allocated region
    private final long     size_;        // total size of allocated region
    private final int      count_;       // number of elements 
    private volatile int   live_;        // see keepItLive() below

    private static final Unsafe unsafe_ = getUnsafe(); // for sun.misc.Unsafe

    public boolean valueEquals(MemoryBlock other) {
        MemoryBlockUnsafe m = (MemoryBlockUnsafe) other;
        if (m.size_ == size_) {
            for (long i=size_-1;i>=0;i--) 
                if (m.getByte((int)i) != getByte((int)i)) // FIXME: types inconsistent!
                    return false;
            return true;
        } else
            return false;
    }

    // Allocate a buffer with size count elements each of given size
    MemoryBlockUnsafe(int count, long size) { 
       count_   = count; 
       size_    = count_*size;
       address_ = unsafe_.allocateMemory(size_); 
    }
    
    // Free the allocated buffer
    protected void finalize() { 
        unsafe_.freeMemory(address_);
    }


    // Return the underlying buffer address
    public long getUnsafeAddress() { 
       return address_;
    }

    public int count() { 
       return count_;
    }

    public long size() {
        return size_;
    }
    
    // Check if d0 is in bounds 
    private void check(int d0) { 
    }

    //
    // 1D Array based set() and get() methods  (d0 is array index)
    //    
     public byte setByte(byte val, int d0) { 
       check(d0); 
       unsafe_.putByte(address_+d0*Allocator.SIZE_BYTE, val); 
       return val;
    }
    public byte getByte(int d0) { 
       check(d0); 
       return unsafe_.getByte(address_+d0*Allocator.SIZE_BYTE); 
    }

    public char setChar(char val, int d0) {
        unsafe_.putChar(address_+d0*Allocator.SIZE_CHAR, val); 
        return val;
    }
    
    public char getChar(int d0) {
        return unsafe_.getChar(address_+d0*Allocator.SIZE_CHAR); 
    }
    
    public short setShort(short val, int d0) { 
       unsafe_.putShort(address_+d0*Allocator.SIZE_SHORT, val); 
       return val;
    }
    public short getShort(int d0) { 
       check(d0); 
       return unsafe_.getShort(address_+d0*Allocator.SIZE_SHORT); 
    }

    public int setInt(int val, int d0) { 
       check(d0); 
       unsafe_.putInt(address_+d0*Allocator.SIZE_INT, val); 
       return val;
    }
    public int getInt(int d0) { 
       check(d0); 
       return unsafe_.getInt(address_+d0*Allocator.SIZE_INT); 
    }
    
    public long setLong(long val, int d0) { 
        check(d0); 
        unsafe_.putLong(address_+d0*Allocator.SIZE_LONG, val); 
        return val;
     }
    
     public long getLong(int d0) { 
        check(d0); 
        return unsafe_.getLong(address_+d0*Allocator.SIZE_LONG); 
     }

    public float setFloat(float val, int d0) { 
       check(d0); 
       unsafe_.putFloat(address_+d0*Allocator.SIZE_FLOAT, val); 
       return val;
    }
    public float getFloat(int d0) { 
       check(d0); 
       return unsafe_.getFloat(address_+d0*Allocator.SIZE_FLOAT); 
    }

    public double setDouble(double val, int d0) { 
       check(d0); 
       unsafe_.putDouble(address_+d0*Allocator.SIZE_DOUBLE, val); 
       return val;
    }
    public double getDouble(int d0) { 
       check(d0); 
       return unsafe_.getDouble(address_+d0*Allocator.SIZE_DOUBLE); 
    }


    // 
    // The following method is used by compiler generated native glue when
    // passing address_ to native code. E.G.
    //     native(o.getBufferAddress());
    //     o.keepItLive(); 
    // 
    // keepItLive() is invoked after the native call so that o will 
    // not be garbage collected during the native call
    //
    public void keepItLive() { 
       live_ = 1; 
    }

  
    //
    // sun.misc.Unsafe is a class that allows unsafe access (put/get)
    // to memory. The methods of sun.misc.Unsafe need an unsafe object
    // for the memory accesses. 
    // 
    //
    // WARNING: sun.misc.Unsafe is internally used in a Sun and IBM 
    // JVMs for implementation of java.nio methods.  It is not an 
    // external API and could disappear in future JVM releases.
    // The following code is also specific to these VMs and may also
    // not work if the implementation of sun.misc.Unsafe changes.
    //
    private static Unsafe getUnsafe() {
        Unsafe unsafe = null;
        try {
            Class uc = Unsafe.class;
            Field[] fields = uc.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getName().equals("theUnsafe")) {
                    fields[i].setAccessible(true);
                    unsafe = (Unsafe) fields[i].get(uc);
                    break;
                }
            }
        } catch (Exception ignore) {
        }
        return unsafe;
   }
}