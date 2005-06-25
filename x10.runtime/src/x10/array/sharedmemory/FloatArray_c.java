/*
 * Created on Oct 20, 2004
 */
package x10.array.sharedmemory;

import java.util.Iterator;
import x10.array.FloatArray;
import x10.array.Operator;
import x10.base.Allocator;
import x10.base.MemoryBlock;
import x10.base.UnsafeContainer;
import x10.lang.Indexable;
import x10.lang.Runtime;
import x10.runtime.RemotePlace;
import x10.lang.place;
import x10.lang.point;
import x10.lang.dist;
import x10.lang.region;
import x10.lang.FloatReferenceArray;
import x10.runtime.Configuration;
import x10.runtime.FatPointer;
import x10.runtime.ElementType;


/**
 * @author Christian Grothoff, Christoph von Praun
 */
public class FloatArray_c extends FloatArray implements UnsafeContainer, Cloneable {

    private final boolean safe_;
    private final MemoryBlock arr_;
    public final boolean mutable_;
    
    public boolean valueEquals(Indexable other) {
        return arr_.valueEquals(((FloatArray_c)other).arr_);
    }

    
    /**
     *  This constructor must not be used directly by an application programmer.
     * Arrays are constructed by the corresponding factory methods in 
     * x10.lang.Runtime.
     */
    protected FloatArray_c(Distribution_c d, boolean safe) {
        this(d, (Operator.Pointwise) null, safe);
    }
    
    protected FloatArray_c(Distribution_c d, Operator.Pointwise c, boolean safe) {
    	this( d, c, safe, true);
    }
    protected FloatArray_c(Distribution_c d, Operator.Pointwise c, boolean safe, boolean mutable) {
        super(d);
        this.mutable_ = mutable;
        this.safe_ = safe;
        int count =  d.region.size();
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
    public FloatArray_c( dist d, float c) {
        this(d, c, true);
    }
    public FloatArray_c( dist d, float c, boolean safe ) {
    	this(d, c, safe, true);
}
    public FloatArray_c( dist d, float c, boolean safe, boolean mutable ) {
    	super(d);
    	this.mutable_ = mutable;
    	int count =  d.region.size();
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
    public FloatArray_c( dist d, FloatArray.pointwiseOp f) {
        this(d, f, true);
    }
    public FloatArray_c( dist d, FloatArray.pointwiseOp f, boolean safe) {
    	this(d, f, safe, true);
    }
    
    public FloatArray_c(/*TODO: add FatPointer containingObject*/
    		dist d, FloatArray.pointwiseOp f, boolean safe, boolean mutable) {
    	super(d);
    	this.mutable_ = mutable;
    	int count =  d.region.size();
        this.safe_ = safe;
        
        if(Configuration.isMultiNodeVM()){
        	System.out.println("In float constructor C");
        	System.out.println("distribution:"+d);
        	System.out.println("region:"+d.region);
        	// find the region applicable to this place
        	place myPlace = Runtime.here();
        	int currentPlaceId = myPlace.id;
        	System.out.println("current place:"+currentPlaceId);
        	region theRegions[] = ((Distribution_c)d).getAllocatedRegions();
        	for(int i=0;i <theRegions.length;++i){
        		System.out.println("place:"+i+"::"+theRegions[i]+" size:"+theRegions[i].size());
        	}
        	place placeCursor = place.FIRST_PLACE;
        	do {
        		if(placeCursor.id != myPlace.id){ 
        			// FIXME RemotePlace remoteP = (RemotePlace)placeCursor;
        			
        			FatPointer tempHandle = new FatPointer(); // TODO remove once it's a parm
        			tempHandle.setObject((Object)this);// bogus
        			
        			RemotePlace.runArrayConstructor(tempHandle,ElementType.FLOAT,4,d,safe,mutable,placeCursor.id);
        		}
        		else {// could do this but perhaps better to optimize and call directly
        			// TODO: create array locally
        	    	System.out.println("Need to create local storage (place="+myPlace.id+") size:4*"
        	    			+theRegions[currentPlaceId].size()+"("+theRegions[currentPlaceId]+")");
        	        
        		}
        		placeCursor = placeCursor.next();
        	}while (placeCursor != place.FIRST_PLACE);
        
        }
        
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
    
    private FloatArray_c( dist d, float[] a, boolean safe, boolean mutable) {
    	super(d);
        int count =  d.region.size();
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
    public static FloatArray_c FloatArray_c( float[] a, boolean safe, boolean mutable ) {
    	dist d = Runtime.factory.getDistributionFactory().local(a.length);
    	return new FloatArray_c(d, a, safe, mutable );
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
            op.apply(arr_.getFloat(i));
    }
    
    /* Overrides the superclass method - this implementation is more efficient */
    protected void assign(FloatArray rhs) {
    	assert rhs instanceof FloatArray_c;
    	
    	FloatArray_c rhs_t = (FloatArray_c) rhs;
    	if (rhs.distribution.equals(distribution)) {
    	    int count = arr_.count();
    		for (int i  = 0; i < count; ++i) 
    			arr_.setFloat(rhs_t.arr_.getFloat(i), i);
    	} else 
    		// fall back to generic implementation
    		super.assign(rhs);
    }

	protected FloatArray newInstance(dist d) {
		assert d instanceof Distribution_c;
		
		return new FloatArray_c((Distribution_c) d, safe_);	
	}
	
	protected FloatArray newInstance(dist d, Operator.Pointwise c) {
		assert d instanceof Distribution_c;
		
		return new FloatArray_c((Distribution_c) d, c, safe_);	
	}
	

	public FloatReferenceArray lift( FloatArray.binaryOp op, x10.lang.floatArray arg ) {
	    assert arg.distribution.equals(distribution); 
	    FloatArray arg1 = (FloatArray)arg;
	    FloatArray result = newInstance(distribution);
	    for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	        point p = (point) it.next();
	        result.set(op.apply(this.get(p), arg1.get(p)),p);
	    }
	    return result;
	}
	public FloatReferenceArray lift( FloatArray.unaryOp op ) {
	    FloatArray result = newInstance(distribution);
	    for (Iterator it = distribution.region.iterator(); it.hasNext();) {
	        point p = (point) it.next();
	        result.set(op.apply(this.get(p)),p);
	    }
	    return result;
	}
    public float reduce( FloatArray.binaryOp op, float unit ) {
        float result = unit;
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
             result = op.apply(this.get(p), result);
        }
        return result;
    }

    public FloatReferenceArray scan( binaryOp op, float unit ) {
        float temp = unit;
        FloatArray result = newInstance(distribution);
        for (Iterator it = distribution.region.iterator(); it.hasNext();) {
            point p = (point) it.next();
            temp = op.apply(this.get(p), temp);
             result.set(temp, p);
        }
        return result;
    }
    
	
    /* (non-Javadoc)
     * @see x10.lang.FloatArray#set(int, int[])
     */
    public float set(float v, point pos) {
        return arr_.setFloat(v, (int) distribution.region.ordinal(pos));
    }
    
    public float setOrdinal(float v, int rawIndex) {
    	
    	return arr_.setFloat(v,rawIndex);
    }
    
    public float set(float v, int d0) {
    	d0 = Helper.ordinal(distribution,d0);
    	return arr_.setFloat(v,d0);
    }
     
    public float set(float v, int d0, int d1) {
    	int	theIndex = Helper.ordinal(distribution,d0,d1);
    	return arr_.setFloat(v,theIndex);
    }
    
    public float set(float v, int d0, int d1, int d2) {
    	int	theIndex = Helper.ordinal(distribution,d0,d1,d2);
    	return arr_.setFloat(v,theIndex);
    }
    
    public float set(float v, int d0, int d1, int d2, int d3) {
    	int	theIndex = Helper.ordinal(distribution,d0,d1,d2,d3);
    	return arr_.setFloat(v,theIndex);   	
    }


    /* (non-Javadoc)
     * @see x10.lang.FloatArray#get(int[])
     */
    public float get(point pos) {
        return arr_.getFloat((int) distribution.region.ordinal(pos));
    }
    
    
    public float getOrdinal(int rawIndex) {
    	
    	return arr_.getFloat(rawIndex);
    }
    
    public float get(int d0) {
    	 if (Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0));
        
        d0 = Helper.ordinal(distribution,d0);
    	return arr_.getFloat(d0);
    }
    
    public float get(int d0, int d1) {
    	int theIndex = Helper.ordinal(distribution,d0,d1);
        
    
    	//int placeId = Runtime.here().id;
    	//System.out.println("convert "+theIndex+"->"+distribution.getVirtualIndexAdjustment(theIndex));
    	
    	return arr_.getFloat(theIndex);
    }
    
    public float get(int d0, int d1, int d2) {
    	int theIndex = Helper.ordinal(distribution,d0,d1,d2);
    	return arr_.getFloat(theIndex);  	
    } 
    
    public float get(int d0, int d1, int d2, int d3) {
    	int theIndex = Helper.ordinal(distribution,d0,d1,d2,d3);	
    	return arr_.getFloat(theIndex);    	
    }
    
    public float get(int[] pos) {
        final point p = Runtime.factory.getPointFactory().point(this.region, pos);
    	return get(p);
    }
    
    public x10.lang.FloatReferenceArray overlay(x10.lang.floatArray d) {
    	dist dist = distribution.overlay(d.distribution);
        FloatArray_c ret = new FloatArray_c(dist, 0, safe_);
        for (Iterator it = dist.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            float val = (d.distribution.region.contains(p)) ? d.get(p) : get(p);
            ret.set(val, p);
        }
        return ret;
    }
    
    public void update(x10.lang.floatArray d) {
        assert (region.contains(d.region));
        for (Iterator it = d.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            set(d.get(p), p);
        }
    }
    
    public FloatReferenceArray union(x10.lang.floatArray d) {
        dist dist = distribution.union(d.distribution);
        FloatArray_c ret = new FloatArray_c(dist, 0, safe_);
        for (Iterator it = dist.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            float val = (distribution.region.contains(p)) ? get(p) : d.get(p);
            ret.set(val, p);
        }
        return ret;
    }
    
    public FloatReferenceArray restriction(dist d) {
        return restriction(d.region);
    }
    
    public FloatReferenceArray restriction(region r) {
        dist dist = distribution.restriction(r);
        FloatArray_c ret = new FloatArray_c(dist, 0, safe_);
        for (Iterator it = dist.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            ret.set(get(p), p);
        }
        return ret;
    }
    
    public x10.lang.floatArray toValueArray() {
    	if (! mutable_) return this;
    	throw new Error("TODO: <T>ReferenceArray --> <T>ValueArray");   
    }
    public boolean isValue() {
        return ! this.mutable_;
    }

    
}
