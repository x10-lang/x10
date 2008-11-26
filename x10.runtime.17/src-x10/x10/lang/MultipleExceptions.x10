/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
 
package x10.lang;

import x10.runtime.StackThrowable;

/**
 * @author Christian Grothoff
 * @author tardieu
 */
public value MultipleExceptions(exceptions: ValRail[Throwable]) extends RuntimeException {
    public def this(stack: StackThrowable) {
        val s = new StackThrowable();
        // flatten MultipleExceptions in the stack
        for (t: Throwable in stack.toValRail()) {
            if (t instanceof MultipleExceptions) {
            	for (u: Throwable in (t as MultipleExceptions).exceptions) s.push(u); 
            } else {
                s.push(t);
            }
        }
        property(s.toValRail());
    }

    public def this(t: Throwable) {
        val s = new StackThrowable();
        if (t instanceof MultipleExceptions) {
         	for (u: Throwable in (t as MultipleExceptions).exceptions) s.push(u); 
        } else {
            s.push(t);
        }
        property(s.toValRail());
    }

/*
    public def printStackTrace(): void {
        super.printStackTrace();
        for (t: Throwable in exceptions) t.printStackTrace();
    }
*/
}
