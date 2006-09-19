/*
 * Created on Oct 20, 2004
 */
package x10.array.sharedmemory;


import x10.array.BooleanArray;
import x10.array.Helper;
import x10.array.Operator;
import x10.base.Allocator;
import x10.base.MemoryBlock;
import x10.base.UnsafeContainer;
import x10.lang.ArrayOperations;
import x10.lang.Indexable;
import x10.lang.Runtime;
import x10.lang.dist;
import x10.lang.point;
import x10.runtime.Configuration;
import x10.array.Distribution_c;


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
     * This constructor must not be used directly by an application programmer.
     * Arrays are constructed by the corresponding factory methods in 
     * x10.lang.Runtime.
     */
    protected BooleanArray_c(dist d, Operator.Pointwise c, boolean safe) {
    	this( d, c, safe, true);
    }
    public BooleanArray_c(dist d, Operator.Pointwise c, boolean safe, boolean mutable, boolean ignored) {
		this(d, c, safe, mutable);
	}
    protected BooleanArray_c(dist d, Operator.Pointwise c, boolean safe, boolean mutable) {
        this(d, safe, mutable, null);
        if (c != null)
            ArrayOperations.scan(this, c, this);
    }
    
    /**
     * Create a new array per the given distribution, initialized to c.
     * @param d
     * @param c
     * @param safe
     */
    public BooleanArray_c(dist d, boolean c) {
        this(d, c, true);
    }
    public BooleanArray_c(dist d, boolean c, boolean safe) {
    	this(d, c, safe, true);
}
    public BooleanArray_c(dist d, boolean c, boolean safe, boolean mutable) {
    	this(d, safe, mutable, null);
    	ArrayOperations.scan(this, new Constant(c), this);
    }

    private BooleanArray_c(dist d, boolean[] a, boolean safe, boolean mutable) {
        this(d, safe, mutable, null);
    }

	/**
	 * @param d
	 * @param safe
	 * @param mutable
	 * @param arr TODO
	 */
	private BooleanArray_c(dist d, boolean safe, boolean mutable, boolean[] arr) {
		super(d);
        assert (d instanceof Distribution_c);
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
            this.arr_ = Allocator.allocSafe(count, Boolean.TYPE);
        }
	}
    /**
     * Return a safe IntArray_c initialized with the given local 1-d (Java) int array.
     * @param a
     * @return
     */
    public static BooleanArray_c BooleanArray_c(boolean[] a, boolean safe, boolean mutable) {
    	dist d = Runtime.factory.getDistributionFactory().local(a.length);
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

	public BooleanArray newInstance(dist d) {
		assert d instanceof Distribution_c;
		
		return new BooleanArray_c(d, (Operator.Pointwise) null, safe_);	
	}
	
	public BooleanArray newInstance(dist d, Operator.Pointwise c) {
		assert d instanceof Distribution_c;
		
		return new BooleanArray_c(d, c, safe_);	
	}

	public boolean set(boolean v, point pos) {return set(v,pos,true,true);}
    /* (non-Javadoc)
     * @see x10.lang.BooleanArray#set(int, int[])
     */
    public boolean set(boolean v, point pos,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(pos));        
        return arr_.setBoolean(v, (int) distribution.region.ordinal(pos));
    }
    
    
    public boolean set(boolean v, int d0) {return set(v,d0,true,true);}
    
    public boolean set(boolean v, int d0,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(distribution,d0,chkAOB);
    	return arr_.setBoolean(v,d0);
    }
    
    public boolean set(boolean v, int d0, int d1) {return set(v,d0,d1,true,true);}
    public boolean set(boolean v, int d0, int d1,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,chkAOB);
    	return arr_.setBoolean(v,theIndex);
    }
    
    public boolean set(boolean v, int d0, int d1,int d2) {return set(v,d0,d1,d2,true,true);}
    public boolean set(boolean v, int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,d2,chkAOB);
    	return arr_.setBoolean(v,theIndex);
    }
    
    public boolean set(boolean v, int d0, int d1,int d2,int d3) {return set(v,d0,d1,d2,d3,true,true);}
    public boolean set(boolean v, int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,d2,d3,chkAOB);
    	return arr_.setBoolean(v,theIndex);
        
    }

    
    /* (non-Javadoc)
     * @see x10.lang.BooleanArray#get(int[])
     */
    public boolean get(point pos) {return get(pos,true,true);}
    public boolean get(point pos,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(pos));
        
        return arr_.getBoolean((int) distribution.region.ordinal(pos));
    }
    
    public boolean get(int d0) {return get(d0,true,true);}
    public boolean get(int d0,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));        
        d0 = Helper.ordinal(distribution,d0,chkAOB);
    	return arr_.getBoolean(d0);
    }
    
    public boolean get(int d0,int d1) {return get(d0,d1,true,true);}
    public boolean get(int d0, int d1,boolean chkPl,boolean chkAOB) {   	
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1));        
        int theIndex = Helper.ordinal(distribution,d0,d1,chkAOB); 
    	return arr_.getBoolean(theIndex);
    }
    
    public boolean get(int d0,int d1,int d2) {return get(d0,d1,d2,true,true);}
    public boolean get(int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));        
        int theIndex = Helper.ordinal(distribution,d0,d1,d2,chkAOB);
    	return arr_.getBoolean(theIndex); 
    }
    
    public boolean get(int d0,int d1,int d2,int d3) {return get(d0,d1,d2,d3,true,true);}
    public boolean get(int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));        
        int	theIndex = Helper.ordinal(distribution,d0,d1,d2,d3,chkAOB);   	
    	return arr_.getBoolean(theIndex);  
    }
    
    public boolean get(int[] pos) {return get(pos,true,true);}
    public boolean get(int[] pos,boolean chkPl,boolean chkAOB) {
        final point p = Runtime.factory.getPointFactory().point( pos);
    	return get(p);
    }

    public x10.lang.booleanArray toValueArray() {
    	if (! mutable_) return this;
    	throw new Error("TODO: <T>ReferenceArray --> <T>ValueArray");   
    }
    public boolean isValue() {
        return ! this.mutable_;
    }
}
