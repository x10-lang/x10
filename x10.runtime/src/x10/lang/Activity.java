package x10.lang;



/**
 * @author Christian Grothoff, Christoph von Praun
 */
public interface Activity extends Runnable {

    /**
     * This is an Activity that has a result in the form of a Future.
     */
    public interface Expr extends Activity {
       
	/**
	 * Wait for the completion of this activity and return the
	 * return value.
	 */
	public x10.lang.Object getResult();

    } // end of Activity.Expr

} // end of Activity