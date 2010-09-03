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
 *
 * TODO: Ported from 2.0 to 2.1 via naive simulation of 
 *       2.0 style global object by wrapping all the global
 *       fields in GlobalRefs.
 *
 * TODO: Seems like Cell could be used instead of GrowableRail
 *       for exception and result fields.
 *
 */
public class Future[+T] implements ()=>T {
    /**
     * Latch for signaling and wait
     */
    private val latch = GlobalRef[Runtime.Latch](new Runtime.Latch());

    /**
     * Set if the activity terminated with an exception.
     * Can only be of type Error or RuntimeException
     */
    private val exception = GlobalRef[GrowableRail[Throwable]](new GrowableRail[Throwable]());

    private val result:GloablRef[GrowableRail[T]];

    private val eval:()=>T;

    def this(eval:()=>T) {
        this.eval = eval;
        result = GlobalRef[GrowableRail[T]](new GrowableRail[T]());
    }

    private def result() = at (result) result();

    /**
     * Return true if this activity has completed.
     */
    public def forced():boolean = at (latch) latch()();

    public def apply():T = force();

    /**
     * Wait for the completion of this activity and return the computed value.
     */
    public def force():T {
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
    //public def toString():String = name;
}

// vim:shiftwidth=4:tabstop=4:expandtab
