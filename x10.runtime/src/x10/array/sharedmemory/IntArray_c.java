/*
 * Created on Oct 20, 2004
 */
package x10.array.sharedmemory;


import x10.array.Array;
import x10.array.Distribution;
import x10.array.IntArray;
import x10.array.Operator;


/**
 * @author Christian Grothoff, Christoph von Praun
 */
public class IntArray_c extends IntArray {

    private final int[] arr_;
    
    /**
     *  This constructor must not be used directly by an application programmer.
     * Arrays are constructed by the corresponding factory methods in 
     * x10.lang.Runtime.
     */
    public IntArray_c(Distribution_c d) {
        super(d);
        this.arr_ = new int[d.size()];
    }
    
    public IntArray_c(Distribution_c d, int c) {
        super(d);
        int size = d.size();
        int[] arr = new int[size];
        for (int i =0; i < size; ++i)
        	arr[i] = c;
        arr_ = arr;
    }
    
    /* Overrides the superclass method - this implementation is more efficient */
    public void reduction(Operator.Reduction op) {
        for (int i  = 0; i < arr_.length; ++i) 
            op.apply(arr_[i]);
    }
    
    /* Overrides the superclass method - this implementation is more efficient */
    protected void assign(Array rhs) {
    	assert rhs instanceof IntArray_c;
    	
    	IntArray_c rhs_t = (IntArray_c) rhs;
    	if (rhs.dist.equals(dist)) {
    		for (int i  = 0; i < arr_.length; ++i) 
    			arr_[i] = rhs_t.arr_[i];
    	} else 
    		// fall back to generic implementation
    		super.assign(rhs);
    }

	protected Array newInstance(Distribution d) {
		assert d instanceof Distribution_c;
		
		return new IntArray_c((Distribution_c) d);	
	}
	
    /* (non-Javadoc)
     * @see x10.lang.IntArray#set(int, int[])
     */
    public void set(int v, int[] pos) {
        arr_[dist.ordinal(pos)] = v;
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
        return arr_[dist.ordinal(pos)];
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
