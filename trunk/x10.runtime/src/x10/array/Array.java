/*
 * Created on Oct 1, 2004
 */
package x10.array;



/**
 * Baseclass of all Arrays in X10.
 * 
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
public abstract class Array {

	public final Distribution dist;
	
	public Array(Distribution d) {
		dist = d;
	}
	
	/**
     * Array overlay operation.
     * This method is reentrant on the target array (this).
     */
	public final Array over(Array arg) {		
		Distribution d_new = dist.asymetricUnion(arg.dist);
		Array ret = newInstance(d_new);
		ret.assign(arg);
		ret.assign(this);
		return ret;	
	}
    
    /**
     * Array assemble operation: returns a new array assembled from 
     * the target array and arg. The distribution of both arrays must 
     * be disjoint.
     * 
     *  This method is reentrant on the target array (this).
     */
    public final Array assemble(Array arg) {
    	Distribution d_new = dist.disjointUnion(arg.dist);
		Array ret = newInstance(d_new);
		ret.assign(arg);
		ret.assign(this);
		return ret;	
    }
    
    /**
     * Returns a restriction of the target array to Distribution <code>d</code>,
     * where <code>d</code> must be a sub-distribution of the distribution of the target 
     * array. 
     * 
     * This method is reentrant on the target array (this).
     */
    public final Array restrict(Distribution d) {
		Array ret = newInstance(d);
		ret.assign(this);
		return ret;	
    }
    
    /**
     * pointwise unary operation
     */
    public abstract void pointwise(Operator.Pointwise op);
    
    /**
     * pointwise binary operation
     */
    public abstract void pointwise(Array arg, Operator.Pointwise op);
    
    /**
     * scan operation
     */
    public abstract void scan(Operator.Scan op);
    
    /**
     * reduction operation - the result is obtained through the reduction
     * operator op
     */
    public abstract void reduction(Operator.Reduction op);

    /** 
     * Circular shift operator
     */
    public abstract void circshift(int[] arg);
    
    protected abstract void assign(Array rhs);
	
    /* "virtual" constructor */
    protected abstract Array newInstance(Distribution d);
    
} // end of Array