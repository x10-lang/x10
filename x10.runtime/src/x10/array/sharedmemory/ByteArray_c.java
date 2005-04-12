/*
 * Created on Oct 20, 2004
 */
package x10.array.sharedmemory;

import java.util.Iterator;



import x10.array.ByteArray;
import x10.array.Operator;
import x10.base.Allocator;
import x10.base.MemoryBlock;
import x10.base.UnsafeContainer;
import x10.lang.Indexable;
import x10.lang.Runtime;
import x10.lang.point;
import x10.lang.distribution;
import x10.lang.region;
import x10.lang.ByteReferenceArray;


/**
 * @author Christian Grothoff, Christoph von Praun
 */
public class ByteArray_c extends ByteArray implements UnsafeContainer, Cloneable {

    private final boolean safe_;
    private final MemoryBlock arr_;
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
    public ByteArray_c( distribution d, byte c) {
        this(d, c, true);
    }
    public ByteArray_c( distribution d, byte c, boolean safe ) {
    	this(d, c, safe, true);
}
    public ByteArray_c( distribution d, byte c, boolean safe, boolean mutable ) {
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
    public ByteArray_c( distribution d, ByteArray.pointwiseOp f) {
        this(d, f, true);
    }
    public ByteArray_c( distribution d, ByteArray.pointwiseOp f, boolean safe) {
    	this(d, f, safe, true);
    }
    public ByteArray_c( distribution d, ByteArray.pointwiseOp f, boolean safe, boolean mutable) {
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
        scan(this, f);
    }
    
    private ByteArray_c( distribution d, byte[] a, boolean safe, boolean mutable) {
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
    	distribution d = Runtime.factory.getDistributionFactory().local(a.length);
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

	protected ByteArray newInstance(distribution d) {
		assert d instanceof Distribution_c;
		
		return new ByteArray_c((Distribution_c) d, safe_);	
	}
	
	protected ByteArray newInstance(distribution d, Operator.Pointwise c) {
		assert d instanceof Distribution_c;
		
		return new ByteArray_c((Distribution_c) d, c, safe_);	
	}
	

	public ByteReferenceArray lift( ByteArray.binaryOp op, x10.lang.byteArray arg ) {
	    assert arg.distribution.equals(distribution); 
	    ByteArray arg1 = (ByteArray)arg;
	    ByteArray result = newInstance(distribution);
	    for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	        point p = (point) it.next();
	        result.set((byte) op.apply(this.get(p), arg1.get(p)),p);
	    }
	    return result;
	}
	public ByteReferenceArray lift( ByteArray.unaryOp op ) {
	    ByteArray result = newInstance(distribution);
	    for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	        point p = (point) it.next();
	        result.set((byte) op.apply(this.get(p)),p);
	    }
	    return result;
	}
	public int reduce( ByteArray.binaryOp op, byte unit ) {
	    byte result = unit;
	    for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	        point p = (point) it.next();
	        result = (byte) op.apply(this.get(p), result);
	    }
	    return result;
	}
	
	public ByteReferenceArray scan( binaryOp op, byte unit ) {
	    byte temp = unit;
	    ByteArray result = newInstance(distribution);
	    for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	        point p = (point) it.next();
	        temp = (byte) op.apply(this.get(p), temp);
	        result.set(temp, p);
	    }
	    return result;
	}
    
	
    /* (non-Javadoc)
     * @see x10.lang.ByteArray#set(int, int[])
     */
    public byte set(byte v, point pos) {
        distribution.checkAccess(pos);
        return arr_.setByte(v, (int) distribution.region.ordinal(pos));
    }
    
    
    public byte set(byte v, int d0) {
    	assert this.region.rank == 1;
        int[] pos = {d0};
    	final point p = Runtime.factory.getPointFactory().point(this.region, pos);
        return set(v, p);
    }
    
    public byte set(byte v, int d0, int d1) {
    	assert this.region.rank == 2;
    	if(false){
    		int[] pos = {d0, d1};
    		final point p = Runtime.factory.getPointFactory().point(this.region, pos);
    		return set(v, p);
    	}else {
    		int theIndex= d1 + (d0 *region.rank(1).size());
    		return arr_.setByte(v,theIndex);
    	}
    }
    
    public byte set(byte v, int d0, int d1, int d2) {
    	assert this.region.rank == 3;
        int[] pos = {d0, d1, d2};
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
        return set(v, p);
    }
    
    public byte set(byte v, int d0, int d1, int d2, int d3) {
    	assert this.region.rank == 4;
        int[] pos = {d0, d1, d2, d3};
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
        return set(v, p);
        
    }

    /* (non-Javadoc)
     * @see x10.lang.ByteArray#get(int[])
     */
    public byte get(point pos) {
        distribution.checkAccess(pos);
        return arr_.getByte((int) distribution.region.ordinal(pos));
    }
    
    public byte get(int d0) {
    	assert this.region.rank == 1;
        int[] pos = {d0};
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
    	return get(p);
    }
    public byte get(int d0, int d1) {
    	assert this.region.rank == 2;
    	if(false){
    		int[] pos = {d0, d1};
    		final point p = Runtime.factory.getPointFactory().point(this.region, pos);
    		return get(p);
    	}else {
    		int theIndex= d1 + (d0 *region.rank(1).size());
    		
    		return arr_.getByte(theIndex);
    	}
    }
    
    public byte get(int d0, int d1, int d2) {
    	assert this.region.rank == 3;
        int[] pos = {d0, d1, d2};
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
    	return get(p);
    }
    
    public byte get(int d0, int d1, int d2, int d3) {
    	assert this.region.rank == 4;
        int[] pos = {d0, d1, d2, d3};
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
    	return get(p);
    }
    public byte get(int[] pos) {
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
    	return get(p);
    }
    
    public x10.lang.ByteReferenceArray overlay(x10.lang.byteArray d) {
    	distribution dist = distribution.overlay(d.distribution);
        ByteArray_c ret = new ByteArray_c(dist, (byte) 0, safe_);
        for (Iterator it = dist.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            byte val = (d.distribution.region.contains(p)) ? d.get(p) : get(p);
            ret.set(val, p);
        }
        return ret;
    }
    
    public void update(x10.lang.byteArray d) {
        assert (region.contains(d.region));
        for (Iterator it = d.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            set(d.get(p), p);
        }
    }
    
    public ByteReferenceArray union(x10.lang.byteArray d) {
        distribution dist = distribution.union(d.distribution);
        ByteArray_c ret = new ByteArray_c(dist, (byte) 0, safe_);
        for (Iterator it = dist.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            byte val = (distribution.region.contains(p)) ? get(p) : d.get(p);
            ret.set(val, p);
        }
        return ret;
    }
    
    public ByteReferenceArray restriction(distribution d) {
        return restriction(d.region);
    }
    
    public ByteReferenceArray restriction(region r) {
        distribution dist = distribution.restriction(r);
        ByteArray_c ret = new ByteArray_c(dist, (byte) 0, safe_);
        for (Iterator it = dist.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            ret.set(get(p), p);
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
