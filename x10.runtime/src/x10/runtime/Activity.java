package x10.runtime;

/**
 * @author Christian Grothoff
 */
public abstract class Activity 
    implements Runnable {

    public static abstract class Future 
	extends Activity {
       
	/**
	 * Wait for the completion of this activity and return the
	 * return value.
	 */
	public abstract Object getResult();

    } // end of Activity.Future

    public static abstract class Result {
       
	/**    
	 * Wait for the completion of this activity and return the
	 * return value.   
	 */    
	public abstract Object force();    

    } // end of Activity.Future


} // end of Activity