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
 * The class Exception and its subclasses are a form of Throwable that indicates conditions that
 * a reasonable application might want to catch.
 */
@NativeRep("java", "java.lang.Exception", null, null)
public class Exception extends Throwable {

    /**
     * Construct an Exception with no detail message and no cause.
     */
    public def this() { super(); }

    /**
     * Construct an Exception with the specified detail message and no cause.
     *
     * @param message the detail message
     */
    public def this(message: String) { super(message); } 

    /**
     * Construct an Exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public def this(message: String, cause: Throwable) { super(message, cause); } 

    /**
     * Construct an Exception with no detail message and the specified cause.
     *
     * @param cause the cause
     */
    public def this(cause: Throwable) { super(cause); } 
}

// vim:tabstop=4:shiftwidth=4:expandtab
