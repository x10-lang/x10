#ifndef X10SWITCH_H_
#define X10SWITCH_H_

namespace x10lib {


//*******************SWITCH OPERATIONS**********************************
/** A switch is an asymmetric clock -- only the thread creating it
 *  may perform a next operation on it. 
 * A thread may create a switch and post one or more remote nonblockng (nb) 
 * operations on it, by passing the switch as an argument 
 * to these operations.  The same switch may be used for nonblocking calls
 * issued to different destination nodes. 
 */

class Switch  {
	public: 
	/**
	 * Create and return a switch. Nonblocking operations involving any remote
	 * node may be posted on this switch. A reference to the switch is 
	 * kept in core API datastructures.
	 */
	  Switch();
	
	
	/** Wait until the switch is closed. The switch must have been created by
	 * the current thread, otherwise the result is undefined. 
	 * The switch is closed when all operations posted to the switch have
	 * been performed. After this call returns, the switch may be used again 
	 * (the clock advances).
	 * Note that the implementation need not start performing any remote nb operations
	 * posted on a switch h until wait(h) has been called. 
	 * At this point, all the remote operations to be performed
	 * are known, and the implementation may aggregate remote put and get
	 * requests for the same node separately.
	 * WARNING: The programmer must ensure that the remote operation is scheduled
	 * on the switch in the right phase.
	 */                    
	int wait(); 
	
	/** Perform an wait(h) on all switches h in the order in which they were created
	 * by this thread. 
	 */     
	static int wait_all();
	
	/** Check if the switch is closed. The switch must have been created by
	 * the current thread, otherwise the result is undefined.
	 * The switch is closed when all operations posted
	 * to the switch have been performed. Returns X10_OK if the switch is closed,
	 * X10_IN_PROGRESS otherwise.
	 * Extension TBD: Consider returning (a lower bound on) the number of incomplete operations.
	 */   
	int try_wait();   
};


typedef class Switch* switch_t;
}
#endif /*X10SWITCH_H_*/
