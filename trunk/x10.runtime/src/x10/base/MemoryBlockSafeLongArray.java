/*
 * Created on Nov 14, 2004
 */
package x10.base;

/**
 * @author Christoph von Praun
 */
public class MemoryBlockSafeLongArray extends MemoryBlockSafe {
    
    private long[] arr_;
    
    public boolean valueEquals(MemoryBlock other) {
        MemoryBlockSafeLongArray m = (MemoryBlockSafeLongArray) other;
        if (m.arr_.length == arr_.length) {
            for (int i=arr_.length-1;i>=0;i--) 
                if (arr_[i] != m.arr_[i])
                    return false;
            return true;
        } else
            return false;
    }

    
    MemoryBlockSafeLongArray(int count) {
        arr_ = new long[count];
    }
    
    MemoryBlockSafeLongArray(long[] a) {
        arr_ = a;
    }
    
    public int count() { 
        return arr_.length;
     }

     public long size() {
         return Allocator.SIZE_DOUBLE;
     }
     
     public long setLong(long val, int d0) {
        return arr_[d0] = val;
    }
    
    public long getLong(int d0) {
        return arr_[d0];
    }
}
