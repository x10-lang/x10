/*
 * Created on Oct 20, 2004
 */
package x10.array.sharedmemory;

import x10.array.Array;
import x10.array.Distribution;
import x10.array.DoubleArray;
import x10.array.Operator;
import x10.base.Allocator;
import x10.base.MemoryBlock;
import x10.base.UnsafeContainer;


/**
 * @author Christian Grothoff, Christoph von Praun
 */
public class DoubleArray_c extends DoubleArray implements UnsafeContainer {

    private boolean safe_;
    private final MemoryBlock arr_;
    
    /**
     *  This constructor must not be used directly by an application programmer.
     * Arrays are constructed by the corresponding factory methods in 
     * x10.lang.Runtime.
     */
    protected DoubleArray_c(Distribution_c d, boolean safe) {
        this(d, null, safe);
    }
    
    protected DoubleArray_c(Distribution_c d, Operator.Pointwise c, boolean safe) {
        super(d);
        int count = d.count();
        this.arr_ = safe ? Allocator.allocSafe(count, Double.TYPE) : Allocator.allocUnsafe(count, Allocator.SIZE_DOUBLE);
        if (c != null)
            pointwise(this, c);
    }
    
    public void keepItLive() {}
    
    public long getUnsafeAddress() {
        return arr_.getUnsafeAddress();
    }
    
    /* Overrides the superclass method - this implementation is more efficient */
    public void reduction(Operator.Reduction op) {
        int count = arr_.count();
        for (int i  = 0; i < count; ++i) 
            op.apply(arr_.getDouble(i));
    }
    
    /* Overrides the superclass method - this implementation is more efficient */
    protected void assign(Array rhs) {
    	assert rhs instanceof DoubleArray_c;
    	
    	DoubleArray_c rhs_t = (DoubleArray_c) rhs;
    	if (rhs.dist.equals(dist)) {
    	    int count = arr_.count();
    		for (int i  = 0; i < count; ++i) 
    			arr_.setDouble(rhs_t.arr_.getDouble(i), i);
    	} else 
    		// fall back to generic implementation
    		super.assign(rhs);
    }

	protected Array newInstance(Distribution d) {
		assert d instanceof Distribution_c;
		
		return new DoubleArray_c((Distribution_c) d, safe_);	
	}
	
	protected Array newInstance(Distribution d, Operator.Pointwise c) {
		assert d instanceof Distribution_c;
		
		return new DoubleArray_c((Distribution_c) d, c, safe_);	
	}
	
    /* (non-Javadoc)
     * @see x10.lang.DoubleArray#set(int, int[])
     */
    public void set(double v, int[] pos) {
        arr_.setDouble(v, dist.ordinal(pos));
    }
    
    public void set(double v, int d0) {
        int[] pos = {d0};
        set(v, pos);
    }
    
    public void set(double v, int d0, int d1) {
        int[] pos = {d0, d1};
        set(v, pos);
    }
    
    public void set(double v, int d0, int d1, int d2) {
        int[] pos = {d0, d1, d2};
        set(v, pos);
    }
    
    public void set(double v, int d0, int d1, int d2, int d3) {
        int[] pos = {d0, d1, d2, d3};
        set(v, pos);
    }

    /* (non-Javadoc)
     * @see x10.lang.DoubleArray#get(int[])
     */
    public double get(int[] pos) {
        return arr_.getDouble(dist.ordinal(pos));
    }
    
    public double get(int d0) {
        int[] pos = {d0};
    	return get(pos);
    }
    public double get(int d0, int d1) {
        int[] pos = {d0, d1};
    	return get(pos);
    }
    public double get(int d0, int d1, int d2) {
        int[] pos = {d0, d1, d2};
    	return get(pos);
    }
    public double get(int d0, int d1, int d2, int d3) {
        int[] pos = {d0, d1, d2, d3};
    	return get(pos);
    }
}
