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

@NativeRep("java", "java.io.FileNotFoundException", null, null)
@NativeRep("c++", "x10aux::ref<x10::io::FileNotFoundException>", "x10::io::FileNotFoundException", null)
public value FileNotFoundException extends IOException {
    public native def this(): FileNotFoundException;
    public native def this(message: String): FileNotFoundException;
}
