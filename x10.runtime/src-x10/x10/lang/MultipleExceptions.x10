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

    /** 
     * Gets exceptions of the given type that are nested within this
     * instance of MultipleExceptions.
     * @param deep perform a deep traversal of the tree of MultipleExceptions
     *   associated with nested finish constructs
     * @return a rail containing only the exceptions of the given type 
     */
    public final def getExceptionsOfType[T](deep:Boolean):Rail[T] {
        val es = new GrowableRail[T]();
        for (e in exceptions) {
            if (e instanceof T) {
                es.add(e as T);
            } else if (deep && e instanceof MultipleExceptions) {
                val es2 = (e as MultipleExceptions).getExceptionsOfType[T]();
                for (e2 in es2) es.add(e2);
            }
        }

        return es.toRail();
    }

    public final def getExceptionsOfType[T]() = getExceptionsOfType[T](true);

    /** 
     * Gets a copy of this MultipleExceptions instance, with all nested
     * exceptions of the given type removed.
     * This method may be used for example is to filter all DeadPlaceExceptions
     * so that exceptions of other types can be handled separately.
     * @param deep perform a deep traversal of the tree of MultipleExceptions
     *   associated with nested finish constructs
     * @return a new MultipleExceptions, filtering out all exceptions of the given type 
     */
    public final def filterExceptionsOfType[T](deep:Boolean):MultipleExceptions {
        val es = new GrowableRail[CheckedThrowable]();
        for (e in exceptions) {
            if (deep && e instanceof MultipleExceptions) {
                val me = (e as MultipleExceptions).filterExceptionsOfType[T]();
                if (me != null) es.add(me);
            } else if (! (e instanceof T)) {
                es.add(e);
            }
        }

        return MultipleExceptions.make(es);
    }

    public final def filterExceptionsOfType[T]() = filterExceptionsOfType[T](true);
}
