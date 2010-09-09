/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.lang;

/**
 * @author Christian Grothoff
 * @author tardieu
 */
public class MultipleExceptions(exceptions: ValRail[Throwable]) extends RuntimeException {
    public def this(stack: Stack[Throwable]) {
        val s = new Stack[Throwable]();
        // flatten MultipleExceptions in the stack
        for (t: Throwable in stack) {
            if (t instanceof MultipleExceptions) {
            	for (u: Throwable in (t as MultipleExceptions).exceptions) s.push(u); 
            } else {
                s.push(t);
            }
        }
        property(Rail.makeVal[Throwable](s.size(), (i:nat)=>s.pop()));
    }
    
    public def printStackTrace(): void {
        super.printStackTrace();
        for (t: Throwable in exceptions) t.printStackTrace();
    }
}
