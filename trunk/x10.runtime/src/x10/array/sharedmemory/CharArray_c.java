/*
 * Created on Oct 20, 2004
 */
package x10.array.sharedmemory;

import java.util.Iterator;


import x10.array.Distribution_c;
import x10.array.Helper;
import x10.array.CharArray;
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
import x10.lang.CharReferenceArray;
import x10.runtime.Configuration;


/**
 * @author Christian Grothoff, Christoph von Praun
 */
public class CharArray_c extends CharArray implements UnsafeContainer, Cloneable {

    private final boolean safe_;
    private final MemoryBlock arr_;
    public final boolean mutable_;
    
    public boolean valueEquals(Indexable other) {
        return arr_.valueEquals(((CharArray_c)other).arr_);
    }
    
    /**
     *  This constructor must not be used directly by an application programmer.
     * Arrays are constructed by the corresponding factory methods in 
     * x10.lang.Runtime.
     */
    protected CharArray_c(Distribution_c d, boolean safe) {
        this(d, (Operator.Pointwise) null, safe);
    }
    
    protected CharArray_c(Distribution_c d, Operator.Pointwise c, boolean safe) {
    	this( d, c, safe, true);
    }
    protected CharArray_c(Distribution_c d, Operator.Pointwise c, boolean safe, boolean mutable) {
        super(d);
        this.mutable_ = mutable;
        this.safe_ = safe;
        int count =  d.region.size();
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_CHAR);
        } else {
            this.arr_ =Allocator.allocSafe(count, Character.TYPE);
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
    public CharArray_c( dist d, char c) {
        this(d, c, true);
    }
    public CharArray_c( dist d, char c, boolean safe ) {
    	this(d, c, safe, true);
}
    public CharArray_c( dist d, char c, boolean safe, boolean mutable ) {
    	super(d);
    	this.mutable_ = mutable;
    	int count =  d.region.size();
        this.safe_ = safe;
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_CHAR);
        } else {
            this.arr_ =Allocator.allocSafe(count, Character.TYPE);
        }
        scan(this, new Assign(c));
    	
    }
    public CharArray_c( dist d, CharArray.pointwiseOp f) {
        this(d, f, true);
    }
    public CharArray_c( dist d, CharArray.pointwiseOp f, boolean safe) {
    	this(d, f, safe, true);
    }
    public CharArray_c( dist d, CharArray.pointwiseOp f, boolean safe, boolean mutable) {
    	super(d);
    	this.mutable_ = mutable;
    	int count =  d.region.size();
        this.safe_ = safe;
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_CHAR);
        } else {
            this.arr_ =Allocator.allocSafe(count, Character.TYPE);
        }
        if (f != null)
            scan(this, f);
    }
    
    private CharArray_c( dist d, char[] a, boolean safe, boolean mutable) {
    	super(d);
        int count =  d.region.size();
    	this.safe_ = safe;
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_CHAR);
        } else {
            this.arr_ =Allocator.allocSafe(count, Character.TYPE);
        }
        this.mutable_ = mutable;
    }
    /** Return a safe IntArray_c initialized with the given local 1-d (Java) int array.
     * 
     * @param a
     * @return
     */
    public static CharArray_c CharArray_c( char[] a, boolean safe, boolean mutable ) {
    	dist d = Runtime.factory.getDistributionFactory().local(a.length);
    	return new CharArray_c(d, a, safe, mutable );
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
            op.apply(arr_.getChar(i));
    }
    
    /* Overrides the superclass method - this implementation is more efficient */
    protected void assign(CharArray rhs) {
    	assert rhs instanceof CharArray_c;
    	
    	CharArray_c rhs_t = (CharArray_c) rhs;
    	if (rhs.distribution.equals(distribution)) {
    	    int count = arr_.count();
    		for (int i  = 0; i < count; ++i) 
    			arr_.setChar(rhs_t.arr_.getChar(i), i);
    	} else 
    		// fall back to generic implementation
    		super.assign(rhs);
    }

	protected CharArray newInstance(dist d) {
		assert d instanceof Distribution_c;
		
		return new CharArray_c((Distribution_c) d, safe_);	
	}
	
	protected CharArray newInstance(dist d, Operator.Pointwise c) {
		assert d instanceof Distribution_c;
		
		return new CharArray_c((Distribution_c) d, c, safe_);	
	}
	

	public CharReferenceArray lift (CharArray.binaryOp op, x10.lang.charArray arg ) {
	    assert arg.distribution.equals(distribution); 
	    CharArray arg1 = (CharArray)arg;
	    CharArray result = newInstance(distribution);
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
    
	public CharReferenceArray lift (CharArray.unaryOp op ) {
	    CharArray result = newInstance(distribution);
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
    
	public char reduce (CharArray.binaryOp op, char unit ) {
	    char result = unit;
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

	public CharReferenceArray scan( binaryOp op, char unit ) {
	    char temp = unit;
	    CharArray result = newInstance(distribution);
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
     * @see x10.lang.CharArray#set(int, int[])
     */
	 public char set(char v, point pos) {return set(v,pos, true,true);}
    public char set(char v, point pos,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(pos));
        
        return arr_.setChar(v, (int) distribution.region.ordinal(pos));
    }
    
    public char setOrdinal(char v, int rawIndex) {
    	return arr_.setChar(v,rawIndex);
    }
    
    
    public char set(char v, int d0) {return set(v,d0,true,true);}
    public char set(char v, int d0,boolean chkPl,boolean chkAOB) {
    	d0 = Helper.ordinal(distribution,d0,chkAOB);
    	return arr_.setChar(v,d0);
    }
     
    public char set(char v, int d0,int d1) {return set(v,d0,d1,true,true);}
    public char set(char v, int d0, int d1,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,chkAOB);
    	return arr_.setChar(v,theIndex);
    }
    
    public char set(char v, int d0,int d1,int d2) {return set(v,d0,d1,d2,true,true);}
    public char set(char v, int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,d2,chkAOB);
    	return arr_.setChar(v,theIndex);
    }
    
    public char set(char v, int d0,int d1,int d2,int d3) {return set(v,d0,d1,d2,d3,true,true);}
    public char set(char v, int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,d2,d3,chkAOB);
    	return arr_.setChar(v,theIndex); 	
    }

    /* (non-Javadoc)
     * @see x10.lang.CharArray#get(int[])
     */
    public char get(point pos) {return get(pos,true,true);}
    public char get(point pos,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(pos));
        
        return arr_.getChar((int) distribution.region.ordinal(pos));
    }
    
    public char getOrdinal(int rawIndex) {    	
    	return arr_.getChar(rawIndex);
    }
    
    
    public char get(int d0) {return get(d0,true,true);}
    public char get(int d0,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(distribution,d0,chkAOB);
    	return arr_.getChar(d0);
    }
    
    public char get(int d0,int d1) {return get(d0,d1,true,true);}
    public char get(int d0, int d1,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,chkAOB);
    	return arr_.getChar(theIndex);
    }
    
    public char get(int d0,int d1,int d2) {return get(d0,d1,d2,true,true);}
    public char get(int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,d2,chkAOB);
    	return arr_.getChar(theIndex);  	
    } 
    
    public char get(int d0,int d1,int d2,int d3) {return get(d0,d1,d2,d3,true,true);}
    public char get(int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,d2,d3,chkAOB);
    	return arr_.getChar(theIndex);
    	
    }
    
    public char get(int[] pos) {return get(pos,true,true);}
    public char get(int[] pos,boolean chkPl,boolean chkAOB) {
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
    	return get(p);
    }
    
    public x10.lang.CharReferenceArray overlay(x10.lang.charArray d) {
        dist dist = distribution.overlay(d.distribution);
        CharArray_c ret = new CharArray_c(dist, (char) 0, safe_);
        place here = x10.lang.Runtime.runtime.currentPlace();
        try {
            for (Iterator it = dist.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                place pl = dist.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                char val = (d.distribution.region.contains(p)) ? d.get(p) : get(p);
                ret.set(val, p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }   
        return ret;
    }
    
    public void update(x10.lang.charArray d) {
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
    
    public CharReferenceArray union(x10.lang.charArray d) {
        dist dist = distribution.union(d.distribution);
        CharArray_c ret = new CharArray_c(dist, (char) 0, safe_);
        place here = x10.lang.Runtime.runtime.currentPlace();
        try {
            for (Iterator it = dist.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                place pl = dist.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                char val = (distribution.region.contains(p)) ? get(p) : d.get(p);
                ret.set(val, p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }   
        return ret;
    }
    
    public CharReferenceArray restriction(dist d) {
        return restriction(d.region);
    }
    
    public CharReferenceArray restriction(region r) {
        dist dist = distribution.restriction(r);
        CharArray_c ret = new CharArray_c(dist, (char) 0, safe_);
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
    
    public x10.lang.charArray toValueArray() {
    	if (! mutable_) return this;
    	throw new Error("TODO: <T>ReferenceArray --> <T>ValueArray");   
    }
    public boolean isValue() {
        return ! this.mutable_;
    }

    
}
