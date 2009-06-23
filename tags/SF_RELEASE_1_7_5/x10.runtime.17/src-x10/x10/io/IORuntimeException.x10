/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.io;

import x10.compiler.Native;
import x10.compiler.NativeRep;

public value IORuntimeException extends RuntimeException {
    public def this(): IORuntimeException { super(); }
    public def this(message: String): IORuntimeException { super(message); }
}
