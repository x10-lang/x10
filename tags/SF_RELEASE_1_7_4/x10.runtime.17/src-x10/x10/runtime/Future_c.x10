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
public value Future_c[T](name:String) extends Future[T] {
    /**
     * CountDownLatch for signaling and wait -- can be replaced by a boolean latch
     */
    private val cdl = new ModCountDownLatch(1);

    /**
     * Set if the activity terminated with an exception.
     * Can only be of type Error or RuntimeException
     */
	private val exception = new GrowableRail[Throwable]();
	
	private val result:GrowableRail[T];
	
	private val eval:()=>T;
	
	public def this(eval:()=>T, name:String) {
		property(name);
		this.eval = eval;
		result = new GrowableRail[T]();
	}
	
    public def forced():boolean = at (cdl.location) cdl.getCount() == 0;
    
    public def apply():T = force();

    public def force():T {
    	return at (cdl.location) {
	    	cdl.await();
	        if (exception.length() > 0) {
	        	val e = exception(0);
	            if (e instanceof Error)
	                throw e as Error;
	            if (e instanceof RuntimeException)
	                throw e as RuntimeException;
	            assert false as boolean;
	        }
	        result(0)
		};
    }

	def run():Void {
		try {
        	finish result.add(eval());
          	cdl.countDown();
        } catch (t:Throwable) {
            exception.add(t);
          	cdl.countDown();
        }
	}
	
	public def toString():String = name; 
}
