/*
 * Created on Nov 14, 2004
 */
package x10.base;

/**
 * @author Christoph von Praun
 * @author vj
 */
public class MemoryBlockSafeIntArray extends MemoryBlockSafe {
    
    private int[] arr_;
    
    public boolean valueEquals(MemoryBlock other) {
        MemoryBlockSafeIntArray m = (MemoryBlockSafeIntArray) other;
        if (m.arr_.length == arr_.length) {
            for (int i=arr_.length-1;i>=0;i--) 
                if (arr_[i] != m.arr_[i])
                    return false;
            return true;
        } else
            return false;
    }

    
    MemoryBlockSafeIntArray(int size) {
        arr_ = new int[size];
    }
    /** Allow the environment to pass in the array.
     *  For instance this might be an array explicitly initialized by the user.
     * @param a
     */
    MemoryBlockSafeIntArray(int[] a) {
    	arr_ = a;
    }
    
    public int[] getBackingArray() { return arr_; }
    
    public int count() { 
        return arr_.length;
    }

    public long size() {
         return Allocator.SIZE_INT;
    }
     
    public int setInt(int val, int d0) {
        return arr_[d0] = val;
    }
    
    public int getInt(int d0) {
        return arr_[d0];
    }
}
