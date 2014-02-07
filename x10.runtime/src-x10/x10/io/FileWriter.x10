/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.io;

import x10.compiler.NativeRep;
import x10.compiler.Native;

/**
 * Supports writing and appending to a File.
 * Modeled after java.io.FileWriter.
 * Note: The backing OutputStream is implicitly created with 
 *       buffering enabled at the native layer.
 */
public class FileWriter extends OutputStreamWriter implements Unserializable { 
    @NativeRep("java", "x10.core.io.FileOutputStream", null, "x10.core.io.FileOutputStream.$RTT")
    @NativeRep("c++", "x10::io::FileWriter__FileOutputStream*", "x10::io::FileWriter__FileOutputStream", null)
    protected final static class FileOutputStream extends OutputStream {
        @Native("java", "new x10.core.io.FileOutputStream((java.lang.System[]) null).x10$io$FileReader$FileOutputStream$$init$S(#path, #append)")
        public native def this(path:String, append:Boolean);
    }

    val file:File;
    
    // @Native("java", "new java.io.BufferedOutputStream(new java.io.FileOutputStream(#path))")
    private static def make(path: String, append: Boolean):OutputStream {
        return new FileOutputStream(path, append);       
    }

    public def this(file: File) {
        this(file, false);
    }

    public def this(file: File, append: Boolean) {
        super(make(file.getPath(), append));
        this.file = file;
    }
}
