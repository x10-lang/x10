/*
 * Created on Nov 16, 2004
 */
package x10.base;



/**
 * @author Christoph von Praun
 */
public interface Runtime {

    public abstract Place currentPlace();
    
    public abstract Place[] getPlaces();
}
