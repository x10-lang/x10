/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime.clock;

/**
 * @author Christian Grothoff
 */
public class ClockUseException extends RuntimeException {
   public ClockUseException(String s) { super(s); }
   public ClockUseException() { }
}
