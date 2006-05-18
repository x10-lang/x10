/*
 * Created on Oct 20, 2004
 */
package x10.array.sharedmemory;


import java.util.Iterator;
import x10.array.Helper;
import x10.base.MemoryBlock;
import x10.array.Distribution_c;
import x10.base.MemoryBlockSafeIntArray;
import x10.base.Allocator;
import x10.base.UnsafeContainer;
import x10.array.IntArray;
import x10.array.Operator;
import x10.lang.Indexable;
import x10.lang.place;
import x10.lang.point;
import x10.lang.dist;
import x10.lang.region;
import x10.lang.Runtime;
import x10.lang.IntReferenceArray;
import x10.runtime.Configuration;


/**
 * @author Christian Grothoff, Christoph von Praun
 */
public class IntArray_c extends IntArray implements UnsafeContainer {

    protected final boolean safe_;
    protected final MemoryBlock arr_;
    public final boolean mutable_;
    
    public boolean valueEquals(Indexable other) {
        return arr_.valueEquals(((IntArray_c)other).arr_);
    }

    
    public void keepItLive() {}
    
    public long getUnsafeAddress() {
        return arr_.getUnsafeAddress();
    }
    
    public long getUnsafeDescriptor() {
        return arr_.getUnsafeDescriptor();
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
    public IntArray_c( dist d, int c) {
	this(d, c, true, true);
    }
    public IntArray_c( dist d, int c, boolean mutable ) {
    	this( d, c, true, mutable);
    }
    public IntArray_c( dist d, int c, boolean safe, boolean mutable) {
    	super(d);
    	this.mutable_ = mutable;
        this.safe_ = safe;
    	int count = d.region.size();
    	if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_INT);
        } else {
            this.arr_ =Allocator.allocSafe(count, Integer.TYPE);
        }
    	scan(this, new Assign(c));
    	
    }
    public IntArray_c( dist d, IntArray.pointwiseOp f){
	this(d, f, true, true);
    }
    
    public IntArray_c( dist d, IntArray.pointwiseOp f, boolean mutable ){
    	this(d, f, true, mutable);
    }
    
    public IntArray_c( dist d, IntArray.pointwiseOp f, boolean safe, boolean mutable) {
    	super(d);
    	this.mutable_ = mutable;
        this.safe_ = safe;
    	int count =  d.region.size();
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_INT);
        } else {
            this.arr_ =Allocator.allocSafe(count, Integer.TYPE);
        }
    	if (f != null)
    	    initScan(this, f);
    }
    /**
     * Called from constructor only.
     */
    protected void initScan( IntArray res, IntArray.pointwiseOp op ) {
    	scan(res, op);
    }
    protected IntArray_c( dist d, int[] a, boolean safe, boolean mutable ) {
    	super(d);
        this.mutable_ = mutable;
        this.safe_ = safe;
        int count =  d.region.size();
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_INT);
        } else {
            this.arr_ =Allocator.allocSafeIntArray(a); // Allocator.allocSafe(count, Integer.TYPE);
        }
    }
    public int[] getBackingArray() { return (arr_ instanceof MemoryBlockSafeIntArray) ?
    		((MemoryBlockSafeIntArray) arr_).getBackingArray()
			: null; }
    /** Return a safe IntArray_c initialized with the given local 1-d (Java) int array.
     * 
     * @param a
     * @return
     */
    public static IntArray_c IntArray_c(int[] a, boolean safe, boolean mutable ) {
    	dist d = Runtime.factory.getDistributionFactory().local(a.length);
    	return new IntArray_c(d, a, safe, mutable );
    }
    protected IntArray_c(Distribution_c d, Operator.Pointwise c, boolean safe) {
        super(d);
        int count = d.region.size();
        this.mutable_ = true;
        this.safe_ = safe;
        if (!safe) {
            int rank = d.region.rank;
            int ranks[] = new int[rank];
            for (int i = 0; i < rank; ++i) 
                ranks[i] = d.region.rank(i).size();
            this.arr_ = Allocator.allocUnsafe(count, ranks, Allocator.SIZE_INT);
        } else {
            this.arr_ =Allocator.allocSafe(count, Integer.TYPE);
        }
        if (c != null)
            pointwise(this, c);
    }
    
    /* Overrides the superclass method - this implementation is more efficient */
    public void reduction(Operator.Reduction op) {
        if (Configuration.BAD_PLACE_RUNTIME_CHECK) {
            super.reduction(op);
        } else {
            int count = arr_.count();
            for (int i  = 0; i < count; ++i) 
                op.apply(arr_.getInt(i));
        }
    }
    
  
    
    public IntReferenceArray lift( IntArray.binaryOp op, x10.lang.intArray arg ) {
        assert arg.distribution.equals(distribution);
        IntReferenceArray result = newInstance(distribution);
        place here = x10.lang.Runtime.runtime.currentPlace();
        try {
            for (Iterator it = distribution.region.iterator(); it.hasNext();) {
                point p = (point) it.next();
                place pl = distribution.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                result.set(op.apply(this.get(p), arg.get(p)),p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }
        return result;
    }
    public IntReferenceArray lift( IntArray.unaryOp op ) {
        IntReferenceArray result = newInstance(distribution);
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



    
    public int reduce(binaryOp op,region localRegion){
    	int result=0;
    	place here = x10.lang.Runtime.runtime.currentPlace();
		try {
			Iterator it = localRegion.iterator();
			point p = (point) it.next();
			place pl = distribution.get(p);
			x10.lang.Runtime.runtime.setCurrentPlace(pl);
			result = this.get(p);
			
			while(it.hasNext()) {
				p = (point) it.next();
				pl = distribution.get(p);
				x10.lang.Runtime.runtime.setCurrentPlace(pl);
				result = op.apply(this.get(p), result);
			}
		} finally {
			x10.lang.Runtime.runtime.setCurrentPlace(here);
		}
		return result;
    }	
		
    public int reduce( IntArray.binaryOp op, int unit ) {
        int result = unit;
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

    public IntReferenceArray scan( IntArray.binaryOp op, int unit ) {
        int temp = unit;
        x10.lang.IntReferenceArray result = newInstance(distribution);
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
    
    /* Overrides the superclass method - this implementation is more efficient */
    protected void assign(IntArray rhs) {
    	assert rhs.getClass() == this.getClass();
    	
    	IntArray_c rhs_t = (IntArray_c) rhs;
    	if (!Configuration.BAD_PLACE_RUNTIME_CHECK &&
                rhs.distribution.equals(distribution)) {
    	    int count = arr_.count();
    		for (int i  = 0; i < count; ++i) 
    			arr_.setInt(rhs_t.arr_.getInt(i), i);
    	} else 
    		// fall back to generic implementation
    		super.assign(rhs);
    }

	protected IntArray newInstance(dist d) {
		assert d instanceof Distribution_c;		
		return new IntArray_c((Distribution_c) d, safe_);	
	}
	
	protected IntArray newInstance( dist d, Operator.Pointwise p) {
		assert d instanceof Distribution_c;		
		return new IntArray_c((Distribution_c) d, p, safe_);	
	}
    
	public int set(int v, point pos) {
		return set(v,pos,true,true);
	}
	public int set(int v, point pos,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(pos));
		
		int theIndex = Helper.ordinal(distribution,pos,chkAOB);
		//  System.out.println("about the set "+pos+"=>"+theIndex+" at "+Runtime.here());
		
		return arr_.setInt(v, theIndex);
	}    
	
	public int setOrdinal(int v, int d0) {
		return arr_.setInt(v, d0);
	}
	
	public int set(int v, int d0) {return set(v,d0,true,true);}
	
	public int set(int v, int d0,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0));
		
		d0 = Helper.ordinal(distribution,d0,chkAOB);
		return arr_.setInt(v,d0);
	}
	
	public int set(int v, int d0,int d1) {return set(v,d0,d1,true,true);}
	public int set(int v, int d0, int d1,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1));
		
		int	theIndex = Helper.ordinal(distribution,d0,d1,chkAOB);
		
		return arr_.setInt(v,theIndex);
	}
	
	public int set(int v, int d0,int d1,int d2) {return set(v,d0,d1,d2,true,true);}
	public int set(int v, int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2));
		
		int	theIndex = Helper.ordinal(distribution,d0,d1,d2,chkAOB);
		return arr_.setInt(v,theIndex);
	}
	
	public int set(int v, int d0,int d1,int d2,int d3) {return set(v,d0,d1,d2,d3,true,true);}
	public int set(int v, int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));
		
		int	theIndex = Helper.ordinal(distribution,d0,d1,d2,d3,chkAOB);
		return arr_.setInt(v,theIndex);
		
	}
	
	/**
	 * the cannonical index has already be calculated and adjusted.  
	 * Can be used by any dimensioned array.
	 */
	public int getOrdinal(int rawIndex) {
		
		return arr_.getInt(rawIndex);
	}
	
	public int get(point pos) {return get(pos,true,true);}
	/* (non-Javadoc)
	 * @see x10.lang.IntArray#get(int[])
	 */
	public int get(point pos,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(pos));
		int theIndex = Helper.ordinal(distribution,pos,chkAOB);
		
		return arr_.getInt(theIndex);
	}   
	
	public int get(int d0) {return get(d0,true,true);}
	public int get(int d0,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0));
		
		d0 = Helper.ordinal(distribution,d0,chkAOB);
		return arr_.getInt(d0);
	}
	
	public int get(int d0,int d1) {return get(d0,d1,true,true);}
	public int get(int d0, int d1,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1));
		
		int	theIndex = Helper.ordinal(distribution,d0,d1,chkAOB);
		return arr_.getInt(theIndex);
	}
	
	public int get(int d0,int d1,int d2) {return get(d0,d1,d2,true,true);}
	public int get(int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2));
		
		int	theIndex = Helper.ordinal(distribution,d0,d1,d2,chkAOB);
		return arr_.getInt(theIndex);  	
	} 
	
	public int get(int d0,int d1,int d2,int d3) {return get(d0,d1,d2,d3,true,true);}
	public int get(int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(d0, d1, d2, d3));
		
		int	theIndex = Helper.ordinal(distribution,d0,d1,d2,d3,chkAOB);
		return arr_.getInt(theIndex);
		
	}
	
	public int get(int[] pos) {return get(pos, true,true);}
	
	public int get(int[] pos,boolean chkPl,boolean chkAOB) {
		if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
			Runtime.hereCheckPlace(distribution.get(pos));
		
		final point p = Runtime.factory.getPointFactory().point( pos);
		return get(p);
	}
	
	
	
    public IntReferenceArray overlay(x10.lang.intArray d) {
        dist dist = distribution.overlay(d.distribution);
        IntArray_c ret = new IntArray_c(dist, 0, safe_);
        place here = x10.lang.Runtime.runtime.currentPlace();
        try {
            for (Iterator it = dist.iterator(); it.hasNext(); ) {                
                point p = (point) it.next();
                place pl = dist.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
                int val = (d.distribution.region.contains(p)) ? d.get(p) : get(p);
                ret.set(val, p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }
        return ret;
    }

    public void update(x10.lang.intArray d) {
        assert (region.contains(d.region));
        place here = x10.lang.Runtime.runtime.currentPlace();
        try {
            for (Iterator it = d.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                place pl = distribution.get(p);                
                x10.lang.Runtime.runtime.setCurrentPlace(pl);                
                // the place of d[p] and this[p] must be the same! 
                set(d.get(p), p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }
    }
    
    public IntReferenceArray union(x10.lang.intArray d) {
        dist dist = distribution.union(d.distribution);
        IntArray_c ret = new IntArray_c(dist, 0, safe_);
        place here = x10.lang.Runtime.runtime.currentPlace();
        try {
            for (Iterator it = dist.iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                place pl = dist.get(p);
                x10.lang.Runtime.runtime.setCurrentPlace(pl);
//              the place of d[p] and this[p] must be the same! 
                int val = (distribution.region.contains(p)) ? get(p) : d.get(p);
                ret.set(val, p);
            }
        } finally {
            x10.lang.Runtime.runtime.setCurrentPlace(here);
        }
        return ret;
    }
    
    public IntReferenceArray restriction( dist d) {
        return restriction(d.region);
    }
    
    public IntReferenceArray restriction(region d) {
        dist dist = distribution.restriction(d);
        IntArray ret = newInstance(dist); //new IntArray_c(dist, 0, safe_);
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
    
    public x10.lang.intArray toValueArray() {
    	if (! mutable_) return this;
    	throw new Error("TODO: <T>ReferenceArray --> <T>ValueArray");
    	
    }
    
    public boolean isValue() {
        return ! this.mutable_;
    }
    
}
