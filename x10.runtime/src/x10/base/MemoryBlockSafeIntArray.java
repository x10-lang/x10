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
