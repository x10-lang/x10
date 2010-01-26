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
 * Thrown to indicate that an array has been accessed with an illegal index.
 * For example, the point is outside of the array's region.
 */
@NativeRep("java", "java.lang.ArrayIndexOutOfBoundsException", null, null)
public class ArrayIndexOutOfBoundsException extends RuntimeException {

    /**
     * Construct an ArrayIndexOutOfBoundsException with no detail message.
     */
    public def this() { super(); }

    /**
     * Construct an ArrayIndexOutOfBoundsException with the specified detail message.
     *
     * @param message the detail message
     */
    public def this(message: String) { super(message); }
}

// vim:tabstop=4:shiftwidth=4:expandtab
