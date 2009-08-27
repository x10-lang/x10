/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

import x10.util.GrowableRail;

/**
 * The representation of an X10 future expression.
 * @author tardieu
 */
public value Future_c[T] extends Future[T] {
    /**
     * Latch for signaling and wait
     */
    private val latch = new Latch();

    /**
     * Set if the activity terminated with an exception.
     * Can only be of type Error or RuntimeException
     */
	private val exception = new GrowableRail[Throwable]();
	
	private val result:GrowableRail[T];
	
	private val eval:()=>T;
	
	public def this(eval:()=>T) {
		this.eval = eval;
		result = new GrowableRail[T]();
	}
	
    public def forced():boolean = at (latch.location) latch();
    
    public def apply():T = force();

    public def force():T {
    	return at (latch.location) {
	    	latch.await();
	        if (exception.length() > 0) {
	        	val e = exception(0);
	            if (e instanceof Error)
	                throw e as Error;
	            if (e instanceof RuntimeException)
	                throw e as RuntimeException;
	            assert false;
	        }
	        result(0)
		};
    }

	def run():Void {
		try {
        	finish result.add(eval());
          	latch.release();
        } catch (t:Throwable) {
            exception.add(t);
          	latch.release();
        }
	}
	
    // [DC] The correct thing to do here is pull the name from the closure
	//public def toString():String = name; 
}
