/*
 * Created on Nov 16, 2004
 */
package x10.base;

import x10.lang.place;
import x10.runtime.Place;

/**
 * @author Christoph von Praun
 */
public interface Runtime {

     Place currentPlace();
    
     Place[] getPlaces();
}
