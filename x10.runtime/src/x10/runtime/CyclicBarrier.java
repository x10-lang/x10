/*
 * Created on Oct 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package x10.runtime;

/**
 * @author Christoph von Praun
 *
 * Cyclic barrier. We do not use the implementation in java.util.concurrent
 * because our implementation requeires that there is a varying number of 
 * participants. This will necessitate more synchronization.
 */
public interface CyclicBarrier extends Collective {
	
	 /**
	  * Cont stands for 'continue'. This method allows a party to signal that it
	  * has accompished all work that needs to be done for the barrier. Unlike
	  * method next(), this method does not block. this might cause the barrier 
	  * to advance.
	  */
	void cont();
	
	/**
	 * Blocks until the barrier count goes down to zero.
	 * This method implies the effect of method cont()
	 * before waiting. 
	 */
	void barrier();
}
