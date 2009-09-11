/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.io;

import x10.compiler.NativeRep;
import x10.compiler.Native;

public value FileWriter extends OutputStreamWriter {
    @NativeRep("java", "java.io.FileOutputStream", null, null)
    @NativeRep("c++", "x10aux::ref<x10::io::FileWriter__FileOutputStream>", "x10::io::FileWriter__FileOutputStream", null)
    protected final static value FileOutputStream extends OutputStream {
        public native def this(String) throws IOException;
    }

    val file: File;
    
    @Native("java", "new java.io.BufferedOutputStream(new java.io.FileOutputStream(#1))")
    private static def make(path: String):OutputStream throws IOException {
        return new FileOutputStream(path);       
    }

    public def this(file: File{self.at(here)}) throws IOException {
        super(make(file.getPath()));
        this.file = file;
    }
}
