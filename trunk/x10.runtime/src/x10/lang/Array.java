/*
 * Created on Oct 1, 2004
 *
 */
package x10.lang;

import x10.lang.Distribution;


/**
* Baseclass of all Arrays in X10.
* 
* @author Christoph von Praun
*/

public abstract class Array {

	public final Distribution dist;
	
	/**
	 * @param d Distribution of the array.
	 */
	protected Array(Distribution d) {
		dist = d;
	}
    
    /**
     * Flat access.
     */
    public abstract void set(X10Object v, int[] pos);
    
    /**
     * Flat access.
     */
    public abstract X10Object get(int[] pos);
    
    /**
     * Pointwise arithmetic ADD operation.
     */
    public abstract Array padd(Array arg);
    
    /**
     * Pointwise arithmetic SUB operation.
     */
    public abstract Array psub(Array arg);

    /**
     * Pointwise arithmetic MULT operation.
     */
    public abstract Array pmult(Array arg);

    /**
     * Matrix MULT operation.
     */
    public abstract Array mult(Array arg);

    /**
     * Arithmetic NEG operation.
     */
    public abstract Array circshift(int[] arg);
} // end of Array