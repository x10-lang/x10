/*
 * Created on Nov 14, 2004
 */
package x10.base;

/**
 * @author Christoph von Praun
 */
public class MemoryBlockSafeCharArray extends MemoryBlockSafe {
    
    private char[] arr_;
    
    public boolean valueEquals(MemoryBlock other) {
        MemoryBlockSafeCharArray m = (MemoryBlockSafeCharArray) other;
        if (m.arr_.length == arr_.length) {
            for (int i=arr_.length-1;i>=0;i--) 
                if (arr_[i] != m.arr_[i])
                    return false;
            return true;
        } else
            return false;
    }
    

    
    MemoryBlockSafeCharArray(int count) {
        arr_ = new char[count];
    }
    
    MemoryBlockSafeCharArray(char[] a) {
        arr_ = a;
    }
    
    public int count() { 
        return arr_.length;
     }

     public long size() {
         return Allocator.SIZE_CHAR;
     }
     
     public char setChar(char val, int d0) {
        return arr_[d0] = val;
    }
    
    public char getChar(int d0) {
        return arr_[d0];
    }
}
