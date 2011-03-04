/*
 * Created on Oct 3, 2004
 */
package x10.lang;

/**
 * @author Christian Grothoff
 */
public interface Future {

	/**    
	 * Wait for the completion of this activity and return the
	 * return value.   
	 */    
	public X10Object force();    
}