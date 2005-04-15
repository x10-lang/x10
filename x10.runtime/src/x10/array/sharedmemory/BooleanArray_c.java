/*
 * Created on Oct 20, 2004
 */
package x10.array.sharedmemory;

import java.util.Iterator;



import x10.array.BooleanArray;
import x10.array.MultiDimRegion;
import x10.array.Operator;
import x10.base.Allocator; 
import x10.base.MemoryBlock;
import x10.base.UnsafeContainer;
import x10.lang.Indexable;
import x10.lang.Runtime;
import x10.lang.point;
import x10.lang.distribution;
import x10.lang.region;
import x10.lang.BooleanReferenceArray;


/**
 * @author Christian Grothoff, Christoph von Praun
 */
public class BooleanArray_c extends BooleanArray implements UnsafeContainer, Cloneable {

    private final boolean safe_;
    private final MemoryBlock arr_;
    public final boolean mutable_;
    
    public boolean valueEquals(Indexable other) {
        return arr_.valueEquals(((BooleanArray_c)other).arr_);
    }

    
    /**
     *  This constructor must not be used directly by an application programmer.
     * Arrays are constructed by the corresponding factory methods in 
     * x10.lang.Runtime.
     */
    protected BooleanArray_c(Distribution_c d, boolean safe) {
        this(d, (Operator.Pointwise) null, safe);
    }
    
    protected BooleanArray_c(Distribution_c d, Operator.Pointwise c, boolean safe) {
    	this( d, c, safe, true);
    }
    protected BooleanArray_c(Distribution_c d, Operator.Pointwise c, boolean safe, boolean mutable) {
        super(d);
        this.mutable_ = mutable;
        this.safe_ = safe;
        int count =  d.region.size();
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_BOOLEAN);
        } else {
            this.arr_ =Allocator.allocSafe(count, Boolean.TYPE);
        }
        if (c != null)
            pointwise(this, c);
    }
    
    /** Create a new array per the given distribution, initialized to c.
     * 
     * @param d
     * @param c
     * @param safe
     */
    public BooleanArray_c( distribution d, boolean c) {
        this(d, c, true);
    }
    public BooleanArray_c( distribution d, boolean c, boolean safe ) {
    	this(d, c, safe, true);
}
    public BooleanArray_c( distribution d, boolean c, boolean safe, boolean mutable ) {
    	super(d);
    	this.mutable_ = mutable;
    	int count =  d.region.size();
        this.safe_ = safe;
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_BOOLEAN);
        } else {
            this.arr_ =Allocator.allocSafe(count, Boolean.TYPE);
        }
    	scan(this, new Assign(c));
    	
    }
    public BooleanArray_c( distribution d, BooleanArray.pointwiseOp f) {
        this(d, f, true);
    }
    public BooleanArray_c( distribution d, BooleanArray.pointwiseOp f, boolean safe) {
    	this(d, f, safe, true);
    }
    public BooleanArray_c( distribution d, BooleanArray.pointwiseOp f, boolean safe, boolean mutable) {
    	super(d);
    	this.mutable_ = mutable;
    	int count =  d.region.size();
        this.safe_ = safe;
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_BOOLEAN);
        } else {
            this.arr_ =Allocator.allocSafe(count, Boolean.TYPE);
        }
        scan(this, f);
    }
    
    private BooleanArray_c( distribution d, boolean[] a, boolean safe, boolean mutable) {
    	super(d);
        int count =  d.region.size();
    	this.safe_ = safe;
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_BOOLEAN);
        } else {
            this.arr_ =Allocator.allocSafe(count, Boolean.TYPE);
        }
        this.mutable_ = mutable;
    }
    /** Return a safe IntArray_c initialized with the given local 1-d (Java) int array.
     * 
     * @param a
     * @return
     */
    public static BooleanArray_c BooleanArray_c( boolean[] a, boolean safe, boolean mutable ) {
    	distribution d = Runtime.factory.getDistributionFactory().local(a.length);
    	return new BooleanArray_c(d, a, safe, mutable );
    }
    
    public void keepItLive() {}
    
    public long getUnsafeAddress() {
        return arr_.getUnsafeAddress();
    }
    
    public long getUnsafeDescriptor() {
        return arr_.getUnsafeDescriptor();
    }
    
    /* Overrides the superclass method - this implementation is more efficient */
    public void reduction(Operator.Reduction op) {
        int count = arr_.count();
        for (int i  = 0; i < count; ++i) 
            op.apply(arr_.getBoolean(i));
    }
    
    /* Overrides the superclass method - this implementation is more efficient */
    protected void assign(BooleanArray rhs) {
    	assert rhs instanceof BooleanArray_c;
    	
    	BooleanArray_c rhs_t = (BooleanArray_c) rhs;
    	if (rhs.distribution.equals(distribution)) {
    	    int count = arr_.count();
    		for (int i  = 0; i < count; ++i) 
    			arr_.setBoolean(rhs_t.arr_.getBoolean(i), i);
    	} else 
    		// fall back to generic implementation
    		super.assign(rhs);
    }

	protected BooleanArray newInstance(distribution d) {
		assert d instanceof Distribution_c;
		
		return new BooleanArray_c((Distribution_c) d, safe_);	
	}
	
	protected BooleanArray newInstance(distribution d, Operator.Pointwise c) {
		assert d instanceof Distribution_c;
		
		return new BooleanArray_c((Distribution_c) d, c, safe_);	
	}
	

	public BooleanReferenceArray lift( BooleanArray.binaryOp op, x10.lang.booleanArray arg ) {
	    assert arg.distribution.equals(distribution); 
	    BooleanArray arg1 = (BooleanArray)arg;
	    BooleanArray result = newInstance(distribution);
	    for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	        point p = (point) it.next();
	        result.set(op.apply(this.get(p), arg1.get(p)),p);
	    }
	    return result;
	}
	public BooleanReferenceArray lift( BooleanArray.unaryOp op ) {
	    BooleanArray result = newInstance(distribution);
	    for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	        point p = (point) it.next();
	        result.set((boolean) op.apply(this.get(p)),p);
	    }
	    return result;
	}
    public boolean reduce( BooleanArray.binaryOp op, boolean unit ) {
        boolean result = unit;
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
             result = op.apply(this.get(p), result);
        }
        return result;
    }

    public BooleanReferenceArray scan( binaryOp op, boolean unit ) {
        boolean temp = unit;
        BooleanArray result = newInstance(distribution);
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
            temp = op.apply(this.get(p), temp);
             result.set(temp, p);
        }
        return result;
    }
    
	
    /* (non-Javadoc)
     * @see x10.lang.BooleanArray#set(int, int[])
     */
    public boolean set(boolean v, point pos) {
        distribution.checkAccess(pos);
        return arr_.setBoolean(v, (int) distribution.region.ordinal(pos));
    }
    
    
    public boolean set(boolean v, int d0) {
    	d0 = ((MultiDimRegion)region).ordinal(d0);
    	return arr_.setBoolean(v,d0);
    }
    
    public boolean set(boolean v, int d0, int d1) {
    	int	theIndex = ((MultiDimRegion)region).ordinal(d0,d1);
    	return arr_.setBoolean(v,theIndex);
    }
    
    public boolean set(boolean v, int d0, int d1, int d2) {
    	int	theIndex = ((MultiDimRegion)region).ordinal(d0,d1,d2);
    	return arr_.setBoolean(v,theIndex);
    }
    
    public boolean set(boolean v, int d0, int d1, int d2, int d3) {
    	int	theIndex = ((MultiDimRegion)region).ordinal(d0,d1,d2,d3);
    	return arr_.setBoolean(v,theIndex);
        
    }

    /* (non-Javadoc)
     * @see x10.lang.BooleanArray#get(int[])
     */
    public boolean get(point pos) {
        distribution.checkAccess(pos);
        return arr_.getBoolean((int) distribution.region.ordinal(pos));
    }
    
    public boolean get(int d0) {
    	d0 = ((MultiDimRegion)region).ordinal(d0); 	
    	return arr_.getBoolean(d0);
    }
    public boolean get(int d0, int d1) {   	
    	int theIndex = ((MultiDimRegion)region).ordinal(d0,d1);   	
    	return arr_.getBoolean(theIndex);
    }
    
    public boolean get(int d0, int d1, int d2) {
    	int theIndex = ((MultiDimRegion)region).ordinal(d0,d1,d2);
    	return arr_.getBoolean(theIndex); 
    }
    
    public boolean get(int d0, int d1, int d2, int d3) {
    	int	theIndex = ((MultiDimRegion)region).ordinal(d0,d1,d2,d3);   	
    	return arr_.getBoolean(theIndex);  
    }
    public boolean get(int[] pos) {
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
    	return get(p);
    }
    
    public x10.lang.BooleanReferenceArray overlay(x10.lang.booleanArray d) {
    	distribution dist = distribution.overlay(d.distribution);
        BooleanArray_c ret = new BooleanArray_c(dist, false, safe_);
        for (Iterator it = dist.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            boolean val = (d.distribution.region.contains(p)) ? d.get(p) : get(p);
            ret.set(val, p);
        }
        return ret;
    }
    
    public void update(x10.lang.booleanArray d) {
        assert (region.contains(d.region));
        for (Iterator it = d.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            set(d.get(p), p);
        }
    }
    
    public BooleanReferenceArray union(x10.lang.booleanArray d) {
        distribution dist = distribution.union(d.distribution);
        BooleanArray_c ret = new BooleanArray_c(dist, false, safe_);
        for (Iterator it = dist.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            boolean val = (distribution.region.contains(p)) ? get(p) : d.get(p);
            ret.set(val, p);
        }
        return ret;
    }
    
    public BooleanReferenceArray restriction(distribution d) {
        return restriction(d.region);
    }
    
    public BooleanReferenceArray restriction(region r) {
        distribution dist = distribution.restriction(r);
        BooleanArray_c ret = new BooleanArray_c(dist, false, safe_);
        for (Iterator it = dist.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            ret.set(get(p), p);
        }
        return ret;
    }
    
    public x10.lang.booleanArray toValueArray() {
    	if (! mutable_) return this;
    	throw new Error("TODO: <T>ReferenceArray --> <T>ValueArray");   
    }
    public boolean isValue() {
        return ! this.mutable_;
    }

    
}
