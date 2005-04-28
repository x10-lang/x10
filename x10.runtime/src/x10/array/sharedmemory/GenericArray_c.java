/*
 * Created on Oct 20, 2004
 */
package x10.array.sharedmemory;

import java.util.Iterator;

import x10.array.GenericArray;
import x10.array.Operator;
import x10.base.Allocator;
import x10.base.MemoryBlock;
import x10.base.UnsafeContainer;
import x10.compilergenerated.Parameter1;
import x10.lang.GenericReferenceArray;
import x10.lang.Indexable;
import x10.lang.Runtime;
import x10.lang.distribution;
import x10.lang.point;
import x10.lang.region;


/**
 * @author Christian Grothoff, Christoph von Praun
 */
public class GenericArray_c extends GenericArray implements UnsafeContainer, Cloneable {

    private final boolean safe_;
    private final MemoryBlock arr_;
    public final boolean mutable_;
    
    public boolean valueEquals(Indexable other) {
        return arr_.valueEquals(((GenericArray_c)other).arr_);
    }

    
    /**
     *  This constructor must not be used directly by an application programmer.
     * Arrays are constructed by the corresponding factory methods in 
     * x10.lang.Runtime.
     */
    protected GenericArray_c(Distribution_c d, boolean safe) {
        this(d, (Operator.Pointwise) null, safe);
    }
    
    protected GenericArray_c(Distribution_c d, Operator.Pointwise c, boolean safe) {
    	this( d, c, safe, true);
    }
    protected GenericArray_c(Distribution_c d, Operator.Pointwise c, boolean safe, boolean mutable) {
        super(d);
        this.mutable_ = mutable;
        safe = true; // just to be GC-safe ;-)
        this.safe_ = safe;
        int count =  d.region.size();
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_DOUBLE);
        } else {
            this.arr_ = Allocator.allocSafe(count, Parameter1.class);
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
    public GenericArray_c( distribution d, Parameter1 c) {
        this(d, c, true);
    }
    public GenericArray_c( distribution d, Parameter1 c, boolean safe ) {
    	this(d, c, safe, true);
}
    public GenericArray_c( distribution d, int c, boolean safe, boolean mutable ) {
        this(d, (Parameter1) null, safe, mutable);
    }
        public GenericArray_c( distribution d, Parameter1 c, boolean safe, boolean mutable ) {
    	super(d);
        assert (safe); // just to be GC-safe ;-)
    	this.mutable_ = mutable;
    	int count =  d.region.size();
        this.safe_ = safe;
        this.arr_ = Allocator.allocSafe(count, Parameter1.class);
        scan(this, new Assign(c));
    }
    public GenericArray_c( distribution d, GenericArray.pointwiseOp f) {
        this(d, f, true);
    }
    public GenericArray_c( distribution d, GenericArray.pointwiseOp f, boolean safe) {
    	this(d, f, safe, true);
    }
    public GenericArray_c( distribution d, GenericArray.pointwiseOp f, boolean safe, boolean mutable) {
    	super(d);
        assert (safe); // just to be GC-safe ;-)
    	this.mutable_ = mutable;
    	int count =  d.region.size();
        this.arr_ = Allocator.allocSafe(count, Parameter1.class);
    	this.safe_ = safe;
        if (f != null)
            scan(this, f);
    }
    
    private GenericArray_c( distribution d, Parameter1[] a, boolean safe, boolean mutable) {
    	super(d);
        assert (safe); // just to be GC-safe ;-)
    	this.arr_ = Allocator.allocSafeObjectArray( a);
        this.safe_ = safe;
        this.mutable_ = mutable;
    }
    /** Return a safe IntArray_c initialized with the given local 1-d (Java) int array.
     * 
     * @param a
     * @return
     */
    public static GenericArray_c GenericArray_c( Parameter1[] a, boolean safe, boolean mutable ) {
    	distribution d = Runtime.factory.getDistributionFactory().local(a.length);
    	return new GenericArray_c(d, a, safe, mutable );
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
            op.apply(arr_.get(i));
    }
    
    /* Overrides the superclass method - this implementation is more efficient */
    protected void assign(GenericArray rhs) {
    	assert rhs instanceof GenericArray_c;
    	
    	GenericArray_c rhs_t = (GenericArray_c) rhs;
    	if (rhs.distribution.equals(distribution)) {
    	    int count = arr_.count();
    		for (int i  = 0; i < count; ++i) 
    			arr_.set(rhs_t.arr_.get(i), i);
    	} else 
    		// fall back to generic implementation
    		super.assign(rhs);
    }

	protected GenericArray newInstance(distribution d) {
		assert d instanceof Distribution_c;
		
		return new GenericArray_c((Distribution_c) d, safe_);	
	}
	
	protected GenericArray newInstance(distribution d, Operator.Pointwise c) {
		assert d instanceof Distribution_c;
		
		return new GenericArray_c((Distribution_c) d, c, safe_);	
	}
	

	public GenericReferenceArray lift( GenericArray.binaryOp op, x10.lang.genericArray arg ) {
	    assert arg.distribution.equals(distribution); 
	    GenericArray arg1 = (GenericArray)arg;
	    GenericArray result = newInstance(distribution);
	    for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	        point p = (point) it.next();
	        result.set(op.apply(this.get(p), arg1.get(p)),p);
	    }
	    return result;
	}
	public GenericReferenceArray lift( GenericArray.unaryOp op ) {
	    GenericArray result = newInstance(distribution);
	    for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	        point p = (point) it.next();
	        result.set(op.apply(this.get(p)),p);
	    }
	    return result;
	}
    public Parameter1 reduce( GenericArray.binaryOp op, Parameter1 unit ) {
        Parameter1 result = unit;
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
             result = op.apply(this.get(p), result);
        }
        return result;
    }

    public GenericReferenceArray scan( binaryOp op, Parameter1 unit ) {
        Parameter1 temp = unit;
        GenericArray result = newInstance(distribution);
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
            temp = op.apply(this.get(p), temp);
             result.set(temp, p);
        }
        return result;
    }
    
	
    /* (non-Javadoc)
     * @see x10.lang.GenericArray#set(int, int[])
     */
    public Parameter1 set(Parameter1 v, point pos) {
        return (Parameter1) arr_.set(v, (int) distribution.region.ordinal(pos));
    }
    
    public Parameter1 setOrdinal(Parameter1 v, int rawIndex) {
    	return (Parameter1) arr_.set(v,rawIndex);
    }
    
    public Parameter1 set(Parameter1 v, int d0) {
    	int	theIndex = Helper.ordinal(region,d0);   	  	 
        return (Parameter1) setOrdinal(v, d0);
    }
    
    public Parameter1 set(Parameter1 v, int d0, int d1) {
    	int	theIndex = Helper.ordinal(region,d0,d1);
        return (Parameter1) setOrdinal(v, theIndex);
    }
    
    public Parameter1 set(Parameter1 v, int d0, int d1, int d2) {
    	int	theIndex = Helper.ordinal(region,d0,d1,d2);
        return (Parameter1) setOrdinal(v, theIndex);
    }
    
    public Parameter1 set(Parameter1 v, int d0, int d1, int d2, int d3) {
    	int	theIndex = Helper.ordinal(region,d0,d1,d2,d3);
        return (Parameter1)  setOrdinal(v, theIndex);
        
    }

    /* (non-Javadoc)
     * @see x10.lang.GenericArray#get(int[])
     */
    public Parameter1 get(point pos) {
        return (Parameter1)  arr_.get((int) distribution.region.ordinal(pos));
    }
    
    public Parameter1 getOrdinal(int rawIndex) {
    	
    	return (Parameter1) arr_.get(rawIndex);
    }
    
    public Parameter1 get(int d0) {
    	d0 = Helper.ordinal(region,d0);
    	return getOrdinal(d0);
    }
    public Parameter1 get(int d0, int d1) {
    	int	theIndex = Helper.ordinal(region,d0,d1);
    	return getOrdinal(theIndex);
    }
    
    public Parameter1 get(int d0, int d1, int d2) {
    	int	theIndex = Helper.ordinal(region,d0,d1,d2);
    	return getOrdinal(theIndex);
    }
    
    public Parameter1 get(int d0, int d1, int d2, int d3) {
    	int	theIndex = Helper.ordinal(region,d0,d1,d2,d3);
    	return getOrdinal(theIndex);
    }
    public Parameter1 get(int[] pos) {
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
    	return get(p);
    }
    
    public x10.lang.GenericReferenceArray overlay(x10.lang.genericArray d) {
    	distribution dist = distribution.overlay(d.distribution);
        GenericArray_c ret = new GenericArray_c(dist, (Parameter1)null, safe_);
        for (Iterator it = dist.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            Parameter1 val = (d.distribution.region.contains(p)) ? d.get(p) : get(p);
            ret.set(val, p);
        }
        return ret;
    }
    
    public void update(x10.lang.genericArray d) {
        assert (region.contains(d.region));
        for (Iterator it = d.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            set(d.get(p), p);
        }
    }
    
    public GenericReferenceArray union(x10.lang.genericArray d) {
        distribution dist = distribution.union(d.distribution);
        GenericArray_c ret = new GenericArray_c(dist, (Parameter1)null, safe_);
        for (Iterator it = dist.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            Parameter1 val = (distribution.region.contains(p)) ? get(p) : d.get(p);
            ret.set(val, p);
        }
        return ret;
    }
    
    public GenericReferenceArray restriction(distribution d) {
        return restriction(d.region);
    }
    
    public GenericReferenceArray restriction(region r) {
        distribution dist = distribution.restriction(r);
        GenericArray_c ret = new GenericArray_c(dist, (Parameter1)null, safe_);
        for (Iterator it = dist.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            ret.set(get(p), p);
        }
        return ret;
    }
    
    public x10.lang.genericArray toValueArray() {
    	if (! mutable_) return this;
    	throw new Error("TODO: <T>ReferenceArray --> <T>ValueArray");   
    }
    public boolean isValue() {
        return ! this.mutable_;
    }

    
}
