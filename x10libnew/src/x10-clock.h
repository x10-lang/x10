#ifndef X10CLOCK_H_
#define X10CLOCK_H_
/** A clock permits phased sequence of operations.
 */


namespace x10lib{

typedef Clock& X10_clock_t; 

public class Clock : public Object {
	public : 

	/**
	 * Create and return a clock. The thread must be executing an activity.
	 * The activity is registered with the clock.
	 */
	Clock();


	/**
	 * Clocks may be communicated to remote nodes. At the remote node
	 * a local proxy clock is created since the original clock cannot
	 * be accessed locally. All operations on the original clock are 
	 * performed with respect to this proxy clock. parent must be a
	 * reference to a remote clock. 
	 * This function is internal to the implementation of clocks.
	 */
	X10_clock_t* X10_clock_make_proxy(X10_gas_ref_t parent);
	
	
	/**
	 * Send a message to the remote proxy to advance it. 
	 * 
	 * This function is internal to the implementation of clocks.
	 */
	int x10_clock_advance_proxy(x10_gas_ref_t remote);

	/** Resume the clock. The thread must be executing an activity
	 * that is registered on the clock, otherwise an x10_ILLEGAL_ARG value
	 * is returned. 
	 * 
	 */

	 error_code resume(); 

	/** Resume all clocks this activity is registered with. The thread must be executing an activity, 
	 * otherwise an x10_ILLEGAL_ARG value is returned. 
	 * 
	 */
	static error_code x10_resume(); 

	/** Perform a next on the given clock. The thread must be executing an activity
	 * that is registered on the clock, otherwise an x10_ILLEGAL_ARG value
	 * is returned. 
	 * 
	 */

	error_code next(); 

	/** Perform a next on all clocks this activity is registered with. 
	 * The thread must be executing an activity, 
	 * otherwise an x10_ILLEGAL_ARG value is returned. 
	 * 
	 */
	static error_code next(); 

	/** Try a next on the given clock. The thread must be executing an activity
	 * that is registered on the clock, otherwise an x10_ILLEGAL_ARG value
	 * is returned. The call returns x10_OK if a next succeeded. It returns
	 * x10_NOT_OK if executing a next would have suspended this activity.
	 * 
	 */

         error_code try_next(); 

	/** Try next on all the clocks this activity is registered with. The thread must 
	 * be executing an activity, otherwise an x10_ILLEGAL_ARG value
	 * is returned. The call returns x10_OK if next succeeded. It returns
	 * x10_NOT_OK if executing next() would have suspended this activity.
	 * 
	 */

	error_code int try_next(); 


	/** Returns true if the current activity is registered with the given clock.
	 * The thread must be executing an activity
	 * that is registered on the clock, otherwise false
	 * is returned. 
	 * 
	 */
	 bool registered();

	/** Returns x10_OK if the current activity is not registered with the given clock.
	 * The thread must be executing an activity
	 * that is registered on the clock, otherwise an x10_ILLEGAL_ARG value
	 * is returned. 
	 * 
	 */
	bool dropped();

	/** Drop the registration of the current activity on the given clock.
	 * The thread must be executing an activity
	 * that is registered on the clock, otherwise an x10_ILLEGAL_ARG value
	 * is returned. 
	 * 
	 */

	error_code drop();

	// collective operations
	/** This thread must be executing an activity that is registered on the 
	 * given clock, otherwise an x10_ILLEGAL_ARG value is returned. In this phase 
	 * of the given clock, all activities registered on the clock must either drop 
	 * the clock or execute a x10_next_broadcast with a buffer of the same size.
	 * Exactly one of the activities must make this call with sender==1. 
	 * The contents of the buffer of this activity are copied into the given
	 * buffers for all other activities.
	 */

	error_code next_broadcast(void* buffer, int len, byte sender);

	error_code next_reduce(void* buffer, int len, byte sender);
}

}
#endif /*X10CLOCK_H_*/
