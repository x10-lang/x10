/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.io;

import x10.compiler.Native;
import x10.compiler.NativeRep;

public class FileReader extends InputStreamReader {
    // TODO: This is questionable.
    //       What does it mean to send a File to another node?
    val file:File;

    @NativeRep("java", "java.io.FileInputStream", null, "x10.rtt.Types.FILE_INPUT_STREAM")
    @NativeRep("c++", "x10aux::ref<x10::io::FileReader__FileInputStream>", "x10::io::FileReader__FileInputStream", null)
    protected final static class FileInputStream extends InputStream {
        @Native("java", "new Object() { java.io.FileInputStream eval(String s) { try { return new java.io.FileInputStream(s); } catch (java.io.FileNotFoundException e) { throw new x10.io.FileNotFoundException(e.getMessage()); } } }.eval(#1)")
        public native def this(String); //throws FileNotFoundException;
    }

    public def this(file: File) //throws IOException 
    {
        super(new FileInputStream(file.getPath()));
        this.file = file;
    }
}
