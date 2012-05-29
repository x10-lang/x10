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

    @NativeRep("java", "x10.core.io.FileInputStream", null, "x10.core.io.FileInputStream.$RTT")
    @NativeRep("c++", "x10aux::ref<x10::io::FileReader__FileInputStream>", "x10::io::FileReader__FileInputStream", null)
    protected final static class FileInputStream extends InputStream {
    	// XTENLANG-3063
        // @Native("java", "new x10.core.io.FileInputStream((java.lang.System[]) null).$init(#path)")
    	@Native("java", "new x10.core.io.FileInputStream((java.lang.System[]) null).x10$io$FileReader$FileInputStream$$init$S(#path)")
        public native def this(path: String); //throws FileNotFoundException;
    }

    public def this(file: File) //throws IOException 
    {
        super(new FileInputStream(file.getPath()));
        this.file = file;
    }
}
