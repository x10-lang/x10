/*
 * Created on Oct 3, 2004
 */
package x10.lang;

/**
 * @author Christian Grothoff
 * @author Christoph von Praun
 */
public abstract class Future extends Object {

	/**    
	 * Wait for the completion of this activity and return the
	 * return value.   
	 */    
	public abstract Object force();
	
}