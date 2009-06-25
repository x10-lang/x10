package x10.lang;

/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.lang;

import x10.util.Iterator;
import x10.util.Stack;

/**
 * @author Christian Grothoff
 */
public class MultipleExceptions extends Exception {
    val exception:Stack; // Throwable
    public def this(s:Stack) = {
	val s_actual = new Stack();
        // cvp: make sure that there are no MultipleExceptions in the stack -- 
        // otherwise toString might result in a stack-overflow.
        for (Iterator it = s.iterator(); it.hasNext(); ) {
            val t = it.next() as Throwable;
            if (t instanceof MultipleExceptions) {
                val  me = t as MultipleExceptions;
                s_actual.addAll(me.exceptions);
            } else {
                s_actual.add(t);
            }
        }
        exceptions = s_actual;
    }
    
    public def toString():String= exceptions.toString();
    
    public def printStackTrace():void = {
        super.printStackTrace();
        for (val it = exceptions.iterator();it.hasNext();)
            (it.next() to Throwable).printStackTrace();
    }
}
