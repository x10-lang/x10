/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.lang;

import java.util.Iterator;
import java.util.Stack;

/**
 * @author Christian Grothoff
 */
public class MultipleExceptions extends x10.lang.Exception {
    public final Stack exceptions; // <Throwable>
    
    public MultipleExceptions(Stack s) {
        Stack s_actual = new Stack();
        // cvp: make sure that there are no MultipleExceptions in the stack -- 
        // otherwise toString might result in a stack-overflow.
        for (Iterator it = s.iterator(); it.hasNext(); ) {
            Throwable t = (Throwable) it.next();
            if (t instanceof MultipleExceptions) {
                MultipleExceptions me = (MultipleExceptions) t;
                s_actual.addAll(me.exceptions);
            } else {
                s_actual.add(t);
            }
        }
        exceptions = s_actual;
    }
    
    public String toString() {
        return exceptions.toString();
    }
    
    public void printStackTrace() {
        super.printStackTrace();
        for (Iterator it = exceptions.iterator();it.hasNext();)
            ((Throwable)it.next()).printStackTrace();
    }
}
