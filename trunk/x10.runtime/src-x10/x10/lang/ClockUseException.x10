/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

/**
 * Thrown to indicate that a clock was used incorrectly.  For example, attempting to operate
 * on a dropped clock throws an instance of this class.
 */
public class ClockUseException extends RuntimeException {

    /**
     * Construct a ClockUseException with the default detail message.
     */
    public def this() { super("clock use exception"); }

    /**
     * Construct a ClockUseException with the specified detail message.
     *
     * @param message the detail message
     */
    public def this(message: String) { super(message); } 
}

// vim:shiftwidth=4:tabstop=4:expandtab
