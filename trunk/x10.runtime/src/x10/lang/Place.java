package x10.lang;

/**
 * @author Christian Grothoff
 */
public interface Place extends TypeArgument, x10.array.Place {

    /**
     * We allow passing Activity and Activity.Future to
     * be passed here.  For an Activity.Future the
     * result will be thrown away.
     */
    public void runAsync(Activity a);

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
    public Future runFuture(Activity.Expr a); 
    
    /**
     * Shutdown this place, the current X10 runtime will exit.
     */
    public void shutdown();
    

} // end of Place

