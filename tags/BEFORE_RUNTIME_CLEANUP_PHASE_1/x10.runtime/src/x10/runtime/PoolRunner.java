package x10.runtime;

import java.util.concurrent.*;
import java.util.*;
import java.lang.*;
import x10.lang.place;


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
	private Activity act;
	
	/** the pace where this runner currently executed - can be different from 
	 *  homePlace, e.g inside an array initiizer, see also 
	 *  DefaultRuntime_c.setCurrentPlace(place p) */
	private Place place;
	
	
    PoolRunner(ThreadGroup group, Runnable r, String namePrefix) {
    	super(group, r, namePrefix, 0);
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
    	act = a;
    }
    
    /**
     * Get Activity
     */
    public Activity getActivity() {
    	return act;
    }
      
    
    /**
     * This thread adds to pool dynamically if  
     */
    public void addPoolQueuePositive() {
		if(!act.getAddPoolCalled()) {
			int ret=place.getThreadPool().addPoolIfQueuePositive();
			if (ret > 0) act.setAddPoolCalled();
		}
	}
    
    /**
     * Add to pool only if number of threads waiting is greater or equal to corepoolsize
     *
     */
    public void addPoolNew() {
    	place.incNumBlocked();
    	if(place.getNumBlocked() >= place.getThreadPool().getCorePoolSize()) 
    		place.getThreadPool().addPool();
    }
    
    public String thisThreadString() {
       	return Thread.currentThread() +  "@" + place + ":" + System.currentTimeMillis();
    }
    
    public static String logString() {
       	return ( (PoolRunner)Thread.currentThread()).thisThreadString();
    }
       
    public String toString() {
       	return "<PoolRunner " + hashCode() + ">";
       			
    }
}
