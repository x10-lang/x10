/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.lang;

import x10.util.GrowableRail;

/**
 * MultipleExceptions is used to to summarize all uncaught exceptions
 * raised during the execution of a <code>finish</code> and rethrow
 * them as a single exception when all activities controlled by the 
 * <code>finish</code> have terminated.
 */
public class MultipleExceptions(exceptions:Rail[CheckedThrowable]) extends Exception {
    public property exceptions():Rail[CheckedThrowable] = exceptions;

    public def this(es:GrowableRail[CheckedThrowable]) {
        property(es.toRail());
    }

    public def this() {
        property(null);
    }

    public def this(t:CheckedThrowable) {
        property(new Rail[CheckedThrowable](1, t));
    }

    public def printStackTrace(): void {
        for (t in exceptions) {
            t.printStackTrace();
        }
    }

    public static def make(es:GrowableRail[CheckedThrowable]):MultipleExceptions {
        if (null == es || es.isEmpty()) return null;
        return new MultipleExceptions(es);
    }

    public static def make(t:CheckedThrowable):MultipleExceptions {
        if (null == t) return null;
        return new MultipleExceptions(t);
    }

    /** @return a rail containing only the exceptions of the given type */
    public final def getExceptionsOfType[T]() {
        val es = new GrowableRail[T]();
        for (e in exceptions) {
            if (e instanceof T) {
                es.add(e as T);
            }
        }

        return es.toRail();
    }

    /** @return a new MultipleExceptions, filtering out all exceptions of the given type */
    public final def filterExceptionsOfType[T]():MultipleExceptions {
        val es = new GrowableRail[CheckedThrowable]();
        for (e in exceptions) {
            if (! (e instanceof T)) {
                es.add(e);
            }
        }

        return MultipleExceptions.make(es);
    }
}
