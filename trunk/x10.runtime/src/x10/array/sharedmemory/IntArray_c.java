/*
 * Created on Oct 20, 2004
 */
package x10.array.sharedmemory;


import java.util.Iterator;

import x10.base.MemoryBlock;
import x10.base.Allocator;
import x10.base.UnsafeContainer;


import x10.array.IntArray;
import x10.array.Operator;
import x10.lang.point;
import x10.lang.distribution;
import x10.lang.region;
import x10.lang.Runtime;
import x10.lang.IntReferenceArray;


/**
 * @author Christian Grothoff, Christoph von Praun
 */
public class IntArray_c extends IntArray implements UnsafeContainer {

    private boolean safe_;
    private final MemoryBlock arr_;
    
    public void keepItLive() {}
    
    public long getUnsafeAddress() {
        return arr_.getUnsafeAddress();
    }
    
    /**
     * This constructor must not be used directly by an application programmer.
     * Arrays are constructed by the corresponding factory methods in 
     * x10.lang.Runtime.
     */
    protected IntArray_c(Distribution_c d, boolean safe) {
        this(d, (Operator.Pointwise) null, safe);
    }
    
    /** Create a new array per the given distribution, initialized to c.
     * 
     * @param d
     * @param c
     * @param safe
     */
    public IntArray_c( distribution d, int c) {
	this(d, c, true);
    }
    public IntArray_c( distribution d, int c, boolean safe) {
    	super(d);
    	int count = d.region.size();
    	this.arr_ = safe ? Allocator.allocSafe(count, Integer.TYPE) 
    			: Allocator.allocUnsafe(count, Allocator.SIZE_INT);
    	scan(this, new Assign(c));
    	
    }
    public IntArray_c( distribution d, IntArray.pointwiseOp f){
	this(d, f, true);
    }
    public IntArray_c( distribution d, IntArray.pointwiseOp f, boolean safe) {
    	super(d);
    	int count =  d.region.size();
    	this.arr_ = safe ? Allocator.allocSafe(count, Integer.TYPE)
    			: Allocator.allocUnsafe(count, Allocator.SIZE_INT);
    	scan(this, f);
    	
    }
    private IntArray_c( distribution d, int[] a) {
    	super(d);
    	this.arr_ = Allocator.allocSafeIntArray( a);
    }
    /** Return a safe IntArray_c initialized with the given local 1-d (Java) int array.
     * 
     * @param a
     * @return
     */
    public static IntArray_c IntArray_c(int[] a) {
    	distribution d = Runtime.factory.getDistributionFactory().here(a.length);
    	return new IntArray_c(d, a);
    }
    protected IntArray_c(Distribution_c d, Operator.Pointwise c, boolean safe) {
        super(d);
        int count = d.region.size();
        this.arr_ = safe ? Allocator.allocSafe(count, Integer.TYPE) : Allocator.allocUnsafe(count, Allocator.SIZE_INT);
        if (c != null)
            pointwise(this, c);
    }
    
    /* Overrides the superclass method - this implementation is more efficient */
    public void reduction(Operator.Reduction op) {
        int count = arr_.count();
        for (int i  = 0; i < count; ++i) 
            op.apply(arr_.getInt(i));
    }
    
  
    
    public x10.lang.intArray lift( IntArray.binaryOp op, x10.lang.intArray arg ) {
        assert arg.distribution == this.distribution;
        IntReferenceArray result = newInstance(distribution);
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
             result.set(op.apply(this.get(p), arg.get(p)),p);
        }
        return result;
    }

    public int reduce( IntArray.binaryOp op, int unit ) {
        int result = unit;
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
             result = op.apply(this.get(p), result);
        }
        return result;
    }

    public x10.lang.intArray scan( IntArray.binaryOp op, int unit ) {
        int temp = unit;
        x10.lang.IntReferenceArray result = newInstance(distribution);
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
            temp = op.apply(this.get(p), temp);
             result.set(temp, p);
        }
        return result;
    }
    
    /* Overrides the superclass method - this implementation is more efficient */
    protected void assign(IntArray rhs) {
    	assert rhs.getClass() == this.getClass();
    	
    	IntArray_c rhs_t = (IntArray_c) rhs;
    	if (rhs.distribution.equals(distribution)) {
    	    int count = arr_.count();
    		for (int i  = 0; i < count; ++i) 
    			arr_.setInt(rhs_t.arr_.getInt(i), i);
    	} else 
    		// fall back to generic implementation
    		super.assign(rhs);
    }

	protected IntArray newInstance(distribution d) {
		assert d instanceof Distribution_c;
		
		return new IntArray_c((Distribution_c) d, safe_);	
	}
	
	protected IntArray newInstance( distribution d, Operator.Pointwise p) {
		assert d instanceof Distribution_c;
		
		return new IntArray_c((Distribution_c) d, p, safe_);	
	}
	
    /* (non-Javadoc)
     * @see x10.lang.IntArray#set(int, int[])
     */
    public void set(int v, point pos) {
        arr_.setInt(v, distribution.region.ordinal(pos));
    }
    
    public void set(int v, int d0) {
    	assert this.region.rank == 1;
        int[] pos = {d0};
    	final point p = Runtime.factory.getPointFactory().point(this.region, pos);
        set(v, p);
    }
    
    public void set(int v, int d0, int d1) {
    	assert this.region.rank == 2;
        int[] pos = {d0, d1};
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
        set(v, p);
    }
    
    public void set(int v, int d0, int d1, int d2) {
    	assert this.region.rank == 3;
        int[] pos = {d0, d1, d2};
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
        set(v, p);
    }
    
    public void set(int v, int d0, int d1, int d2, int d3) {
    	assert this.region.rank == 4;
        int[] pos = {d0, d1, d2, d3};
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
        set(v, p);
        
    }

    /* (non-Javadoc)
     * @see x10.lang.IntArray#get(int[])
     */
    public int get(point pos) {
        return arr_.getInt((int) distribution.region.ordinal(pos));
    }
    public int get(int d0) {
    	assert this.region.rank == 1;
        int[] pos = {d0};
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
    	return get(p);
    }
    public int get(int d0, int d1) {
    	assert this.region.rank == 2;
        int[] pos = {d0, d1};
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
    	return get(p);
    }
    public int get(int d0, int d1, int d2) {
    	assert this.region.rank == 3;
        int[] pos = {d0, d1, d2};
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
    	return get(p);
    }
    public int get(int d0, int d1, int d2, int d3) {
    	assert this.region.rank == 4;
        int[] pos = {d0, d1, d2, d3};
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
    	return get(p);
    }
    public x10.lang.intArray overlay(x10.lang.intArray d) {
    	throw new Error("TODO");
    }
    public x10.lang.intArray union(x10.lang.intArray d) {
    	throw new Error("TODO");
    }
    public x10.lang.intArray restriction( distribution d) {
    	throw new Error("TODO");
    }
    public x10.lang.intArray restriction( region d) {
    	throw new Error("TODO");
    }
}
