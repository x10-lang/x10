/*
 * Created on Nov 14, 2004
 */
package x10.base;

/**
 * @author Christoph von Praun
 */
public class MemoryBlockSafeDoubleArray extends MemoryBlockSafe {
    
    private double[] arr_;
    
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
     
     public void setDouble(double val, int d0) {
        arr_[d0] = val;
    }
    
    public double getDouble(int d0) {
        return arr_[d0];
    }
}