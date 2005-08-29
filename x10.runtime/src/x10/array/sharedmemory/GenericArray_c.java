/*
 * Created on Oct 20, 2004
 */
package x10.array.sharedmemory;

import java.util.Iterator;
import x10.array.Helper;
import x10.array.GenericArray;
import x10.array.Operator;
import x10.base.Allocator;
import x10.base.MemoryBlock;
import x10.base.UnsafeContainer;
import x10.compilergenerated.Parameter1;
import x10.lang.GenericReferenceArray;
import x10.lang.Indexable;
import x10.lang.Runtime;
import x10.lang.dist;
import x10.lang.place;
import x10.lang.point;
import x10.lang.region;
import x10.runtime.Configuration;


/**
 * @author Christian Grothoff, Christoph von Praun
 */
public class GenericArray_c extends GenericArray implements UnsafeContainer, Cloneable {

    private final boolean safe_;
    private final MemoryBlock arr_;
    private final boolean mutable_;
    private final boolean refsToValues_;
    
    public boolean valueEquals(Indexable other) {
        boolean ret;
        if (refsToValues_)
            ret = arr_.deepEquals(((GenericArray_c)other).arr_);
        else
            ret = arr_.valueEquals(((GenericArray_c)other).arr_);
        return ret;
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
    	this( d, c, safe, true, false);
    }
    protected GenericArray_c(Distribution_c d, Operator.Pointwise c, boolean safe, boolean mutable, boolean ref_to_values) {
        super(d);
        this.mutable_ = mutable;
        this.refsToValues_ = ref_to_values;
        safe = true; // just to be GC-safe ;-)
        this.safe_ = safe;
        int count =  d.region.size();
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_PTR);
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
    public GenericArray_c( dist d, Parameter1 c) {
        this(d, c, true);
    }
    public GenericArray_c( dist d, Parameter1 c, boolean safe ) {
    	this(d, c, safe, true, false);
}
    public GenericArray_c( dist d, int c, boolean safe, boolean mutable, boolean ref_to_values) {
        this(d, (Parameter1) null, safe, mutable, ref_to_values);
    }
    public GenericArray_c( dist d, Parameter1 c, boolean safe, boolean mutable, boolean ref_to_values) {
        super(d);
        assert (safe); // just to be GC-safe ;-)
        this.mutable_ = mutable;
        this.refsToValues_ = ref_to_values;
        int count =  d.region.size();
        this.safe_ = safe;
        this.arr_ = Allocator.allocSafe(count, Parameter1.class);
        scan(this, new Assign(c));
    }
    public GenericArray_c( dist d, GenericArray.pointwiseOp f) {
        this(d, f, true);
    }
    public GenericArray_c( dist d, GenericArray.pointwiseOp f, boolean safe) {
    	this(d, f, safe, true, false);
    }
    public GenericArray_c( dist d, GenericArray.pointwiseOp f, boolean safe, boolean mutable, boolean ref_to_values) {
    	super(d);
        assert (safe); // just to be GC-safe ;-)
    	this.mutable_ = mutable;
    	this.refsToValues_ = ref_to_values;
        int count =  d.region.size();
        this.arr_ = Allocator.allocSafe(count, Parameter1.class);
    	this.safe_ = safe;
        if (f != null)
            scan(this, f);
    }
    
    private GenericArray_c( dist d, Parameter1[] a, boolean safe, boolean mutable, boolean ref_to_values) {
    	super(d);
        assert (safe); // just to be GC-safe ;-)
    	this.arr_ = Allocator.allocSafeObjectArray( a);
        this.safe_ = safe;
        this.mutable_ = mutable;
        this.refsToValues_ = ref_to_values;
    }
    /** Return a safe IntArray_c initialized with the given local 1-d (Java) int array.
     * 
     * @param a
     * @return
     */
    public static GenericArray_c GenericArray_c( Parameter1[] a, boolean safe, boolean mutable, boolean ref_to_values) {
    	dist d = Runtime.factory.getDistributionFactory().local(a.length);
    	return new GenericArray_c(d, a, safe, mutable, ref_to_values);
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

	protected GenericArray newInstance(dist d) {
		assert d instanceof Distribution_c;
		
		return new GenericArray_c((Distribution_c) d, safe_);	
	}
	
	protected GenericArray newInstance(dist d, Operator.Pointwise c) {
		assert d instanceof Distribution_c;
		
		return new GenericArray_c((Distribution_c) d, c, safe_);	
	}
	

	public GenericReferenceArray lift( GenericArray.binaryOp op, x10.lang.genericArray arg ) {
	    assert arg.distribution.equals(distribution); 
	    GenericArray arg1 = (GenericArray)arg;
	    GenericArray result = newInstance(distribution);
	    place here = x10.lang.Runtime.runtime.currentPlace();
	    try {
	        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	            point p = (point) it.next();
	            place pl = distribution.get(p);
	            x10.lang.Runtime.runtime.setCurrentPlace(pl);
	            result.set(op.apply(this.get(p), arg1.get(p)),p);
	        }
	    } finally {
	        x10.lang.Runtime.runtime.setCurrentPlace(here);
	    }  
	    return result;
	}
    
	public GenericReferenceArray lift( GenericArray.unaryOp op ) {
	    GenericArray result = newInstance(distribution);
	    place here = x10.lang.Runtime.runtime.currentPlace();
	    try {
	        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	            point p = (point) it.next();
	            place pl = distribution.get(p);
	            x10.lang.Runtime.runtime.setCurrentPlace(pl);
	            result.set(op.apply(this.get(p)),p);
	        }
	    } finally {
	        x10.lang.Runtime.runtime.setCurrentPlace(here);
	    }  
	    return result;
	}
    
	public Parameter1 reduce( GenericArray.binaryOp op, Parameter1 unit ) {
	    Parameter1 result = unit;
	    place here = x10.lang.Runtime.runtime.currentPlace();
	    try {
	        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	            point p = (point) it.next();
	            place pl = distribution.get(p);
	            x10.lang.Runtime.runtime.setCurrentPlace(pl);
	            result = op.apply(this.get(p), result);
	        }
	    } finally {
	        x10.lang.Runtime.runtime.setCurrentPlace(here);
	    }  
	    return result;
	}

	public GenericReferenceArray scan( binaryOp op, Parameter1 unit ) {
	    Parameter1 temp = unit;
	    GenericArray result = newInstance(distribution);
	    place here = x10.lang.Runtime.runtime.currentPlace();
	    try { 
	        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	            point p = (point) it.next();
	            place pl = distribution.get(p);
	            x10.lang.Runtime.runtime.setCurrentPlace(pl);
	            temp = op.apply(this.get(p), temp);
	            result.set(temp, p);
	        }
	    } finally {
	        x10.lang.Runtime.runtime.setCurrentPlace(here);
	    }  
	    return result;
    }    
	
    /* (non-Javadoc)
     * @see x10.lang.GenericArray#set(int, int[])
     */
    public Parameter1 set(Parameter1 v, point pos) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(pos));        
        return (Parameter1) arr_.set(v, (int) distribution.region.ordinal(pos));
    }
    
    public Parameter1 setOrdinal(Parameter1 v, int rawIndex) {
    	return (Parameter1) arr_.set(v,rawIndex);
    }
    
    public Parameter1 set(Parameter1 v, int d0) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        int	theIndex = Helper.ordinal(distribution,d0);   	  	 
        return (Parameter1) setOrdinal(v, d0);
    }
    
    public Parameter1 set(Parameter1 v, int d0, int d1) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));        
        int	theIndex = Helper.ordinal(distribution,d0,d1);
        return (Parameter1) setOrdinal(v, theIndex);
    }
    
    public Parameter1 set(Parameter1 v, int d0, int d1, int d2) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));
        
        int	theIndex = Helper.ordinal(distribution,d0,d1,d2);
        return (Parameter1) setOrdinal(v, theIndex);
    }
    
    public Parameter1 set(Parameter1 v, int d0, int d1, int d2, int d3) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));
        
        int	theIndex = Helper.ordinal(distribution,d0,d1,d2,d3);
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
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
    	d0 = Helper.ordinal(distribution,d0);
    	return getOrdinal(d0);
    }
    
    public Parameter1 get(int d0, int d1) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));        
        int	theIndex = Helper.ordinal(distribution,d0,d1);
    	return getOrdinal(theIndex);
    }
    
    public Parameter1 get(int d0, int d1, int d2) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));
        
        int	theIndex = Helper.ordinal(distribution,d0,d1,d2);
    	return getOrdinal(theIndex);
    }
    
    public Parameter1 get(int d0, int d1, int d2, int d3) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));        
        int	theIndex = Helper.ordinal(distribution,d0, d1, d2, d3);
    	return getOrdinal(theIndex);
    }
    public Parameter1 get(int[] pos) {
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
    	return get(p);
    }
    
    public x10.lang.GenericReferenceArray overlay(x10.lang.genericArray d) {
        dist dist = distribution.overlay(d.distribution);
        GenericArray_c ret = new GenericArray_c(dist, (Parameter1)null, safe_);
        place here = x10.lang.Runtime.runtime.currentPlace();
        try { 
            for (Iterator it = dist.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                place pl = dist.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                Parameter1 val = (d.distribution.region.contains(p)) ? d.get(p) : get(p);
                ret.set(val, p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }  
        return ret;
    }
    
    public void update(x10.lang.genericArray d) {
        assert (region.contains(d.region));
        place here = x10.lang.Runtime.runtime.currentPlace();
        try { 
            for (Iterator it = d.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                place pl = distribution.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                set(d.get(p), p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }  
    }
    
    public GenericReferenceArray union(x10.lang.genericArray d) {
        dist dist = distribution.union(d.distribution);
        GenericArray_c ret = new GenericArray_c(dist, (Parameter1)null, safe_);
        place here = x10.lang.Runtime.runtime.currentPlace();
        try { 
            for (Iterator it = dist.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                place pl = dist.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                Parameter1 val = (distribution.region.contains(p)) ? get(p) : d.get(p);
                ret.set(val, p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }  
        return ret;
    }
    
    public GenericReferenceArray restriction(dist d) {
        return restriction(d.region);
    }
    
    public GenericReferenceArray restriction(region r) {
        dist dist = distribution.restriction(r);
        GenericArray_c ret = new GenericArray_c(dist, (Parameter1)null, safe_);
        place here = x10.lang.Runtime.runtime.currentPlace();
        try { 
            for (Iterator it = dist.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                place pl = dist.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                ret.set(get(p), p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
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
