/*
 * Created on Oct 3, 2004
 */
package x10.lang;

/**
 * @author Christian Grothoff
 */
public abstract class Future extends X10Object {

	/**    
	 * Wait for the completion of this activity and return the
	 * return value.   
	 */    
	public abstract X10Object force();    
}