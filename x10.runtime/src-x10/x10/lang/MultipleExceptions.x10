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

import x10.util.Stack;
import x10.io.Printer;


/**
 * @author Christian Grothoff
 * @author tardieu
 */
public class MultipleExceptions(exceptions:Rail[CheckedThrowable]) extends Exception {
    public property exceptions():Rail[CheckedThrowable] = exceptions;

    public def this(stack:Stack[CheckedThrowable]) {
        property(stack.toArray());
    }

    public def this() {
        property(null);
    }

    public def this(t:CheckedThrowable) {
        property(new Array[CheckedThrowable](1, t));
    }

    public def printStackTrace(): void {
        for (t:CheckedThrowable in exceptions.values()) {
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

    public static def make(stack:Stack[CheckedThrowable]):MultipleExceptions {
        if (null == stack || stack.isEmpty()) return null;
        return new MultipleExceptions(stack);
    }

    public static def make(t:CheckedThrowable):MultipleExceptions {
        if (null == t) return null;
        return new MultipleExceptions(t);
    }
}
