package x10.lang;


/**
 * @author Christian Grothoff
 */
public abstract class Activity 
    implements Runnable {

    /**
     * This is an Activity that has a result in the form of a Future.
     */
    public static abstract class Expr 
	extends Activity {
       
	/**
	 * Wait for the completion of this activity and return the
	 * return value.
	 */
	public abstract X10Object getResult();

    } // end of Activity.Expr


} // end of Activity