package x10.runtime;

/**
 * @author Christian Grothoff
 */
public interface Place {

    /**
     * We allow passing Activity and Activity.Future to
     * be passed here.  For an Activity.Future the
     * result will be thrown away.
     */
    public void runAsync(Activity a);

    public Activity.Result runFuture(Activity.Future a);

}


// X10: x = force future(p) { code };

// Place p;

// Object x = p.runFuture(new Activity.Future() { /* code */} ).force();

// Object x = new Activity.Fucture() { /* code */}).force(); // bad
