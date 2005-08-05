package x10.runtime;

import x10.array.IntArray;
import x10.lang.Future;
import x10.lang.Object;
import x10.lang.Runtime;
import x10.lang.dist;
import x10.lang.place;
import x10.lang.point;
import x10.runtime.distributed.AsyncResult;
import x10.runtime.distributed.FatPointer;


/**
 * @author Christian Grothoff
 */
public abstract class Place extends place 
implements Comparable {
	
        public final int vm_;       // index into VMInfo.VM_
        public final int place_no_; // index into runtime.getLocalPlaces()
                                    //    when evaluated on VM identified
                                    //    by vm_

	protected Place(int vm_, int place_no_) {
		super();
                this.vm_ = vm_;
                this.place_no_ = place_no_;
	}
	
	public abstract void runAsync(Activity a);
	
	/**
	 * We return an Activity.Result here to force the programmer
	 * to actually run the future at a place before forcing the
	 * result.
	 * 
	 * The use-case is the following.  Suppose we have the x10 code:
	 * <code>
	 * p = here;
	 * Object x = force future(p) { code };
	 * </code>
	 * The resulting translation to Java would look like this:
	 * <code>
	 * Place p = Runtime.here(); 
	 * Object x = p.runFuture(new Activity.Future() { code }).force();
	 * </code>
	 * 
	 * @param a reference to the closure that encapsulates the code to run
	 * @return the placeholder for the future result.
	 */
	public abstract Future runFuture(Activity.Expr a); 
	
	/**
	 * Shutdown this place, the current X10 runtime will exit.
	 */
	public abstract void shutdown();
	
	public static Place[] places() {
		return Runtime.places();
	}
	
	public static place here() {
		return Runtime.here();
	}
    
    /* lexicographical ordering */
    public final int compareTo(java.lang.Object o) {
        assert o instanceof Place;
        Place tmp = (Place) o;
        
        int res;
        // row major ordering (C conventions)
        if (id < tmp.id) 
            res = -1;
        else if (id > tmp.id)
            res = 1;
        else 
            res = 0;
        return res;
    }
    
    public final int hashCode() {
        return id;   
    }
    
    public final boolean equals(Object o) {
        assert o instanceof Place;
        // works because every place has unique id
        return this == o;
    }
    
    
    public abstract FatPointer registerGlobalObject(java.lang.Object o);
    public abstract FatPointer shadowRemoteEntry(java.lang.Object o,int key);
    public abstract FatPointer findGlobalObject(java.lang.Object o);
} // end of Place

