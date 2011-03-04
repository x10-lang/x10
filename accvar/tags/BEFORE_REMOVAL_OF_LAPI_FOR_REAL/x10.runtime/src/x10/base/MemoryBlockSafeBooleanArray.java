/*
 * Created on Nov 14, 2004
 */
package x10.base;

/**
 * @author Christoph von Praun
 */
public class MemoryBlockSafeBooleanArray extends MemoryBlockSafe {
    
    private boolean[] arr_;
    
    public boolean valueEquals(MemoryBlock other) {
        MemoryBlockSafeBooleanArray m = (MemoryBlockSafeBooleanArray) other;
        if (m.arr_.length == arr_.length) {
            for (int i=arr_.length-1;i>=0;i--) 
                if (arr_[i] != m.arr_[i])
                    return false;
            return true;
        } else
            return false;
    }
    

    
    MemoryBlockSafeBooleanArray(int count) {
        arr_ = new boolean[count];
    }
    
    MemoryBlockSafeBooleanArray(boolean[] a) {
        arr_ = a;
    }
    
    public int count() { 
        return arr_.length;
     }

     public long size() {
         return Allocator.SIZE_BOOLEAN;
     }
     
     public boolean setBoolean(boolean val, int d0) {
        return arr_[d0] = val;
    }
    
    public boolean getBoolean(int d0) {
        return arr_[d0];
    }
}
