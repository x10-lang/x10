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
import x10.compiler.PerProcess;

public class Console {
        @Native("java", "new x10.core.io.OutputStream(java.lang.System.out)")
        @Native("c++", "x10::io::OutputStreamWriter__OutputStream::STANDARD_OUT()")
        private native static def realOut(): OutputStreamWriter.OutputStream;

        @Native("java", "new x10.core.io.OutputStream(java.lang.System.err)")
        @Native("c++", "x10::io::OutputStreamWriter__OutputStream::STANDARD_ERR()")
        private native static def realErr(): OutputStreamWriter.OutputStream;

        @Native("java", "new x10.core.io.InputStream(java.lang.System.in)")
        @Native("c++", "x10::io::InputStreamReader__InputStream::STANDARD_IN()")
        private native static def realIn(): InputStreamReader.InputStream;
    
        @PerProcess
        public static OUT:Printer{self!=null} = new Printer(new OutputStreamWriter(realOut()));
        @PerProcess
        public static ERR:Printer{self!=null} = new Printer(new OutputStreamWriter(realErr()));
        @PerProcess
        public static IN:Reader{self!=null}  = new InputStreamReader(realIn());
        
   /*
        public static def write(b: Byte): void throws IOException = OUT.write(b);
        public static def println(): void throws IOException = OUT.println();
        public static def print(o: Object): void throws IOException = OUT.print(o);
        public static def print(o: String): void throws IOException = OUT.print(o);
        public static def println(o: Object): void throws IOException = OUT.print(o);
        public static def println(o: String): void throws IOException = OUT.print(o);
    
        public static def printf(fmt: String, args: Rail[Object]): void throws IOException = OUT.printf(fmt, args);

        public static def ewrite(b: Byte): void throws IOException = ERR.write(b);
        public static def eprintln(): void throws IOException = ERR.println();
        public static def eprint(o: Object): void throws IOException = ERR.print(o);
        public static def eprint(o: String): void throws IOException = ERR.print(o);
        public static def eprintln(o: Object): void throws IOException = ERR.print(o);
        public static def eprintln(o: String): void throws IOException = ERR.print(o);
    
        public static def eprintf(fmt: String, args: Rail[Object]): void throws IOException = ERR.printf(fmt, args);
        
        public static def read(): Byte throws IOException = IN.read();
        public static def readln(): Byte throws IOException = IN.readLine();
        public static def readByte(): Byte throws IOException = IN.readByte();
        public static def readChar(): Char throws IOException = IN.readChar();
   */
}
