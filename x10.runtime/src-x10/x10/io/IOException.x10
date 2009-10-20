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

@NativeRep("java", "java.io.IOException", null, null)
public class IOException extends Exception {
    public def this() { super(); }
    public def this(message: String) { super(message); }
}
