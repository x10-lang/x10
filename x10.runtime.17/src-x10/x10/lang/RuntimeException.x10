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

@NativeRep("java", "java.lang.RuntimeException")
public class RuntimeException extends Exception {
    public native def this(): RuntimeException;
    public native def this(message: String): RuntimeException;
    public native def this(message: String, cause: Throwable): RuntimeException;
    public native def this(cause: Throwable): RuntimeException;
}
