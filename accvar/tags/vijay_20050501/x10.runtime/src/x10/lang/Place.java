package x10.lang;

import x10.base.TypeArgument;


/**
 * @author Christian Grothoff
 */
public abstract class Place implements TypeArgument, x10.base.Place {

    /* is initialized by a specific runtime, e.g. x10.runtime.DefaukltRuntime_c */
    public static int MAX_PLACES;

    private static int count_ = 0;
    
    private final int id; 
    
    protected Place() {
        synchronized (Place.class) {
            id = count_++;
        }
    }
    /**
     * We allow passing Activity and Activity.Future to
     * be passed here.  For an Activity.Future the
     * result will be thrown away.
     */
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
    
    public static Place here() {
        return Runtime.here();
    }

} // end of Place

