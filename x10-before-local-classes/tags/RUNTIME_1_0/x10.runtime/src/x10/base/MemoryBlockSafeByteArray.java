/*
 * Created on Nov 14, 2004
 */
package x10.base;

/**
 * @author Christoph von Praun
 */
public class MemoryBlockSafeByteArray extends MemoryBlockSafe {
    
    private byte[] arr_;
    
    public boolean valueEquals(MemoryBlock other) {
        MemoryBlockSafeByteArray m = (MemoryBlockSafeByteArray) other;
        if (m.arr_.length == arr_.length) {
            for (int i=arr_.length-1;i>=0;i--) 
                if (arr_[i] != m.arr_[i])
                    return false;
            return true;
        } else
            return false;
    }
    
    MemoryBlockSafeByteArray(int count) {
        arr_ = new byte[count];
    }
    
    MemoryBlockSafeByteArray(byte[] a) {
        arr_ = a;
    }
    
    public int count() { 
        return arr_.length;
     }

     public long size() {
         return Allocator.SIZE_BYTE;
     }
     
     public byte setByte(byte val, int d0) {
        return arr_[d0] = val;
    }
    
    public byte getByte(int d0) {
        return arr_[d0];
    }
}
