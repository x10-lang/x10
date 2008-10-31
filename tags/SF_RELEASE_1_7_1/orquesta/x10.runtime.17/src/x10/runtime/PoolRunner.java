/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime;

import java.util.concurrent.*;
import java.util.*;
import java.lang.*;


/**
* Thread in the thread pool that can be used to run multiple
* activities over time.
*
*Moved out of LocalPlace_c by vj.
*Should be replaced by a standard thread pool.
*
* @author Christian Grothoff
* @author vj
* 
* @author Raj Barik, Vivek Sarkar
* 3/6/2006: replaced original PoolRunner by JCU implementation
*/

public class PoolRunner extends Thread implements ActivityRunner{
    
	/**
	 * Current activity being run by this thread 
	 */
	private Activity activity;
	
	/** the place where this runner currently executed - can be different from 
	 *  homePlace, e.g inside an array initializer, see also 
	 *  DefaultRuntime_c.setCurrentPlace(place p) */
	private Place place;

	private final String namePrefix;
	
    PoolRunner(ThreadGroup group, Runnable r, String namePrefix) {
    	super(group, r, namePrefix, 0);
    	this.namePrefix= namePrefix;
    }
    
    /**
     * Set place
     */
    public void setPlace(Place p) {
    	place = p;
    }
    
    /**
     * get place
     */
    public Place getPlace(){
    	return place;
    }
    
    /**
     * Set Activity
     */
    public void setActivity(Activity a) {
    	activity = a;
    	setName(namePrefix + ": " + a.myName());
    }
    
    /**
     * Get Activity
     */
    public Activity getActivity() {
    	return activity;
    }
    
    private String thisThreadString() {
       	return Thread.currentThread() +  "@" + place + ":" + System.currentTimeMillis();
    }
    
    public static String logString() {
       	return ((PoolRunner)Thread.currentThread()).thisThreadString();
    }
       
    public String toString() {
       	return "<PoolRunner " + hashCode() + ">";
       			
    }
}
