/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * Thrown to indicate that the code has attempted to cast an object to a subclass of which it is
 * not an instance.
 */
@NativeRep("java", "java.lang.ClassCastException", null, null)
public class ClassCastException extends RuntimeException {

    /**
     * Construct a ClassCastException with no detail message.
     */
    public def this() { super(); }

    /**
     * Construct a ClassCastException with the specified detail message.
     *
     * @param message the detail message
     */
    public def this(message: String) { super(message); }
}

// vim:tabstop=4:shiftwidth=4:expandtab
