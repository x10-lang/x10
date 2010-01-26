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
 * Thrown when an exceptional arithmetic condition has occurred.
 * For example, an integer "divide by zero" throws an instance of this class.
 */
@NativeRep("java", "java.lang.ArithmeticException", null, null)
public class ArithmeticException extends RuntimeException {

    /**
     * Construct an ArithmeticException with no detail message.
     */
    public def this() { super(); }

    /**
     * Construct an ArithmeticException with the specified detail message.
     *
     * @param message the detail message
     */
    public def this(message: String) { super(message); }
}

// vim:tabstop=4:shiftwidth=4:expandtab
