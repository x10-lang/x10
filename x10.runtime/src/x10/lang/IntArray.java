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
public interface IntArray extends Array {

    /**
     * Flat access.
     */
    public abstract void set(int v, int[] pos);
    
    /**
     * Flat access.
     */
    public abstract int get(int[] pos);
    
} // end of IntArray