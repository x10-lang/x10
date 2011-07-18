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
public class MultipleExceptions(exceptions:Rail[Throwable]) extends Exception {
    public property exceptions():Rail[Throwable] = exceptions;

    public def this(stack:Stack[Throwable]) {
        property(stack.toArray());
    }

    public def this() {
        property(null);
    }

    public def this(t:Throwable) {
        property(new Array[Throwable](1, t));
    }

    public def printStackTrace(): void {
        for (t:Throwable in exceptions.values()) {
            t.printStackTrace();
        }
    }

    public def printStackTrace(p:Printer): void {
        for (t:Throwable in exceptions.values()) {
            t.printStackTrace(p);
        }
    }

    public def toString() {
        var me:String = super.toString();
        if (exceptions.size > 0) {
	  for (e in exceptions.values()) {
              me += "\n\t"+e;
          }
        }
        return me;
    }

    public static def make(stack:Stack[Throwable]):MultipleExceptions {
        if (null == stack || stack.isEmpty()) return null;
        return new MultipleExceptions(stack);
    }

    public static def make(t:Throwable):MultipleExceptions {
        if (null == t) return null;
        return new MultipleExceptions(t);
    }
}
