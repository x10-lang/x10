/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.util.concurrent;

import x10.compiler.Global;
import x10.compiler.Pinned;
import x10.compiler.SuppressTransientError;
import x10.util.GrowableRail;
import x10.xrx.Runtime;

/**
 * The representation of an X10 future expression.
 */
public class Future[T] implements ()=>T { 
    private val root = GlobalRef[Future[T]](this);

    /**
     * Latch for signaling and wait
     */
    @SuppressTransientError transient private val latch = new Latch();

    /**
     * Set if the activity terminated with an exception.
     * Can only be of type Error or Exception
     *
     */
    // This cant be Cell because I need to create it before I know the value
    // that will go in.
    @SuppressTransientError transient private val exception = new GrowableRail[Exception]();
    @SuppressTransientError transient private val result = new GrowableRail[T]();
    transient private val eval:()=>T;

    public static def make[T](eval:()=> T) {
    	Runtime.ensureNotInAtomic();
    	val f = new Future[T](eval);
    	async f.run();
    	return f;
    }
    def this(eval:()=>T) {
        this.eval = eval;
    }

    /**
     * Return true if this activity has completed.
     */
    public def forced():Boolean = at (root) root().latch();
    public operator this():T = force();

    /**
     * Wait for the completion of this activity and return the computed value.
     */
    @Global public def force():T {
    	Runtime.ensureNotInAtomic();
    	return at (root) root().forceLocal();
    }
    @Pinned private def forceLocal():T {
    	 latch.await();
         if (exception.size() > 0) {
             throw exception(0);
         }
         return result(0);
    }
    @Pinned def run():void {
        try {
            finish result.add(eval());
            latch.release();
        } catch (t:Exception) {
            exception.add(t);
            latch.release();
        }
    }

    // [DC] The correct thing to do here is pull the name from the closure
    //public def toString():String = name;
}

// vim:shiftwidth=4:tabstop=4:expandtab
