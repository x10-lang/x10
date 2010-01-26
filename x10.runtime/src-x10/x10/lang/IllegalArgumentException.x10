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
 * Thrown to indicate that a method has been passed an illegal or inappropriate argument.
 */
@NativeRep("java", "java.lang.IllegalArgumentException", null, null)
public class IllegalArgumentException extends RuntimeException {

    /**
     * Construct an IllegalArgumentException with no detail message and no cause.
     */
    public def this() { super(); }

    /**
     * Construct an IllegalArgumentException with the specified detail message and no cause.
     *
     * @param message the detail message
     */
    public def this(message: String) { super(message); } 

    /**
     * Construct an IllegalArgumentException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public def this(message: String, cause: Throwable) { super(message, cause); } 

    /**
     * Construct an IllegalArgumentException with no detail message and the specified cause.
     *
     * @param cause the cause
     */
    public def this(cause: Throwable) { super(cause); } 
}

// vim:tabstop=4:shiftwidth=4:expandtab
