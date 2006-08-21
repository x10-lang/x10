/*
 * Created on Nov 14, 2004
 */
package x10.base;

/**
 * @author Christoph von Praun
 */
public class MemoryBlockSafeFloatArray extends MemoryBlockSafe {
    
    private float[] arr_;
    
    public boolean valueEquals(MemoryBlock other) {
        MemoryBlockSafeFloatArray m = (MemoryBlockSafeFloatArray) other;
        if (m.arr_.length == arr_.length) {
            for (int i=arr_.length-1;i>=0;i--) 
                if (arr_[i] != m.arr_[i])
                    return false;
            return true;
        } else
            return false;
    }
    

    
    MemoryBlockSafeFloatArray(int count) {
        arr_ = new float[count];
    }
    
    MemoryBlockSafeFloatArray(float[] a) {
        arr_ = a;
    }
    
    public float[] getBackingArray() { return arr_; }
    public int count() { 
        return arr_.length;
     }

     public long size() {
         return Allocator.SIZE_FLOAT;
     }
     
     public float setFloat(float val, int d0) {
        return arr_[d0] = val;
    }
    
    public float getFloat(int d0) {
        return arr_[d0];
    }
}
