/*
 * Created on Nov 16, 2004
 */
package x10.base;

import x10.array.Place;

/**
 * @author Christoph von Praun
 */
public interface Runtime {

    public abstract Place currentPlace();
    
    public abstract Place[] getPlaces();
}
