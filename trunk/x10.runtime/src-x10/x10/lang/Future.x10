/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.lang;

import x10.util.GrowableRail;

/**
 * The representation of an X10 future expression.
 * @author Christian Grothoff
 * @author Christoph von Praun
 * @author tardieu
 */
public class Future[+T] implements ()=>T {
    /**
     * Latch for signaling and wait
     */
    private global val latch = new Runtime.Latch();

    /**
     * Set if the activity terminated with an exception.
     * Can only be of type Error or RuntimeException
     */
    private global val exception = new GrowableRail[Throwable]();

    private global val result:GrowableRail[T];

    private global val eval:()=>T;

    def this(eval:()=>T) {
        this.eval = eval;
        result = new GrowableRail[T]();
    }

    private global def result() = result as GrowableRail[T]!;

    /**
     * Return true if this activity has completed.
     */
    public global def forced():boolean = at (latch) latch();

    public global def apply():T = force();

    /**
     * Wait for the completion of this activity and return the computed value.
     */
    public global def force():T {
        return at (latch) {
            latch.await();
            if (exception.length() > 0) {
                val e = exception(0);
                if (e instanceof Error)
                    throw e as Error;
                if (e instanceof RuntimeException)
                    throw e as RuntimeException;
                assert false;
            }
            result()(0)
        };
    }

    def run():Void {
        try {
            finish result().add(eval());
            latch.release();
        } catch (t:Throwable) {
            exception.add(t);
            latch.release();
        }
    }

    // [DC] The correct thing to do here is pull the name from the closure
    //public global def toString():String = name;
}

// vim:shiftwidth=4:tabstop=4:expandtab
