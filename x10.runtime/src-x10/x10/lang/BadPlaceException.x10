/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

/**
 * Thrown to indicate that an entity residing in a different place is accessed from 'here'.
 * Since most place checking is done statically, only used when accessing array elements.
 */
public class BadPlaceException extends RuntimeException {

    /**
     * Construct a BadPlaceException with the default detail message.
     */
    public def this() = super("bad place exception at "+here);

    /**
     * Construct a BadPlaceException with the specified detail message.
     *
     * @param message the detail message
     */
    public def this(message: String) = super(message);
}

// vim:tabstop=4:shiftwidth=4:expandtab
