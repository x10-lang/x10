/*
 * Created on Nov 14, 2004
 */
package x10.base;

/**
 * @author Christoph von Praun
 */
public class MemoryBlockSafeIntArray extends MemoryBlockSafe {
    
    private int[] arr_;
    
    MemoryBlockSafeIntArray(int size) {
        arr_ = new int[size];
    }
    
    public int count() { 
        return arr_.length;
    }

    public long size() {
         return Allocator.SIZE_INT;
    }
     
    public void setInt(int val, int d0) {
        arr_[d0] = val;
    }
    
    public int getInt(int d0) {
        return arr_[d0];
    }
}
