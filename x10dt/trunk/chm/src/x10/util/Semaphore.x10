/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/* An X10 implementation of a semaphore
 * 
 * Because there are currently no multi-place atomic operations
 * available, semaphores are needed in order to control access without
 * placing code within an atomic block. Semaphore supports access by
 * max_access_limit+1 number of processes and threads.
 * 
 * @author Shane Markstrum
 * @version 08/08/06
 */
package x10.util;

public class Semaphore extends x10.lang.Object {

	private int num_access = 1;
	private int max_access_limit = 0;
	
	public Semaphore() {};
	
	public Semaphore(int num_access){
		this.num_access = num_access;
		this.max_access_limit = num_access-1;
	}
	
	/* p_aux returns true if the semaphore is grabbed,
    	   otherwise it returns false. */
    	   
    	private boolean p_aux(){
    		int tmp;
    		when(num_access > 0){
    			tmp = --num_access;
    		}
    		if(tmp < 0){
    			v();
    			return false;
    		}    		
    		return true;
    	}
    	
    	/* p calls p_aux until the semaphore grants access. */
    	
    	public void p(){
    		while(!p_aux());
    	}
    	
    	/* v atomically increments the semaphore. */
    	public atomic void v(){
    		if(num_access <= max_access_limit)
    			num_access++;
    	}
}