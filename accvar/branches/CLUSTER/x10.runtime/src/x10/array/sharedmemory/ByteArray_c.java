/*
 * Created on Oct 20, 2004
 */
package x10.array.sharedmemory;

import java.util.Iterator;

import x10.array.ByteArray;
import x10.array.Distribution_c;
import x10.array.Helper;
import x10.array.Operator;
import x10.base.Allocator;
import x10.base.MemoryBlock;
import x10.base.UnsafeContainer;
import x10.lang.ByteReferenceArray;
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
public class ByteArray_c extends ByteArray implements UnsafeContainer, Cloneable {

    protected final boolean safe_;
    protected /*private*/ final MemoryBlock arr_;
    public final boolean mutable_;
    
    public boolean valueEquals(Indexable other) {
        return arr_.valueEquals(((ByteArray_c)other).arr_);
    }

    
    /**
     *  This constructor must not be used directly by an application programmer.
     * Arrays are constructed by the corresponding factory methods in 
     * x10.lang.Runtime.
     */
    protected ByteArray_c(Distribution_c d, boolean safe) {
        this(d, (Operator.Pointwise) null, safe);
    }
    
    protected ByteArray_c(Distribution_c d, Operator.Pointwise c, boolean safe) {
    	this( d, c, safe, true);
    }
    protected ByteArray_c(Distribution_c d, Operator.Pointwise c, boolean safe, boolean mutable) {
        super(d);
        this.mutable_ = mutable;
        this.safe_ = safe;
        int count =  d.region.size();
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_BYTE);
        } else {
            this.arr_ =Allocator.allocSafe(count, Byte.TYPE);
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
    public ByteArray_c( dist d, byte c) {
        this(d, c, true);
    }
    public ByteArray_c( dist d, byte c, boolean safe ) {
    	this(d, c, safe, true);
}
    public ByteArray_c( dist d, byte c, boolean safe, boolean mutable ) {
    	super(d);
    	this.mutable_ = mutable;
    	int count =  d.region.size();
        this.safe_ = safe;
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_BYTE);
        } else {
            this.arr_ =Allocator.allocSafe(count, Byte.TYPE);
        }
        scan(this, new Assign(c));
    	
    }
    public ByteArray_c( dist d, ByteArray.pointwiseOp f) {
        this(d, f, true);
    }
    public ByteArray_c( dist d, ByteArray.pointwiseOp f, boolean safe) {
    	this(d, f, safe, true);
    }
    public ByteArray_c( dist d, ByteArray.pointwiseOp f, boolean safe, boolean mutable) {
    	super(d);
    	this.mutable_ = mutable;
    	int count =  d.region.size();
        this.safe_ = safe;
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_BYTE);
        } else {
            this.arr_ =Allocator.allocSafe(count, Byte.TYPE);
        }
        if (f != null)
            scan(this, f);
    }
    
    protected ByteArray_c( dist d, byte[] a, boolean safe, boolean mutable) {
    	super(d);
        int count =  d.region.size();
    	this.safe_ = safe;
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_BYTE);
        } else {
            this.arr_ =Allocator.allocSafe(count, Byte.TYPE);
        }
        this.mutable_ = mutable;
    }
    
    /** Return a safe IntArray_c initialized with the given local 1-d (Java) int array.
     * 
     * @param a
     * @return
     */
    public static ByteArray_c ByteArray_c( byte[] a, boolean safe, boolean mutable ) {
    	dist d = Runtime.factory.getDistributionFactory().local(a.length);
    	return new ByteArray_c(d, a, safe, mutable );
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
            op.apply(arr_.getByte(i));
    }
    
    /* Overrides the superclass method - this implementation is more efficient */
    protected void assign(ByteArray rhs) {
    	assert rhs instanceof ByteArray_c;
    	
    	ByteArray_c rhs_t = (ByteArray_c) rhs;
    	if (rhs.distribution.equals(distribution)) {
    	    int count = arr_.count();
    		for (int i  = 0; i < count; ++i) 
    			arr_.setByte(rhs_t.arr_.getByte(i), i);
    	} else 
    		// fall back to generic implementation
    		super.assign(rhs);
    }

	protected ByteArray newInstance(dist d) {
		assert d instanceof Distribution_c;
		
		return new ByteArray_c((Distribution_c) d, safe_);	
	}
	
	protected ByteArray newInstance(dist d, Operator.Pointwise c) {
		assert d instanceof Distribution_c;
		
		return new ByteArray_c((Distribution_c) d, c, safe_);	
	}	

	public ByteReferenceArray lift( ByteArray.binaryOp op, x10.lang.byteArray arg ) {
	    assert arg.distribution.equals(distribution); 
	    ByteArray arg1 = (ByteArray)arg;
	    ByteArray result = newInstance(distribution);
	    place here = x10.lang.Runtime.runtime.currentPlace();
	    try {
	        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	            point p = (point) it.next();
	            place pl = distribution.get(p);
	            x10.lang.Runtime.runtime.setCurrentPlace(pl);
	            result.set((byte) op.apply(this.get(p), arg1.get(p)),p);
	        }
	    } finally {
	        x10.lang.Runtime.runtime.setCurrentPlace(here);
	    }   
	    return result;
	}
    
	public ByteReferenceArray lift( ByteArray.unaryOp op ) {
	    ByteArray result = newInstance(distribution);
	    place here = x10.lang.Runtime.runtime.currentPlace();
	    try {
	        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	            point p = (point) it.next();
	            place pl = distribution.get(p);
	            x10.lang.Runtime.runtime.setCurrentPlace(pl);
	            result.set((byte) op.apply(this.get(p)),p);
	        }
	    } finally {
	        x10.lang.Runtime.runtime.setCurrentPlace(here);
	    }   
	    return result;
	}
    
	public int reduce( ByteArray.binaryOp op, byte unit ) {
	    byte result = unit;
	    place here = x10.lang.Runtime.runtime.currentPlace();
	    try {
	        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	            point p = (point) it.next();
	            place pl = distribution.get(p);
	            x10.lang.Runtime.runtime.setCurrentPlace(pl);
	            result = (byte) op.apply(this.get(p), result);
	        }
	    } finally {
	        x10.lang.Runtime.runtime.setCurrentPlace(here);
	    }   
	    return result;
	}
	
	public ByteReferenceArray scan( binaryOp op, byte unit ) {
	    byte temp = unit;
	    ByteArray result = newInstance(distribution);
	    place here = x10.lang.Runtime.runtime.currentPlace();
	    try {
	        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	            point p = (point) it.next();
	            place pl = distribution.get(p);
	            x10.lang.Runtime.runtime.setCurrentPlace(pl);
	            temp = (byte) op.apply(this.get(p), temp);
	            result.set(temp, p);
	        }
	    } finally {
	        x10.lang.Runtime.runtime.setCurrentPlace(here);
	    }   
	    return result;
	}
    	
    /* (non-Javadoc)
     * @see x10.lang.ByteArray#set(int, int[])
     */
	public byte set(byte v, point pos) {return set(v,pos,true,true);}
    public byte set(byte v, point pos,boolean chkPl,boolean chkAOB) {
        return arr_.setByte(v, (int) localDist.region.ordinal(pos));
    }
    
    public byte setOrdinal(byte v, int rawIndex) {
    	return arr_.setByte(v,rawIndex);
    }
    
	public byte set(byte v, int d0) {return set(v,d0,true,true);}
    public byte set(byte v, int d0,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(localDist,d0,chkAOB);
    	return arr_.setByte(v,d0);
    }
     
    public byte set(byte v, int d0,int d1) {return set(v,d0,d1,true,true);}
    public byte set(byte v, int d0, int d1,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));        
    	int	theIndex = Helper.ordinal(localDist,d0,d1,chkAOB);
    	return arr_.setByte(v,theIndex);
    }
    
    public byte set(byte v, int d0,int d1,int d2) {return set(v,d0,d1,d2,true,true);}
    public byte set(byte v, int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));        
    	int	theIndex = Helper.ordinal(localDist,d0,d1,d2,chkAOB);
    	return arr_.setByte(v,theIndex);
    }
    
    public byte set(byte v, int d0,int d1,int d2,int d3) {return set(v,d0,d1,d2,d3,true,true);}
    public byte set(byte v, int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));        
        int	theIndex = Helper.ordinal(localDist,d0,d1,d2,d3,chkAOB);
    	return arr_.setByte(v,theIndex);    	
    }


    /* (non-Javadoc)
     * @see x10.lang.ByteArray#get(int[])
     */
    public byte get(point pos) {return get(pos,true,true);}
    
    public byte get(point pos,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(pos));        
        return arr_.getByte((int) localDist.region.ordinal(pos));
    }
    
    public byte getOrdinal(int rawIndex) {    	
    	return arr_.getByte(rawIndex);
    }
    
    public byte get(int d0) {return get(d0,true,true);}
    public byte get(int d0,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(localDist,d0,chkAOB);
    	return arr_.getByte(d0);
    }
    
    public byte get(int d0,int d1) {return get(d0,d1,true,true);}
    public byte get(int d0, int d1,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));        
        int	theIndex = Helper.ordinal(localDist,d0,d1,chkAOB);
    	return arr_.getByte(theIndex);
    }
    
    public byte get(int d0,int d1,int d2) {return get(d0,d1,d2,true,true);}
    public byte get(int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));        
        int	theIndex = Helper.ordinal(localDist,d0,d1,d2,chkAOB);
    	return arr_.getByte(theIndex);  	
    } 
    
    public byte get(int d0,int d1,int d2,int d3) {return get(d0,d1,d2,d3,true,true);}
    public byte get(int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));        
        int	theIndex = Helper.ordinal(localDist,d0,d1,d2,d3,chkAOB);
    	return arr_.getByte(theIndex);
    	
    }
    
    public byte get(int[] pos) {return get(pos,true,true);}
    
    public byte get(int[] pos,boolean chkPl,boolean chkAOB) {        
        final point p = Runtime.factory.getPointFactory().point( pos);
    	return get(p);
    }
    
    public x10.lang.ByteReferenceArray overlay(x10.lang.byteArray d) {
        dist dist = distribution.overlay(d.distribution);
        ByteArray_c ret = new ByteArray_c(dist, (byte) 0, safe_);
        place here = x10.lang.Runtime.runtime.currentPlace();
        try {
            for (Iterator it = dist.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                place pl = dist.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                byte val = (d.distribution.region.contains(p)) ? d.get(p) : get(p);
                ret.set(val, p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }   
        return ret;
    }
    
    public void update(x10.lang.byteArray d) {
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
    
    public ByteReferenceArray union(x10.lang.byteArray d) {
        dist dist = distribution.union(d.distribution);
        ByteArray_c ret = new ByteArray_c(dist, (byte) 0, safe_);
        place here = x10.lang.Runtime.runtime.currentPlace();
        try {
            for (Iterator it = dist.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                place pl = dist.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                byte val = (distribution.region.contains(p)) ? get(p) : d.get(p);
                ret.set(val, p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }   
        return ret;
    }
    
    public ByteReferenceArray restriction(dist d) {
        return restriction(d.region);
    }
    
    public ByteReferenceArray restriction(region r) {
        dist dist = distribution.restriction(r);
        ByteArray_c ret = new ByteArray_c(dist, (byte) 0, safe_);
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
    
    public x10.lang.byteArray toValueArray() {
    	if (! mutable_) return this;
    	throw new Error("TODO: <T>ReferenceArray --> <T>ValueArray");   
    }
    
    public boolean isValue() {
        return ! this.mutable_;
    }

    
}
