package x10.io;

import x10.compiler.NativeRep;
import x10.compiler.Native;

public class FileWriter extends OutputStreamWriter {
    @NativeRep("java", "java.io.FileOutputStream", null, null)
    protected static class FileOutputStream extends OutputStream {
        public native def this(String) throws IOException;
    }

    val file: File;
    
    public def this(file: File) throws IOException {
        super(new FileOutputStream(file.getPath()));
        this.file = file;
    }
}
