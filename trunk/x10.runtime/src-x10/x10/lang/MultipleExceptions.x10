/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
 
package x10.lang;

import x10.util.Stack;
import x10.io.Printer;


/**
 * @author Christian Grothoff
 * @author tardieu
 */
public value MultipleExceptions(exceptions: ValRail[Throwable]) extends RuntimeException {

    public def this(stack: Stack[Throwable]{self.at(here)}) {
        val s = new Stack[Throwable]();
        // flatten MultipleExceptions in the stack
        for (t in stack.toValRail()) {
            if (t instanceof MultipleExceptions) {
                for (u: Throwable in (t as MultipleExceptions).exceptions) 
		    s.push(u); 
            } else {
                s.push(t);
            }
        }
        property(s.toValRail());
    }

    public def this(t: Throwable) {
        val s = new Stack[Throwable]();
        if (t instanceof MultipleExceptions) {
            for (u: Throwable in (t as MultipleExceptions).exceptions) 
		s.push(u); 
        } else {
            s.push(t);
        }
        property(s.toValRail());
    }

    // workarounds for XTENLANG-283, 284

    public def printStackTrace(): void {
        //super.printStackTrace();
        for (t: Throwable in exceptions) {
	        t.printStackTrace();
        }
    }

    public def printStackTrace(p:Printer): void {
        //super.printStackTrace(p);
        //for (t: Throwable in exceptions) t.printStackTrace(p);
    }
}
