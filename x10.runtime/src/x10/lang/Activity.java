package x10.lang;


/**
 * @author Christian Grothoff
 */
public abstract class Activity 
    implements Runnable {

    /**
     * @author Christian Grothoff
     */
    public static abstract class FutureActivity 
	extends Activity {
       
	/**
	 * Wait for the completion of this activity and return the
	 * return value.
	 */
	public abstract Object getResult();

    } // end of Activity.Future


} // end of Activity