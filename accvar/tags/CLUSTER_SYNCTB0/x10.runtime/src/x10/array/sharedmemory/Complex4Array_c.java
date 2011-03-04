/*
 * Created on Oct 20, 2004
 */
package x10.array.sharedmemory;

import java.util.Iterator;


import x10.array.Helper;
import x10.array.Complex4Array;
import x10.array.Distribution_c;
import x10.array.Operator;
import x10.base.Allocator;
import x10.base.MemoryBlock;
import x10.base.UnsafeContainer;
import x10.lang.Indexable;
import x10.lang.Runtime;
import x10.lang.place;
import x10.lang.point;
import x10.lang.dist;
import x10.lang.region;
import x10.lang.Complex4ReferenceArray;
import x10.runtime.Configuration;


/**
 * @author Christian Grothoff, Christoph von Praun
 */
public class Complex4Array_c extends Complex4Array implements UnsafeContainer, Cloneable {

    private final boolean safe_;
    private final MemoryBlock arr_;
    public final boolean mutable_;
    
    public boolean valueEquals(Indexable other) {
        return arr_.valueEquals(((Complex4Array_c)other).arr_);
    }

    
    /**
     *  This constructor must not be used directly by an application programmer.
     * Arrays are constructed by the corresponding factory methods in 
     * x10.lang.Runtime.
     */
    protected Complex4Array_c(Distribution_c d, boolean safe) {
        this(d, (Operator.Pointwise) null, safe);
    }
    
    protected Complex4Array_c(Distribution_c d, Operator.Pointwise c, boolean safe) {
    	this( d, c, safe, true);
    }
    protected Complex4Array_c(Distribution_c d, Operator.Pointwise c, boolean safe, boolean mutable) {
        super(d);
        this.mutable_ = mutable;
        this.safe_ = safe;
        int count =  d.region.size() * 2;
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_FLOAT);
        } else {
            this.arr_ =Allocator.allocSafe(count, Float.TYPE);
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
    public Complex4Array_c( dist d, float c) {
        this(d, c, true);
    }
    public Complex4Array_c( dist d, float c, boolean safe ) {
    	this(d, c, safe, true);
}
    public Complex4Array_c( dist d, float c, boolean safe, boolean mutable ) {
    	super(d);
    	this.mutable_ = mutable;
    	int count =  d.region.size()*2;
        this.safe_ = safe;
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_FLOAT);
        } else {
            this.arr_ =Allocator.allocSafe(count, Float.TYPE);
        }
        scan(this, new Assign(c));
    	
    }
    public Complex4Array_c( dist d, Complex4Array.pointwiseOp f) {
        this(d, f, true);
    }
    public Complex4Array_c( dist d, Complex4Array.pointwiseOp f, boolean safe) {
    	this(d, f, safe, true);
    }
    public Complex4Array_c( dist d, Complex4Array.pointwiseOp f, boolean safe, boolean mutable) {
    	super(d);
    	this.mutable_ = mutable;
    	int count =  d.region.size()*2;
        this.safe_ = safe;
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_FLOAT);
        } else {
            this.arr_ =Allocator.allocSafe(count, Float.TYPE);
        }
        if (f != null)
            scan(this, f);
    }
    
    private Complex4Array_c( dist d, float[] a, boolean safe, boolean mutable) {
    	super(d);
        int count =  d.region.size()*2;
    	this.safe_ = safe;
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_FLOAT);
        } else {
            this.arr_ =Allocator.allocSafe(count, Float.TYPE);
        }
        this.mutable_ = mutable;
    }
    /** Return a safe IntArray_c initialized with the given local 1-d (Java) int array.
     * 
     * @param a
     * @return
     */
    public static Complex4Array_c Complex4Array_c( float[] a, boolean safe, boolean mutable ) {
    	dist d = Runtime.factory.getDistributionFactory().local(a.length);
    	return new Complex4Array_c(d, a, safe, mutable );
    }
    
    public void keepItLive() {}
    
    public long getUnsafeAddress() {
        return arr_.getUnsafeAddress();
    }
    
    public long getUnsafeDescriptor() {
        return arr_.getUnsafeDescriptor();
    }
    
    /* Overrides the superclass method - this implementation is more efficient */
   /* public void reduction(Operator.Reduction op) {// FIXME: maybe more efficient to code inline
        int count = arr_.count();
        for (int i  = 0; i < count; ++i) {
        	float real,imag;
        	real = arr_.getFloat(i);
        	imag = arr_.getFloat(i+1);
            op.apply(real,imag);
        }
    }
    */
    
    
    /* Overrides the superclass method - this implementation is more efficient */
    /*
     * protected void assign(Complex4Array rhs) {
    	assert rhs instanceof Complex4Array_c;
    	
    	Complex4Array_c rhs_t = (Complex4Array_c) rhs;
    	if (rhs.distribution.equals(distribution)) {
    	    int count = arr_.count();
    		for (int i  = 0; i < count; ++i) 
    			arr_.setComplex4(rhs_t.arr_.getComplex4(i), i);
    	} else 
    		// fall back to generic implementation
    		super.assign(rhs);
    }
*/
	protected Complex4Array newInstance(dist d) {
		assert d instanceof Distribution_c;
		
		return new Complex4Array_c((Distribution_c) d, safe_);	
	}
	
	protected Complex4Array newInstance(dist d, Operator.Pointwise c) {
		assert d instanceof Distribution_c;
		
		return new Complex4Array_c((Distribution_c) d, c, safe_);	
	}
	

	
	public Complex4ReferenceArray lift( Complex4Array.binaryOp op, x10.lang.complex4Array arg ) {
	    assert arg.distribution.equals(distribution); 
	    Complex4Array arg1 = (Complex4Array)arg;
	    Complex4Array result = newInstance(distribution);
	    place here = x10.lang.Runtime.runtime.currentPlace();
	    try {
	        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	            point p = (point) it.next();
	            place pl = distribution.get(p);
	            x10.lang.Runtime.runtime.setCurrentPlace(pl);
	            throw new RuntimeException("unimplemented");
	            //result.set(op.apply(this.get(p), arg1.get(p)),p);
	        } 
	    } finally {
	        x10.lang.Runtime.runtime.setCurrentPlace(here);
	    }   
	    return result;
	}
	
	public Complex4ReferenceArray lift( Complex4Array.unaryOp op ) {
	    Complex4Array result = newInstance(distribution);
	    place here = x10.lang.Runtime.runtime.currentPlace();
	    try {
	        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	            point p = (point) it.next();
	            place pl = distribution.get(p);
	            x10.lang.Runtime.runtime.setCurrentPlace(pl);
	            result.setReal(op.apply(this.getReal(p)),p);
	            result.setImag(op.apply(this.getImag(p)),p);
	        }
	    } finally {
	        x10.lang.Runtime.runtime.setCurrentPlace(here);
	    }   
	    return result;
	}
	
	public float reduce( Complex4Array.binaryOp op, float unit ) {
	    float result = unit;
	    place here = x10.lang.Runtime.runtime.currentPlace();
	    try {
	        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	            point p = (point) it.next();
	            place pl = distribution.get(p);
	            x10.lang.Runtime.runtime.setCurrentPlace(pl);
	            throw new RuntimeException("unimplemented");
	            // result = op.apply(this.get(p), result);
	        }
	    } finally {
	        x10.lang.Runtime.runtime.setCurrentPlace(here);
	    }   
	    return result;
	}
    	
	public Complex4ReferenceArray scan( binaryOp op, float unit ) {
	    float temp = unit;
	    Complex4Array result = newInstance(distribution);
	    place here = x10.lang.Runtime.runtime.currentPlace();
	    try {
	        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	            point p = (point) it.next();
	            place pl = distribution.get(p);
	            x10.lang.Runtime.runtime.setCurrentPlace(pl);
	            if(true)throw new RuntimeException("unimplemented");
	            /*  temp = op.apply(this.getReal(p), temp);
	             result.setReal(temp, p);
	             */
	        }
	    } finally {
	        x10.lang.Runtime.runtime.setCurrentPlace(here);
	    }  
	    return result;
	}
    
	
    /* (non-Javadoc)
     * @see x10.lang.Complex4Array#set(int, int[])
     */
    public float setReal(float v, point pos) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(pos));
        
        return arr_.setFloat(v,2*(int) distribution.region.ordinal(pos));
    }
    
    public float setImag(float v, point pos) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(pos));
        
        return arr_.setFloat(v,1+ 2*(int) distribution.region.ordinal(pos));
    }
    
    public float setOrdinalReal(float v, int rawIndex) {
    	
    	return arr_.setFloat(v,rawIndex*2);
    }
    public float setOrdinalImag(float v, int rawIndex) {
    	
    	return arr_.setFloat(v,rawIndex*2 + 1);
    }
    
    public float setReal(float v, int d0) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(distribution,d0,true);
    	return arr_.setFloat(v,d0*2);
    }
     
    public float setReal(float v, int d0, int d1) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,true);
    	return arr_.setFloat(v,theIndex*2);
    }
    
    public float setReal(float v, int d0, int d1, int d2) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,d2,true);
    	return arr_.setFloat(v,theIndex*2);
    }
    
    public float setReal(float v, int d0, int d1, int d2, int d3) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,d2,d3,true);
    	return arr_.setFloat(v,theIndex*2);   	
    }

    public float setImag(float v, int d0) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(distribution,d0,true);
    	return arr_.setFloat(v,d0*2 + 1);
    }
     
    public float setImag(float v, int d0, int d1) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,true);
    	return arr_.setFloat(v,theIndex*2 + 1);
    }
    
    public float setImag(float v, int d0, int d1, int d2) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,d2,true);
    	return arr_.setFloat(v,theIndex*2 + 1);
    }
    
    public float setImag(float v, int d0, int d1, int d2, int d3) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,d2,d3,true);
    	return arr_.setFloat(v,theIndex*2 + 1);   	
    }

    /* (non-Javadoc)
     * @see x10.lang.FloatArray#get(int[])
     */
    public float getReal(point pos) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(pos));        
        return arr_.getFloat(2*(int) distribution.region.ordinal(pos));
    }
    public float getImag(point pos) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(pos));        
        return arr_.getFloat(1 + 2*(int) distribution.region.ordinal(pos));
    }
     public float getOrdinalReal( int rawIndex) {
    	
    	return arr_.getFloat(rawIndex*2);
    }
    public float getOrdinalImag( int rawIndex) {
    	
    	return arr_.getFloat(rawIndex*2 + 1);
    }
    
    public float getReal( int d0) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(distribution,d0,true);
    	return arr_.getFloat(d0*2);
    }
     
    public float getReal( int d0, int d1) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,true);
    	return arr_.getFloat(theIndex*2);
    }
    
    public float getReal( int d0, int d1, int d2) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,d2,true);
    	return arr_.getFloat(theIndex*2);
    }
    
    public float getReal( int d0, int d1, int d2, int d3) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,d2,d3,true);
    	return arr_.getFloat(theIndex*2);   	
    }

    public float getImag( int d0) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(distribution,d0,true);
    	return arr_.getFloat(d0*2 + 1);
    }
     
    public float getImag( int d0, int d1) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,true);
    	return arr_.getFloat(theIndex*2 + 1);
    }
    
    public float getImag( int d0, int d1, int d2) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,d2,true);
    	return arr_.getFloat(theIndex*2 + 1);
    }
    
    public float getImag( int d0, int d1, int d2, int d3) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,d2,d3,true);
    	return arr_.getFloat(theIndex*2 + 1);   	
    }
    
    public float getReal(int[] pos) {
        final point p = Runtime.factory.getPointFactory().point( pos);
    	return getReal(p);
    }
    
    public float getImag(int[] pos) {
        final point p = Runtime.factory.getPointFactory().point( pos);
    	return getImag(p);
    }
    
    public x10.lang.Complex4ReferenceArray overlay(x10.lang.complex4Array d) {
        dist dist = distribution.overlay(d.distribution);
        Complex4Array_c ret = new Complex4Array_c(dist, 0, safe_);
        place here = x10.lang.Runtime.runtime.currentPlace();
        try {
            for (Iterator it = dist.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                place pl = dist.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                float real,imag;
                if (d.distribution.region.contains(p)){
                    real= d.getReal(p);
                    imag= d.getImag(p);
                } else {
                    real = getReal(p);
                    imag = getImag(p);
                }
                ret.setReal(real, p);
                ret.setImag(real, p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }   
        return ret;
    }
    
    public void update(x10.lang.complex4Array d) {
        assert (region.contains(d.region));
        place here = x10.lang.Runtime.runtime.currentPlace();
        try {
            for (Iterator it = d.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                place pl = distribution.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                setReal(d.getReal(p), p);
                setImag(d.getImag(p), p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }   
    }
    
    public Complex4ReferenceArray union(x10.lang.complex4Array d) {
        dist dist = distribution.union(d.distribution);
        Complex4Array_c ret = new Complex4Array_c(dist, 0, safe_);
        place here = x10.lang.Runtime.runtime.currentPlace();
        try {
            for (Iterator it = dist.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                place pl = dist.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                float real,imag;
                if (distribution.region.contains(p)){
                    real = getReal(p);
                    imag = getImag(p);
                }else {
                    real = d.getReal(p);
                    imag = d.getImag(p);
                }
                ret.setReal(real, p);
                ret.setImag(imag,p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }  
        return ret;
    }
    
    public Complex4ReferenceArray restriction(dist d) {
        return restriction(d.region);
    }
    
    public Complex4ReferenceArray restriction(region r) {
        dist dist = distribution.restriction(r);
        Complex4Array_c ret = new Complex4Array_c(dist, 0, safe_);
        place here = x10.lang.Runtime.runtime.currentPlace();
        try {
            for (Iterator it = dist.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                place pl = dist.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                ret.setReal(getReal(p), p);
                ret.setImag(getImag(p), p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }  
        return ret;
    }
    
    public x10.lang.complex4Array toValueArray() {
    	if (! mutable_) return this;
    	throw new Error("TODO: <T>ReferenceArray --> <T>ValueArray");   
    }
    
    public boolean isValue() {
        return ! this.mutable_;
    }    
}
