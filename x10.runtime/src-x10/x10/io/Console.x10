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

public class Console {
        @Native("java", "new x10.core.io.OutputStream(java.lang.System.out)")
        @Native("c++", "::x10::io::OutputStreamWriter__OutputStream::STANDARD_OUT()")
        private native static def realOut(): OutputStreamWriter.OutputStream;

        @Native("java", "new x10.core.io.OutputStream(java.lang.System.err)")
        @Native("c++", "::x10::io::OutputStreamWriter__OutputStream::STANDARD_ERR()")
        private native static def realErr(): OutputStreamWriter.OutputStream;

        @Native("java", "new x10.core.io.InputStream(java.lang.System.in)")
        @Native("c++", "::x10::io::InputStreamReader__InputStream::STANDARD_IN()")
        private native static def realIn(): InputStreamReader.InputStream;
    
        public static OUT:Printer{self!=null} = new Printer(new OutputStreamWriter(realOut()));
        public static ERR:Printer{self!=null} = new Printer(new OutputStreamWriter(realErr()));
        public static IN:Reader{self!=null}  = new InputStreamReader(realIn());
        
}
