/*
 * Created on Nov 16, 2004
 */
package x10.array.sharedmemory;

import x10.base.Place;
import x10.base.Runtime;

/**
 * @author Christoph von Praun
 */
public class Runtime_c implements Runtime {

    private class PlaceImpl implements Place {};
    
    private final Place[] places_;
    
    public Runtime_c(int no_places) {
        places_ = new Place[no_places];
        for (int i = 0; i < no_places; ++i)
            places_[i] = new PlaceImpl();
    }
    
    public Place currentPlace() {
        return places_[0];
    }
    
    public Place[] getPlaces() {
        return (Place[]) places_.clone();
    }
}
