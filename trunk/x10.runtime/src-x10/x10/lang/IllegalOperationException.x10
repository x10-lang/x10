/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

/**
 * Thrown to indicate that the operation is not legal for a particular object.
 */
public class IllegalOperationException extends RuntimeException {
    /**
     * Construct an IllegalOperationException with the default detail message.
     */
    public def this() { super("illegal operation exception"); } 

    /**
     * Construct an IllegalOperationException with the specified detail message.
     *
     * @param message the detail message
     */
    public def this(message: String) { super(message); }
}

// vim:tabstop=4:shiftwidth=4:expandtab
