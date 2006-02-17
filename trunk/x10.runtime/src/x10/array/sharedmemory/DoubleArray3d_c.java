package x10.array.sharedmemory;

import java.util.Iterator;

import x10.array.Distribution_c;
import x10.array.DoubleArray;
import x10.array.Helper;
import x10.array.MultiDimRegion;
import x10.array.Region3D0Base;
import x10.array.Operator;
import x10.array.DoubleArray.Assign;
import x10.base.Allocator;
import x10.base.MemoryBlock;
import x10.base.UnsafeContainer;
import x10.lang.DoubleReferenceArray;
import x10.lang.Indexable;
import x10.lang.Runtime;
import x10.lang.dist;
import x10.lang.doubleArray;
import x10.lang.place;
import x10.lang.point;
import x10.lang.region;
import x10.lang.doubleArray.binaryOp;
import x10.lang.doubleArray.unaryOp;
import x10.runtime.Configuration;

/*
 * Intended to implement a dense 3d array [0:I, 0:J,0:K].
 * The underlying region must be a MultiDim region over 3d. 
 * A single 1-d array is allocated in the VM. Portions of this array logically belong to different
 * places, as specified by the distribution.
 * @author vj
 */
public class DoubleArray3d_c extends DoubleArray_c  {
	 
	 /**
     *  This constructor must not be used directly by an application programmer.
     * Arrays are constructed by the corresponding factory methods in 
     * x10.lang.Runtime. Assume the distribution is 3d.
     */
	protected final int I,J,K;
	public DoubleArray3d_c(dist d) { this(d, (DoubleArray.pointwiseOp) null, true, false);}
    public DoubleArray3d_c(Distribution_c d, boolean safe) {
        this(d, (Operator.Pointwise) null, safe);
    }
    
    protected DoubleArray3d_c(Distribution_c d, Operator.Pointwise c, boolean safe) {
    	this( d, c, safe, true);
    }
    protected DoubleArray3d_c(Distribution_c d, Operator.Pointwise c, boolean safe, boolean mutable) {
        super(d,c,safe,mutable);
        assert d.region.rank == 3;
       // assert d.region instanceof Region3D0Base;
        //Region3D0Base r = (Region3D0Base) d.region;
        I=d.region.rank(0).high()+1; 
        J=d.region.rank(1).high()+1; 
        K=d.region.rank(2).high()+1;
        
    }
    /** Create a new array per the given distribution, initialized to c.
     * 
     * @param d
     * @param c
     * @param safe
     */
    public DoubleArray3d_c( dist d, double c) {
        this(d, c, true);
    }
    public DoubleArray3d_c( dist d, double c, boolean safe ) {
    	this(d, c, safe, true);
}
    public DoubleArray3d_c( dist d, double c, boolean safe, boolean mutable ) {
    	super(d,c,safe,mutable);
    	assert d.region.rank==3;
    	// assert d.region instanceof Region3D0Base;
    	 I=d.region.rank(0).high()+1; 
         J=d.region.rank(1).high()+1; 
         K=d.region.rank(2).high()+1;
    }
    public DoubleArray3d_c( dist d, DoubleArray.pointwiseOp f) {
        this(d, f, true);
    }
    public DoubleArray3d_c( dist d, DoubleArray.pointwiseOp f, boolean safe) {
    	this(d, f, safe, true);
    }
    public DoubleArray3d_c( dist d, DoubleArray.pointwiseOp f, boolean safe, boolean mutable) {
    	super(d,f,safe,mutable);
    	assert d.region.rank==3;
    	 //assert d.region instanceof Region3D0Base;
    	 I=d.region.rank(0).high()+1; 
         J=d.region.rank(1).high()+1; 
         K=d.region.rank(2).high()+1;
    }
    
    private DoubleArray3d_c( dist d, double[] a, boolean safe, boolean mutable) {
    	super(d,a,safe,mutable);
       assert d.region.rank==3;
       //assert d.region instanceof Region3D0Base;
       I=d.region.rank(0).high()+1; 
       J=d.region.rank(1).high()+1; 
       K=d.region.rank(2).high()+1;
    }
    /** Return a safe IntArray_c initialized with the given local 1-d (Java) int array.
     * 
     * @param a
     * @return
     */
    public static DoubleArray3d_c DoubleArray3d_c( double[] a, boolean safe, boolean mutable ) {
    	dist d = Runtime.factory.getDistributionFactory().local(a.length);
    	return 	new DoubleArray3d_c(d, a, safe, mutable );
    }
	
    
    protected void assign(DoubleArray rhs) {
    	assert rhs instanceof DoubleArray3d_c;
    	super.assign(rhs);
    }

	protected DoubleArray newInstance(dist d) {
		assert d instanceof Distribution_c;
		assert d.region instanceof Region3D0Base;
		return new DoubleArray3d_c((Distribution_c) d, safe_);	
	}
	
	protected DoubleArray newInstance(dist d, Operator.Pointwise c) {
		assert d instanceof Distribution_c;
		return new DoubleArray3d_c((Distribution_c) d, c, safe_);	
	}
	
	
    /* (non-Javadoc)
     * @see x10.lang.DoubleArray#set(int, int[])
     */
    public double set(double a, point pos) {
    	int[] v = pos.val();
    	assert (v.length==3);
    	return set(a,v[0],v[1],v[2],false,false);
    	}
 
    /**
     * the cannonical index has already be calculated and adjusted.  
     * Can be used by any dimensioned array.
     */
    public double setOrdinal(double v, int rawIndex) {
    	return arr_.setDouble(v,rawIndex);
    }
    
    public double set(double v, int d0) { throw new ArrayIndexOutOfBoundsException("3darray");}
    public double set(double v, int d0,boolean chkPl,boolean chkAOB) {
    	throw new ArrayIndexOutOfBoundsException("3darray");
    }
    public double set(double v, int d0,int d1) {throw new ArrayIndexOutOfBoundsException("3darray");}
    public double set(double v, int d0, int d1,boolean chkPl,boolean chkAOB) {  	
    	throw new ArrayIndexOutOfBoundsException("3darray");
    }
    public double set(double v, int d0,int d1,int d2,int d3) {
    	throw new ArrayIndexOutOfBoundsException("3darray");
    	}
    public double set(double v, int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {  
    	throw new ArrayIndexOutOfBoundsException("3darray");	
    }
    public double set(double v, int d0,int d1,int d2) {
    	return arr_.setDouble(v, (d2 + K*(d1 + J*d0)));
    	}
    public double set(double v, int d0, int d1, int d2,boolean chkPl,boolean chkAOB) { 
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));    
        if (chkAOB) {
        	if (d0 < 0 || d1 < 0 || d2 < 0 || d0 >= I || d1 >= J || d2>= K)
        		throw new ArrayIndexOutOfBoundsException();
        }
    	return arr_.setDouble(v, (d2 + K*(d1 + J*d0)));
    }
    public int ord(int i, int j, int k ) { return k+K*(j+J*i);}
    public int[] coord(int p) { int[] r = new int[3]; r[2]= p%K; p=p/K;r[1]=p%J;r[0]=p;return r;} 

    /* (non-Javadoc)
     * @see x10.lang.DoubleArray#get(int[])
     */
    public double get(point pos) {return get(pos,true,true);}
    public double get(point pos,boolean chkPl,boolean chkAOB) {
       return get(pos.val(), chkPl, chkAOB);
    }
    
    
    public double get(int d0) {return get(d0,true,true);}
    public double get(int d0,boolean chkPl,boolean chkAOB) {
    	throw new ArrayIndexOutOfBoundsException("3darray");
    }
    public double get(int d0,int d1) {return get(d0,d1,true,true);}
    public double get(int d0, int d1,boolean chkPl,boolean chkAOB) {   	
    	throw new ArrayIndexOutOfBoundsException("3darray");
    }
    public double get(int d0,int d1,int d2,int d3) {return get(d0,d1,d2,d3,true,true);}
    public double get(int d0, int d1, int d2, int d3,boolean chkPl,boolean chkAOB) {   	
    	throw new ArrayIndexOutOfBoundsException("3darray");
    }
    public double get(int d0,int d1,int d2) {
    	return arr_.getDouble(d2 + K*(d1 + J*d0));
    	}
    public double get(int d0, int d1, int d2,boolean chkPl,boolean chkAOB) {
        if (chkPl && Configuration.BAD_PLACE_RUNTIME_CHECK && mutable_)
            Runtime.hereCheckPlace(distribution.get(d0, d1, d2));    
        if (chkAOB) {
        	if (d0 < 0 || d1 < 0 || d2 < 0 || d0 >= I || d1 >= J || d2 >= K)
        		throw new ArrayIndexOutOfBoundsException("["+d0+","+d1+"," + d2+"] is not in [" + (I-1) + ","+(J-1)+","+(K-1)+"]");
        }
    	return arr_.getDouble(d2 + K*(d1 + J*d0));
    } 
    public double get(int[] pos) {return get(pos,true,true);}
    public double get(int[] pos,boolean chkPl,boolean chkAOB) {
    // May throw an array index out of bounds. Let it propagate through.
    	return get(pos[0],pos[1],pos[2], chkPl, chkAOB);	
    	
    }
    
    public void update(x10.lang.doubleArray d) {
    	assert (region.contains(d.region));
    	place here = x10.lang.Runtime.runtime.currentPlace();
    	try {
    		for (int i=0; i <= I; i++)
    			for (int j=0; j<J;j++)
    				for (int k=0;k<K;k++){
    					place pl = distribution.get(i,j,k);
    					x10.lang.Runtime.runtime.setCurrentPlace(pl);
    					set(d.get(i,j,k), i,j,k);
    				}
    	} finally {
    		x10.lang.Runtime.runtime.setCurrentPlace(here);
    	}
    }
    
    

}
