#ifndef X10SWITCH_H_
#define X10SWITCH_H_
//*******************SWITCH OPERATIONS**********************************
/** A switch is an asymmetric clock -- only the thread creating it
 *  may perform a next operation on it. 
 * A thread may create a switch and post one or more remote nonblockng (nb) 
 * operations on it, by passing the switch as an argument 
 * to these operations.  The same switch may be used for nonblocking calls
 * issued to different destination nodes. 
 */
typedef struct {
	// data needed for book-keeping associaed with remote operation.
	X10_remoteop_t* next;
} X10_remoteop_t;
typedef struct {
	X10_remoteop_t* op;
} X10_switch_t;

/**
 * Create and return a switch. Nonblocking operations involving any remote
 * node may be posted on this switch. A reference to the switch is 
 * kept in core API datastructures.
 */
extern X10_switch_t* X10_make_switch();

/** Wait until the switch is closed. The switch must have been created by
 * the current thread, otherwise the result is undefined. 
 * The switch is closed when all operations posted to the switch have
 * been performed. After this call returns, the switch may be used again 
 * (the clock advances).
 * Note that the implementation need not start performing any remote nb operations
 * posted on a switch h until X10_wait(h) has been called. 
 * At this point, all the remote operations to be performed
 * are known, and the implementation may aggregate remote put and get
 * requests for the same node separately.
 * WARNING: The programmer must ensure that the remote operation is scheduled
 * on the switch in the right phase.
 */                    
extern int X10_wait(X10_switch_t* handle); 

/** Perform an X10_wait(h) on all switches h in the order in which they were created
 * by this thread. 
 */     
extern int X10_wait(void);

/** Check if the switch is closed. The switch must have been created by
 * the current thread, otherwise the result is undefined.
 * The switch is closed when all operations posted
 * to the switch have been performed. Returns X10_OK if the switch is closed,
 * X10_IN_PROGRESS otherwise.
 * Extension TBD: Consider returning (a lower bound on) the number of incomplete operations.
 */   
extern int X10_try_wait(X10_switch_t* handle);   

#endif /*X10SWITCH_H_*/
