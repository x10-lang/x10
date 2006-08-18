/*
 * Created on Nov 14, 2004
 */
package x10.base;

/**
 * @author Christoph von Praun
 */
public class MemoryBlockSafeDoubleArray extends MemoryBlockSafe {
    
    private double[] arr_;
    
    public boolean valueEquals(MemoryBlock other) {
        MemoryBlockSafeDoubleArray m = (MemoryBlockSafeDoubleArray) other;
        if (m.arr_.length == arr_.length) {
            for (int i=arr_.length-1;i>=0;i--) 
                if (arr_[i] != m.arr_[i])
                    return false;
            return true;
        } else
            return false;
    }
    

    
    MemoryBlockSafeDoubleArray(int count) {
        arr_ = new double[count];
    }
    
    MemoryBlockSafeDoubleArray(double[] a) {
        arr_ = a;
    }
    
    public int count() { 
        return arr_.length;
     }

     public long size() {
         return Allocator.SIZE_DOUBLE;
     }
     
     public double setDouble(double val, int d0) {
        return arr_[d0] = val;
    }
    
    public double getDouble(int d0) {
        return arr_[d0];
    }
}