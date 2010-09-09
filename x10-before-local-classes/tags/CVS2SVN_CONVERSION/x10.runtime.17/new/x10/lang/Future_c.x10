/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

/**
 * This class encapsulates the return value of a local async
 * call and allows the client to wait for the completion of the
 * async call (force future).
 *
 * @author Christian Grothoff
 * @author Christoph von Praun
 * @author vj
 * @author Raj Barik, Vivek Sarkar
 * @author tardieu
 */
public abstract class Future_c[T] extends Activity implements Future[T] {
    /**
     * Set if the activity terminated with an exception.
     * Can only be of type Error or RuntimeException
     * (since X10 only has unchecked exceptions).
     */
    private var exception: Throwable;

    private var result: T;

    /**
     * CountDownLatch for signaling and wait -- can be replaced by a boolean latch
     */
    private val cdl = new ModCountDownLatch(1);

    public def apply(): T {
        return force();
    }

    public def force(): T {
  	    cdl.await();
        if (null != exception) {
            if (exception instanceof Error)
                throw exception as Error;
            if (exception instanceof RuntimeException)
                throw exception as RuntimeException;
            assert false as boolean;
        }
        return result;
    }

    public def forced(): boolean {
        return cdl.getCount() == 0;
    }

    public abstract def eval(): T;
        
    public def runX10Task(): void {
		try {
        	startFinish();
        	try {
                result = eval();
            } catch (t: Throwable) {
                pushException(t);
            }
            stopFinish();
        } catch (t: Throwable) {
            // Now nested asyncs have terminated.
            exception = t;
        } finally {
          	cdl.countDown();
		}        
    }
}
