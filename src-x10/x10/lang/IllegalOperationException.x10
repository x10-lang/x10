/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

public value IllegalOperationException extends RuntimeException {
    public def this() { super("illegal operation exception"); } 
    public def this(message: String) { super(message); }
}
