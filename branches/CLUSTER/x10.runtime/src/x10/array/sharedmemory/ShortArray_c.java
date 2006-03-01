/*
 * Created on Oct 20, 2004
 */
package x10.array.sharedmemory;

import x10.array.Distribution_c;
import java.util.Iterator;
import x10.array.ShortArray;
import x10.array.Helper;
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
import x10.lang.ShortReferenceArray;
import x10.runtime.Configuration;


/**
 * @author Christian Grothoff, Christoph von Praun
 */
public class ShortArray_c extends ShortArray implements UnsafeContainer, Cloneable {

    private final boolean safe_;
    private final MemoryBlock arr_;
    public final boolean mutable_;
    
    public boolean valueEquals(Indexable other) {
        return arr_.valueEquals(((ShortArray_c)other).arr_);
    }
    
    /**
     *  This constructor must not be used directly by an application programmer.
     * Arrays are constructed by the corresponding factory methods in 
     * x10.lang.Runtime.
     */
    protected ShortArray_c(Distribution_c d, boolean safe) {
        this(d, (Operator.Pointwise) null, safe);
    }
    
    protected ShortArray_c(Distribution_c d, Operator.Pointwise c, boolean safe) {
    	this( d, c, safe, true);
    }
    
    protected ShortArray_c(Distribution_c d, Operator.Pointwise c, boolean safe, boolean mutable) {
        super(d);
        this.mutable_ = mutable;
        this.safe_ = safe;
        int count =  d.region.size();
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_SHORT);
        } else {
            this.arr_ =Allocator.allocSafe(count, Short.TYPE);
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
    public ShortArray_c( dist d, short c) {
        this(d, c, true);
    }
    
    public ShortArray_c( dist d, short c, boolean safe ) {
    	this(d, c, safe, true);
    }
    
    public ShortArray_c( dist d, short c, boolean safe, boolean mutable ) {
    	super(d);
    	this.mutable_ = mutable;
    	int count =  d.region.size();
        this.safe_ = safe;
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_SHORT);
        } else {
            this.arr_ = Allocator.allocSafe(count, Short.TYPE);
        }
    	scan(this, new Assign(c));
    	
    }
    
    public ShortArray_c( dist d, ShortArray.pointwiseOp f) {
        this(d, f, true);
    }
    
    public ShortArray_c( dist d, ShortArray.pointwiseOp f, boolean safe) {
    	this(d, f, safe, true);
    }
    
    public ShortArray_c( dist d, ShortArray.pointwiseOp f, boolean safe, boolean mutable) {
    	super(d);
    	this.mutable_ = mutable;
    	int count =  d.region.size();
        this.safe_ = safe;
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_SHORT);
        } else {
            this.arr_ = Allocator.allocSafe(count, Short.TYPE);
        }
        if (f != null)
            scan(this, f);
    }
    
    private ShortArray_c( dist d, short[] a, boolean safe, boolean mutable) {
    	super(d);
        int count =  d.region.size();
    	this.safe_ = safe;
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_SHORT);
        } else {
            this.arr_ = Allocator.allocSafe(count, Short.TYPE);
        }
        this.mutable_ = mutable;
    }
    
    /** Return a safe IntArray_c initialized with the given local 1-d (Java) int array.
     * 
     * @param a
     * @return
     */
    public static ShortArray_c ShortArray_c( short[] a, boolean safe, boolean mutable ) {
    	dist d = Runtime.factory.getDistributionFactory().local(a.length);
    	return new ShortArray_c(d, a, safe, mutable);
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
            op.apply(arr_.getShort(i));
    }
    
    /* Overrides the superclass method - this implementation is more efficient */
    protected void assign(ShortArray rhs) {
    	assert rhs instanceof ShortArray_c;
    	
    	ShortArray_c rhs_t = (ShortArray_c) rhs;
    	if (rhs.distribution.equals(distribution)) {
    	    int count = arr_.count();
    		for (int i  = 0; i < count; ++i) 
    			arr_.setShort(rhs_t.arr_.getShort(i), i);
    	} else 
    		// fall back to generic implementation
    		super.assign(rhs);
    }

	protected ShortArray newInstance(dist d) {
		assert d instanceof Distribution_c;
		
		return new ShortArray_c((Distribution_c) d, safe_);	
	}
	
	protected ShortArray newInstance(dist d, Operator.Pointwise c) {
		assert d instanceof Distribution_c;
		
		return new ShortArray_c((Distribution_c) d, c, safe_);	
	}
	
	public ShortReferenceArray lift( ShortArray.binaryOp op, x10.lang.shortArray arg ) {
	    assert arg.distribution.equals(distribution); 
	    ShortArray arg1 = (ShortArray)arg;
	    ShortArray result = newInstance(distribution);
	    place here = x10.lang.Runtime.runtime.currentPlace();
	    try { 
	        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	            point p = (point) it.next();
	            place pl = distribution.get(p);
	            x10.lang.Runtime.runtime.setCurrentPlace(pl);
	            result.set((short) op.apply(this.get(p), arg1.get(p)),p);
	        }
	    } finally {
	        x10.lang.Runtime.runtime.setCurrentPlace(here);
	    }  
	    return result;
	}
    
	public ShortReferenceArray lift( ShortArray.unaryOp op ) {
	    ShortArray result = newInstance(distribution);
	    place here = x10.lang.Runtime.runtime.currentPlace();
	    try { 
	        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	            point p = (point) it.next();
	            place pl = distribution.get(p);
	            x10.lang.Runtime.runtime.setCurrentPlace(pl);
	            result.set((short) op.apply(this.get(p)),p);
	        }
	    } finally {
	        x10.lang.Runtime.runtime.setCurrentPlace(here);
	    }  
	    return result;
	}
    
	public int reduce( ShortArray.binaryOp op, short unit ) {
	    int result = unit;
	    place here = x10.lang.Runtime.runtime.currentPlace();
	    try { 
	        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	            point p = (point) it.next();
	            place pl = distribution.get(p);
	            x10.lang.Runtime.runtime.setCurrentPlace(pl);
	            result = op.apply(this.get(p), (short) result);
	        }
	    } finally {
	        x10.lang.Runtime.runtime.setCurrentPlace(here);
	    }  
	    return result;
	}

	public ShortReferenceArray scan( binaryOp op, short unit ) {
	    short temp = unit;
	    ShortArray result = newInstance(distribution);
	    place here = x10.lang.Runtime.runtime.currentPlace();
	    try { 
	        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	            point p = (point) it.next();
	            place pl = distribution.get(p);
	            x10.lang.Runtime.runtime.setCurrentPlace(pl);
	            temp = (short) op.apply(this.get(p), temp);
	            result.set(temp, p);
	        }
	    } finally {
	        x10.lang.Runtime.runtime.setCurrentPlace(here);
	    }  
	    return result;
	}
    
	
    /* (non-Javadoc)
     * @see x10.lang.ShortArray#set(int, int[])
     */
	public short set(short v, point pos) {return set(v,pos,true,true);}
	    
    public short set(short v, point pos,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(pos));
        
        return arr_.setShort(v, (int) distribution.region.ordinal(pos));
    }
    public short setOrdinal(short v, int rawIndex) {
    	return arr_.setShort(v,rawIndex);
    }
    
    public short set(short v, int d0) {return set(v,d0,true,true);}
	
    public short set(short v, int d0,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(distribution,d0,chkAOB);
    	return arr_.setShort(v,d0);
    }
     
    public short set(short v, int d0,int d1) {return set(v,d0,d1,true,true);}
	
    public short set(short v, int d0, int d1,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,chkAOB);
    	return arr_.setShort(v,theIndex);
    }
    
    public short set(short v, int d0,int d1,int d2) {return set(v,d0,d1,d2,true,true);}
    public short set(short v, int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,d2,chkAOB);
    	return arr_.setShort(v,theIndex);
    }
    
    public short set(short v, int d0,int d1,int d2,int d3) {return set(v,d0,d1,d2,d3,true,true);}
    
    public short set(short v, int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,d2,d3,chkAOB);
    	return arr_.setShort(v,theIndex);  	
    }
    
    /* (non-Javadoc)
     * @see x10.lang.ShortArray#get(int[])
     */
    public short get(point pos) {return get(pos,true,true);}
    public short get(point pos,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(pos));
        
        return arr_.getShort((int) distribution.region.ordinal(pos));
    }
    
    public short getOrdinal(int rawIndex) {    	
    	return arr_.getShort(rawIndex);
    }
    
    public short get(int d0) {return get(d0,true,true);}
    
    public short get(int d0,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(distribution,d0,chkAOB);
        return arr_.getShort(d0);
    }
    
    public short get(int d0,int d1) {return get(d0,d1,true,true);}
    public short get(int d0, int d1,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));        
        int theIndex = Helper.ordinal(distribution,d0, d1,chkAOB);
        return arr_.getShort(theIndex);
    }
    
    public short get(int d0,int d1,int d2) {return get(d0,d1,d2,true,true);}
    public short get(int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));        
        int theIndex = Helper.ordinal(distribution, d0, d1, d2,chkAOB);
        return arr_.getShort(theIndex);  	
    } 
    
    public short get(int d0,int d1,int d2,int d3) {return get(d0,d1,d2,d3,true,true);}
    public short get(int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2,d3));        
        int theIndex = Helper.ordinal(distribution,d0,d1,d2,d3,chkAOB); 
        return arr_.getShort(theIndex);
        
    }
    
    public short get(int[] pos) {return get(pos,true,true);}
    public short get(int[] pos,boolean chkPl,boolean chkAOB) {
        final point p = Runtime.factory.getPointFactory().point( pos);
    	return get(p);
    }
    
    public x10.lang.ShortReferenceArray overlay(x10.lang.shortArray d) {
        dist dist = distribution.overlay(d.distribution);
        ShortArray_c ret = new ShortArray_c(dist, (short) 0, safe_);
        place here = x10.lang.Runtime.runtime.currentPlace();
        try { 
            for (Iterator it = dist.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                place pl = dist.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                short val = (d.distribution.region.contains(p)) ? d.get(p) : get(p);
                ret.set(val, p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }  
        return ret;
    }
    
    public void update(x10.lang.shortArray d) {
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
    
    public ShortReferenceArray union(x10.lang.shortArray d) {
        dist dist = distribution.union(d.distribution);
        ShortArray_c ret = new ShortArray_c(dist, (short) 0, safe_);
        place here = x10.lang.Runtime.runtime.currentPlace();
        try { 
            for (Iterator it = dist.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                place pl = dist.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                short val = (distribution.region.contains(p)) ? get(p) : d.get(p);
                ret.set(val, p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }  
        return ret;
    }
    
    public ShortReferenceArray restriction(dist d) {
        return restriction(d.region);
    }
    
    public ShortReferenceArray restriction(region r) {
        dist dist = distribution.restriction(r);
        ShortArray_c ret = new ShortArray_c(dist, (short) 0, safe_);
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
    
    public x10.lang.shortArray toValueArray() {
    	if (! mutable_) return this;
    	throw new Error("TODO: <T>ReferenceArray --> <T>ValueArray");   
    }
    
    public boolean isValue() {
        return ! this.mutable_;
    }

    
}
