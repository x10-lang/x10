/*
 * Created on Oct 10, 2004
 *
 */
package x10.lang;


/**
 * Integer arrays.
 * 
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
public interface DoubleArray extends Array {

    /**
     * Flat access.
     */
    public abstract void set(double v, int[] pos);
    
    /**
     * Flat access.
     */
    public abstract double get(int[] pos);
    
} // end of IntArray