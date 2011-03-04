package x10.base;

/**
 * @author Christian Grothoff
 */
public class MemoryBlockSafeObjectArray extends MemoryBlockSafe {
    
    private Object[] arr_;
    
    public boolean valueEquals(MemoryBlock other) {
        MemoryBlockSafeObjectArray m = (MemoryBlockSafeObjectArray) other;
        if (m.arr_.length == arr_.length) {
            for (int i=arr_.length-1;i>=0;i--) 
                if (arr_[i] != m.arr_[i])
                    return false;
            return true;
        } else
            return false;
    }
    

    
    MemoryBlockSafeObjectArray(int count) {
        arr_ = new Object[count];
    }
    
    MemoryBlockSafeObjectArray(Object[] a) {
        arr_ = a;
    }
    
    public int count() { 
        return arr_.length;
     }

     public long size() {
         return Allocator.SIZE_PTR;
     }
     
     public Object set(Object val, int d0) {
        return arr_[d0] = val;
    }
    
    public Object get(int d0) {
        return arr_[d0];
    }
}