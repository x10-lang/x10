/*
 * Created on Oct 20, 2004
 */
package x10.array.sharedmemory;


import x10.base.MemoryBlock;
import x10.base.Allocator;
import x10.array.Array;
import x10.array.Distribution;
import x10.array.IntArray;
import x10.array.Operator;


/**
 * @author Christian Grothoff, Christoph von Praun
 */
public class IntArray_c extends IntArray {

    private boolean safe_;
    private final MemoryBlock arr_;
    
    /**
     * This constructor must not be used directly by an application programmer.
     * Arrays are constructed by the corresponding factory methods in 
     * x10.lang.Runtime.
     */
    public IntArray_c(Distribution_c d, boolean safe) {
        super(d);
        int count = d.count();
        this.arr_ = safe ? Allocator.allocSafe(count, Integer.TYPE) : Allocator.allocUnsafe(count, Allocator.SIZE_INT);
    }
    
    public IntArray_c(Distribution_c d, int c, boolean safe) {
        super(d);
        int count = d.count();
        this.arr_ = safe ? Allocator.allocSafe(count, Integer.TYPE) : Allocator.allocUnsafe(count, Allocator.SIZE_INT);
        for (int i = 0; i < count; ++i)
        	arr_.setInt(c, i);
    }
    
    /* Overrides the superclass method - this implementation is more efficient */
    public void reduction(Operator.Reduction op) {
        int count = arr_.count();
        for (int i  = 0; i < count; ++i) 
            op.apply(arr_.getInt(i));
    }
    
    /* Overrides the superclass method - this implementation is more efficient */
    protected void assign(Array rhs) {
    	assert rhs.getClass() == this.getClass();
    	
    	IntArray_c rhs_t = (IntArray_c) rhs;
    	if (rhs.dist.equals(dist)) {
    	    int count = arr_.count();
    		for (int i  = 0; i < count; ++i) 
    			arr_.setInt(rhs_t.arr_.getInt(i), i);
    	} else 
    		// fall back to generic implementation
    		super.assign(rhs);
    }

	protected Array newInstance(Distribution d) {
		assert d instanceof Distribution_c;
		
		return new IntArray_c((Distribution_c) d, safe_);	
	}
	
    /* (non-Javadoc)
     * @see x10.lang.IntArray#set(int, int[])
     */
    public void set(int v, int[] pos) {
        arr_.setInt(v, dist.ordinal(pos));
    }
    
    public void set(int v, int d0) {
        int[] pos = {d0};
        set(v, pos);
    }
    
    public void set(int v, int d0, int d1) {
        int[] pos = {d0, d1};
        set(v, pos);
    }
    
    public void set(int v, int d0, int d1, int d2) {
        int[] pos = {d0, d1, d2};
        set(v, pos);
    }
    
    public void set(int v, int d0, int d1, int d2, int d3) {
        int[] pos = {d0, d1, d2, d3};
        set(v, pos);
    }

    /* (non-Javadoc)
     * @see x10.lang.IntArray#get(int[])
     */
    public int get(int[] pos) {
        return arr_.getInt(dist.ordinal(pos));
    }
    
    public int get(int d0) {
        int[] pos = {d0};
    	return get(pos);
    }
    public int get(int d0, int d1) {
        int[] pos = {d0, d1};
    	return get(pos);
    }
    public int get(int d0, int d1, int d2) {
        int[] pos = {d0, d1, d2};
    	return get(pos);
    }
    public int get(int d0, int d1, int d2, int d3) {
        int[] pos = {d0, d1, d2, d3};
    	return get(pos);
    }
}
