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

public class FileReader extends InputStreamReader {
    global val file:File;

    @NativeRep("java", "java.io.FileInputStream", null, null)
    @NativeRep("c++", "x10aux::ref<x10::io::FileReader__FileInputStream>", "x10::io::FileReader__FileInputStream", null)
    protected final static class FileInputStream extends InputStream {
        public native def this(String);
    }

    public def this(file: File!) throws IOException {
        super(new FileInputStream(file.getPath()));
        this.file = file;
    }
}
