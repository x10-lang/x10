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
public interface Array {

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