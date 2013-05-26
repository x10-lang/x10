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
import x10.io.Printer;


/**
 * MultipleExceptions is used to to summarize all uncaught exceptions
 * raised during the execution of a <code>finish</code> and rethrow
 * them as a single exception when all activities controlled by the 
 * <code>finish</code> have terminated.
 */
public class MultipleExceptions(exceptions:Rail[Exception]) extends Exception {
    public property exceptions():Rail[Exception] = exceptions;

    public def this(es:GrowableRail[Exception]) {
        property(es.toRail());
    }

    public def this() {
        property(null);
    }

    public def this(t:Exception) {
        property(new Rail[Exception](1, t));
    }

    public def printStackTrace(): void {
        for (t in exceptions) {
            t.printStackTrace();
        }
    }

    // FIXME CheckedThrowable.printStackTrace(Printer) is now final
    /*
    public def printStackTrace(p:Printer): void {
        for (t in exceptions.values()) {
            t.printStackTrace(p);
        }
    }
    */

    // FIXME CheckedThrowable.toString() is now final
    /*
    public def toString() {
        var me:String = super.toString();
        if (exceptions.size > 0) {
	  for (e in exceptions.values()) {
              me += "\n\t"+e;
          }
        }
        return me;
    }
    */

    public static def make(es:GrowableRail[Exception]):MultipleExceptions {
        if (null == es || es.isEmpty()) return null;
        return new MultipleExceptions(es);
    }

    public static def make(t:Exception):MultipleExceptions {
        if (null == t) return null;
        return new MultipleExceptions(t);
    }
}
