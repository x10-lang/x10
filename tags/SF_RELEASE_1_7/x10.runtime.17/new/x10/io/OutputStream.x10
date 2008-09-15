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

@NativeRep("java", "java.io.OutputStream")
public class OutputStream {
    @Native("java", "#0.write(#1)")
    public native def write(Byte): void throws IOException;
    
    @Native("java", "#0.write(#1, #2, #3)")
    public native def write(Rail[Byte], Int, Int): void throws IOException;
}
