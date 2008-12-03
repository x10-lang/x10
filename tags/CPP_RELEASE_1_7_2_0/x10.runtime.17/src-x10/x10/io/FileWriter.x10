package x10.io;

import x10.compiler.NativeRep;
import x10.compiler.Native;

public value FileWriter extends OutputStreamWriter {
    @NativeRep("java", "java.io.FileOutputStream", null, null)
    @NativeRep("c++", "x10aux::ref<x10::io::FileOutputStream>", "x10::io::FileOutputStream", null)
    protected static value FileOutputStream extends OutputStream {
        public native def this(String) throws IOException;
    }

    val file: File;
    
    public def this(file: File) throws IOException {
        super(new FileOutputStream(file.getPath()));
        this.file = file;
    }
}
