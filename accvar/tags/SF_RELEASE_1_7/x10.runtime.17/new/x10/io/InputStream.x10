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

@NativeRep("java", "java.io.InputStream")
public class InputStream {
    @Native("java", "$0.read(#1)")
    public native def read(): Int;
}
