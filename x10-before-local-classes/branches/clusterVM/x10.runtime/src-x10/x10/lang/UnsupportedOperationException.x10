/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

public value UnsupportedOperationException extends RuntimeException {
    public def this() = super("unsupported operation exception");
    public def this(message: String) = super(message);
}
