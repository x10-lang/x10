/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime;

/**
 * X10 Activity must implements this interface in order to 
 * be schedulable by X10 runtime.
 * 
 * This interface has been introduced to avoid creation of 
 * extra activities at runtime.
 * Now the run method is directly implemented by X10 activity's 
 * mother abstract class <code>Activity</code>
 * @author vcave
 */
public interface X10Runnable extends Runnable {

	/**
	 * Method implementation encapsulates user code to run as an X10 activity. 
	 */
	public void runX10Task() throws Throwable;
}
