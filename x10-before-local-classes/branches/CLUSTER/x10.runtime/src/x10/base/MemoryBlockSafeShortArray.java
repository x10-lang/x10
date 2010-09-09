/*
 * Created on Nov 14, 2004
 */
package x10.base;

/**
 * @author Christoph von Praun
 */
public class MemoryBlockSafeShortArray extends MemoryBlockSafe {
    
    private short[] arr_;
    
    public boolean valueEquals(MemoryBlock other) {
        MemoryBlockSafeShortArray m = (MemoryBlockSafeShortArray) other;
        if (m.arr_.length == arr_.length) {
            for (int i=arr_.length-1;i>=0;i--) 
                if (arr_[i] != m.arr_[i])
                    return false;
            return true;
        } else
            return false;
    }
    

    
    MemoryBlockSafeShortArray(int count) {
        arr_ = new short[count];
    }
    
    MemoryBlockSafeShortArray(short[] a) {
        arr_ = a;
    }
    
    public short[] getBackingArray() { return arr_; }
    public int count() { 
        return arr_.length;
     }

     public long size() {
         return Allocator.SIZE_SHORT;
     }
     
     public short setShort(short val, int d0) {
        return arr_[d0] = val;
    }
    
    public short getShort(int d0) {
        return arr_[d0];
    }
}
