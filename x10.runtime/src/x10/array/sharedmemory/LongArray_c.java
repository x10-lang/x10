/*
 * Created on Oct 20, 2004
 */
package x10.array.sharedmemory;

import java.util.Iterator;
import x10.array.LongArray;
import x10.array.LongArray.Assign;
import x10.array.Operator;
import x10.base.Allocator;
import x10.base.MemoryBlock;
import x10.base.UnsafeContainer;
import x10.lang.Indexable;
import x10.lang.Runtime;
import x10.lang.point;
import x10.lang.distribution;
import x10.lang.region;
import x10.lang.LongReferenceArray;


/**
 * @author Christian Grothoff, Christoph von Praun
 */
public class LongArray_c extends LongArray implements UnsafeContainer {

    private final boolean safe_;
    private final MemoryBlock arr_;
    public final boolean mutable_;

    public boolean valueEquals(Indexable other) {
        return arr_.valueEquals(((LongArray_c)other).arr_);
    }

    /**
     *  This constructor must not be used directly by an application programmer.
     * Arrays are constructed by the corresponding factory methods in 
     * x10.lang.Runtime.
     */
    protected LongArray_c(Distribution_c d, boolean safe) {
        this(d, (Operator.Pointwise) null, safe);
    }
    
    protected LongArray_c(Distribution_c d, Operator.Pointwise c, boolean safe) {
    	this( d, c, safe, true);
    }
    protected LongArray_c(Distribution_c d, Operator.Pointwise c, boolean safe, boolean mutable) {
        super(d);
        this.mutable_ = mutable;
        this.safe_ = safe;
        int count =  d.region.size();
        this.arr_ = safe ? Allocator.allocSafe(count, Long.TYPE) : Allocator.allocUnsafe(count, Allocator.SIZE_LONG);
        if (c != null)
            pointwise(this, c);
    }
    
    /** Create a new array per the given distribution, initialized to c.
     * 
     * @param d
     * @param c
     * @param safe
     */
    public LongArray_c( distribution d, long c) {
        this(d, c, true);
    }
    public LongArray_c( distribution d, long c, boolean safe ) {
    	this(d, c, safe, true);
}
    public LongArray_c( distribution d, long c, boolean safe, boolean mutable ) {
    	super(d);
    	this.mutable_ = mutable;
    	int count =  d.region.size();
    	this.arr_ = safe ? Allocator.allocSafe(count, Long.TYPE) : Allocator.allocUnsafe(count, Allocator.SIZE_LONG);
        this.safe_ = safe;
    	scan(this, new Assign(c));
    	
    }
    public LongArray_c( distribution d, LongArray.pointwiseOp f) {
        this(d, f, true);
    }
    public LongArray_c( distribution d, LongArray.pointwiseOp f, boolean safe) {
    	this(d, f, safe, true);
    }
    public LongArray_c( distribution d, LongArray.pointwiseOp f, boolean safe, boolean mutable) {
    	super(d);
    	this.mutable_ = mutable;
    	int count =  d.region.size();
    	this.arr_ = safe ? Allocator.allocSafe(count, Long.TYPE) : Allocator.allocUnsafe(count, Allocator.SIZE_LONG);
    	this.safe_ = safe;
        scan(this, f);
    }
    
    private LongArray_c( distribution d, long[] a) {
    	super(d);
    	this.arr_ = Allocator.allocSafeLongArray( a);
        this.safe_ = true;
        this.mutable_ = true;
    }
    /** Return a safe IntArray_c initialized with the given local 1-d (Java) int array.
     * 
     * @param a
     * @return
     */
    public static LongArray_c LongArray_c( long[] a) {
    	distribution d = Runtime.factory.getDistributionFactory().local(a.length);
    	return new LongArray_c(d, a);
    }
    
    public void keepItLive() {}
    
    public long getUnsafeAddress() {
        return arr_.getUnsafeAddress();
    }
    
    /* Overrides the superclass method - this implementation is more efficient */
    public void reduction(Operator.Reduction op) {
        int count = arr_.count();
        for (int i  = 0; i < count; ++i) 
            op.apply(arr_.getLong(i));
    }
    
    /* Overrides the superclass method - this implementation is more efficient */
    protected void assign(LongArray rhs) {
    	assert rhs instanceof LongArray_c;
    	
    	LongArray_c rhs_t = (LongArray_c) rhs;
    	if (rhs.distribution.equals(distribution)) {
    	    int count = arr_.count();
    		for (int i  = 0; i < count; ++i) 
    			arr_.setLong(rhs_t.arr_.getLong(i), i);
    	} else 
    		// fall back to generic implementation
    		super.assign(rhs);
    }

	protected LongArray newInstance(distribution d) {
		assert d instanceof Distribution_c;
		
		return new LongArray_c((Distribution_c) d, safe_);	
	}
	
	protected LongArray newInstance(distribution d, Operator.Pointwise c) {
		assert d instanceof Distribution_c;
		
		return new LongArray_c((Distribution_c) d, c, safe_);	
	}
	

	public LongReferenceArray lift( LongArray.binaryOp op, x10.lang.longArray arg ) {
	    assert arg.distribution.equals(distribution); 
	    LongArray arg1 = (LongArray)arg;
	    LongArray result = newInstance(distribution);
	    for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	        point p = (point) it.next();
	        result.set(op.apply(this.get(p), arg1.get(p)),p);
	    }
	    return result;
	}
	public LongReferenceArray lift( LongArray.unaryOp op ) {
	    LongArray result = newInstance(distribution);
	    for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	        point p = (point) it.next();
	        result.set(op.apply(this.get(p)),p);
	    }
	    return result;
	}
    public long reduce( LongArray.binaryOp op, long unit ) {
        long result = unit;
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
             result = op.apply(this.get(p), result);
        }
        return result;
    }

    public LongReferenceArray scan( binaryOp op, long unit ) {
        long temp = unit;
        LongArray result = newInstance(distribution);
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
            temp = op.apply(this.get(p), temp);
             result.set(temp, p);
        }
        return result;
    }
    
	
    /* (non-Javadoc)
     * @see x10.lang.LongArray#set(int, int[])
     */
    public long set(long v, point pos) {
        distribution.checkAccess(pos);
        return arr_.setLong(v, (int) distribution.region.ordinal(pos));
    }
    
    
    public long set(long v, int d0) {
    	assert this.region.rank == 1;
        int[] pos = {d0};
    	final point p = Runtime.factory.getPointFactory().point(this.region, pos);
        return set(v, p);
    }
    
    public long set(long v, int d0, int d1) {
    	assert this.region.rank == 2;
        int[] pos = {d0, d1};
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
        return set(v, p);
    }
    
    public long set(long v, int d0, int d1, int d2) {
    	assert this.region.rank == 3;
        int[] pos = {d0, d1, d2};
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
        return set(v, p);
    }
    
    public long set(long v, int d0, int d1, int d2, int d3) {
    	assert this.region.rank == 4;
        int[] pos = {d0, d1, d2, d3};
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
        return set(v, p);
        
    }

    /* (non-Javadoc)
     * @see x10.lang.LongArray#get(int[])
     */
    public long get(point pos) {
        distribution.checkAccess(pos);
        return arr_.getLong((int) distribution.region.ordinal(pos));
    }
    
    public long get(int d0) {
    	assert this.region.rank == 1;
        int[] pos = {d0};
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
    	return get(p);
    }
    public long get(int d0, int d1) {
    	assert this.region.rank == 2;
        int[] pos = {d0, d1};
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
    	return get(p);
    }
    
    public long get(int d0, int d1, int d2) {
    	assert this.region.rank == 3;
        int[] pos = {d0, d1, d2};
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
    	return get(p);
    }
    
    public long get(int d0, int d1, int d2, int d3) {
    	assert this.region.rank == 4;
        int[] pos = {d0, d1, d2, d3};
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
    	return get(p);
    }
    public long get(int[] pos) {
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
    	return get(p);
    }
    public LongReferenceArray overlay(x10.lang.longArray d) {
    	distribution dist = distribution.overlay(d.distribution);
        LongArray_c ret = new LongArray_c(dist, 0, safe_);
        for (Iterator it = dist.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            long val = (d.distribution.region.contains(p)) ? d.get(p) : get(p);
            ret.set(val, p);
        }
        return ret;
    }
    

    public void update(x10.lang.longArray d) {
        assert (region.contains(d.region));
        for (Iterator it = d.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            set(d.get(p), p);
        }
    }
    
    
    public LongReferenceArray union(x10.lang.longArray d) {
        distribution dist = distribution.union(d.distribution);
        LongArray_c ret = new LongArray_c(dist, 0, safe_);
        for (Iterator it = dist.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            long val = (distribution.region.contains(p)) ? get(p) : d.get(p);
            ret.set(val, p);
        }
        return ret;
    }
    
    public LongReferenceArray restriction(distribution d) {
        return restriction(d.region);
    }
    
    public LongReferenceArray restriction(region r) {
        distribution dist = distribution.restriction(r);
        LongArray_c ret = new LongArray_c(dist, 0, safe_);
        for (Iterator it = dist.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            ret.set(get(p), p);
        }
        return ret;
    }
    public x10.lang.longArray toValueArray() {
    	if (! mutable_) return this;
    	throw new Error("TODO: <T>ReferenceArray --> <T>ValueArray");
    	
    }
    public boolean isValue() {
        return ! this.mutable_;
    }

}
