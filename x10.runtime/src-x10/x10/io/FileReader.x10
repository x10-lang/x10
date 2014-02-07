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

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.util.GrowableRail;

/**
 * A reader that reads its data from a File.
 * Note: The backing InputStream is implicitly created with 
 *       buffering enabled at the native layer.
 */
public class FileReader extends InputStreamReader implements Unserializable {
    @NativeRep("java", "x10.core.io.FileInputStream", null, "x10.core.io.FileInputStream.$RTT")
    @NativeRep("c++", "x10::io::FileReader__FileInputStream*", "x10::io::FileReader__FileInputStream", null)
    protected final static class FileInputStream extends InputStream {
    	@Native("java", "new x10.core.io.FileInputStream((java.lang.System[]) null).x10$io$FileReader$FileInputStream$$init$S(#path)")
        public native def this(path: String);
    }

    val file:File;

    private val buf = new GrowableRail[Byte]();

    @Native("c++", "reinterpret_cast< ::x10::io::FileReader__FileInputStream* >((#this)->FMGL(stream))->readLine()")
    public def readLine():String { 
        try {
            while (true) {
                val b = read();
                if ((b as Char) == '\n') break;
		buf.add(b);
            }
        } catch (e:IOException) {
            if (buf.size() == 0) {
                throw e;
            }
        }
        val ans = new String(buf);
        buf.clear();
        return ans;
    }

    // Specialize code path to call readLine directly since
    // that will get to the efficient @Native impl for Native X10
    private static class FRLM implements Marshal[String] {
        public def read(r:Reader):String = r.readLine();
        public def write(w:Writer, s:String):void {
	    w.write(s.bytes());
        }
    };
    private static val M = new FRLM();
    public def lines():ReaderIterator[String] = new ReaderIterator[String](M, this);

    public def this(file: File) {
        super(new FileInputStream(file.getPath()));
        this.file = file;
    }
}
