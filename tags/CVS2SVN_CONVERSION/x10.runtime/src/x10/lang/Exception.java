/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.lang;

/**
 * @author Christian Grothoff
 * 
 * This is the base-class for all X10 exceptions.  Now, it would be nice if we 
 * could make it extend x10.lang.Object, but since we're on top of java, we 
 * cannot.  It extends error since X10 exceptions should (IMO) be unchecked, and 
 * on top of Java this is the best way to get that.
 */
public class Exception extends java.lang.Error {
    public Exception(String s) { super(s); }
    public Exception() { }
}
