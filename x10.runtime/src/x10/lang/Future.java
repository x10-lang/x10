/*
 * Created on Oct 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package x10.lang;



/**
 * @author Christian Grothoff
 */
public abstract class Future {

/**    
 * Wait for the completion of this activity and return the
 * return value.   
 */    
public abstract Object force();    

}